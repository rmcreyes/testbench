package com.example.johnnyma.testbench;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class ProfessorActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView QuestionListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.filter_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        QuestionListView = findViewById(R.id.list_view);

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
            case R.id.newest:
                Toast.makeText(this, "Newest", Toast.LENGTH_SHORT).show();
                break;

            case R.id.oldest:
                Toast.makeText(this, "Oldest", Toast.LENGTH_SHORT).show();
                break;

            case R.id.easiest:
                Toast.makeText(this, "Easiest", Toast.LENGTH_SHORT).show();
                break;

            case R.id.hardest:
                Toast.makeText(this, "Hardest", Toast.LENGTH_SHORT).show();
                break;

            case R.id.best_rated:
                Toast.makeText(this, "Best rated", Toast.LENGTH_SHORT).show();
                break;

            case R.id.worst_rated:
                Toast.makeText(this, "Worst rated", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
