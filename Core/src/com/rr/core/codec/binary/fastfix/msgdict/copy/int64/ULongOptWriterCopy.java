/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.core.codec.binary.fastfix.msgdict.copy.int64;

import com.rr.core.codec.binary.fastfix.FastFixBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapWriter;
import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.msgdict.copy.CopyFieldWriter;
import com.rr.core.lang.Constants;

public final class ULongOptWriterCopy extends CopyFieldWriter {

    private final long _init;
    private       long _previous;
    
    public ULongOptWriterCopy( String name, int id, String init ) {
        this( name, id, FieldUtils.parseLong(init) );
    }

    public ULongOptWriterCopy( String name, int id ) {
        this( name, id, Constants.UNSET_LONG );
    }
    
    public ULongOptWriterCopy( String name, int id, long init ) {
        super( name, id, true );
        _init = init;
        reset();
    }

    public void reset() {
        _previous = _init;
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining  
     * 
     * @param encoder
     * @param mapWriter
     * @param value
     */
    public void write( final FastFixBuilder encoder, final PresenceMapWriter mapWriter, final long value ) {
        if ( value != Constants.UNSET_LONG ) {
            if ( value != _previous ) {       
                _previous = value;                      // new value must be encoded
                mapWriter.setCurrentField();
                encoder.encodeOptionalULong( value );
            } else {                                    // value unchanged dont need encode (it will be copied on decode)
                mapWriter.clearCurrentField();
            }
        } else {
            if ( _previous != Constants.UNSET_LONG ) {  // current value is null, need null previous value and encode null byte
                _previous = Constants.UNSET_LONG;
                mapWriter.setCurrentField();
                encoder.encodeNull();
            } else {
                mapWriter.clearCurrentField();
            }
        }
    }

    public long getPreviousValue() {
        return _previous;
    }

    public long getInitValue() {
        return _init;
    }
}
