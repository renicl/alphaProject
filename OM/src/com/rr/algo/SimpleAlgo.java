/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.algo;

import com.rr.core.component.SMTComponent;
import com.rr.core.model.Book;
import com.rr.core.model.Message;

/**
 * if the changed and handleMarketEvent can be invoked on seperate threads then the Algo implementation should use the appropriate dispatcher
 *
 * @author Richard Rose
 */
public interface SimpleAlgo extends SMTComponent {

    public void changed( Book book );
    
    public void handleMarketEvent( Message mktMsg );
}
