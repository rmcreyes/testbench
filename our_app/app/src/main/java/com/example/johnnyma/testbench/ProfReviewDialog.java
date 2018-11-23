package com.example.johnnyma.testbench;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class ProfReviewDialog extends AppCompatDialogFragment {

    private Question question;

    private TextView question_body;

    private Button correct_answer;
    private Button wrong_answer_1;
    private Button wrong_answer_2;
    private Button wrong_answer_3;

    private CheckBox verified_checkbox;
    private CheckBox reported_checkbox;

    private Button submit_btn;
    private Button delete_btn;

    private ProfReviewDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (ProfReviewDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("must implement listener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_prof_review, null);
        builder.setView(v);

        String s_question = getArguments().getString("question");
        try {
            Log.e("question", s_question);
            question = new Question(new JSONObject(s_question));

            question_body = v.findViewById(R.id.question_body);

            correct_answer = v.findViewById(R.id.answer_1);
            wrong_answer_1 = v.findViewById(R.id.answer_2);
            wrong_answer_2 = v.findViewById(R.id.answer_3);
            wrong_answer_3 = v.findViewById(R.id.answer_4);

            verified_checkbox = v.findViewById(R.id.verified_checkbox);
            reported_checkbox = v.findViewById(R.id.reported_checkbox);

            submit_btn = v.findViewById(R.id.submit_btn);
            delete_btn = v.findViewById(R.id.delete_btn);

            question_body.setText(question.getBody());
            correct_answer.setText(question.getCorrectAnswer());
            wrong_answer_1.setText(question.getIncorrectAnswer1());
            wrong_answer_2.setText(question.getIncorrectAnswer2());
            wrong_answer_3.setText(question.getIncorrectAnswer3());
            verified_checkbox.setChecked(question.isVerified());
            reported_checkbox.setChecked(question.isReported());

            submit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean verify = verified_checkbox.isChecked();
                    boolean report = reported_checkbox.isChecked();

                    String verify_response, report_response;

                    try {
                        if(question.isVerified() != verify)
                            verify_response = new OkHttpTask()
                                    .execute(OkHttpTask.SET_QUESTION_VERIFIED_STATUS, question.getId(),
                                            Boolean.toString(verify)).get();

                        if(question.isReported() != report)
                            report_response = new OkHttpTask()
                                    .execute(OkHttpTask.SET_QUESTION_REPORTED_STATUS, question.getId(),
                                            Boolean.toString(report)).get();
                    } catch (InterruptedException e) {
                        Toast.makeText(getContext(), "Failed to send review", Toast.LENGTH_SHORT).show();
                        dismiss();
                    } catch (ExecutionException e) {
                        Toast.makeText(getContext(), "Failed to send review", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                    listener.responseCollected(true);
                    dismiss();
                }
            });

            delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String delete_response;

                    try {
                        delete_response = new OkHttpTask()
                                .execute(OkHttpTask.DELETE_QUESTION, question.getId()).get();
                    } catch(ExecutionException e) {
                        Toast.makeText(getContext(), "Failed to delete question", Toast.LENGTH_SHORT).show();
                        dismiss();
                    } catch(InterruptedException e) {
                        Toast.makeText(getContext(), "Failed to delete question", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }

                    listener.responseCollected(false);
                    dismiss();
                }
            });

        } catch(JSONException e) {
            Toast.makeText(getContext(), "error getting question", Toast.LENGTH_SHORT).show();
            dismiss();
        }


        return builder.create();
    }

    public interface ProfReviewDialogListener {
        void responseCollected(boolean submittedOrDeleted);
    }
}
