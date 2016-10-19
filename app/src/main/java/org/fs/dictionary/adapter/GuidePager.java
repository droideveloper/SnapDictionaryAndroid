package org.fs.dictionary.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.fs.dictionary.core.CorePagerAdapter;
import org.fs.dictionary.fragment.GuideFragmentItem;

import java.util.List;

/**
 * Created by Fatih on 30/11/14.
 */
public class GuidePager extends CorePagerAdapter<Integer> {

    public GuidePager(List<Integer> mDataSet, FragmentManager mFragmentManager) {
        super(mDataSet, mFragmentManager);
    }

    @Override
    public Fragment getItem(int i) {
        GuideFragmentItem fragment = new GuideFragmentItem();
        return fragment.setKey(getObjectAt(i));
    }

    @Override
    public int getCount() {
        return mDataSet != null ? mDataSet.size() : 0;
    }
}
