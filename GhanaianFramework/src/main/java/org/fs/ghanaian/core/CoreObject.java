/*
Copyright 2014 GhanaianFramework.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.fs.ghanaian.core;

import android.util.Log;

/**
 * Created by Fatih on 6.10.2014.
 */
public abstract class CoreObject {

    public void log(String m) {
        log(Log.DEBUG, m);
    }

    public void log(int p , String m) {
        if(isLogEnabled()) {
            Log.println(p, getClassTag(), m);
        }
    }

    public abstract boolean isLogEnabled();
    public abstract String getClassTag();
}
