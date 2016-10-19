package org.fs.dictionary.model;

import android.os.Parcel;
import android.os.Parcelable;


import org.fs.ghanaian.core.CoreObject;

/**
 * Created by Fatih on 02/12/14.
 */
public class CategoryObject extends CoreObject implements Parcelable{

    private String categoryNameEN;
    private String categoryNameTR;

    public CategoryObject(Parcel input) {
        setCategoryNameEN(input.readString());
        setCategoryNameTR(input.readString());
    }

    public CategoryObject(String categoryNameEN, String categoryNameTR) {
        setCategoryNameEN(categoryNameEN);
        setCategoryNameTR(categoryNameTR);
    }

    public void setCategoryNameEN(String categoryNameEN) {
        this.categoryNameEN = categoryNameEN;
    }

    public String getCategoryNameEN() {
        return categoryNameEN;
    }

    public void setCategoryNameTR(String categoryNameTR) {
        this.categoryNameTR = categoryNameTR;
    }

    public String getCategoryNameTR() {
        return categoryNameTR;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof CategoryObject) {
            CategoryObject c = (CategoryObject)o;
            return this.getCategoryNameEN().equalsIgnoreCase(c.getCategoryNameEN()) || this.getCategoryNameTR().equalsIgnoreCase(c.getCategoryNameTR());
        }
        return super.equals(o);
    }

    @Override
    public boolean isLogEnabled() {
        return true;
    }

    @Override
    public String getClassTag() {
        return CategoryObject.class.getSimpleName();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(getCategoryNameEN());
        parcel.writeString(getCategoryNameTR());
    }

    public final static Creator<CategoryObject> CREATOR = new Creator<CategoryObject>() {
        @Override
        public CategoryObject createFromParcel(Parcel parcel) {
            return new CategoryObject(parcel);
        }

        @Override
        public CategoryObject[] newArray(int i) {
            return new CategoryObject[i];
        }
    };
}
