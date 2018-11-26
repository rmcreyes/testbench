package com.example.johnnyma.testbench;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
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
import java.util.concurrent.ExecutionException;

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

    public void submitQuestionButton(View view) throws ExecutionException, InterruptedException {
        if(correctAnswer.getText().toString().equals("") ||
                wrongAnswer1.getText().toString().equals("") ||
                wrongAnswer2.getText().toString().equals("") ||
                wrongAnswer3.getText().toString().equals("") )
        {
            Snackbar.make(findViewById(android.R.id.content), "At least one of your fields is empty. Please fix this before proceeding.", Snackbar.LENGTH_LONG)
                    .setActionTextColor(Color.RED)
                    .show();
            return;
        }
        String ques_reponse = new OkHttpTask().execute("ADD_QUESTION",
                question.getText().toString(),
                correctAnswer.getText().toString(),
                wrongAnswer1.getText().toString(),
                wrongAnswer2.getText().toString(),
                wrongAnswer3.getText().toString(),
                GlobalTokens.USER_ID,
                "false",
                this.course.substring(0,4).toUpperCase(),
                this.course.substring(4,7)).get();

        if(ques_reponse.equals("400"))
        {
            Snackbar.make(findViewById(android.R.id.content), "Some of your answers are identical. Please fix this before proceeding.", Snackbar.LENGTH_LONG)
                    .setActionTextColor(Color.RED)
                    .show();
        } else {
            Toast.makeText(AddQuestionActivity.this,
                    "Question Created!",Toast.LENGTH_LONG).show();

            finish();
        }
    }
}
