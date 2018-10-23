package com.example.johnnyma.testbench;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.*;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity"; //tag for sending info through intents
    public static final int ADD_COURSE_REQUEST = 0;

    private ListView courseList;
    //TODO: strings are okay for now but courses need coursesID
    List<String> courses = new ArrayList<String>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //attach ListView to courseList
        courseList = (ListView) findViewById(R.id.courseList);

        //TODO: read you saved courses file. Or if none exists, create one. Add the read courses to courses list

        //create the list view using adapter. TODO: NEED TO MAKE CUSTOM ARRAY ADAPTER TO ADD COURSE ID TO EACH VIEW USING. using getTag and setTag to add courseID to views should work
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, courses);
        //attach adapter to
        courseList.setAdapter(adapter);
        //create onclicklistners for the list of courses
        courseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) { //open course activity on click
                String course_val = (String)(((TextView)view).getText());
                openCourse(course_val);
            }
        });
    };

    //function to go the selected course, used by onclicklistner of course
    public void openCourse(String course_val){
        Intent intent = new Intent(this, CourseActivity.class);
        intent.putExtra(TAG, course_val);
        startActivity(intent);
    }

    //function for login button
    public void login(View view){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    //fuction for add course button
    public void addCourse(View view){
        Intent intent = new Intent(this, AddCourseActivity.class);
        //this is so we can get data from AddCourseActivity.java
        startActivityForResult(intent,ADD_COURSE_REQUEST);
    }

    //for when when activity returns a result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ADD_COURSE_REQUEST){
            if(resultCode == RESULT_OK){
                //TODO: right now we only pass course so String is okay. We need to pass course id.
                String course = data.getStringExtra(AddCourseActivity.TAG);
                courses.add(course);
                //tell the adapter to update the view since we changed courses
                adapter.notifyDataSetChanged();
            }
        }
    }
}


