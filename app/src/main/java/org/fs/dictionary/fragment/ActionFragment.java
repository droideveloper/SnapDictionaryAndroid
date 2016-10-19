package org.fs.dictionary.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.fs.dictionary.R;
import org.fs.dictionary.SnapDictionaryApplication;
import org.fs.dictionary.adapter.ActionAdapter;
import org.fs.dictionary.core.CoreFragment;
import org.fs.dictionary.core.PreferenceManager;
import org.fs.dictionary.listener.ActionsCallback;
import org.fs.dictionary.listener.PreviewsCallback;
import org.fs.dictionary.model.MenuObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Fatih on 26/11/14.
 */
public class ActionFragment extends CoreFragment implements PreviewsCallback {

    private ListView        mListView;
    private RelativeLayout  mLayout;
    private ActionAdapter   mActionAdapter;

    private List<MenuObject> mMenuObjectList;

    private ActionsCallback  mActionsCallback;
    private View             mZoomControlView;

    private List<Integer> mFlashModeIconSet = new ArrayList<Integer>(Arrays.asList(R.drawable.ic_flash_off, R.drawable.ic_flash_on, R.drawable.ic_flash_auto));

    public ActionFragment() {
        switchModelPreview();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_actions, null);
        mLayout = (RelativeLayout)rootView.findViewById(R.id.viewHolder);
        mListView = (ListView)rootView.findViewById(R.id.listView);
        mZoomControlView = rootView.findViewById(R.id.zoomControlView);
        SeekBar mSeekBar = (SeekBar)mZoomControlView.findViewById(R.id.seekBarView);
        mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
        return rootView;
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int index, long id) {
            Animation scaleAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.radar_animation);
            View animationView = view.findViewById(R.id.animationView);
            animationView.setAnimation(scaleAnimation);
            animationView.startAnimation(scaleAnimation);

            if(index < mMenuObjectList.size()) {
                MenuObject mMenuObject = mMenuObjectList.get(index);
                //Language Handle
                if("Language".equalsIgnoreCase(mMenuObject.getActionName())) {
                    String newLanguageConfig = null;
                    switch (mMenuObject.getActionRes()) {
                        case R.drawable.tr: {
                            mMenuObject.setActionRes(R.drawable.uk);
                            newLanguageConfig = "eng";
                            break;
                        }
                        case R.drawable.uk: {
                            mMenuObject.setActionRes(R.drawable.tr);
                            newLanguageConfig = "tur";
                            break;
                        }
                    }
                    SnapDictionaryApplication.mPreferenceManager.setValueForKey(PreferenceManager.KEY_LANGUAGE_PREFERENCE, newLanguageConfig);
                    mMenuObjectList.set(index, mMenuObject);
                    mActionAdapter.notifyDataSetChanged();
                }
                //Flash
                else if("Flash".equalsIgnoreCase(mMenuObject.getActionName())) {
                    Integer mNextResourceId = mFlashModeIconSet.remove(0);
                    mMenuObject.setActionRes(mNextResourceId);
                    mFlashModeIconSet.add(mNextResourceId);
                    int option = (mNextResourceId == R.drawable.ic_flash_off ? -1 : (mNextResourceId == R.drawable.ic_flash_on ? 0 : (mNextResourceId == R.drawable.ic_flash_auto ? 1 : (Integer.MIN_VALUE))));
                    SnapDictionaryApplication.mPreferenceManager.setValueForKey(PreferenceManager.KEY_FLASH_MODE, option);
                    if(mActionsCallback != null) {
                        mActionsCallback.onLight(option);
                    }
                    mMenuObjectList.set(index, mMenuObject);
                    mActionAdapter.notifyDataSetChanged();
                }
                //Focus
                else if("Focus".equalsIgnoreCase(mMenuObject.getActionName())) {
                    if(mActionsCallback != null) {
                        mActionsCallback.onFocus();
                    }
                }
                //serach result
                else if("Look Up".equalsIgnoreCase(mMenuObject.getActionName())) {
                    if(mActionsCallback != null) {
                        mActionsCallback.lookUp();
                    }
                }

                //Zoom
                else if("Zoom".equalsIgnoreCase(mMenuObject.getActionName())) {
                    if(mZoomControlView.getVisibility() == View.GONE) {
                        Animation enterAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_left_in);
                        mZoomControlView.setVisibility(View.VISIBLE);
                        mZoomControlView.setAnimation(enterAnimation);
                        mZoomControlView.startAnimation(enterAnimation);
                    } else {
                        Animation exitAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_left_out);
                        exitAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) { }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                mZoomControlView.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) { }
                        });
                        mZoomControlView.setAnimation(exitAnimation);
                        mZoomControlView.startAnimation(exitAnimation);
                    }
                }
                //Listen
                else if("Listen".equalsIgnoreCase(mMenuObject.getActionName())) {
                    if(mActionsCallback != null) {
                        mActionsCallback.onListen();
                    }
                }
                //Back
                else if("Back".equalsIgnoreCase(mMenuObject.getActionName())) {
                    if(mActionsCallback != null) {
                        mActionsCallback.onBack();
                    }
                }
            }
        }
    };

    /***
     * initialized listView instance resize to fit its content with calculating height of itself.
     ***/
    void invalidateListView() {
        if(mActionAdapter != null) {
            int size = mActionAdapter.getCount();
            RelativeLayout.LayoutParams mListViewLayoutParameters = (RelativeLayout.LayoutParams)mListView.getLayoutParams();
            mListViewLayoutParameters.height = Math.round(size *
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 72.0f,
                            getResources().getDisplayMetrics()));
            mListViewLayoutParameters.addRule(RelativeLayout.CENTER_VERTICAL);
            mListView.requestLayout();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mListView.postDelayed(mUpdateModelThread, 1000L);
    }

    private Runnable mUpdateModelThread = new Runnable() {
        @Override
        public void run() {
            if(isReady()) {
                mActionAdapter = new ActionAdapter(getActivity(), mMenuObjectList);
                mListView.setAdapter(mActionAdapter);
                mListView.setOnItemClickListener(mItemClickListener);
                invalidateListView();
            }
        }
    };

    public void setActionsCallback(ActionsCallback mActionsCallback) {
        this.mActionsCallback = mActionsCallback;
        //we re sure now that all the application loaded else we would not get the callback result int supported stuff.
        if(isReady()) {
            SeekBar mSeekBar = (SeekBar)mZoomControlView.findViewById(R.id.seekBarView);
            if(mActionsCallback != null) {
                int min = mActionsCallback.minZoom();
                int max = mActionsCallback.maxZoom();

                mSeekBar.setProgress(min);
                mSeekBar.setMax(max);

                Integer current = SnapDictionaryApplication.mPreferenceManager.getValueForKey(PreferenceManager.KEY_ZOOM_LEVEL, 0);
                mActionsCallback.onZoom(current);

                TextView mTextView = (TextView) mZoomControlView.findViewById(R.id.textView);
                mTextView.setText(String.valueOf(current));

                Integer options = SnapDictionaryApplication.mPreferenceManager.getValueForKey(PreferenceManager.KEY_FLASH_MODE, 1);//auto flash
                mActionsCallback.onLight(options);
            }
        }
    }

    private void switchModelPreview() {
        mMenuObjectList = new ArrayList<MenuObject>();
        //first initialization
        String language = SnapDictionaryApplication.mPreferenceManager.getValueForKey(PreferenceManager.KEY_LANGUAGE_PREFERENCE, "tur");
        if("tur".equalsIgnoreCase(language)) {
            mMenuObjectList.add(new MenuObject("Language", R.drawable.tr));
        } else if("eng".equalsIgnoreCase(language)) {
            mMenuObjectList.add(new MenuObject("Language", R.drawable.uk));
        }

        Integer options = SnapDictionaryApplication.mPreferenceManager.getValueForKey(PreferenceManager.KEY_FLASH_MODE, 1);//auto flash
        if(options == 1) {
            mMenuObjectList.add(new MenuObject("Flash", R.drawable.ic_flash_auto));
        } else if(options == 0) {
            mMenuObjectList.add(new MenuObject("Flash", R.drawable.ic_flash_on));
            mFlashModeIconSet.add(mFlashModeIconSet.remove(0));
            mFlashModeIconSet.add(mFlashModeIconSet.remove(0));
        } else if(options == -1) {
            mMenuObjectList.add(new MenuObject("Flash", R.drawable.ic_flash_off));
            mFlashModeIconSet.add(mFlashModeIconSet.remove(0));
        }

        mMenuObjectList.add(new MenuObject("Focus", R.drawable.ic_focus));
        mMenuObjectList.add(new MenuObject("Zoom", R.drawable.ic_zoom));
    }

    private void switchModelDeital() {
        mMenuObjectList = new ArrayList<MenuObject>();
        //detail initialization
        mMenuObjectList.add(new MenuObject("Look Up", R.drawable.ic_search));
        mMenuObjectList.add(new MenuObject("Listen", R.drawable.ic_listen));
        mMenuObjectList.add(new MenuObject("Back", R.drawable.ic_refresh));
    }

    private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar mSeekBar, int mProgress, boolean mFromUser) {
            if(mFromUser) {
                TextView mTextView = (TextView)mZoomControlView.findViewById(R.id.textView);
                mTextView.setText(String.valueOf(mProgress));
                if(mActionsCallback != null) {
                    mActionsCallback.onZoom(mProgress);
                }
            }
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {  }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {  }
    };

    @Override
    public void onSwitchMenu(int type) {
        if(type == 1) {
            switchModelDeital();
        } else if(type == 0) {
            switchModelPreview();
        }
        mListView.postDelayed(mUpdateModelThread, 500L);
    }
}
