/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.core.codec.binary.fastfix.common.def;

import com.rr.core.codec.binary.fastfix.common.FieldReader;

public abstract class DefaultFieldReader extends FieldReader {

    public DefaultFieldReader( String name, int id, boolean isOptional ) {
        super( name, id, isOptional );
    } 
    
    @Override
    public boolean requiresPMap() {
        return true;
    }
}
