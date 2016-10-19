package org.fs.dictionary;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import org.fs.dictionary.fragment.ActionFragment;
import org.fs.dictionary.fragment.PreviewFragment;

/**
 * Created by Fatih on 23/11/14.
 */
public class MainActivity extends ActionBarActivity {

    private PreviewFragment mPreviewFragment;
    private ActionFragment mActionFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.previewFragment);
        if (f instanceof PreviewFragment) {
            mPreviewFragment = (PreviewFragment) f;
        }

        f = getSupportFragmentManager().findFragmentById(R.id.actionFragment);
        if (f instanceof ActionFragment) {
            mActionFragment = (ActionFragment) f;
        }

        //initialize the callback object for result
        if (mActionFragment != null && mPreviewFragment != null) {
            mPreviewFragment.setPreviewsCallback(mActionFragment);
        }
    }

    public boolean isLogEnabled() {
        return true;
    }

    public String getClassTag() {
        return MainActivity.class.getSimpleName();
    }

    public void onPreviewFragmentInitialized() {
        if(mActionFragment != null && mPreviewFragment != null) {
            mActionFragment.setActionsCallback(mPreviewFragment);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.push_right, R.anim.right_out);
    }
}
