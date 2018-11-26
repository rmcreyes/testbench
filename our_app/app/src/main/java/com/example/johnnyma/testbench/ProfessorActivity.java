package com.example.johnnyma.testbench;

import android.app.ProgressDialog;
import android.content.Intent;
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
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Activity that allows professors to audit questions for a certain course.
 */
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

    /**
     * Uses the options button to sort the questions by rank, in increasing or decreasing order.
     * @param item - option picked
     * @return super.onOptionsItemSelected(item);
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
                case R.id.best_rated:
                Collections.sort(shownQuestions, new QuestionComparator());
                Collections.reverse(shownQuestions);
                break;

            case R.id.worst_rated:
                Collections.sort(shownQuestions, new QuestionComparator());
                break;

            default:
                break;
        }

        resetQuestionList();
        return super.onOptionsItemSelected(item);
    }

    /**
     * Resets the listview to show the shownQuestions.
     */
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
            }
        });

    }

    /**
     * Uses the spinner to filter the questions
     * @param adapterView
     * @param view
     * @param i
     * @param l
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String filter = (String) adapterView.getItemAtPosition(i);

        switch(filter) {
            case "All" :
                setShownQuestions(ALL);
                current_filter = ALL;
                break;

            case "Verified" :
                setShownQuestions(VERIFIED);
                current_filter = VERIFIED;
                break;

            case "Reported" :
                setShownQuestions(REPORTED);
                current_filter = REPORTED;
                break;

            case "Unchecked" :
                setShownQuestions(UNCHECKED);
                current_filter = UNCHECKED;
                break;

            default :
                setShownQuestions(ALL);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Toast.makeText(this, "nothing selected", Toast.LENGTH_SHORT).show();
    }


    /**
     * Depending on the filter, decides which items to show and then resets the list view.
     * @param filter - decides which questions get shown
     */
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

    /**
     * Makes an HTTP request to get the questions of this course
     */
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
    }

    /**
     * Listens to the review question dialog for their response
     * @param submittedOrDeleted
     */
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
