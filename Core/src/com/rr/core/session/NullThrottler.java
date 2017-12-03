/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.core.session;

import com.rr.core.model.Message;

public final class NullThrottler implements Throttler {

    @Override
    public void setThrottleNoMsgs( int throttleNoMsgs ) {
        // nothing
    }

    @Override
    public void setThrottleTimeIntervalMS( long throttleTimeIntervalMS ) {
        // nothing
    }

    @Override
    public void setDisconnectLimit( int disconnectLimit ) {
        // nothing
    }

    @Override
    public void checkThrottle( Message msg ) throws ThrottleException {
        // nothing
    }

    @Override
    public boolean throttled( long now ) {
        return false;
    }
}
