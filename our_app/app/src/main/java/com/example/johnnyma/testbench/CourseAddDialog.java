package com.example.johnnyma.testbench;

import android.support.annotation.Nullable;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.concurrent.ExecutionException;

/**
 * Dialog that allows the user to add a course to their profile.
 * Has the CourseSelectActivity listening for its events.
 */
public class CourseAddDialog extends BottomSheetDialogFragment {

    private BottomSheetListener mListener;
    Button submit_btn;
    EditText subj_text;
    EditText num_text;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_addcourse, container, false);

        submit_btn = (Button) v.findViewById(R.id.submit_btn);
        subj_text = (EditText) v.findViewById(R.id.subj_text);
        num_text = (EditText) v.findViewById(R.id.num_text);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String course_resp;
                try {
                    course_resp = new OkHttpTask().execute(OkHttpTask.ADD_TO_USER_COURSES, subj_text.getText().toString(), num_text.getText().toString()).get();
                } catch (InterruptedException e) {
                    course_resp = null;
                } catch (ExecutionException e) {
                    course_resp = null;
                }
                if(course_resp.equals("400")) {
                    Toast.makeText(getActivity(), "That's not a course", Toast.LENGTH_SHORT).show();
                } else if (course_resp.equals("409")) {
                    Toast.makeText(getActivity(), "You already have this course", Toast.LENGTH_SHORT).show();
                }
                    else {
                    Toast.makeText(getActivity(), "Course added!", Toast.LENGTH_SHORT).show();
                    mListener.onCourseAdded();
                    dismiss();
                }


            }
        });
        return v;
    }

    public interface BottomSheetListener {
        void onCourseAdded();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }
}
