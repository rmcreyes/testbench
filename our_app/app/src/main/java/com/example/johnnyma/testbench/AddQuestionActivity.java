package com.example.johnnyma.testbench;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.Key;

public class AddQuestionActivity extends AppCompatActivity {

    private String course;
    private TextView title;
    EditText question;
    EditText correctAnswer;
    EditText wrongAnswer1;
    EditText wrongAnswer2;
    EditText wrongAnswer3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        Intent onCreateIntent = getIntent();
        Bundle extras = onCreateIntent.getExtras();
        title = findViewById(R.id.title);
        if(extras.containsKey("course"))
            this.course = onCreateIntent.getStringExtra("course");
        title.setText("Make a question for " + course.substring(0,4) + " " + course.substring(4,7));
        question = findViewById(R.id.questionInput);
        correctAnswer = findViewById(R.id.correctAnswerInput);
        wrongAnswer1 = findViewById(R.id.wrongAnswer1Input);
        wrongAnswer2 = findViewById(R.id.wrongAnswer2Input);
        wrongAnswer3 = findViewById(R.id.wrongAnswer3Input);

        question.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void afterTextChanged(Editable s) {
                for(int i = s.length(); i > 0; i--) {

                    if(s.subSequence(i-1, i).toString().equals("\n"))
                        s.replace(i-1, i, "");
                }
            }
        });

    }

    public void submitQuestionButton(View view){
            new OkHttpTask().execute("ADD_QUESTION",
                    question.getText().toString(),
                    correctAnswer.getText().toString(),
                    wrongAnswer1.getText().toString(),
                    wrongAnswer2.getText().toString(),
                    wrongAnswer3.getText().toString(),
                    GlobalTokens.USER_ID,
                    "false",
                    this.course.substring(0,4).toUpperCase(),
                    this.course.substring(4,7));
        finish();
    }
}
