/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.core.codec.binary.fastfix.common.noop.vector;

import com.rr.core.codec.binary.fastfix.FastFixBuilder;
import com.rr.core.codec.binary.fastfix.common.noop.NoOpFieldWriter;
import com.rr.core.lang.ReusableString;

public final class ByteVectorMandWriterNoOp extends NoOpFieldWriter {

    public ByteVectorMandWriterNoOp( String name, int id, String init ) {
        this( name, id );
    }
    
    public ByteVectorMandWriterNoOp( String name, int id ) {
        super( name, id, false );
        reset();
    }

    public void reset() {
        // nothing
    }
    
    /**
     * write the field, note the code could easily be extracted and templated but then would get autoboxing and unable to optimise inlining
     * 
     * compares the new value with previous, checking for best matching at start or end of string, then 
     * 
     * @param encoder
     * @param mapWriter
     * @param value
     */
    public void write( final FastFixBuilder encoder, final ReusableString value ) {

        encoder.encodeMandByteVector( value.getBytes(), 0, value.length() );
    }
}
