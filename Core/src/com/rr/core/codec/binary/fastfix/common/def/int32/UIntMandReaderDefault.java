/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.core.codec.binary.fastfix.common.def.int32;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.common.FixFieldReader;
import com.rr.core.codec.binary.fastfix.common.IntFieldValWrapper;
import com.rr.core.codec.binary.fastfix.common.def.DefaultFieldReader;
import com.rr.core.lang.Constants;

public final class UIntMandReaderDefault extends DefaultFieldReader implements FixFieldReader<IntFieldValWrapper> {

    private final int _init;
    
    public UIntMandReaderDefault( String name, int id, String init ) {
        this( name, id, FieldUtils.parseInt(init) );
    }

    public UIntMandReaderDefault( String name, int id ) {
        this( name, id, Constants.UNSET_INT );
    }

    public UIntMandReaderDefault( String name, int id, int init ) {
        super( name, id, false );
        _init = init;
        reset();
    }

    @Override
    public void reset() {
        // nothing
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining  
     * 
     * @param encoder
     * @param mapWriter
     * @param _value
     */
    public int read( final FastFixDecodeBuilder decoder, final PresenceMapReader mapReader ) {
        
        final boolean present = mapReader.isNextFieldPresent();
        
        if ( present ) {
            final int value = decoder.decodeMandUInt();

            return value;
        }
        
        if ( _init != Constants.UNSET_INT ) {  
            return _init;                       // value not present, but have previous value to copy
        }

        throwMissingValueException();
        
        return Constants.UNSET_INT; // keep compiler happy as it cant see throw methods runtime exception
    }
    
    public int getInitValue() {
        return _init;
    }

    @Override
    public void read( FastFixDecodeBuilder decoder, PresenceMapReader mapReader, IntFieldValWrapper dest ) {
        dest.setVal( read( decoder, mapReader ) );
    }
}
