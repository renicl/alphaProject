/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.model.generated.internal.events.interfaces;

import com.rr.core.model.Instrument;
import com.rr.core.model.ClientProfile;
import com.rr.core.model.Currency;
import com.rr.core.model.SecurityIDSource;
import com.rr.model.generated.internal.type.Side;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ReusableString;
import com.rr.core.model.Message;

public interface BaseOrderRequest extends CommonClientHeader, Message {

   // Getters and Setters
    public Instrument getInstrument();

    public ClientProfile getClient();

    public ViewString getClOrdId();

    public ViewString getOrigClOrdId();

    public ViewString getSecurityId();

    public ViewString getSymbol();

    public Currency getCurrency();

    public SecurityIDSource getSecurityIDSource();

    public int getTransactTime();

    public int getSendingTime();

    public Side getSide();

    public ViewString getSrcLinkId();

    @Override
    public void dump( ReusableString out );

}
