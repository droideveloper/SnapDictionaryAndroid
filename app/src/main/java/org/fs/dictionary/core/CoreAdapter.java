package org.fs.dictionary.core;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by Fatih on 26/11/14.
 */
public abstract class CoreAdapter<T> extends ArrayAdapter<T> {

    public CoreAdapter(Context context, List<T> objects) {
        this(context, Integer.MIN_VALUE, objects);
    }

    private CoreAdapter(Context context, int resource, List<T> objects) {
        super(context, resource, objects);
    }

    protected LayoutInflater inflater() {
        return LayoutInflater.from(getContext());
    }

    protected T getObjectAt(int index) {
        return getItem(index);
    }
}
