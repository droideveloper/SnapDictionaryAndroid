package org.fs.android.dictionary.backend.util;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

/**
 * Created by Fatih on 29/12/14.
 * as org.fs.android.dictionary.backend.util.ObjectifyHelper
 */
public class ObjectifyHelper {
    static {
        //entity registration goes here
    }

    public static Objectify objectify() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
