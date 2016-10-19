package org.fs.dictionary.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.fs.ghanaian.core.CoreObject;
import org.fs.ghanaian.util.JsonUtility;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fatih on 25/11/14.
 */
public class TranslateObject extends CoreObject implements Parcelable {
    
    private final static String KEY_CATEGORY_EN = "CategoryEN";
    private final static String KEY_CATEGORY_TR = "CategoryTR";
    private final static String KEY_TERM        = "Term";
    private final static String KEY_TYPE_EN     = "TypeEN";
    private final static String KEY_TYPE_TR     = "TypeTR";
    
    private String mCategoryEn;
    private String mCategoryTr;
    private String mTerm;
    private String mTypeEn;
    private String mTypeTr;
    
    public TranslateObject(Parcel input) {
        setCategoryEn(input.readString());
        setCategoryTr(input.readString());
        setTerm(input.readString());
        setTypeEn(input.readString());
        setTypeTr(input.readString());
    }
    
    public TranslateObject() { }
    
    public static TranslateObject fromJsonObject(JSONObject json) {
        TranslateObject mTranslateObject = null;
        if(json != null) {
            mTranslateObject = new TranslateObject();
            mTranslateObject.setCategoryEn(JsonUtility.getJsonString(json, KEY_CATEGORY_EN, null));
            mTranslateObject.setCategoryTr(JsonUtility.getJsonString(json, KEY_CATEGORY_TR, null));
            mTranslateObject.setTerm(JsonUtility.getJsonString(json, KEY_TERM, null));
            mTranslateObject.setTypeEn(JsonUtility.getJsonString(json, KEY_TYPE_EN, null));
            mTranslateObject.setTypeTr(JsonUtility.getJsonString(json, KEY_TYPE_TR, null));
        }
        return mTranslateObject;
    }
    
    public static List<TranslateObject> fromJsonArray(JSONArray array) {
        List<TranslateObject> mTranslateObjects = null;
        if(array != null) {
            int size = array.length();
            mTranslateObjects = new ArrayList<TranslateObject>(size);
            for(int i = 0; i < size; i++) {
                JSONObject json = JsonUtility.getArrayObject(array, i, null);
                TranslateObject mTranslateObject = fromJsonObject(json);
                if(mTranslateObject != null) {
                    mTranslateObjects.add(mTranslateObject);
                }
            }
        }
        return mTranslateObjects;
    }

    public String getTypeTr() {
        return mTypeTr;
    }

    public void setTypeTr(String mTypeTr) {
        this.mTypeTr = mTypeTr;
    }

    public String getCategoryEn() {
        return mCategoryEn;
    }

    public void setCategoryEn(String mCategoryEn) {
        this.mCategoryEn = mCategoryEn;
    }

    public String getCategoryTr() {
        return mCategoryTr;
    }

    public void setCategoryTr(String mCategoryTr) {
        this.mCategoryTr = mCategoryTr;
    }

    public String getTerm() {
        return mTerm;
    }

    public void setTerm(String mTerm) {
        this.mTerm = mTerm;
    }

    public String getTypeEn() {
        return mTypeEn;
    }

    public void setTypeEn(String mTypeEn) {
        this.mTypeEn = mTypeEn;
    }

    @Override
    public boolean isLogEnabled() {
        return true;
    }

    @Override
    public String getClassTag() {
        return TranslateObject.class.getSimpleName();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flag) {
        parcel.writeString(getCategoryEn());
        parcel.writeString(getCategoryTr());
        parcel.writeString(getTerm());
        parcel.writeString(getTypeEn());
        parcel.writeString(getTypeTr());
    }
    
    public final static Creator<TranslateObject> CREATOR = new Creator<TranslateObject>() {
        @Override
        public TranslateObject createFromParcel(Parcel parcel) {
            return new TranslateObject(parcel);
        }

        @Override
        public TranslateObject[] newArray(int size) {
            return new TranslateObject[size];
        }
    };
}
