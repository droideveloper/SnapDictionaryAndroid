package org.fs.dictionary.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import org.fs.dictionary.R;
import org.fs.dictionary.adapter.LookUpAdapter;
import org.fs.dictionary.model.CategoryObject;
import org.fs.dictionary.model.TranslateObject;
import org.fs.dictionary.model.TranslateResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Fatih on 02/12/14.
 */
public class LookUpFragment extends DialogFragment {

    private ListView         mListview;
    private ImageView        mImageView;
    private List<Parcelable> mDataSet;

    private OnLookUpItemClickedListener mLookupClickListener;

    int value;

    public LookUpFragment(TranslateResult mTranslateResult) {
        if(mTranslateResult == null) {
            throw new IllegalArgumentException("You can not set its null");
        }

        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CoreDialog);
        setCancelable(true);

        //this values been segmented into there so you can call these in background thread.
        Map<String, Integer> mValues = new HashMap<String, Integer>();
        List<TranslateObject> translateObjects = mTranslateResult.getResults();
        if(translateObjects != null) {
            int c = 0;
            for(TranslateObject t : translateObjects) {
                String key = t.getCategoryEn() + "," + t.getCategoryTr();
                if(!mValues.containsKey(key)) {
                    mValues.put(key, c);
                }
                c++;
            }
        }

        value = mTranslateResult.getIsTrEng();

        mDataSet = new ArrayList<Parcelable>(translateObjects);

        Set<Map.Entry<String, Integer>> set = mValues.entrySet();
        for(Map.Entry<String, Integer> e : set) {
            String[] data = e.getKey().split(",");
            CategoryObject c = new CategoryObject(data[0], data[1]);
            int i = e.getValue();
            mDataSet.add(i, c);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lookup, container, false);
        mListview = (ListView)rootView.findViewById(R.id.listView);
        mImageView = (ImageView)rootView.findViewById(R.id.closeView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(isReady()) {
            LookUpAdapter adapter = new LookUpAdapter(getActivity(), mDataSet);
            adapter.setDirection(value == 1 ? LookUpAdapter.Direction.TR_EN : LookUpAdapter.Direction.EN_TR);
            adapter.setListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(view != null) {
                        String[] data = ((String)view.getTag()).split(",");

                        String language = data[0];
                        String text = data[1];

                        if(mLookupClickListener != null) {
                            mLookupClickListener.onListen(text, language);
                        }
                    }
                }
            });
            mListview.setAdapter(adapter);
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LookUpFragment.this.dismissAllowingStateLoss();
                }
            });
        }
    }

    public void setListener(OnLookUpItemClickedListener listener) {
        this.mLookupClickListener = listener;
    }

    boolean isReady() {
        return getActivity() != null && isAdded();
    }

    public interface OnLookUpItemClickedListener {
        public void onListen(String text, String language);
    }
}
