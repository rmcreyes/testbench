package com.example.johnnyma.testbench;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class makeQuestionActivity extends AppCompatActivity {

    private String course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_question);
        Intent intent = getIntent();
        this.course = intent.getStringExtra(CourseActivity.TAG);
    }
}
