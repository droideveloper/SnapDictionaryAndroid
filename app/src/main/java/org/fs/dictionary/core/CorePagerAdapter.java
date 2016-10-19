package org.fs.dictionary.core;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by Fatih on 30/11/14.
 */
public abstract class CorePagerAdapter<T> extends FragmentStatePagerAdapter {

    protected List<T> mDataSet = null;

    public CorePagerAdapter(List<T> mDataSet, FragmentManager mFragmentManager) {
        super(mFragmentManager);
        this.mDataSet = mDataSet;
        if(mDataSet == null) {
            throw new IllegalArgumentException("Collection can not be null in pager adapter.");
        }
    }

    public T getObjectAt(int index) {
        if(index < mDataSet.size()) {
            return mDataSet.get(index);
        }
        return null;
    }

    public void setObjectAt(int index, T mNewObject) {
        if(index < mDataSet.size()) {
            mDataSet.set(index, mNewObject);
            notifyDataSetChanged();
        }
    }

    public int size() {
        return mDataSet != null ? mDataSet.size() : 0;
    }
}
