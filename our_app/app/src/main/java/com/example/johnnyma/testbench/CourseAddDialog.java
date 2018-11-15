package com.example.johnnyma.testbench;

import android.support.annotation.Nullable;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

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
                //mListener.onButtonClicked("Button 1 clicked");
                String course_resp;
                try {
                    course_resp = new OkHttpTask().execute(OkHttpTask.ADD_TO_USER_COURSES, subj_text.getText().toString(), num_text.getText().toString()).get();
                } catch (InterruptedException e) {
                    Log.d("BELHTDFG","Toast " +"InterruptedException" );
                    course_resp = null;
                } catch (ExecutionException e) {
                    Log.d("BELHTDFG","Toast " +"ExecutionException" );
                    course_resp = null;
                }

                Pattern p = Pattern.compile("(?:(?<!err)\\s+?)(\\d{3})\\b");
                if(course_resp.equals("400")) {
//                    Snackbar.make(findViewById(R.id.placeSnackBar), "That's not a course", Snackbar.LENGTH_SHORT)
//                            .show();
//                    Toast.makeText(CourseAddDialog.this, "That's not a course", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), "That's not a course", Toast.LENGTH_SHORT).show();
                } else if (course_resp.equals("409")) {
//                    Snackbar.make(v.findViewById(R.id.placeSnackBar), "You already have this course", Snackbar.LENGTH_SHORT)
//                            .show();
                    Toast.makeText(getActivity(), "You already have this course", Toast.LENGTH_SHORT).show();
                }
//                else if (p.matcher(course_resp).find()){
////                    Snackbar.make(v.findViewById(R.id.placeSnackBar), "Unknown error!", Snackbar.LENGTH_SHORT)
////                            .show();
//                    Toast.makeText(getActivity(), "Unknown error!", Toast.LENGTH_SHORT).show();
//                }
                    else {
//                    Snackbar.make(v.findViewById(R.id.placeSnackBar), "Course added!", Snackbar.LENGTH_SHORT)
//                            .show();
                    Toast.makeText(getActivity(), "Course added!", Toast.LENGTH_SHORT).show();
                    dismiss();
                }


            }
        });
        return v;
    }

    public interface BottomSheetListener {
        void onButtonClicked(String text);
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
