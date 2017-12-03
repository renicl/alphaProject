#*******************************************************************************
# Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
# Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
# You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
# Unless required by applicable law or agreed to in writing,  software distributed under the License 
# is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and limitations under the License.
#*******************************************************************************
#!/bin/bash

export JVM_COMMON="-Djava.net.preferIPv4Stack=true"
export SIM_CLIENT_TO_OM_LOCAL_PORT="14801"
export OM_CLIENT_SERVER_PORT="14802"
export OM_TO_EX_SIM_LOCAL_PORT="14811"
export EX_SIM_SERVER_PORT="14812"


