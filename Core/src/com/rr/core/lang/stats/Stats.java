/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.core.lang.stats;


public interface Stats {

    /**
     * initialise the stats container
     */

    public void initialise();

    /**
     * @param id 
     * @return the defined size for the id supplied
     * @throws RuntimeException if type is unknown
     */
    public int find( SizeType id );
    
    /**
     * @param id
     * @param val
     */
    public void set( SizeType id, int val );

    /**
     * persist stats if implementation supports persistence
     */
    public void store();
    
    /**
     * reread the stats from persistent store if available
     */
    public void reload();
}
