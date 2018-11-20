package com.example.johnnyma.testbench;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EmailActivity extends AppCompatActivity {

    private Button submit_btn;
    private EditText email_text;
    private EditText faculty_text;
    private EditText dept_text;
    private EditText subj_text;

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
    };

    private void sendMail(String school_email,String faculty,String dept, String subjects) {

        String recipientList = "andrea_mah22@hotmail.com";
        String[] recipients = recipientList.split(",");

        String subject = "Professor Verification";
        String message =
                "Professor Verification details:\n\n" +
                        "Name: "+ "bobo" +
                        "\nSign-up email: "+ "roob@roob.ca" +
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


//    private EmailActivity.EmailDialogListener listener;

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//
//        try {
//            listener = (EmailActivity.EmailDialogListener) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException("must implement listener");
//        }
//    }
//
//    private String s_course;
//
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//
//        // course is passed in as an argument from the button click
////        String course = getArguments().getString("course");
////        s_course = course;
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        View v = inflater.inflate(R.layout.activity_profcontact, null);
//
//        builder.setView(v);
//
//        // have each button signal a different action in CourseSelectActivity
//        submit_btn = v.findViewById(R.id.submit_btn);
//        submit_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                listener.submitProfReq( s_course);
//                dismiss();
//            }
//        });
//
//        add_question_btn = v.findViewById(R.id.add_question_btn);
//        add_question_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                listener.submitProfReq(CourseActionDefs.ADD_QUESTION, s_course);
//                dismiss();
//            }
//        });
//
//        stats_btn = v.findViewById(R.id.stats_btn);
//        stats_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                listener.submitProfReq(CourseActionDefs.GET_STATS, s_course);
//                dismiss();
//            }
//        });
//
//        return builder.create();
//    }
//
//    public interface EmailDialogListener {
//        void submitProfReq(String school_email,String fac, String dept, String courses);
//    }
}
