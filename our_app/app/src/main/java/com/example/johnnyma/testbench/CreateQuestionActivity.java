package com.example.johnnyma.testbench;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class CreateQuestionActivity extends AppCompatActivity {

    private String course;
    private String user_name;
    private TextView title;
    private HTTPService my_httpservice;
    private boolean is_bound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);
        Intent onCreateIntent = getIntent();
        Bundle extras = onCreateIntent.getExtras();
        title = findViewById(R.id.title);
        if(extras.containsKey("name"))
            this.user_name = onCreateIntent.getStringExtra("name");

        if(extras.containsKey("course"))
            this.course = onCreateIntent.getStringExtra("course");
        title.setText("Make A Question for " + course);
    }

    public void submitQuestionButton(View view){

        EditText question = findViewById(R.id.questionInput);
        EditText correctAnswer = findViewById(R.id.correctAnswerInput);
        EditText wrongAnswer1 = findViewById(R.id.wrongAnswer1Input);
        EditText wrongAnswer2 = findViewById(R.id.wrongAnswer2Input);
        EditText wrongAnswer3 = findViewById(R.id.wrongAnswer3Input);
        JSONObject questionJson = new JSONObject();
        try {
            questionJson.put("question", question.getText().toString());
            questionJson.put("correct_ans", correctAnswer.getText().toString());
            questionJson.put("wrong_ans1", wrongAnswer1.getText().toString());
            questionJson.put("wrong_ans2", wrongAnswer2.getText().toString());
            questionJson.put("wrong_ans3", wrongAnswer3.getText().toString());
            questionJson.put("difficulty", Integer.valueOf(5));
            questionJson.put("creator_id", this.user_name);
            questionJson.put("prof_verified", false);
            questionJson.put("reported", false);
            questionJson.put("question_id", this.course+this.user_name+question.getText().toString()); //TODO maybe also uncomment the course json
            questionJson.put("course", this.course);
            //Toast.makeText(this, "JsonMade", Toast.LENGTH_SHORT).show();
        }
        catch (JSONException e){
            Toast.makeText(this, "JSON EXCEPTION", Toast.LENGTH_SHORT).show();
            return;
        }
        //https shit
        Intent service_intent = new Intent(this, HTTPService.class);
        bindService(service_intent, my_connection, Context.BIND_AUTO_CREATE);
        //TODO app crashes after this maybe try okhttptask
        /*try{
            my_httpservice.addQuestion(questionJson);
        } catch(IOException b){
            Toast.makeText(this, "IO EXCEPTION", Toast.LENGTH_SHORT).show();
            return;
        }*/
        new OkHttpTask().execute("ADD_QUESTION", questionJson.toString());
        finish();
    }
    //for https service
    private ServiceConnection my_connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            HTTPService.LocalBinder binder = (HTTPService.LocalBinder) iBinder;
            my_httpservice = binder.getService();
            is_bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            is_bound = false;
        }
    };
}
