package org.fs.dictionary.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.fs.dictionary.R;
import org.fs.dictionary.core.CoreAdapter;
import org.fs.dictionary.model.CategoryObject;
import org.fs.dictionary.model.TranslateObject;

import java.util.List;

/**
 * Created by Fatih on 02/12/14.
 */
public class LookUpAdapter extends CoreAdapter<Parcelable> {

    private View.OnClickListener listener;
    private Direction direction;

    public LookUpAdapter(Context context, List<Parcelable> objects) {
        super(context, objects);
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Parcelable p = getObjectAt(position);
        if(p instanceof CategoryObject) {
            CategoryObject c = (CategoryObject)p;
            return getSection(c, convertView, parent);
        } else {
            TranslateObject t = (TranslateObject)p;
            return getCell(t, convertView, parent);
        }
    }

    private View getSection(CategoryObject c, View view, ViewGroup parent) {

        if(view == null || !(view.getTag() instanceof ViewSectionHolder)) {
            view = inflater().inflate(R.layout.view_section, parent, false);

            ViewSectionHolder holder = new ViewSectionHolder();
            holder.sectionTitle = (TextView)view.findViewById(R.id.sectionTitle);

            view.setTag(holder);
        }

        ViewSectionHolder holder = (ViewSectionHolder)view.getTag();
        if(direction.equals(Direction.EN_TR)) {
            holder.sectionTitle.setText(c.getCategoryNameEN());
        } else {
            holder.sectionTitle.setText(c.getCategoryNameTR());
        }
        return view;
    }


    private View getCell(TranslateObject t, View view, ViewGroup parent) {

        if(view == null || !(view.getTag() instanceof ViewCellHolder)) {
            view = inflater().inflate(R.layout.view_translate, parent, false);

            ViewCellHolder holder = new ViewCellHolder();
            holder.translateTitle = (TextView)view.findViewById(R.id.translateTitle);
            holder.listenView = (ImageView)view.findViewById(R.id.listenView);

            view.setTag(holder);
        }

        ViewCellHolder holder = (ViewCellHolder)view.getTag();
        if(direction.equals(Direction.EN_TR)) {
            holder.translateTitle.setText(String.format("%s %s", t.getTypeEn(), t.getTerm()));
            holder.listenView.setTag("en-US," + t.getTerm());
        } else {
            holder.listenView.setTag("tr-TR," + t.getTerm());
            holder.translateTitle.setText(String.format("%s %s", t.getTypeTr(), t.getTerm()));
        }
        holder.listenView.setOnClickListener(listener);
        return view;
    }

    public enum Direction {
        EN_TR,
        TR_EN
    }

    private class ViewSectionHolder {
        TextView sectionTitle;
    }

    private class ViewCellHolder {
        TextView translateTitle;
        ImageView listenView;
    }

}
