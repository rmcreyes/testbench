package com.example.johnnyma.testbench;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseSelectActivity extends AppCompatActivity {

    private ListView CourseListView;


    private Map<String, List<String>> Courses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_select);

        setTitle("");

        fillCourses();

        CourseListView = findViewById(R.id.list_view);
        CourseAdapter courseAdapter = new CourseAdapter(this, Courses);
        CourseListView.setAdapter(courseAdapter);

    }

    private void fillCourses() {
        Courses = new HashMap<String, List<String>>();
        List<String> cpen_courses = new ArrayList<>();
        cpen_courses.add("311");
        cpen_courses.add("321");
        cpen_courses.add("331");
        cpen_courses.add("341");
        cpen_courses.add("351");
        Courses.put("CPEN", cpen_courses);

        List<String> elec_courses = new ArrayList<>();
        elec_courses.add("201");
        elec_courses.add("221");
        elec_courses.add("301");
        Courses.put("ELEC", elec_courses);

    }


}


