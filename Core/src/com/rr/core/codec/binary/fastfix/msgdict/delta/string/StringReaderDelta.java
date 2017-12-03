/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.core.codec.binary.fastfix.msgdict.delta.string;

import com.rr.core.codec.RuntimeEncodingException;
import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.FixFieldReader;
import com.rr.core.codec.binary.fastfix.common.StringFieldValWrapper;
import com.rr.core.codec.binary.fastfix.msgdict.delta.DeltaFieldReader;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;

public final class StringReaderDelta extends DeltaFieldReader implements FixFieldReader<StringFieldValWrapper> {

    private final ReusableString _init     = new ReusableString();
    private final ReusableString _previous = new ReusableString();
    
    public StringReaderDelta( String name, int id ) {
        this( name, id, null );
    }
    
    public StringReaderDelta( String name, int id, String init ) {
        super( name, id, false );
        _init.copy( init );
        reset();
    }

    @Override
    public void reset() {
        _previous.copy( _init );
    }
    
    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining  
     * 
     * @param encoder
     * @param mapWriter
     * @param value
     */
    public void read( final FastFixDecodeBuilder decoder, ReusableString dest ) {
        
        dest.reset();
        
        final int deltaIdx = decoder.decodeOptionalInt();
        
        if ( deltaIdx == Constants.UNSET_INT ) return;
        
        if ( deltaIdx < 0 ) {
            final int substringIdx = -1 - deltaIdx;
            
            if ( substringIdx > _previous.length() ) {
                throwBadDeltaValueException(substringIdx );
            }
            
            decoder.decodeString( dest );
            dest.append( _previous, substringIdx );
        } else {
            if ( deltaIdx > _previous.length() ) {
                throwBadDeltaValueException(deltaIdx );
            }
            
            dest.append( _previous, 0, _previous.length() - deltaIdx );
            
            decoder.decodeString( dest );
        }
        
        _previous.copy( dest );
    }

    private final void throwBadDeltaValueException( int idx ) {
        throw new RuntimeEncodingException( "Bad string delta idx of " + idx + ", prevVal=" + _previous.toString() + ", id=" + getName() );
    }
    
    public ZString getInitValue() {
        return _init;
    }

    @Override
    public void read( FastFixDecodeBuilder decoder, PresenceMapReader mapReader, StringFieldValWrapper dest ) {
        read( decoder, dest.getVal() );
    }
}
