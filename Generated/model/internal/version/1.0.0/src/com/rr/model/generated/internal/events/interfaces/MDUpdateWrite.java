/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.model.generated.internal.events.interfaces;

import com.rr.core.model.Book;
import com.rr.model.generated.internal.events.interfaces.TickUpdate;
import com.rr.core.lang.ReusableString;

public interface MDUpdateWrite extends BaseMDResponse, MDUpdate {

   // Getters and Setters
    public void setMdReqId( byte[] buf, int offset, int len );
    public ReusableString getMdReqIdForUpdate();

    public void setBook( Book val );

    public void setNoMDEntries( int val );

    public void setTickUpdates( TickUpdate val );

    public void setMsgSeqNum( int val );

    public void setPossDupFlag( boolean val );

}
