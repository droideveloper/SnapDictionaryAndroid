package org.fs.dictionary;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

import org.fs.dictionary.adapter.GuidePager;
import org.fs.dictionary.adapter.NavigationPager;
import org.fs.dictionary.fragment.SystemFragment;
import org.fs.dictionary.library.CirclePageIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by Fatih on 30/11/14.
 */
public class StartUpActivity extends ActionBarActivity {


    private ViewPager mGuidePager           = null;
    private ViewPager mNavigationPager      = null;
    private CirclePageIndicator mIndicator  = null;

    private Session.StatusCallback mSessionStatusCallback = null;

    private final static List<String> permissions_read = Arrays.asList("user_about_me",
                                                                       "user_interests",
                                                                       "user_location",
                                                                       "user_photos",
                                                                       "user_birthday");
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWithActionbar(savedInstanceState);
        setContentView(R.layout.activity_startup);
        mGuidePager = (ViewPager)findViewById(R.id.mainGuidePager);
        mNavigationPager = (ViewPager)findViewById(R.id.navigationGuidePager);
        mIndicator = (CirclePageIndicator)findViewById(R.id.circlePagerIndicator);
        initWithFacebookSession(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session session = Session.getActiveSession();
        if(session != null) {
            session.onActivityResult(this, requestCode, resultCode, data);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session.saveSession(Session.getActiveSession(), outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        new RegisterDeviceTask().context(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
//        findViewById(android.R.id.content).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                new RegisterUserTask().context(StartUpActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
//            }
//        }, 5000L);


        if(!isFinishing()) {
            List<Integer> mResourcesArray = new ArrayList<Integer>();
            TypedArray array = getResources().obtainTypedArray(R.array.navaigation_items);
            if(array != null) {
                for(int i = 0; i < array.length(); i++) {
                    Integer id = array.getResourceId(i, -1);
                    mResourcesArray.add(id);
                }
            }

            array.recycle();

            List<String> mTextArray = Arrays.asList(getResources().getStringArray(R.array.navigation_list));

            GuidePager mGuideAdapter = new GuidePager(mResourcesArray, getSupportFragmentManager());
            final NavigationPager mNavigationAdapter = new NavigationPager(mTextArray, getSupportFragmentManager());

            mGuidePager.setAdapter(mGuideAdapter);
            mNavigationPager.setAdapter(mNavigationAdapter);
            mNavigationAdapter.setClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //start sign
                    startFacebookLogin();
                }
            });

            mGuidePager.setOffscreenPageLimit(mResourcesArray.size() - 1);
            mNavigationPager.setOffscreenPageLimit(mTextArray.size() - 1);

            mIndicator.setViewPager(mGuidePager);
            mNavigationPager.setOnPageChangeListener(mNavigationListener);
            mGuidePager.setOnPageChangeListener(mGuideListener);
        }
    }

    private ViewPager.OnPageChangeListener mGuideListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i2) {
            mNavigationPager.scrollTo(mGuidePager.getScrollX(), mGuidePager.getScrollY());
        }
        @Override
        public void onPageSelected(int i) {  }
        @Override
        public void onPageScrollStateChanged(int i) { }
    };

    private ViewPager.OnPageChangeListener mNavigationListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i2) {
            mGuidePager.scrollTo(mNavigationPager.getScrollX(), mNavigationPager.getScrollY());
        }
        @Override
        public void onPageSelected(int i) {  }
        @Override
        public void onPageScrollStateChanged(int i) {  }
    } ;

    private void initWithActionbar(Bundle savedInstanceState) {
        ActionBar mActionBar = getSupportActionBar();
        if(mActionBar != null) {
            mActionBar.setDefaultDisplayHomeAsUpEnabled(false);
            mActionBar.setDisplayHomeAsUpEnabled(false);
            mActionBar.setTitle(null);
        }
    }

    private void initWithFacebookSession(Bundle savedInstanceState) {
        mSessionStatusCallback = new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                if(isActive()) {
                    if(session != null && session.isOpened()) {
                        Request.newMeRequest(session, new Request.GraphUserCallback() {
                            @Override
                            public void onCompleted(GraphUser mUser, Response mResponse) {
                                if(mUser != null) {
                                    //we get the user object here
                                    //collect data we requested from user.
                                    Set<String> keys = mUser.asMap().keySet();
                                    for(String key : keys) {
                                        Object value = mUser.asMap().get(key);
                                        if(value != null) {
                                            Log.println(Log.ERROR, StartUpActivity.class.getSimpleName(), key + " : " + String.valueOf(value));
                                        }
                                    }

                                }
                            }
                        }).executeAsync();
                    }

                    else if(SessionState.CLOSED_LOGIN_FAILED.equals(state)) {
                        final SystemFragment sys = new SystemFragment();
                        sys.setCancelInvisible(true);
                        sys.setTitleText("Sign With Facebook Failed");
                        sys.setContentText("You ve failed to sign with facebook, try later will ya?");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
                                trans.add(sys, "failed_error");
                                trans.addToBackStack(null);
                                trans.commitAllowingStateLoss();
                            }
                        });
                    }
                }
            }
        };

        Session session = Session.getActiveSession();
        if(session == null) {
            if(savedInstanceState != null) {
                session = Session.restoreSession(this, null, null, savedInstanceState);
            }
            if(session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
        }
    }

    private void startFacebookLogin() {
//        Session session = Session.getActiveSession();
//        if(!session.isOpened() && !session.isClosed()) {
//            session.openForRead(new Session.OpenRequest(StartUpActivity.this)
//                    .setPermissions(permissions_read)
//                    .setCallback(mSessionStatusCallback));
//        } else {
//            Session.openActiveSession(StartUpActivity.this, true, mSessionStatusCallback);
//        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.push_left);
        finish();
    }

    protected boolean isActive() {
        return this != null && !isFinishing();
    }
}
