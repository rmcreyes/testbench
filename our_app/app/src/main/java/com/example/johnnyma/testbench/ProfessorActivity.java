package com.example.johnnyma.testbench;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class ProfessorActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, ProfReviewDialog.ProfReviewDialogListener {

    private Toolbar toolbar;
    private ListView QuestionListView;

    private List<Question> allQuestions;
    private List<Question> shownQuestions;

    private static final int ALL = 0;
    private static final int VERIFIED = 1;
    private static final int REPORTED = 2;
    private static final int UNCHECKED = 3;

    private ProgressDialog progressDialog;

    private int current_filter;

    private String course;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor);

        Intent intent = getIntent();
        course = intent.getStringExtra("course");

        getQuestions();

        toolbar = findViewById(R.id.toolbar);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        QuestionListView = findViewById(R.id.list_view);
        setSupportActionBar(toolbar);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.filter_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        setShownQuestions(ALL);
        resetQuestionList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.question_sort, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
                case R.id.best_rated:
                Collections.sort(shownQuestions, new QuestionComparator());
                Collections.reverse(shownQuestions);
                Toast.makeText(this, "Best rated", Toast.LENGTH_SHORT).show();
                break;

            case R.id.worst_rated:
                Collections.sort(shownQuestions, new QuestionComparator());
                Toast.makeText(this, "Worst rated", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }

        resetQuestionList();
        return super.onOptionsItemSelected(item);
    }

    public void resetQuestionList() {
        QuestionAdapter questionAdapter = new QuestionAdapter(this, shownQuestions);
        QuestionListView.setAdapter(questionAdapter);
        QuestionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ProfReviewDialog profReviewDialog = new ProfReviewDialog();
                Bundle args = new Bundle();
                args.putString("question", shownQuestions.get(i).getJSONString());
                profReviewDialog.setArguments(args);
                profReviewDialog.show(getSupportFragmentManager(), "prof review");
                Toast.makeText(ProfessorActivity.this, shownQuestions.get(i).getCorrectAnswer(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String filter = (String) adapterView.getItemAtPosition(i);

        switch(filter) {
            case "All" :
                setShownQuestions(ALL);
                current_filter = ALL;
                Toast.makeText(this, "ALL", Toast.LENGTH_SHORT).show();
                break;

            case "Verified" :
                setShownQuestions(VERIFIED);
                current_filter = VERIFIED;
                Toast.makeText(this, "VERIFIED", Toast.LENGTH_SHORT).show();
                break;

            case "Reported" :
                setShownQuestions(REPORTED);
                current_filter = REPORTED;
                Toast.makeText(this, "REPORTED", Toast.LENGTH_SHORT).show();
                break;

            case "Unchecked" :
                setShownQuestions(UNCHECKED);
                current_filter = UNCHECKED;
                Toast.makeText(this, "UNCHECKED", Toast.LENGTH_SHORT).show();
                break;

            default :
                setShownQuestions(ALL);
                Toast.makeText(this, "DEFAULT", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Toast.makeText(this, "nothing selected", Toast.LENGTH_SHORT).show();
    }




    private void setShownQuestions(int filter) {

        if(filter == ALL)
            shownQuestions = new ArrayList<Question>(allQuestions);
        else {
            shownQuestions =  new ArrayList<Question>();

            switch (filter) {
                case VERIFIED:
                    for (Question q : allQuestions)
                        if (q.isVerified())
                            shownQuestions.add(q);
                    break;

                case REPORTED:
                    for (Question q : allQuestions)
                        if (q.isReported())
                            shownQuestions.add(q);
                    break;

                case UNCHECKED:
                    for(Question q : allQuestions)
                        if(!q.isReported() && !q.isVerified())
                            shownQuestions.add(q);
                    break;

                default:
                    shownQuestions = new ArrayList<Question>(allQuestions);
                    break;
            }
        }

        resetQuestionList();
    }

//    private void getQuestions(String questions_json) {
//        allQuestions = new ArrayList<Question>();
//
//        try {
//            JSONArray jsonArray = new JSONArray(questions_json);
//
//            for(int i = 0; i < jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                allQuestions.add(new Question(jsonObject));
//            }
//        } catch(JSONException e) {
//            e.printStackTrace();
//        }
//    }



    private void getQuestions() {
        allQuestions = new ArrayList<Question>();

        String course_subject = course.substring(0, 4);
        String course_number = course.substring(4, 7);

        String course_questions;

        try {
            course_questions = new OkHttpTask().execute(OkHttpTask.GET_COURSE_QUESTIONS, course_subject, course_number).get();
        } catch (InterruptedException e) {
            Toast.makeText(this, "Failed to get questions", Toast.LENGTH_SHORT).show();
            return;
        } catch (ExecutionException e) {
            Toast.makeText(this, "Failed to get questions", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONArray jsonArray = new JSONArray(course_questions);

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                allQuestions.add(new Question(jsonObject));
            }
        } catch(JSONException e) {
            Toast.makeText(this, "Failed to get questions", Toast.LENGTH_SHORT).show();
            return;
        }


//        try {
//            JSONObject json1 = new JSONObject();
//            json1.put("_id", "some id");
//            json1.put("question_text", "This is a question?");
//            json1.put("correct_answer", "1 true true");
//            json1.put("incorrect_answer_1", "This is the first incorrect answer.");
//            json1.put("incorrect_answer_2", "This is the second incorrect answer.");
//            json1.put("incorrect_answer_3", "This is the third incorrect answer.");
//            json1.put("rating", 1);
//            json1.put("verified", true);
//            json1.put("reported", true);
//            allQuestions.add(new Question(json1));
//
//            JSONObject json2 = new JSONObject();
//            json2.put("_id", "some id");
//            json2.put("question_text", "This is a question?");
//            json2.put("correct_answer", "1 false false");
//            json2.put("incorrect_answer_1", "This is the first incorrect answer.");
//            json2.put("incorrect_answer_2", "This is the second incorrect answer.");
//            json2.put("incorrect_answer_3", "This is the third incorrect answer.");
//            json2.put("rating", 1);
//            json2.put("verified", false);
//            json2.put("reported", false);
//            allQuestions.add(new Question(json2));
//
//            JSONObject json3 = new JSONObject();
//            json3.put("_id", "some id");
//            json3.put("question_text", "This is a question?");
//            json3.put("correct_answer", "2 true false");
//            json3.put("incorrect_answer_1", "This is the first incorrect answer.");
//            json3.put("incorrect_answer_2", "This is the second incorrect answer.");
//            json3.put("incorrect_answer_3", "This is the third incorrect answer.");
//            json3.put("rating", 2);
//            json3.put("verified", true);
//            json3.put("reported", false);
//            allQuestions.add(new Question(json3));
//
//            JSONObject json4 = new JSONObject();
//            json4.put("_id", "some id");
//            json4.put("question_text", "This is a question?");
//            json4.put("correct_answer", "3 false false");
//            json4.put("incorrect_answer_1", "This is the first incorrect answer.");
//            json4.put("incorrect_answer_2", "This is the second incorrect answer.");
//            json4.put("incorrect_answer_3", "This is the third incorrect answer.");
//            json4.put("rating", 3);
//            json4.put("verified", false);
//            json4.put("reported", false);
//            allQuestions.add(new Question(json4));
//
//
//        } catch(JSONException e) {
//            Toast.makeText(this, "json exception", Toast.LENGTH_SHORT).show();
//            return;
//        }
    }

    @Override
    public void responseCollected(boolean submittedOrDeleted) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        getQuestions();
        setShownQuestions(current_filter);

        progressDialog.dismiss();

        if(submittedOrDeleted)
            Toast.makeText(this, "Your response has been submitted!", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "The question has been deleted!", Toast.LENGTH_SHORT).show();
    }
}
