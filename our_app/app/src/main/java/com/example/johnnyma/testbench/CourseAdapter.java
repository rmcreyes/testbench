package com.example.johnnyma.testbench;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Adapter used to set up the list view in CourseSelectActivity.
 */
public class CourseAdapter extends BaseAdapter {

    Context c;
    LayoutInflater mInflater;
    Map<String, List<String>> Courses;
    FragmentManager fm;

    private ProgressDialog progressDialog;

    private String json_stat_http;
    private String json_ranking_http;
    private boolean is_prof_of;

    private CourseSelectLock courseSelectLock;

    public CourseAdapter(Context c, Map<String, List<String>> courses, FragmentManager fm) {
        this.c = c;
        Courses = courses;
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.fm = fm;
        this.courseSelectLock = courseSelectLock;
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

        if(course_codes.size() == 2) {
            Button btn1 = new Button(c);
            btn1.setText(course_codes.get(0));
            btn1.setTextSize(btn1.getTextSize() * 0.225f);
            btn1.setTypeface(null, Typeface.BOLD);

            GridLayout.LayoutParams params1 = new GridLayout.LayoutParams();
            params1.setMargins(0, 30, 0, 40);
            DisplayMetrics dm1 = c.getResources().getDisplayMetrics();
            params1.width = dm1.widthPixels / 5;

            params1.setGravity(Gravity.START);

            btn1.setLayoutParams(params1);

            btn1.setMaxHeight(0);

            btn1.setBackgroundTintList(c.getResources().getColorStateList(R.color.colorAccent, null));


            course_grid.addView(btn1, 0);

            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // opens a dialog associated with the text of the button
                    synchronized (CourseSelectLock.lock) {
                        if (!CourseSelectLock.pressed) {
                            CourseSelectLock.pressed = true;
                            openDialog(s_course_header + ((Button) view).getText().toString());
                        }
                    }
                }
            });

            Button btn2 = new Button(c);
            btn2.setText(course_codes.get(0));
            btn2.setTextSize(btn2.getTextSize() * 0.225f);
            btn2.setTypeface(null, Typeface.BOLD);

            GridLayout.LayoutParams params2 = new GridLayout.LayoutParams();
            params2.setMargins(0, 30, 0, 40);
            DisplayMetrics dm2 = c.getResources().getDisplayMetrics();
            params2.width = dm2.widthPixels / 5;

            params2.setGravity(Gravity.CENTER);

            btn2.setLayoutParams(params2);

            btn2.setMaxHeight(0);

            btn2.setBackgroundTintList(c.getResources().getColorStateList(R.color.colorAccent, null));


            course_grid.addView(btn2, 1);

            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // opens a dialog associated with the text of the button
                    synchronized (CourseSelectLock.lock) {
                        if (!CourseSelectLock.pressed) {
                            CourseSelectLock.pressed = true;
                            openDialog(s_course_header + ((Button) view).getText().toString());
                        }
                    }
                }
            });

            Button btn3 = new Button(c);
            GridLayout.LayoutParams params3 = new GridLayout.LayoutParams();
            params3.setMargins(0, 30, 0, 40);
            params3.setGravity(Gravity.CENTER_VERTICAL);
            btn3.setLayoutParams(params3);
            course_grid.addView(btn3, 2);
            btn3.setVisibility(View.INVISIBLE);



            ImageView accent = v.findViewById(R.id.accent);
            accent.getLayoutParams().height = course_grid.getHeight();

            return v;

        }

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
//                params.setGravity(Gravity.START);
                params.setGravity(Gravity.CENTER);
            else if(index % 3 == 1)
                params.setGravity(Gravity.CENTER);
            else
                params.setGravity(Gravity.CENTER_VERTICAL);

            btn.setLayoutParams(params);

            btn.setMaxHeight(0);

            btn.setBackgroundTintList(c.getResources().getColorStateList(R.color.colorAccent, null));


            course_grid.addView(btn, index);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // opens a dialog associated with the text of the button
                    synchronized (CourseSelectLock.lock) {
                        if (!CourseSelectLock.pressed) {
                            CourseSelectLock.pressed = true;
                            openDialog(s_course_header + ((Button) view).getText().toString());
                        }
                    }
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
    public void openDialog(final String s_course){
        final SelectedCourseDialog selectedCourseDialog = new SelectedCourseDialog();
        Toast.makeText(c, "make dialog", Toast.LENGTH_SHORT).show();
        progressDialog = new ProgressDialog(c);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

//        Thread thread = new Thread(){
//            @Override
//            public void run() {
//                boolean success = makeHttpRequests(s_course);
//
//                if(success) {
//                    Bundle args = new Bundle();
//                    args.putString("course", s_course);
//                    args.putString("json_stat_http", json_stat_http);
//                    args.putString("json_ranking_http", json_ranking_http);
//                    args.putBoolean("is_prof_of", is_prof_of);
//                    selectedCourseDialog.setArguments(args);
//                    ((Activity) c).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                    progressDialog.dismiss();
//                    selectedCourseDialog.show(fm, "selected course dialog");
//                }
//                else {
//                    ((Activity) c).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                    progressDialog.dismiss();
//                }
//            }
//        };
//        thread.start();

        boolean success = makeHttpRequests(s_course);

        if(success) {
            Bundle args = new Bundle();
            args.putString("course", s_course);
            args.putString("json_stat_http", json_stat_http);
            args.putString("json_ranking_http", json_ranking_http);
            args.putBoolean("is_prof_of", is_prof_of);
            selectedCourseDialog.setArguments(args);
            progressDialog.dismiss();
            Toast.makeText(c, "dialog dismiss", Toast.LENGTH_SHORT).show();
            selectedCourseDialog.show(fm, "selected course dialog");
        }
        else {
            progressDialog.dismiss();
            Toast.makeText(c, "Unable to connect to server. Try again later", Toast.LENGTH_SHORT).show();
        }

//        final Handler handler = new Handler();
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                boolean success = makeHttpRequests(s_course);
//
//                if(success) {
//                    Bundle args = new Bundle();
//                    args.putString("course", s_course);
//                    args.putString("json_stat_http", json_stat_http);
//                    args.putString("json_ranking_http", json_ranking_http);
//                    args.putBoolean("is_prof_of", is_prof_of);
//                    selectedCourseDialog.setArguments(args);
//                    ((Activity) c).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                    progressDialog.dismiss();
//                    Toast.makeText(c, "dialog dismiss", Toast.LENGTH_SHORT).show();
//                    selectedCourseDialog.show(fm, "selected course dialog");
//                }
//                else {
//                    ((Activity) c).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                    progressDialog.dismiss();
//                    Toast.makeText(c, "Unable to connect to server. Try again later", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });

        
    }

    public boolean makeHttpRequests(String s_course) {
        json_stat_http = null;
        json_ranking_http = null;
        is_prof_of = false;

        boolean success = true;

        try {
            json_stat_http = new OkHttpTask().execute(OkHttpTask.GET_USER_STAT, s_course.substring(0,4), s_course.substring(4,7)).get();
        } catch (InterruptedException e) {
            success = false;
            return success;
        } catch (ExecutionException e) {
            success = false;
            return success;
        }

        try {
            json_ranking_http = new OkHttpTask().execute(OkHttpTask.GET_RANK, s_course.substring(0,4), s_course.substring(4,7)).get();
        } catch (InterruptedException e) {
            success = false;
            return success;
        } catch (ExecutionException e) {
            success = false;
            return success;
        }

        try {
            String prof_courses_json = new OkHttpTask().execute(OkHttpTask.GET_PROF_COURSES).get();
            try {
                JSONArray jsonArray = new JSONArray(prof_courses_json);


                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String course_subject = jsonObject.getString("course_subject");
                    int course_number = jsonObject.getInt("course_number");

                    if((course_subject + course_number).equals(s_course)) {
                        is_prof_of = true;
                        break;
                    }

                }
            } catch (JSONException e) {
                success = false;
                return success;
            }
        } catch (InterruptedException e) {
            success = false;
            return success;
        } catch (ExecutionException e) {
            success = false;
            return success;
        }

        return success;
    }
}
