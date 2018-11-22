package com.example.johnnyma.testbench;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Adapter used to set up the list view in CourseSelectActivity.
 */
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

        // transform the keyset of the hashmap into a list of strings,
        // allowing for access to each individual course header
        Set<String> courseSet = Courses.keySet();
        List<String> courseList = new ArrayList<String>(courseSet);

        // for list view element i, set the course header to be the ith string
        // in the keyset
        final String s_course_header = courseList.get(i);
        course_header.setText(s_course_header);
        // retrieve the list of course codes pertaining to the course header
        List<String> course_codes = Courses.get(s_course_header);

        // create a button for each course code of each course header
        for(int index = 0; index < course_codes.size(); index++) {
            Button btn = new Button(c);

            btn.setText(course_codes.get(index));
            btn.setTextSize(btn.getTextSize() * 0.225f);
            btn.setTypeface(null, Typeface.BOLD);
            //btn.setPadding(0, 10, 0, 10);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.setMargins(0, 30, 0, 40);
            DisplayMetrics dm = c.getResources().getDisplayMetrics();
            params.width = dm.widthPixels / 5;

            if(index % 3 == 0)
                params.setGravity(Gravity.CENTER);

            else if (index % 3 == 2){
                params.setGravity(Gravity.CENTER_VERTICAL);
            }
            btn.setLayoutParams(params);

            btn.setMaxHeight(0);
            //btn.setBackgroundTint(R.color.colorAccent);
            btn.setBackgroundTintList(c.getResources().getColorStateList(R.color.colorAccent, null));
                    //getColorStateList(R.color.colorAccent));

            course_grid.addView(btn, index);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // opens a dialog associated with the text of the button
                    openDialog(s_course_header + ((Button) view).getText().toString());
                }
            });
        }

        ImageView accent = v.findViewById(R.id.accent);
        accent.getLayoutParams().height = course_grid.getHeight();

        return v;
    }

    /**
     * Opens a dialog that allows users to take action with the course
     * associated with the button used to open the dialog
     * @param s_course - course name of the dialog's course
     */
    public void openDialog(String s_course){
        SelectedCourseDialog selectedCourseDialog = new SelectedCourseDialog();
        Bundle args = new Bundle();
        args.putString("course", s_course);
        selectedCourseDialog.setArguments(args);
        selectedCourseDialog.show(fm, "selected course dialog");
    }
}
