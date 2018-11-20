package com.example.johnnyma.testbench;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class CourseActivity extends AppCompatActivity {
    public static final String TAG = "CourseActivity"; //tag for sending info through intents
    private String course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        //set course title
//        Intent intent = getIntent();
//        String course_title = intent.getStringExtra(CourseSelectActivity.TAG);
//        this.course = course_title.replaceAll("\\s+","").toUpperCase();
//        TextView courseTitle = findViewById(R.id.courseTitle);
//        courseTitle.setText(course_title);
    }

    //function for find match button
    public void findMatch(View view){
        Intent intent = new Intent(this, MatchmakingActivity.class);
        intent.putExtra(TAG,this.course);
        // add player name/rank/profile fields
        startActivity(intent);
    }
}
