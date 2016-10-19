package org.fs.dictionary.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.fs.dictionary.R;
import org.fs.dictionary.core.CoreFragment;

/**
 * Created by Fatih on 30/11/14.
 */
public class GuideFragmentItem extends CoreFragment {

    private int key;

    private ImageView imageView;

    public GuideFragmentItem setKey(int key) {
        this.key = key;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_guide_item, null);
        imageView = (ImageView)rootView.findViewById(R.id.imageView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(isReady()) {
            if(key != 0) {
                imageView.setImageResource(key);
            }
        }
    }
}
