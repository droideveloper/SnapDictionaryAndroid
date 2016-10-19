package org.fs.dictionary.utils;

import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Fatih on 23/11/14.
 */
public class LogHelper {

    /**
     * Constructor
     * @param mInstance
     */
    public LogHelper(Object mInstance) {
        this.mInstance = mInstance;
    }

    private Object mInstance;

    /**
     * object instance setter
     * @param mInstnace
     */
    public void setInstance(Object mInstnace) {
        this.mInstance = mInstnace;
    }

    /**
     * default level of logget method
     * @param message
     */
    public void log(String message) {
       log(Log.DEBUG, message);
    }

    /**
     * Last level of logger method
     * @param level
     * @param message
     */
    public void log(int level, String message) {
        if(isLogEnabled()) {
            Log.println(level, getClassTag(), message);
        }
    }

    /**
     * gets the method of object instance
     * @return
     */
    private boolean isLogEnabled() {
        boolean isEnabled = false;
        if(mInstance == null) { throw  new NullPointerException("you need to pass object instance in the constructor or setter.");  }
        Class<?> clazz = mInstance.getClass();
        try {
            Method method = clazz.getMethod("isLogEnabled", null);
            Object result = method.invoke(mInstance, null);
            isEnabled = (Boolean)result;
            return isEnabled;
        } catch (NoSuchMethodException nme) {
            nme.printStackTrace();
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
        } catch (InvocationTargetException ite) {
            ite.printStackTrace();
        }
        return isEnabled;
    }

    /**
     * gets the method of object instance
     * @return
     */
    private String getClassTag() {
        String classTag = null;
        if(mInstance == null) { throw  new NullPointerException("you need to pass object instance in the constructor or setter.");  }
        Class<?> clazz = mInstance.getClass();
        try {
            Method method = clazz.getMethod("getClassTag", null);
            Object result = method.invoke(mInstance, null);
            classTag = (String)result;
            return classTag;
        } catch (NoSuchMethodException nme) {
            nme.printStackTrace();
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
        } catch (InvocationTargetException ite) {
            ite.printStackTrace();
        }
        return classTag;
    }
}
