/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.core.codec.binary.fastfix.fulldict.entry;

import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;

public final class LongFieldDictEntry implements DictEntry {

    private final long _init;
    private       long _val;

    public LongFieldDictEntry( String initVal ) {
        this( FieldUtils.parseLong( initVal ) );
    }

    public LongFieldDictEntry( long init ) {
        _init = init;
    }
    
    public long getVal() {
        return _val;
    }

    public long getInit() {
        return _init;
    }

    public void setVal( long val ) {
        _val = val;
    }

    @Override
    public void log( ReusableString dest ) {
        dest.append( _val );
    }

    @Override
    public boolean hasValue() {
        return _val != Constants.UNSET_LONG;
    }

    @Override
    public void reset() {
        _val = _init;
    }
}
