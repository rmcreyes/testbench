package com.example.johnnyma.testbench;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AddCourseActivity extends AppCompatActivity {
    public static final String TAG = "AddCourseActivity"; //tag for sending info through intents

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
    }

    /* TODO
    Dummy function for adding courses. You would add a something like this to the
    onclicklistener for the list of courses we get from server.
     */
    public void addCourse(View view){
        Intent intent = new Intent();
        EditText courseText = findViewById(R.id.courseText);
        String course = courseText.getText().toString();
        intent.putExtra(TAG,course);
        setResult(RESULT_OK,intent);
        finish();
    }
}
