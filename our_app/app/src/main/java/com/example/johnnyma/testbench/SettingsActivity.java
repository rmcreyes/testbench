package com.example.johnnyma.testbench;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class SettingsActivity extends AppCompatActivity {
    private Button prof_contact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        prof_contact = (Button) findViewById(R.id.prof_contact);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        prof_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_prof = new Intent(SettingsActivity.this, EmailActivity.class);
                startActivity(intent_prof);
            }
        });
    };
}
