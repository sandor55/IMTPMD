package com.imtpmd.sandor.imtpmdws;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.imtpmd.sandor.imtpmdws.Models.Course;
import com.imtpmd.sandor.imtpmdws.R;

import java.util.List;

/**
 * Created by sandor on 22-3-2016.
 */
public class VakListAdapter extends ArrayAdapter<Course> {

    public VakListAdapter(Context context, int resource, List<Course> objects){
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;

        if (convertView == null ) {
            vh = new ViewHolder();
            LayoutInflater li = LayoutInflater.from(getContext());
            convertView = li.inflate(R.layout.vak_list_item, parent, false);
            vh.name = (TextView) convertView.findViewById(R.id.vakcode);
           vh.ects = (TextView) convertView.findViewById(R.id.ects);
            vh.period = (TextView) convertView.findViewById(R.id.vakperiode);
            vh.grade = (TextView) convertView.findViewById(R.id.grade);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        Course cm = getItem(position);
        vh.name.setText(cm.name);
        vh.ects.setText(String.valueOf(cm.ects));
        vh.grade.setText(String.valueOf(cm.grade));
        vh.period.setText(String.valueOf(cm.period));
        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        TextView ects;
        TextView period;
        TextView grade;

    }
}

