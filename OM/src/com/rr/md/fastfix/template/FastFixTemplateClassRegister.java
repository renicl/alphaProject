/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.md.fastfix.template;

import com.rr.core.collections.IntHashMap;

public class FastFixTemplateClassRegister implements TemplateClassRegister {

    private final IntHashMap<Class<? extends FastFixTemplateReader>> _readers = new IntHashMap<Class<? extends FastFixTemplateReader>>( 128, 0.75f );
    private final IntHashMap<Class<? extends FastFixTemplateWriter>> _writers = new IntHashMap<Class<? extends FastFixTemplateWriter>>( 128, 0.75f );
    
    @Override
    public synchronized Class<? extends FastFixTemplateReader> findReader( String name, int id ) {
        return _readers.get( id );
    }

    @Override
    public synchronized Class<? extends FastFixTemplateWriter> findWriter( String name, int id ) {
        return _writers.get( id );
    }

    @Override
    public synchronized void registerReader( Class<? extends FastFixTemplateReader> reader, String name, int id ) {
        _readers.put( id, reader );
    }

    @Override
    public synchronized void registerWriter( Class<? extends FastFixTemplateWriter> writer, String name, int id ) {
        _writers.put( id, writer );
    }
}
