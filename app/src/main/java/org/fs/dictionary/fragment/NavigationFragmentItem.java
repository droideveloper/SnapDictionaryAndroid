package org.fs.dictionary.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.fs.dictionary.R;
import org.fs.dictionary.core.CoreFragment;
import org.fs.ghanaian.util.StringUtility;

/**
 * Created by Fatih on 30/11/14.
 */
public class NavigationFragmentItem extends CoreFragment {

    private TextView textView;

    private String text;

    private View.OnClickListener mClickListener;

    public NavigationFragmentItem setText(String text) {
        this.text = text;
        return this;
    }

    public void setClickListener(View.OnClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_navigation_item, null);
        textView = (TextView)rootView.findViewById(R.id.textView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(isReady()) {
            if("Sign With Facebook".equalsIgnoreCase(text)) {
                RelativeLayout.LayoutParams mParams = (RelativeLayout.LayoutParams)textView.getLayoutParams();
                if(mParams != null) {
                    mParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
                    mParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                    mParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                    //textView.setBackgroundColor(Color.parseColor("#FF091679"));
                    textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.facebook_sign_background));
                    textView.setPadding(15, 10, 15, 10);
                    textView.setTextColor(Color.parseColor("#FFFFFF"));

                    //bold font of same font etc.
                    textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
                    textView.setText(text);

                    if(mClickListener != null) {
                        textView.setOnClickListener(mClickListener);
                    }
                }
            }
            else if(!StringUtility.isNullOrEmpty(text)) {
                textView.setText(text);
            }
        }
    }

    public TextView getTextView() {
        return textView;
    }
}
