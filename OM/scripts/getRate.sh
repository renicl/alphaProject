#*******************************************************************************
# Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
# Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
# You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
# Unless required by applicable law or agreed to in writing,  software distributed under the License 
# is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and limitations under the License.
#*******************************************************************************
#!/bin/sh

FILE=$1
WARM=$2

cat $FILE | sed -e "s/ .*$//" -e "s/^.*://" -e "s/\./ /" | gawk '
BEGIN {
	MAX=0
	CURSEC=0
	CNT=0
}

{
	SEC=$1

	if ( CURSEC == SEC ) {
		++CNT
	} else {
		if ( CNT > MAX ) {
			MAX=CNT
		}
		CNT=0
		CURSEC=SEC
	}
}

END {
	if ( CNT > MAX ) {
		MAX=CNT
	}
	print "MAX RATE per second " MAX
}'


