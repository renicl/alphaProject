/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.md.us.cme.writer;

import com.rr.core.codec.binary.fastfix.FastFixBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapWriter;
import com.rr.core.codec.binary.fastfix.common.constant.int32.IntMandWriterConst;
import com.rr.core.codec.binary.fastfix.common.constant.string.StringMandWriterConst;
import com.rr.core.codec.binary.fastfix.common.def.string.StringWriterDefault;
import com.rr.core.codec.binary.fastfix.common.noop.int32.IntMandWriterNoOp;
import com.rr.core.codec.binary.fastfix.common.noop.int32.UIntMandWriterNoOp;
import com.rr.core.codec.binary.fastfix.common.noop.int64.ULongMandWriterNoOp;
import com.rr.core.codec.binary.fastfix.common.ComponentFactory;
import com.rr.core.codec.binary.fastfix.msgdict.copy.int32.UIntMandWriterCopy;
import com.rr.core.codec.binary.fastfix.msgdict.copy.string.StringWriterCopy;
import com.rr.core.codec.binary.fastfix.msgdict.custom.defexp.delmant.MandDefExpDeltaMantDecimalWriter;
import com.rr.core.codec.binary.fastfix.msgdict.delta.int32.IntMandWriterDelta;
import com.rr.core.codec.binary.fastfix.msgdict.delta.int32.UIntMandWriterDelta;
import com.rr.core.codec.binary.fastfix.msgdict.increment.int32.UIntMandWriterIncrement;
import com.rr.core.lang.ReusableString;
import com.rr.md.fastfix.template.MDIncRefreshFastFixTemplateWriter;
import com.rr.model.generated.internal.events.impl.MDEntryImpl;
import com.rr.model.generated.internal.events.impl.MDIncRefreshImpl;

/*
 * Hand coded template for CME MDIncRefresh #83
 * 
 * only boxes ints in constructor
 
    <template name="MDIncRefresh_83" id="83" dictionary="83"
        <string name="ApplVerID" id="1128"> <constant value="9" /> </string>
        <string name="MessageType" id="35"> <constant value="X" /> </string>
        <string name="SenderCompID" id="49"> <constant value="CME" /> </string>
        <uInt32 name="MsgSeqNum" id="34"></uInt32>
        <uInt64 name="SendingTime" id="52"></uInt64>
        <string name="PosDupFlag" id="43" presence="optional"> <default /> </string>
        <uInt32 name="TradeDate" id="75"></uInt32>
        <sequence name="MDEntries">
            <length name="NoMDEntries" id="268"></length>
            <uInt32 name="MDUpdateAction" id="279"> <copy value="1" /> </uInt32>
            <uInt32 name="MDPriceLevel" id="1023"> <increment /> </uInt32>
            <string name="MDEntryType" id="269"> <copy value="0" /> </string>
            <uInt32 name="MDEntryTime" id="273"> <copy /> </uInt32>
            <uInt32 name="SecurityIDSource" id="22"> <constant value="8" /> </uInt32>
            <uInt32 name="SecurityID" id="48"> <copy /> </uInt32>
            <uInt32 name="RptSeq" id="83"> <increment /> </uInt32>
            <decimal name="MDEntryPx" id="270"> <exponent> <default value="0" /> <mantissa> <delta />
            <int32 name="MDEntrySize" id="271"> <delta /> </int32>
            <uInt32 name="NumberOfOrders" id="346"> <delta /> </uInt32>
            <string name="TradingSessionID" id="336"> <default value="2" /> </string>
        </sequence>
 */

public final class MDIncRefresh_83_Writer implements MDIncRefreshFastFixTemplateWriter {

    private static final int INIT_SEQ_PMAP_SIZE = 2; 

    private final String _name;
    private final int    _id;
    private final ReusableString _mdEntryType = new ReusableString(2);

    private final PresenceMapWriter seqPMap = new PresenceMapWriter();
    
    private final StringMandWriterConst _f1_ApplVerID;
    private final StringMandWriterConst _f2_MessageType;
    private final StringMandWriterConst _f3_SenderCompID;
    private final UIntMandWriterNoOp    _f4_MsgSeqNum;
    private final ULongMandWriterNoOp   _f5_SendingTime;
    private final StringWriterDefault   _f6_PosDupFlag;
    private final UIntMandWriterNoOp    _f6b_TradeDate;
    
    private final IntMandWriterNoOp                 _f7_NoMDEntries;
    private final UIntMandWriterCopy                _f8_MDUpdateAction;
    private final UIntMandWriterIncrement           _f9_PriceLevel;
    private final StringWriterCopy                  _f10_MDEntryType;
    private final UIntMandWriterCopy                _f10b_MDEntryTime;
    private final IntMandWriterConst                _f11_SecurityIDSource; // should be UInt const
    private final UIntMandWriterCopy                _f12_SecurityID;
    private final UIntMandWriterIncrement           _f13_RptSeq;
    private final MandDefExpDeltaMantDecimalWriter  _f14_MDEntryPx;
    private final IntMandWriterDelta                _f16_MDEntrySize;
    private final UIntMandWriterDelta               _f18_NumberOfOrders;
    private final StringWriterDefault               _f19_TradingSessionID;
    
    @SuppressWarnings( "boxing" )
    public MDIncRefresh_83_Writer( ComponentFactory cf, String name, int id ) {
        _name = name;
        _id = id;
        
        _f1_ApplVerID           = cf.getWriter( StringMandWriterConst.class,            "ApplVerID",        1128,   new ReusableString("9") );
        _f2_MessageType         = cf.getWriter( StringMandWriterConst.class,            "MessageType",      35,     new ReusableString("X") );
        _f3_SenderCompID        = cf.getWriter( StringMandWriterConst.class,            "SenderCompID",     49,     new ReusableString("CME") );
        _f4_MsgSeqNum           = cf.getWriter( UIntMandWriterNoOp.class,               "MsgSeqNum",        49 );
        _f5_SendingTime         = cf.getWriter( ULongMandWriterNoOp.class,              "SendingTime",      52 );
        _f6_PosDupFlag          = cf.getWriter( StringWriterDefault.class,              "PosDupFlag",       43,     new ReusableString("") );
        _f6b_TradeDate          = cf.getWriter( UIntMandWriterNoOp.class,               "TradeDate",        75 );
        _f7_NoMDEntries         = cf.getWriter( IntMandWriterNoOp.class,                "NoMDEntries",      268 );
        _f8_MDUpdateAction      = cf.getWriter( UIntMandWriterCopy.class,               "MDUpdateAction",   279,    1 );
        _f9_PriceLevel          = cf.getWriter( UIntMandWriterIncrement.class,          "PriceLevel",       1023 );
        _f10_MDEntryType        = cf.getWriter( StringWriterCopy.class,                 "MDEntryType",      269,    new ReusableString("0") );
        _f10b_MDEntryTime       = cf.getWriter( UIntMandWriterCopy.class,               "MDEntryTime",      273 );
        _f11_SecurityIDSource   = cf.getWriter( IntMandWriterConst.class,               "SecurityIDSource", 22,     8 );
        _f12_SecurityID         = cf.getWriter( UIntMandWriterCopy.class,               "SecurityID",       48 );
        _f13_RptSeq             = cf.getWriter( UIntMandWriterIncrement.class,          "RptSeq",           83 );
        
        _f14_MDEntryPx          = cf.getWriter( MandDefExpDeltaMantDecimalWriter.class,  cf, "MDEntryPx",   270,    0, 0L );
        
        _f16_MDEntrySize        = cf.getWriter( IntMandWriterDelta.class,               "MDEntrySize",      271 );
        _f18_NumberOfOrders     = cf.getWriter( UIntMandWriterDelta.class,              "NumberOfOrders",   346 );
        _f19_TradingSessionID   = cf.getWriter( StringWriterDefault.class,              "TradingSessionID", 336,    new ReusableString("2") );
        
    }

    @Override
    public void write( final FastFixBuilder encoder, final PresenceMapWriter pMap, MDIncRefreshImpl dest ) {
        
        _f1_ApplVerID.write( encoder );                        // pass null to skip field
        _f2_MessageType.write( encoder );
        _f3_SenderCompID.write( encoder );
        
        _f4_MsgSeqNum.write( encoder, dest.getMsgSeqNum() );
        _f5_SendingTime.write( encoder, dest.getSendingTime() );
        
        _f6_PosDupFlag.write( encoder, pMap, null );                 // skip pos dup as we dont care
        _f6b_TradeDate.write( encoder, 0 );        
        
        final int noMDEntries = dest.getNoMDEntries();
        _f7_NoMDEntries.write( encoder, noMDEntries );    
        
        int curEntry = 0;
        
        if ( noMDEntries > 0 ) { 
            MDEntryImpl tmp = (MDEntryImpl) dest.getMDEntries();
            
            do {
                seqPMap.set( encoder, encoder.getCurrentIndex(), INIT_SEQ_PMAP_SIZE );
                
                // 1, 2, 3, 4, 5, 6, 6b, 7, 8, 9, 10, 10b, 11, 12, 13, 14, 16, 17, 18, 19
                
                _f8_MDUpdateAction.write( encoder, seqPMap, tmp.getMdUpdateAction().getVal()-'0' );
                _f9_PriceLevel.write( encoder, seqPMap, tmp.getMdPriceLevel() );
                
                _mdEntryType.reset();
                _mdEntryType.append( tmp.getMdEntryType().getVal() );

                _f10_MDEntryType.write( encoder, seqPMap, _mdEntryType ); 
                _f10b_MDEntryTime.write( encoder, seqPMap, tmp.getMdEntryTime() );
                
                _f11_SecurityIDSource.write();
                _f12_SecurityID.write( encoder, seqPMap, (int)tmp.getSecurityID() );
                
                _f13_RptSeq.write( encoder, seqPMap, tmp.getRepeatSeq() );
                
                _f14_MDEntryPx.write( encoder, seqPMap, tmp.getMdEntryPx() );
                _f16_MDEntrySize.write( encoder, tmp.getMdEntrySize() );

                _f18_NumberOfOrders.write( encoder, tmp.getNumberOfOrders() );
                
                _f19_TradingSessionID.write( encoder, seqPMap, null );

                seqPMap.end();
                
                if ( ++curEntry == noMDEntries ) {
                    break;
                }
                
                tmp = tmp.getNext();
                
            } while( tmp != null );
        }
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public int getId() {
        return _id;
    }

    public void reset() {
        _f4_MsgSeqNum.reset();
        _f5_SendingTime.reset();
        _f6_PosDupFlag.reset();
        _f6b_TradeDate.reset();
        _f7_NoMDEntries.reset();
        _f8_MDUpdateAction.reset();
        _f9_PriceLevel.reset();
        _f10_MDEntryType.reset();
        _f10b_MDEntryTime.reset();
        _f12_SecurityID.reset();
        _f13_RptSeq.reset();
        _f14_MDEntryPx.reset();
        _f16_MDEntrySize.reset();
        _f18_NumberOfOrders.reset();
        _f19_TradingSessionID.reset();
    }
}
