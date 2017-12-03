/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.core.codec.binary.fastfix.msgdict.delta.decimal;

import com.rr.core.codec.binary.fastfix.FastFixBuilder;
import com.rr.core.codec.binary.fastfix.common.FastFixDecimal;
import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.codec.binary.fastfix.msgdict.delta.DeltaFieldWriter;
import com.rr.core.lang.Constants;

public final class DecimalMandWriterDelta extends DeltaFieldWriter {

    private final double         _init;
    private final FastFixDecimal _prevDecimal = new FastFixDecimal();
    
    public DecimalMandWriterDelta( String name, int id, String init ) {
        this( name, id, FieldUtils.parseDouble(init) );
    }
    
    public DecimalMandWriterDelta( String name, int id ) {
        this( name, id, 0 );
    }
    
    public DecimalMandWriterDelta( String name, int id, double init ) {
        super( name, id, false );
        _init = init;
        reset();
    }

    public void reset() {
        _prevDecimal.set( _init );
    }

    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining  
     * 
     * @param encoder
     * @param mapWriter
     * @param value
     */
    public void write( final FastFixBuilder encoder, final double value ) {
        if ( value != Constants.UNSET_DOUBLE ) {
            if ( ! _prevDecimal.isNull() ) {
                
                final long prevMant = _prevDecimal.getMantissa();
                final int  prevExp  = _prevDecimal.getExponent();

                _prevDecimal.set( value );  // we extracted prev value can now overwrite ready for next call

                final long curMant  = _prevDecimal.getMantissa();
                final int  curExp   = _prevDecimal.getExponent();
                
                final long  mantissaDelta = curMant - prevMant;
                final int   expDelta      = curExp  - prevExp;

                boolean overflow = false;
                
                if( curMant > -1 && prevMant <= (Long.MIN_VALUE + curMant) ) {
                    overflow = true;
                }
                else if ( curMant < -1 && prevMant > (curMant - Long.MIN_VALUE) ) {
                    overflow = true;
                }

                encoder.encodeMandInt( expDelta );
                encoder.encodeMandLongOverflow( mantissaDelta, overflow );
                
            } else {
                throwMissingPreviousException();
            }
        } else {
            throwMissingValueException();
        }
    }

    public double getInitValue() {
        return _prevDecimal.get();
    }
}
