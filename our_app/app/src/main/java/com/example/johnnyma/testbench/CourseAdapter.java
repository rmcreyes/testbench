package com.example.johnnyma.testbench;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CourseAdapter extends BaseAdapter {

    Context c;
    LayoutInflater mInflater;
    Map<String, List<String>> Courses;
    FragmentManager fm;

    public CourseAdapter(Context c, Map<String, List<String>> courses, FragmentManager fm) {
        this.c = c;
        Courses = courses;
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.fm = fm;
    }

    @Override
    public int getCount() {
        return Courses.keySet().size();
    }

    @Override
    public Object getItem(int i) {
        return Courses.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = mInflater.inflate(R.layout.detail_courselist, null);

        TextView course_header = (TextView) v.findViewById(R.id.course_header);

        GridLayout course_grid = (GridLayout) v.findViewById(R.id.course_grid);

        Set<String> courseSet = Courses.keySet();
        List<String> courseList = new ArrayList<String>(courseSet);
        String s_course_header = courseList.get(i);
        course_header.setText(s_course_header);

        List<String> course_codes = Courses.get(courseList.get(i));

        for(int index = 0; index < course_codes.size(); index++) {
            Button btn = new Button(c);
//            btn.setMaxHeight(150);
//            btn.setMaxWidth(300);
            btn.setMinWidth(0);
            btn.setText(s_course_header + " " + course_codes.get(index));
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.setMargins(20, 30, 20, 40);
            params.height = 150;
            params.width = 300;
            if(index % 3 == 0)
                params.setGravity(Gravity.START);
            else if(index % 3 == 1)
                params.setGravity(Gravity.CENTER);
            else
                params.setGravity(Gravity.CENTER_VERTICAL);
            btn.setLayoutParams(params);
            btn.setBackgroundResource(R.drawable.capsule_btn);
            course_grid.addView(btn, index);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDialog(((Button) view).getText().toString());
                }
            });
        }

        return v;
    }

    public void openDialog(String s_course){
        SelectedCourseDialog selectedCourseDialog = new SelectedCourseDialog();
        Bundle args = new Bundle();
        args.putString("course", s_course);
        selectedCourseDialog.setArguments(args);
        selectedCourseDialog.show(fm, "selected course dialog");
    }
}
