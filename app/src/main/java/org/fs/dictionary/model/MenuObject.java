package org.fs.dictionary.model;

/**
 * Created by Fatih on 26/11/14.
 */
public class MenuObject {
    
    private String mActionName;
    private int    mActionRes;
    
    public MenuObject(String mActionName, int mActionRes) {
        setActionName(mActionName);
        setActionRes(mActionRes);
    }

    public MenuObject() { }

    public String getActionName() {
        return mActionName;
    }

    public void setActionName(String mActionName) {
        this.mActionName = mActionName;
    }

    public int getActionRes() {
        return mActionRes;
    }

    public void setActionRes(int mActionRes) {
        this.mActionRes = mActionRes;
    }
}
