package org.fs.dictionary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import org.fs.dictionary.R;
import org.fs.dictionary.core.CoreAdapter;
import org.fs.dictionary.model.MenuObject;

import java.util.List;

/**
 * Created by Fatih on 26/11/14.
 */
public class ActionAdapter extends CoreAdapter<MenuObject> {

    public ActionAdapter(Context context, List<MenuObject> objects) {
        super(context, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = inflater();
            convertView = inflater.inflate(R.layout.view_action_cell, parent, false);

            ViewHolder holder = new ViewHolder();
            holder.imageView = (ImageView)convertView.findViewById(R.id.imageView);
            holder.animationView = convertView.findViewById(R.id.animationView);
            holder.isCreated = true;
            convertView.setTag(holder);
        }

        ViewHolder holder = (ViewHolder)convertView.getTag();
        MenuObject obj = getObjectAt(position);
        int res = obj.getActionRes();
        if(res != 0) {
            holder.imageView.setImageResource(obj.getActionRes());
        }

        if(holder.isCreated) {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.translate_right_to_left);
            convertView.setAnimation(animation);
            convertView.startAnimation(animation);
            holder.isCreated = false;
        }
        return convertView;
    }

    public class ViewHolder {
        ImageView imageView;
        View      animationView;
        boolean   isCreated;
    }
}
