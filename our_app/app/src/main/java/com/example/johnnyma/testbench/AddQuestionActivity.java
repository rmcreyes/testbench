package com.example.johnnyma.testbench;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class AddQuestionActivity extends AppCompatActivity {

    private String course;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        Intent onCreateIntent = getIntent();
        Bundle extras = onCreateIntent.getExtras();
        title = findViewById(R.id.title);
        if(extras.containsKey("course"))
            this.course = onCreateIntent.getStringExtra("course");
        title.setText("Make a question for " + course);
    }

    public void submitQuestionButton(View view){
        EditText question = findViewById(R.id.questionInput);
        EditText correctAnswer = findViewById(R.id.correctAnswerInput);
        EditText wrongAnswer1 = findViewById(R.id.wrongAnswer1Input);
        EditText wrongAnswer2 = findViewById(R.id.wrongAnswer2Input);
        EditText wrongAnswer3 = findViewById(R.id.wrongAnswer3Input);
        JSONObject questionJson = new JSONObject();
        try {
            questionJson.put("question_text", question.getText().toString());
            questionJson.put("correct_answer", correctAnswer.getText().toString());
            questionJson.put("incorrect_answer_1", wrongAnswer1.getText().toString());
            questionJson.put("incorrect_answer_2", wrongAnswer2.getText().toString());
            questionJson.put("incorrect_answer_3", wrongAnswer3.getText().toString());
            questionJson.put("creator_uID", GlobalTokens.USER_ID);
            questionJson.put("verified", false);
            questionJson.put("course_subject", this.course);
            Toast.makeText(this, "JsonMade", Toast.LENGTH_SHORT).show();
            new OkHttpTask().execute("ADD_QUESTION", questionJson.toString());
        }
        catch (JSONException e){
            Toast.makeText(this, "JSON EXCEPTION", Toast.LENGTH_SHORT).show();
            return;
        }
        finish();
    }
}
