package org.fs.dictionary.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.client.json.Json;

import org.fs.ghanaian.core.CoreObject;
import org.fs.ghanaian.util.JsonUtility;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fatih on 25/11/14.
 */
public class TranslateResult extends CoreObject implements Parcelable {

    private final static String KEY_IS_FOUND    = "IsFound";
    private final static String KEY_TR_TO_ENG   = "IsTRToEN";
    private final static String KEY_TERM        = "Term";
    private final static String KEY_RESULTS     = "Results";
    private final static String KEY_SUGGESTIONS = "Suggestions";

    private int mIsFound;
    private int isTrEng;
    private String mTerm;
    private List<TranslateObject> mResults;
    private List<String> mSuggestions;

    public TranslateResult() { }

    public TranslateResult(Parcel input) {
        setIsFound(input.readInt());
        setTerm(input.readString());
        setIsTrEng(input.readInt());
        setResults(input.readArrayList(TranslateObject.class.getClassLoader()));
        setSuggestions(input.readArrayList(String.class.getClassLoader()));
    }

    public static TranslateResult fromJsonObject(JSONObject json) {
        TranslateResult mTranslateResult = null;
        if(json != null) {
            mTranslateResult = new TranslateResult();
            mTranslateResult.setIsFound(JsonUtility.getJsonInteger(json, KEY_IS_FOUND, 0));
            mTranslateResult.setIsTrEng(JsonUtility.getJsonInteger(json, KEY_TR_TO_ENG, 0));
            mTranslateResult.setTerm(JsonUtility.getJsonString(json, KEY_TERM, null));
            JSONArray array = JsonUtility.getJsonArray(json, KEY_RESULTS, null);
            mTranslateResult.setResults(TranslateObject.fromJsonArray(array));
            JSONArray stringArray = JsonUtility.getJsonArray(json, KEY_SUGGESTIONS, null);
            mTranslateResult.setSuggestions(parse(stringArray));
        }
        return mTranslateResult;
    }

    public static List<TranslateResult> fromJsonArray(JSONArray array) {
        List<TranslateResult> mTranslateResults = null;
        if(array != null) {
            int size = array.length();
            mTranslateResults = new ArrayList<TranslateResult>(size);
            for(int i = 0; i < size; i++) {
                JSONObject json = JsonUtility.getArrayObject(array, i , null);
                TranslateResult mTranslateResult = fromJsonObject(json);
                if(mTranslateResult != null) {
                    mTranslateResults.add(mTranslateResult);
                }
            }
        }
        return mTranslateResults;
    }

    private static List<String> parse(JSONArray array) {
        List<String> values = null;
        if(array != null) {
            int size = array.length();
            values = new ArrayList<String>(size);
            for(int i = 0; i < size; i++) {
                String value = JsonUtility.getArrayString(array, i, null);
                if(value != null) {
                    values.add(value);
                }
            }
        }
        return values;
    }

    public int getIsFound() {
        return mIsFound;
    }

    public void setIsFound(int mIsFound) {
        this.mIsFound = mIsFound;
    }

    public String getTerm() {
        return mTerm;
    }

    public void setTerm(String mTerm) {
        this.mTerm = mTerm;
    }

    public List<TranslateObject> getResults() {
        return mResults;
    }

    public void setResults(List<TranslateObject> mResults) {
        this.mResults = mResults;
    }

    public List<String> getSuggestions() {
        return mSuggestions;
    }

    public void setSuggestions(List<String> mSuggestions) {
        this.mSuggestions = mSuggestions;
    }

    public void setIsTrEng(int isTrEng) {
        this.isTrEng = isTrEng;
    }

    public int getIsTrEng() {
        return isTrEng;
    }

    @Override
    public boolean isLogEnabled() {
        return false;
    }

    @Override
    public String getClassTag() {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flag) {
        parcel.writeInt(getIsFound());
        parcel.writeString(getTerm());
        parcel.writeInt(getIsTrEng());
        if(getResults() != null) {
            parcel.writeList(getResults());
        }
        if(getSuggestions() != null) {
            parcel.writeList(getSuggestions());
        }
    }

    public final static Creator<TranslateResult> CREATOR = new Creator<TranslateResult>() {
        @Override
        public TranslateResult createFromParcel(Parcel parcel) {
            return new TranslateResult(parcel);
        }

        @Override
        public TranslateResult[] newArray(int size) {
            return new TranslateResult[size];
        }
    };
}
