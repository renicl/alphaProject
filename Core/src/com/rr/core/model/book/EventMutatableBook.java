/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.core.model.book;

import com.rr.core.model.Message;


/**
 * EventMutableBook is a book designed to only be updated via events
 */
public interface EventMutatableBook extends MutableBook {

    /**
     * apply the event to the order book in an atomic manner 
     * 
     * @param event
     * 
     * @return true if the event changed the book
     */
    public boolean apply( Message event );
}
