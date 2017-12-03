/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.model.generated.internal.type;


/**
 
*/

import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( "unused" )

public enum OrdType implements SingleByteLookup {

    Market( TypeIds.ORDTYPE_MARKET, "1" ),
    Limit( TypeIds.ORDTYPE_LIMIT, "2" ),
    Stop( TypeIds.ORDTYPE_STOP, "3" ),
    StopLimit( TypeIds.ORDTYPE_STOPLIMIT, "4" ),
    WithOrWithout( TypeIds.ORDTYPE_WITHORWITHOUT, "6" ),
    LimitOrBetter( TypeIds.ORDTYPE_LIMITORBETTER, "7" ),
    LimitWithOrWithout( TypeIds.ORDTYPE_LIMITWITHORWITHOUT, "8" ),
    OnBasis( TypeIds.ORDTYPE_ONBASIS, "9" ),
    PrevQuoted( TypeIds.ORDTYPE_PREVQUOTED, "D" ),
    PrevIndicated( TypeIds.ORDTYPE_PREVINDICATED, "E" ),
    ForexSwap( TypeIds.ORDTYPE_FOREXSWAP, "G" ),
    Funari( TypeIds.ORDTYPE_FUNARI, "I" ),
    MarketIfTouched( TypeIds.ORDTYPE_MARKETIFTOUCHED, "J" ),
    MktWithLeftOverAsLimit( TypeIds.ORDTYPE_MKTWITHLEFTOVERASLIMIT, "K" ),
    PrevFundValPoint( TypeIds.ORDTYPE_PREVFUNDVALPOINT, "L" ),
    NextFundValPoint( TypeIds.ORDTYPE_NEXTFUNDVALPOINT, "M" ),
    Pegged( TypeIds.ORDTYPE_PEGGED, "P" ),
    Unknown( TypeIds.ORDTYPE_UNKNOWN, "?" );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 1; }

    private final byte _val;
    private final int _id;

    OrdType( int id, String val ) {
        _val = val.getBytes()[0];
        _id = id;
    }
    private static final int _indexOffset = 49;
    private static final OrdType[] _entries = new OrdType[32];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) {
             _entries[i] = Unknown; }

        for ( OrdType en : OrdType.values() ) {
             if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    public static OrdType getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for OrdType" );
        }
        OrdType eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for OrdType" );
        return eval;
    }

    @Override
    public final byte getVal() {
        return _val;
    }

    public final int getID() {
        return _id;
    }

}
