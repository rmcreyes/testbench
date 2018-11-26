package com.example.johnnyma.testbench;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/*
Activity to control emailing for Professor verification
 */
public class EmailActivity extends AppCompatActivity {

    private Button submit_btn;
    private EditText email_text;
    private EditText faculty_text;
    private EditText dept_text;
    private EditText subj_text;
    private Intent intent;
    private Bundle extras;
    private String name;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profcontact);
        submit_btn = (Button) findViewById(R.id.submit_btn);

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMail(email_text.getText().toString(),faculty_text.getText().toString(),dept_text.getText().toString(),subj_text.getText().toString());
            }
        });
        email_text = (EditText) findViewById(R.id.email_text);
        faculty_text = (EditText) findViewById(R.id.faculty_text);
        dept_text = (EditText) findViewById(R.id.dept_text);
        subj_text = (EditText) findViewById(R.id.subj_text);

        intent = getIntent();
        extras = intent.getExtras();

        if(extras.containsKey("name")) {
            name = intent.getStringExtra("name");
        }
        if(extras.containsKey("email")) {
            email = intent.getStringExtra("email");
        }
    };

    /*
        Construct email for professor verification and open email client to send to admin
     */
    private void sendMail(String school_email,String faculty,String dept, String subjects) {

        String recipientList = "testbench.segfaultstudios@gmail.com";
        String[] recipients = recipientList.split(",");

        String subject = "Professor Verification";
        String message =
                "Professor Verification details:\n\n" +
                        "Name: "+ name +
                        "\nSign-up email: "+ email +
                        "\nSchool Email: "+ school_email +
                        "\nDepartment: " + dept +
                        "\nFaculty: " + faculty +
                        "\nSubjects: " + subjects ;
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Choose an email client"));
    }

}
