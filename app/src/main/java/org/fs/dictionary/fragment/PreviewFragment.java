package org.fs.dictionary.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;


import com.squareup.okhttp.Request;

import org.fs.dictionary.MainActivity;
import org.fs.dictionary.R;
import org.fs.dictionary.SnapDictionaryApplication;
import org.fs.dictionary.core.AmbientLightManager;
import org.fs.dictionary.core.CameraManager;
import org.fs.dictionary.core.CoreFragment;
import org.fs.dictionary.listener.ActionsCallback;
import org.fs.dictionary.listener.PreviewsCallback;
import org.fs.dictionary.listener.SurfaceCallback;
import org.fs.dictionary.model.LookupObject;
import org.fs.dictionary.model.TranslateResult;
import org.fs.dictionary.utils.LogHelper;
import org.fs.dictionary.utils.SpeechHelper;
import org.fs.dictionary.view.CameraPreviewView;
import org.fs.dictionary.view.SnapView;
import org.fs.ghanaian.json.JsonObjectCallback;
import org.fs.ghanaian.util.RequestUtility;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Fatih on 23/11/14.
 */
public class PreviewFragment extends CoreFragment implements ActionsCallback {

    public PreviewFragment() { }

    private CameraPreviewView        mCameraPreviewView;
    private LogHelper                mLogHelper;
    private CameraManager            mCameraManager;
    private AmbientLightManager      mAmbientLightManager;
    private int                      mDegree;

    private RelativeLayout           mRelativeLayout;
    private SnapView                 mSnapView;

    private View                     mCaptureHolder;

    private PreviewsCallback         mPreviewsCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_preview, null);
        mRelativeLayout = (RelativeLayout)rootView.findViewById(R.id.relativeLayout);
        mCameraPreviewView = (CameraPreviewView)rootView.findViewById(R.id.cameraPreviewView);
        mCaptureHolder = rootView.findViewById(R.id.captureHolder);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //if(isReady()) {
            mLogHelper = new LogHelper(this);

            mCameraPreviewView.setSurfaceCallback(mSurfaceCallback);
            mCaptureHolder.setOnClickListener(mClickListener);
            mCameraManager = new CameraManager(getActivity(), SnapDictionaryApplication.mPreferenceManager);

            mAmbientLightManager = new AmbientLightManager(getActivity());
            mAmbientLightManager.start(mCameraManager);

            mCameraPreviewView.initialize();
            //initialize resources
        //}
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            View animationView = view.findViewById(R.id.captureAnimationView);
            Animation scaleAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.radar_animation);
            animationView.setAnimation(scaleAnimation);
            animationView.startAnimation(scaleAnimation);

            if(mCameraManager != null) {
                final Camera mCamera = mCameraManager.getCamera();
                if (mCamera != null) {
                    mCamera.takePicture(null, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] bytes, Camera camera) {

                            if(mPreviewsCallback != null) {
                                mPreviewsCallback.onSwitchMenu(1);
                            }

                            mSnapView = new SnapView(PreviewFragment.this.getActivity(), 0, 0);
                            BitmapFactory.Options mOptions = new BitmapFactory.Options();
                            mOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
                            Bitmap mBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, mOptions);

                            if(mDegree != 0) {
                                Matrix mMatrix = new Matrix();
                                mMatrix.setRotate(mDegree);
                                mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), mMatrix, false);
                            }

                            RelativeLayout.LayoutParams mLayoutParams =
                                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                            mRelativeLayout.addView(mSnapView, mLayoutParams);
                            mSnapView.setImageBitmap(mBitmap);

                            Animation enterAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_bottom_in);
                            mSnapView.setAnimation(enterAnimation);
                            mSnapView.startAnimation(enterAnimation);
                        }
                    });
                }
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        //if(isReady()) {
            mLogHelper = null;
            mCaptureHolder.setOnClickListener(null);
            mCameraPreviewView.setSurfaceCallback(null);

            mAmbientLightManager.stop();
            mAmbientLightManager = null;

            mCameraManager.stopPreview();
            mCameraManager.closeDriver();
            mCameraManager = null;
            //clear resources
        //}
    }

    public void setPreviewsCallback(PreviewsCallback mPreviewsCallback) {
        this.mPreviewsCallback = mPreviewsCallback;
    }

    private SurfaceCallback mSurfaceCallback = new SurfaceCallback() {
        @Override
        public void onCreated(SurfaceHolder mSurfaceHolder) {
            try {
                if (mCameraManager != null) {
                    mCameraManager.openDriver(mSurfaceHolder);
                    mCameraManager.startPreview();

                    MainActivity mMainActivity = (MainActivity)PreviewFragment.this.getActivity();
                    if(mMainActivity != null) {
                        mMainActivity.onPreviewFragmentInitialized();
                    }

                    if(mLogHelper != null) {
                        mLogHelper.log(Log.INFO, "Camera initialized");
                    }
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        @Override
        public void onDestroyed(SurfaceHolder mSurfaceHolder) {
            if(mCameraManager != null) {
                mCameraManager.stopPreview();
            }
        }

        @Override
        public void onChanged(SurfaceHolder mSurfaceHolder, int mFormat, int mWidth, int mHeight) {
            if(mCameraManager != null) {
                mCameraManager.stopPreview();
            }
            Display mDisplay = ((WindowManager)getActivity().getSystemService(Activity.WINDOW_SERVICE)).getDefaultDisplay();
            Camera mCamera = mCameraManager.getCamera();
            if(mCamera != null && mDisplay != null) {
                if(mDisplay.getRotation() == Surface.ROTATION_0) {
                    mCamera.setDisplayOrientation(90);
                    mDegree = 90;
                } else if(mDisplay.getRotation() == Surface.ROTATION_90) {
                } else if(mDisplay.getRotation() == Surface.ROTATION_180) {
                } else if(mDisplay.getRotation() == Surface.ROTATION_270) {
                    mCamera.setDisplayOrientation(180);
                    mDegree = 180;
                }
                mCameraManager.startPreview();
            }
        }
    };

    public boolean isLogEnabled() {
        return true;
    }
    public String getClassTag() {
        return PreviewFragment.class.getSimpleName();
    }


    //Bridge with ActionFragment and PreviewFragment
    @Override
    public void onLight(int option) {
        if(option == -1) {
            //close
            if(mAmbientLightManager != null) {
                mAmbientLightManager.stop();
            }
            if(mCameraManager != null) {
                mCameraManager.setTorch(false);
            }

        } else if(option == 0) {
            //open
            if(mAmbientLightManager != null) {
                mAmbientLightManager.stop();
            }
            if(mCameraManager != null) {
                mCameraManager.setTorch(true);
            }

        } else if(option == 1) {
            //auto
            if(mAmbientLightManager != null && mCameraManager != null) {
                mAmbientLightManager.start(mCameraManager);
                if(mCameraManager != null) {
                    mCameraManager.setTorch(true);//might not need to set this because its might gone be already open or gone be closed depending on sensor value
                }
            }
        }
    }

    @Override
    public void onFocus() {
        if(mCameraManager != null) {
            mCameraManager.focus();
        }
    }

    @Override
    public void onZoom(int zoom) {
        if(mCameraManager != null) {
            Camera mCamera = mCameraManager.getCamera();
            Camera.Parameters mParameters = mCamera.getParameters();
            int max = mParameters.getMaxZoom();
            if(max > zoom) {
                mParameters.setZoom(zoom);
                mCamera.setParameters(mParameters);
            }
        }
    }

    @Override
    public void onBack() {
        if(mSnapView != null) {
            mSnapView.release();

            Animation exitAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_out_bttom);
            exitAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) { }
                @Override
                public void onAnimationEnd(Animation animation) {
                    mRelativeLayout.removeView(mSnapView);
                    mSnapView = null;
                }
                @Override
                public void onAnimationRepeat(Animation animation) {  }
            });

            mSnapView.setAnimation(exitAnimation);
            mSnapView.startAnimation(exitAnimation);


            if(mCameraManager != null) {
                mCameraManager.stopPreview();
                mCameraManager.startPreview();
            }

            if(mPreviewsCallback != null) {
                mPreviewsCallback.onSwitchMenu(0);
            }
        }
    }

    @Override
    public void onListen() {
        if(mSnapView != null) {
            mSnapView.listen();
        }
    }

    @Override
    public int maxZoom() {
        List<Integer> mZooms = getZooms();
        if(mZooms != null) {
            return mZooms.get(1);
        }
        return 0;
    }

    @Override
    public int minZoom() {
        List<Integer> mZooms = getZooms();
        if(mZooms != null) {
            return mZooms.get(0);
        }
        return 0;
    }

    @Override
    public void lookUp() {
        if(mSnapView != null) {
            LookupObject o = new LookupObject(mSnapView.text());
            Request request = RequestUtility.buildPost("http://ws.tureng.com/TurengSearchServiceV4.svc/Search", null, o.toJson(), "application/json; charset=utf-8");
            new JsonObjectCallback<TranslateResult>(TranslateResult.class) {
                @Override
                protected void complete(TranslateResult dataSet, int code) {
                    if(dataSet != null && dataSet.getResults() != null && dataSet.getResults().size() > 0) {
                        final LookUpFragment fragment = new LookUpFragment(dataSet);
                        fragment.setListener(new LookUpFragment.OnLookUpItemClickedListener() {
                            @Override
                            public void onListen(String text, String language) {
                                SpeechHelper speechHelper = new SpeechHelper(new Locale(language), getActivity());
                                speechHelper.speak(text);

                            }
                        });

                        mSnapView.post(new Runnable() {
                            @Override
                            public void run() {
                                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                                transaction.add(fragment, "look_up");
                                transaction.addToBackStack(null);
                                transaction.commitAllowingStateLoss();
                            }
                        });

                    } else {

                        final SystemFragment sys = new SystemFragment();
                        sys.setCancelInvisible(true);
                        sys.setTitleText("Oops :( Sorry!");
                        sys.setContentText("We can not recognize the text you re looking for.");

                        mSnapView.post(new Runnable() {
                            @Override
                            public void run() {
                                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                                transaction.add(sys, "error");
                                transaction.addToBackStack(null);
                                transaction.commitAllowingStateLoss();
                            }
                        });
                    }
                }
            }.map("MobileResult")
                .addRequest(request)
                .consume();
        }
    }

    private List<Integer> getZooms() {
        if(mCameraManager != null) {
            Camera mCamera = mCameraManager.getCamera();
            if(mCamera != null) {
                Camera.Parameters mParameters = mCamera.getParameters();
                if(mParameters != null) {
                    return Arrays.asList(mParameters.getZoom(), mParameters.getMaxZoom());
                }
            }
        }
        return null;
    }
}
