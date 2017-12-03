/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.md.fastfix.cme;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.rr.core.collections.ConcLinkedMsgQueueSingle;
import com.rr.core.collections.MessageQueue;
import com.rr.core.dispatch.DirectDispatcher;
import com.rr.core.dispatch.DirectDispatcherNonThreadSafe;
import com.rr.core.dispatch.MessageDispatcher;
import com.rr.core.dispatch.ThreadedDispatcher;
import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.Reusable;
import com.rr.core.log.Logger;
import com.rr.core.log.LoggerFactory;
import com.rr.core.model.Book;
import com.rr.core.model.Message;
import com.rr.core.model.MessageHandler;
import com.rr.core.pool.SuperPool;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.utils.Percentiles;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.Utils;
import com.rr.md.book.l2.L2BookDispatchAdapter;
import com.rr.md.book.l2.L2LongIdBookFactory;
import com.rr.md.fastfix.cme.SimFastFixMDGenWithMsgHandler.Listener;
import com.rr.md.us.cme.CMEBookAdapter;
import com.rr.md.us.cme.CMEMarketDataController;
import com.rr.model.generated.internal.events.impl.MDEntryImpl;
import com.rr.model.generated.internal.events.impl.MDIncRefreshImpl;
import com.rr.om.dummy.warmup.DummyInstrumentLocator;
import com.rr.om.main.BaseSMTMain;


public class PerfTestTickDecodeBookUpdate extends BaseTestCase {

    private static final Logger _log = LoggerFactory.console( PerfTestTickDecodeBookUpdate.class );
    
    
    private int     _iter  = 5;
    private int     _count = 1000000;
    private int     _curIdx;
    private int[]   _time;

    private final List<byte[]>              _templateRequests = new ArrayList<byte[]>();
    private SimFastFixMDGenWithMsgHandler   _sender;
    private CMEMarketDataController         _mktDataController;


    private boolean _nanoTiming;

    public PerfTestTickDecodeBookUpdate() throws IOException {
        // nothing

        BaseSMTMain.loadSampleData( "./data/cme/sampleMD.dat", _templateRequests, 30 );

    }

    public void testSync() throws IOException {
        _time = new int[ _count*2 ];

        doTest( _iter, _count, true,  10, 1, 100 );
        doTest( _iter, _count, false, 10, 1, 100 );
    }
    

    private void doTest( int iter, int max, boolean nanoTiming, int numMdEntries, int pMapInitSize, int chainSize ) throws IOException {
        int chains     = max / chainSize;
        int extraAlloc = 10;

        presize( MDIncRefreshImpl.class, chains, chainSize, extraAlloc );
        presize( MDEntryImpl.class, chains, chainSize, extraAlloc );

        _mktDataController = getMktDataController( false, ThreadPriority.Other );

        _sender = new SimFastFixMDGenWithMsgHandler( _templateRequests, nanoTiming, 
                                                     new Listener(){
                                                                    @Override
                                                                    public void onEvent( Message msg, int templateId ) {
                                                                        processEvent( msg, templateId );
                                                                    }} );

        for ( int i=0 ; i < iter ; ++i ) {
            runTest( max, nanoTiming, numMdEntries, pMapInitSize, chainSize );
        }
    }

    private static <T extends Reusable<T>> void presize( Class<T> aclass, int chains, int chainSize, int extraAlloc ) {
        SuperPool<T> sp = SuperpoolManager.instance().getSuperPool( aclass );
        sp.init( chains, chainSize, extraAlloc );
    }
    
    private void runTest( int max, boolean nanoTiming, int numMDEntries, int initPMapSize, int chainSize ) throws IOException {
        
        _nanoTiming = nanoTiming;
        
        _curIdx = 0;
        
        _log.info( "Forcing gc START" );
        System.gc();
        _log.info( "Forcing gc COMPLETE" );
        
        _log.info(  "START TEST ==================" );
        
        long start = Utils.nanoTime();

        for( int i=0 ; i < max ; i++ ) {
            sendNext( i );
        }
        
        long end = Utils.nanoTime();
        
        long timeMicros = (end - start)/1000;
        
        System.out.println( "Tick Decode cnt=" + max + ", ave=" + ((timeMicros)/max) );

        if ( nanoTiming ) {
            Percentiles p = new Percentiles( _time );
            
            System.out.println( "Tick Decode NanoSecond stats " + " count=" + max + 
                                ", med=" + p.median() + 
                                ", ave=" + p.getAverage() + 
                                ", min=" + p.getMinimum() + 
                                ", max=" + p.getMaximum() + 
                                "\n                 " +
                                ", p99=" + p.calc( 99 ) + 
                                ", p95=" + p.calc( 95 ) + 
                                ", p90=" + p.calc( 90 ) + 
                                ", p80=" + p.calc( 80 ) + 
                                ", p70=" + p.calc( 70 ) + 
                                ", p50=" + p.calc( 50 ) + "\n" );
        }
        
        _log.info( "Forcing gc START" );
        System.gc();
        _log.info( "Forcing gc COMPLETE" );
    }

    public void sendNext( int idx ) throws IOException {
        _sender.sendNext( idx );
    }

    /**
     * invoked when tick has been decoded
     * @param msg
     * @param templateId
     */
    protected void processEvent( Message msg, int templateId ) {
        _mktDataController.handle( msg );
    }

    private CMEMarketDataController getMktDataController( boolean async, ThreadPriority priority ) {
        
        MessageDispatcher inboundDispatcher;
        
        if ( async ) {
            MessageQueue queue = new ConcLinkedMsgQueueSingle();
            inboundDispatcher = new ThreadedDispatcher( "SimMktDataDispatcher", queue, priority );
        } else {
            inboundDispatcher = new DirectDispatcher();
        }

        L2LongIdBookFactory<CMEBookAdapter> bookFactory = new L2LongIdBookFactory<CMEBookAdapter>( CMEBookAdapter.class, false, new DummyInstrumentLocator(), 10 );
        
        MessageDispatcher algoDispatcher = new DirectDispatcherNonThreadSafe();

        algoDispatcher.setHandler( new MessageHandler() {
            
            @Override
            public void handle( Message event ) {
                handleNow( event );
            }

            @Override public void handleNow( Message event ) {
                Book book = (Book) event;
                
                long time = Utils.nanoTime() - book.getLastTickInNanos();
                
                store( (int) time );
            }

            @Override public String     getComponentId() { return null; }
            @Override public void       threadedInit()   { /* nothing */ }
            @Override public boolean    canHandle()      { return true; }
        });
        
        L2BookDispatchAdapter<CMEBookAdapter> asyncListener = new L2BookDispatchAdapter<CMEBookAdapter>( algoDispatcher );
        
        CMEMarketDataController ctlr = new CMEMarketDataController( "TestController", "2", inboundDispatcher, bookFactory, asyncListener, false );
        
        algoDispatcher.start();
        inboundDispatcher.start();
        
        return ctlr;
    }
    
    protected void store( int durationNanos ) {
        if ( _nanoTiming && _curIdx < _time.length ) {
            _time[_curIdx++] = durationNanos;
        }
    }
}
