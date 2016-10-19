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
package org.fs.ghanaian.xml;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Fatih on 17/10/14.
 */
public class SerializationHelper {

    private static List<Class> classList = Arrays.asList(new Class[] { String.class, Double.class, Long.class, Integer.class, Date.class, Boolean.class,
                                                                       double.class, long.class, int.class, boolean.class });

    /**
     * is it in collection or not. collection list is String, Double, Long, Integer, Date. else it return false always.
     * @param clazz
     * @return
     */
    public static boolean isTypeKnown(Class<?> clazz) {
        if(classList.contains(clazz)) {
           return true;
        }
        return false;
    }
}
