package com.example.johnnyma.testbench;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Activity from where the user picks a course to battle with,
 * add a question to, or view their stats in
 */
public class CourseSelectActivity extends AppCompatActivity implements SelectedCourseDialog.SelectedCourseDialogListener {

    public static final String TAG = "CourseSelectActivity"; //tag for sending info through intents
    private ListView CourseListView;
    private FloatingActionButton fab;
    private TextView name;
    private ImageView profile_pic;

    private String user_name;
    private String profile_pic_url;

    // each course header(eg. CPEN) is a key to a list of course codes (eg. 311, 321, 331)
    private Map<String, List<String>> Courses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_select);

        CourseListView = findViewById(R.id.list_view);
        fab = findViewById(R.id.fab);
        name = findViewById(R.id.name);
        profile_pic = findViewById(R.id.profile_pic);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        // coming from the LoginActivity, the intent may come with information relating to the user
        if(extras.containsKey("name")) {
            user_name = intent.getStringExtra("name");
            name.setText(user_name + "      |      student");
        }
        else
            name.setText("error");

        if(extras.containsKey("profile_pic_url")) {
            profile_pic_url = intent.getStringExtra("profile_pic_url");
            Picasso.with(this).load(profile_pic_url)
                    .transform(new ProfilePicTransformation(200, 0))
                    .into(profile_pic);
        }

        fillCourses();



        CourseAdapter courseAdapter = new CourseAdapter(this, Courses, getSupportFragmentManager());
        CourseListView.setAdapter(courseAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CourseSelectActivity.this, "add course", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * Initializes and loads the map `Courses` with the courses tied to
     * the user's account.
     */
    private void fillCourses() {
        // TODO: remove mock implemetation and use REST API
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

    /**
     * Abstract method implementation that allows SelectCourseActivity to
     * determine the action decided on in the SelectedCourseDialog
     * @param action - action to be taken with the course
     * @param course - course whose action is to be enacted
     */
    @Override
    public void chooseCourseView(int action, String course) {
        switch (action) {
            case CourseActionDefs.BATTLE:
                //Toast.makeText(this, "BATTLE " + course, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MatchmakingActivity.class);
                intent.putExtra(TAG, course);
                intent.putExtra("name", user_name);
                startActivity(intent);
                break;
            case CourseActionDefs.ADD_QUESTION:
                //Toast.makeText(this, "ADD QUESTION " + course, Toast.LENGTH_SHORT).show();
                Intent createQIntent = new Intent(this, CreateQuestionActivity.class);
                createQIntent.putExtra("course", course);
                createQIntent.putExtra("name", user_name);
                startActivity(createQIntent);
                break;
            case CourseActionDefs.GET_STATS:
                Toast.makeText(this, "GET STATS " + course, Toast.LENGTH_SHORT).show();
                break;
            default: break;
        }

    }
}


