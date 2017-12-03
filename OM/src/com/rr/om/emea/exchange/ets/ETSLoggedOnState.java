/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.om.emea.exchange.ets;

import com.rr.core.codec.RejectDecodeException;
import com.rr.core.lang.CoreReusableType;
import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.log.Logger;
import com.rr.core.log.LoggerFactory;
import com.rr.core.model.Message;
import com.rr.core.model.MsgFlag;
import com.rr.core.session.Session;
import com.rr.core.session.socket.SeqNumSession;
import com.rr.core.session.socket.SessionStateException;
import com.rr.model.generated.internal.core.EventIds;
import com.rr.model.generated.internal.events.interfaces.TestRequest;
import com.rr.om.session.state.SessionState;

public class ETSLoggedOnState implements SessionState {

    private static final Logger  _log = LoggerFactory.create( ETSLoggedOnState.class );

    private static final ErrorCode ERR_NO_SEQ_NUM           = new ErrorCode( "ULS100", "Non decodable message, cant find seqNum, assume its nextExpected" );
    
    private static final ZString SESSION_REJECT_RECEIVED    = new ViewString( "Session-level reject message received" );

    private final Session        _session;
    private final ETSController  _controller;

    private final ReusableString _logMsg     = new ReusableString(100);
    private final ReusableString _logMsgBase;
    
    public ETSLoggedOnState( SeqNumSession session, ETSController sessionController ) {
        _session   = session;
        _controller = sessionController;
        _logMsgBase = new ReusableString( "[UTPLoggedIn-" + _session.getComponentId() + "] " );
    }
    
    @Override
    public final void handle( Message msg ) throws SessionStateException { 
        
        switch( msg.getReusableType().getSubId() ) {
        case EventIds.ID_NEWORDERSINGLE:
        case EventIds.ID_NEWORDERACK:
        case EventIds.ID_TRADENEW:
        case EventIds.ID_CANCELREPLACEREQUEST:
        case EventIds.ID_CANCELREQUEST:
        case EventIds.ID_CANCELREJECT:
        case EventIds.ID_REJECTED:
        case EventIds.ID_CANCELLED:
        case EventIds.ID_REPLACED:
        case EventIds.ID_DONEFORDAY:
        case EventIds.ID_STOPPED:
        case EventIds.ID_EXPIRED:
        case EventIds.ID_SUSPENDED:
        case EventIds.ID_RESTATED:
        case EventIds.ID_TRADECORRECT:
        case EventIds.ID_TRADECANCEL:
        case EventIds.ID_ORDERSTATUS:
            acceptMessage( msg );
            _session.dispatchInbound( msg );
            return;                          // dont recycle
        case EventIds.ID_HEARTBEAT:
            _controller.setHeartbeatReceived();
            acceptSessionMessage( msg );
            break;
        case EventIds.ID_TESTREQUEST:
            acceptSessionMessage( msg );
            ZString reqId = ((TestRequest)msg).getTestReqID();
            
            if ( reqId.length() == 0 ) {
                _log.warn( "No valid TestReqID on TestRequest message" );
            }
            
            _controller.enqueueHeartbeat( reqId );
            break;
        case EventIds.ID_SESSIONREJECT:
            acceptSessionMessage( msg );
            logWarn( SESSION_REJECT_RECEIVED );
            break;
        case EventIds.ID_UTPTRADINGSESSIONSTATUS:
            _log.warn( "UTPLoggedOnState trading session status not yet implemented" );
            acceptMessage( msg );
            break;
        case EventIds.ID_UTPLOGONREJECT:
            acceptSessionMessage( msg );
            _session.disconnect( false );
            break;
        case EventIds.ID_UTPLOGON:
            throw new SessionStateException( "Already Logged in" );
        case EventIds.ID_SEQUENCERESET:
        case EventIds.ID_RESENDREQUEST:
        case EventIds.ID_LOGON:
        case EventIds.ID_LOGOUT:
            throw new SessionStateException( "Unsupported event type " + msg.getClass().getSimpleName() );
        default:
            if ( msg.getReusableType() == CoreReusableType.RejectDecodeException ) {
                if ( msg.getMsgSeqNum() == 0 ) {
                    // unable to decode message avoid perm loop by assuming seqNum is next expected !
                    // CAN CAUSE SYNC ISSUES SO LOG ERROR
                    
                    RejectDecodeException e = (RejectDecodeException) msg;
                    
                    _log.error( ERR_NO_SEQ_NUM, e.getMessage() );
                    
                    msg.setMsgSeqNum( _controller.getNextExpectedInSeqNo() );
                }
            }
            acceptMessage( msg );
            _session.dispatchInbound( msg );
            return;
        }
        _session.inboundRecycle( msg );
    }
    
    private void acceptSessionMessage( Message msg ) {
        // nothing to do
    }

    private void acceptMessage( Message msg ) throws SessionStateException {
        final int newSeqNum          = msg.getMsgSeqNum();
        final int nextExpectedSeqNum = _controller.getNextExpectedInSeqNo();
        
        if ( newSeqNum == 0 ) return;
        
        if ( newSeqNum == nextExpectedSeqNum ) {
            _controller.persistInMsgAndUpdateSeqNum( newSeqNum + 1 );
        } else if ( newSeqNum < nextExpectedSeqNum ) {
            
            if ( msg.isFlagSet( MsgFlag.PossDupFlag ) == false )
                throw new SessionStateException( "Sequence number mismatch. Expecting=" + nextExpectedSeqNum + ", Received=" + newSeqNum );
            
            // pos dup dont DECREASE the seqnum

            _controller.persistPosDupMsg( newSeqNum );
            
        } else { // missing messages
            throw new SessionStateException( "Message Gap: Sequence number mismatch. Expecting=" + nextExpectedSeqNum + ", Received=" + newSeqNum );
        }
    }

    private void logWarn( ZString msg ) {
        _logMsg.copy( _logMsgBase ).append( msg );
        _log.warn( msg );
    }

    @Override
    public void connected() {
        _log.warn( "Unexpected connected event when already loggedIn" );
    }
}
