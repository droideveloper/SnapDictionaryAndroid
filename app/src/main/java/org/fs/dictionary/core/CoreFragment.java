package org.fs.dictionary.core;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Created by Fatih on 23/11/14.
 */
public class CoreFragment extends Fragment {

    /**
     * Helper method for using Fragmentation.
     * @return
     */
    protected boolean isReady() {
        Activity activity = getActivity();
        return isAdded() && activity != null && !activity.isFinishing();
    }

    /**
     * defaults
     * @return
     */
    public boolean isLogEnabled() {
        return false;
    }

    /**
     * defaults
     * @return
     */
    public String getClassTag() {
        return null;
    }
}
