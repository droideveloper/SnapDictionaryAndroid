package org.fs.dictionary.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import org.fs.dictionary.core.CorePagerAdapter;
import org.fs.dictionary.fragment.NavigationFragmentItem;

import java.util.List;

/**
 * Created by Fatih on 30/11/14.
 */
public class NavigationPager extends CorePagerAdapter<String> {

    public View.OnClickListener mClickListener;

    public NavigationPager(List<String> mDataSet, FragmentManager mFragmentManager) {
        super(mDataSet, mFragmentManager);
    }

    public void setClickListener(View.OnClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    @Override
    public Fragment getItem(int i) {
        NavigationFragmentItem fragment = new NavigationFragmentItem();
        if(i == getCount() - 1) {
            fragment.setClickListener(mClickListener);
        }
        return fragment.setText(getObjectAt(i));
    }

    @Override
    public int getCount() {
        return mDataSet != null ? mDataSet.size() : 0;
    }
}
