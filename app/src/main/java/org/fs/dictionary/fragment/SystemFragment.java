package org.fs.dictionary.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.fs.dictionary.R;

/**
 * Created by Fatih on 02/12/14.
 */
public class SystemFragment extends DialogFragment {

    TextView okView;
    TextView cancelView;
    TextView contentView;
    TextView titleView;

    String titleText;
    String contentText;

    boolean cancelInvisible;

    public void setCancelInvisible(boolean v) {
        cancelInvisible = v;
    }

    boolean isReady() {
        return getActivity() != null && isAdded();
    }

    public SystemFragment() {
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CoreDialog);
        setCancelable(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_system_dialog, container, false);
        titleView = (TextView) rootView.findViewById(R.id.titleView);
        contentView = (TextView)rootView.findViewById(R.id.contentView);
        okView = (TextView) rootView.findViewById(R.id.okView);
        cancelView = (TextView)rootView.findViewById(R.id.cancelView);
        return rootView;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(isReady()) {
            if(cancelInvisible) {
                cancelView.setVisibility(View.GONE);
            }

            contentView.setText(contentText);
            titleView.setText(titleText);

            okView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SystemFragment.this.dismissAllowingStateLoss();
                }
            });

            cancelView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SystemFragment.this.dismissAllowingStateLoss();
                }
            });
        }
    }
}
