/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.core.model.book;

import java.util.concurrent.atomic.AtomicBoolean;

import com.rr.core.model.Instrument;

public final class LockableL1Book extends BaseL1Book {

    private final AtomicBoolean _lock = new AtomicBoolean( false );

    public LockableL1Book( Instrument inst ) { 
        super( inst );
    }

    @Override
    public boolean isLockable() {
        return true;
    }
    
    @Override
    public void grabLock() {
        while( !_lock.compareAndSet( false, true ) ) {
            // spin
        }
    }

    @Override
    public void releaseLock() {
        _lock.set( false );
    }
}
