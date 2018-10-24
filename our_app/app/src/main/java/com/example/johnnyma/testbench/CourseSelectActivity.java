package com.example.johnnyma.testbench;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseSelectActivity extends AppCompatActivity implements SelectedCourseDialog.SelectedCourseDialogListener {

    public static final String TAG = "CourseSelectActivity"; //tag for sending info through intents
    private ListView CourseListView;
    private FloatingActionButton fab;


    private Map<String, List<String>> Courses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_select);

        setTitle("");

        fillCourses();

        CourseListView = findViewById(R.id.list_view);
        fab = findViewById(R.id.fab);

        CourseAdapter courseAdapter = new CourseAdapter(this, Courses, getSupportFragmentManager());
        CourseListView.setAdapter(courseAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CourseSelectActivity.this, "add course", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void fillCourses() {
        Courses = new HashMap<String, List<String>>();

        List<String> elec_courses = new ArrayList<>();
        elec_courses.add("201");
        elec_courses.add("221");
        elec_courses.add("301");
        Courses.put("ELEC", elec_courses);

        List<String> cpen_courses = new ArrayList<>();
        cpen_courses.add("311");
        cpen_courses.add("321");
        cpen_courses.add("331");
        cpen_courses.add("341");
        cpen_courses.add("351");
        Courses.put("CPEN", cpen_courses);

        List<String> math_courses = new ArrayList<>();
        math_courses.add("311");
        math_courses.add("321");
        math_courses.add("331");
        math_courses.add("341");
        math_courses.add("351");
        Courses.put("MATH", math_courses);

        List<String> cpsc_courses = new ArrayList<>();
        cpsc_courses.add("311");
        cpsc_courses.add("321");
        cpsc_courses.add("331");
        cpsc_courses.add("341");
        cpsc_courses.add("351");
        Courses.put("CPSC", cpsc_courses);

        List<String> cons_courses = new ArrayList<>();
        cons_courses.add("311");
        cons_courses.add("321");
        cons_courses.add("331");
        cons_courses.add("341");
        cons_courses.add("351");
        Courses.put("CONS", cons_courses);

    }


    @Override
    public void chooseCourseView(int action, String course) {
        switch (action) {
            case CourseActionDefs.BATTLE:
                //Toast.makeText(this, "BATTLE " + course, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MatchmakingActivity.class);
                intent.putExtra(TAG, course);
                startActivity(intent);
                break;
            case CourseActionDefs.ADD_QUESTION:
                Toast.makeText(this, "ADD QUESTION " + course, Toast.LENGTH_SHORT).show();
                break;
            case CourseActionDefs.GET_STATS:
                Toast.makeText(this, "GET STATS " + course, Toast.LENGTH_SHORT).show();
                break;
            default: break;
        }

    }
}


