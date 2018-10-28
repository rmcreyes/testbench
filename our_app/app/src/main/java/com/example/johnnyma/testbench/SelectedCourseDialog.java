package com.example.johnnyma.testbench;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SelectedCourseDialog extends AppCompatDialogFragment {

    private TextView course_text;
    private Button battle_btn;
    private Button add_question_btn;
    private Button stats_btn;
    private SelectedCourseDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (SelectedCourseDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("must implement listener");
        }
    }

    private String s_course;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String course = getArguments().getString("course");
        s_course = course;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_selected_course, null);

        builder.setView(v);

        course_text = v.findViewById(R.id.course_text);
        course_text.setText(course);

        battle_btn = v.findViewById(R.id.battle_btn);
        battle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.chooseCourseView(CourseActionDefs.BATTLE, s_course);
                dismiss();
            }
        });

        add_question_btn = v.findViewById(R.id.add_question_btn);
        add_question_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.chooseCourseView(CourseActionDefs.ADD_QUESTION, s_course);
                dismiss();
            }
        });

        stats_btn = v.findViewById(R.id.stats_btn);
        stats_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.chooseCourseView(CourseActionDefs.GET_STATS, s_course);
                dismiss();
            }
        });

        return builder.create();
    }

    public interface SelectedCourseDialogListener {
        void chooseCourseView(int action, String course);
    }
}
