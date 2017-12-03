/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.core.socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;

import org.junit.Assert;

import com.rr.core.dummy.warmup.DummyAppProperties;
import com.rr.core.dummy.warmup.TestStats;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.lang.stats.StatsMgr;
import com.rr.core.log.LoggerFactory;
import com.rr.core.os.SocketFactory;
import com.rr.core.session.socket.SocketConfig;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.ThreadUtils;

// test using selector for reading from channel

public class MulticastPingClient {

    String               _mcastGroupAddressIn     = "226.1.1.1";
    String               _mcastGroupAddressOut    = "226.1.1.2";

    static String        _nicIP;

    static boolean       _spin;
    static boolean       _disableLookback;
    static boolean       _debug = false;
    static int           _qos;
    static int           _msgSize;

    public static void main( String args[] ) {
        
        LoggerFactory.setDebug( true );
        StatsMgr.setStats( new TestStats() );
        Map<String,String> p = new HashMap<String,String>();
        
        try {
            DummyAppProperties.testInit( p );
        } catch( Exception e ) {
            Assert.fail( e.getMessage() );
        }
        
        if ( args.length != 8 ) {
            System.out.println( "Args : msgsToSend  NIC  cpuMask  spin{1|0}  disableLoopback{1|0}  debug{1|0}  qos  msgSize" );
            System.exit(-1);
        }

        int msgsToSend = Integer.parseInt( args[0] );
        _nicIP = args[1];
        int mask       = Integer.parseInt( args[2] );
        _spin = Integer.parseInt( args[3] ) == 1;
        _disableLookback = Integer.parseInt( args[4] ) == 1; 
        _debug = Integer.parseInt( args[5] ) == 1; 
        _qos = Integer.parseInt( args[6] );
        _msgSize = Integer.parseInt( args[7] );

        try {
            MulticastPingClient ps = new MulticastPingClient();
            ps.doRun( 1, msgsToSend, 14880, 14981, mask );
        } catch( IOException e ) {
            System.out.println( "ERROR "  + e.getMessage() );
            e.printStackTrace();
        }
    }
    
    
    
    private class EchoBack {
        
        private final String               _id;
        private final ByteBuffer           _inBuf  = ByteBuffer.allocateDirect( _msgSize );
        private final ByteBuffer           _outBuf = ByteBuffer.allocateDirect( _msgSize );
        private       LiteMulticastSocket  _outSocket = null; 
        private final int                  _port;

        public EchoBack( String id, int port ) throws IOException {
            _id = id;
            _port = port;
            outConnect();
        }

        protected final void writeSocket() throws IOException {
            
            _outSocket.write();
        }

        public void outConnect() throws IOException {
            SocketConfig sc = new SocketConfig();
            sc.setServer( true );
            sc.setPort( _port );
            sc.setDisableLoopback( _disableLookback );
            sc.setQOS( _qos );
            sc.setTTL( 1 );
            sc.setNic( new ViewString(_nicIP) );
            ZString[] mcastAddrs = { new ViewString(_mcastGroupAddressOut) };
            sc.setMulticastGroups( mcastAddrs );
            
            _outSocket  = SocketFactory.instance().createMulticastSocket( sc, _inBuf, _outBuf ); 
            _outSocket.configureBlocking( _spin == false );
        }
        
        public void outClose() {
            try { if ( _outSocket != null ) _outSocket.close(); } catch( Exception e ) { /* NADA */ }
        }
        
        public void send( long idx, long nanoTime ) throws IOException {

            _outBuf.clear();
            _outBuf.putLong( idx );
            _outBuf.putLong( nanoTime );
            _outBuf.position( _msgSize );
            
            _outBuf.flip();
            
            writeSocket();

            if ( _debug ) System.out.println( _id + " ECHO Sent id=" + idx + ", time=" + nanoTime );
        }
    }
    
    private class Consumer implements Runnable {

        private       LiteMulticastSocket  _inSocket = null;
        private final ByteBuffer           _inBuf  = ByteBuffer.allocateDirect( _msgSize );
        private final ByteBuffer           _outBuf = ByteBuffer.allocateDirect( _msgSize );
        private       String               _id;
        private final int                  _consume;
        private final int                  _port;
        private final CyclicBarrier        _cb;
        private final EchoBack             _echo;
        private final int                  _mask;

        private volatile int               _in;

        public Consumer( String id, int consume, int port, CyclicBarrier cb, EchoBack echo, int mask ) {
            _id = id;
            _consume = consume;
            _port = port;
            _cb = cb;
            _echo = echo;
            _mask = mask;
        }

        @Override
        public void run() {
            try {
                _cb.await();

                ThreadUtils.setPriority( Thread.currentThread(), ThreadPriority.HIGHEST, _mask ); 
                
                inConnect();
                
                consume( _consume );
                
                inClose();
            } catch( Exception e ) {
                Assert.fail( e.getMessage() );
            }
        }
        
        public void inConnect() throws IOException {

            SocketConfig sc = new SocketConfig();
            sc.setServer( false );
            sc.setPort( _port );
            sc.setDisableLoopback( _disableLookback );
            sc.setQOS( _qos );
            sc.setTTL( 1 );
            sc.setNic( new ViewString(_nicIP) );
            ZString[] mcastAddrs = { new ViewString(_mcastGroupAddressIn) };
            sc.setMulticastGroups( mcastAddrs );
            
            _inSocket = SocketFactory.instance().createMulticastSocket( sc, _inBuf, _outBuf );
            _inSocket.configureBlocking( _spin == false );
        }

        public void inClose() {
            try { if ( _inSocket != null )  _inSocket.close();  } catch( Exception e ) { /* NADA */ }
            _echo.outClose();
        }
        
        void consume( int max ) throws Exception {
            _in = 0;
            
            while( _in < max ) {
                getMessage( _msgSize );
                _inBuf.flip();
                
                while( _inBuf.hasRemaining() ) {

                    int startPos = _inBuf.position();
                    
                    long id   = _inBuf.getLong();
                    long sent = _inBuf.getLong();
                    
                    _echo.send( id, sent );
                    
                    if ( _debug ) System.out.println( "Consumer " + _id + ", Read id=" + id + ", time=" + sent );
                    
                    ++_in;
                    
                    _inBuf.position( startPos + _msgSize );
                }
            }
        }

        private void getMessage( int msgSize ) throws Exception {
            _inBuf.clear();
            
            while( true ) {
                
                _inSocket.read();
        
                if ( _inBuf.position() >= msgSize ) {
                    break;
                }
            }
        }
    }

    {
        Map<String,String> p = new HashMap<String,String>();
        
        try {
            DummyAppProperties.testInit( p );
        } catch( Exception e ) {
            Assert.fail( e.getMessage() );
        }
    }
    
    private void doRun( int numConsumers, int sendPerProducer, int port, int echoPort, int mask ) throws IOException {
        int totalConsume = numConsumers * sendPerProducer;
        
        Consumer[] consumers = new Consumer[ numConsumers ];
        Thread[] tCons = new Thread[ numConsumers ];

        CyclicBarrier cb = new CyclicBarrier( numConsumers );
        
        for( int i=0; i < numConsumers ; ++i ) {
            EchoBack echo = new EchoBack( "ECHO" + i, echoPort+i );
            
            consumers[i] = new Consumer( "C"+i, totalConsume, port+i, cb, echo, mask );
            tCons[i] = new Thread( consumers[i], "CONSUMER" + i );
        }
        
        for( int i=0; i < numConsumers ; ++i ) {
            tCons[i].start();
        }
        
        for( int i=0; i < numConsumers ; ++i ) {
            try {
                tCons[i].join();
            } catch( InterruptedException e ) {
                // dont care
            }
        }
    }
}
