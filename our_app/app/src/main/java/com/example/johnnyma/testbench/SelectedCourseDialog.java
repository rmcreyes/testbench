package com.example.johnnyma.testbench;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * Dialog used to allow user to choose an action with the course
 * they selected in the CourseSelectActivity.
 */
public class SelectedCourseDialog extends AppCompatDialogFragment {

    private TextView course_text;
    private Button battle_btn;
    private Button add_question_btn;
    private Button stats_btn;
    private Button prof_btn;
    private SelectedCourseDialogListener listener;
    private LinearLayout leaderboard_layout;
    private LinearLayout stats_layout;
    private int rank;
    private String level_progress;
    private int course_ranking;
    private double correctness_rate;
    private double average_response_time;
    private double level_amt;
    private double level_max;
    private TextView rank_txt;
    private TextView level_progress_txt;
    private TextView course_ranking_txt;
    private TextView correctness_rate_txt;
    private TextView avg_response_time_txt;
    private String leaderboard_http;
    private JSONArray leaderboard_json;
    private TextView first_place;
    private TextView second_place;
    private TextView third_place;
    private LinearLayout first_place_layout;
    private LinearLayout second_place_layout;
    private LinearLayout third_place_layout;

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

        // course is passed in as an argument from the button click
        String course = getArguments().getString("course");
        s_course = course;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_selected_course, null);
        builder.setView(v);

        leaderboard_layout = v.findViewById(R.id.leaderboard_layout);
        stats_layout = v.findViewById(R.id.rate_layout);
        course_text = v.findViewById(R.id.course_text);
        course_text.setText(s_course.substring(0,4) + " " + s_course.substring(4,7));
        //stat fields
        rank_txt = v.findViewById(R.id.rank_txt);
        level_progress_txt = v.findViewById(R.id.level_progress_txt);
        course_ranking_txt = v.findViewById(R.id.course_ranking_txt);
        correctness_rate_txt = v.findViewById(R.id.correctness_rate_txt);
        avg_response_time_txt = v.findViewById(R.id.avg_response_time_txt);

        first_place = v.findViewById(R.id.first_place);
        second_place = v.findViewById(R.id.second_place);
        third_place = v.findViewById(R.id.third_place);

        first_place_layout = v.findViewById(R.id.first_place_layout);
        second_place_layout = v.findViewById(R.id.second_place_layout);
        third_place_layout = v.findViewById(R.id.third_place_layout);

        leaderboard_json = null;

        String json_stat_http = getArguments().getString("json_stat_http");
        String json_ranking_http = getArguments().getString("json_ranking_http");

        try {
            JSONArray json_stat = new JSONArray(json_stat_http);
            JSONObject stats_obj = json_stat.getJSONObject(0).getJSONArray("stats_list").getJSONObject(0);
            rank = stats_obj.getInt("rank");
            level_amt = stats_obj.getInt("level_progress");
            level_max = stats_obj.getDouble("level_max");
            average_response_time = stats_obj.getDouble("avg_response_time");
            correctness_rate = stats_obj.getDouble("correctness_rate");
            level_progress = level_amt + "/" + level_max;
            if(json_ranking_http.equals("[]"))
            {
                course_ranking = 1;
            } else {
                JSONArray rank_json = new JSONArray(json_ranking_http);
                course_ranking = rank_json.getJSONObject(0).getInt("current_rank");
            }

            rank_txt.setText(Integer.toString(rank));
            level_progress_txt.setText(level_progress);
            course_ranking_txt.setText(Integer.toString(course_ranking));
            correctness_rate_txt.setText(Double.toString(correctness_rate));
            avg_response_time_txt.setText(Double.toString(average_response_time));

        } catch (JSONException e) {
            //stats object is not found
            stats_layout.setVisibility(View.GONE);
            rank = 1;
        }


        // have each button signal a different action in CourseSelectActivity
        battle_btn = v.findViewById(R.id.battle_btn);
        battle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.chooseCourseView(CourseActionDefs.BATTLE, s_course, rank);
                dismiss();
            }
        });

        add_question_btn = v.findViewById(R.id.add_question_btn);
        add_question_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.chooseCourseView(CourseActionDefs.ADD_QUESTION, s_course,0);
                dismiss();
            }
        });

        stats_btn = v.findViewById(R.id.stats_btn);
        stats_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(leaderboard_layout.getVisibility() == View.GONE) {
                    if(leaderboard_json == null) {
                        Log.d("BELHTDFG","yeet 1");
                        try {
                            leaderboard_http = new OkHttpTask().execute(OkHttpTask.GET_LEADERBOARD, s_course.substring(0, 4), s_course.substring(4, 7)).get();
                            leaderboard_json = new JSONArray(leaderboard_http);
                            Log.d("BELHTDFG","yeet 2");

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    Log.d("BELHTDFG","yeet 4");
                    Log.d("BELHTDFG",leaderboard_http);


                    String json_username;
                    ArrayList<TextView> leaderboard_views = new ArrayList<TextView>
                            (Arrays.asList(first_place,second_place,third_place));
                    ArrayList<LinearLayout> leaderboard_layouts = new ArrayList<LinearLayout>
                            (Arrays.asList(first_place_layout,second_place_layout,third_place_layout));
                    int hidden_entries = 0;
                    for(int i = 0; i< 3;i++){
                        try {
                            json_username = leaderboard_json.getJSONObject(i).getString("username");
                            leaderboard_views.get(i).setText(json_username);
                            Log.d("BELHTDFG",json_username);
                        } catch (JSONException e){
                            leaderboard_layouts.get(i).setVisibility(View.GONE);
                            hidden_entries++;
                        }
                        Log.d("BELHTDFG","yeet 3");
                    }

                    if(hidden_entries>2)
                    {
                        Toast.makeText(getContext(), "No users on leaderboard",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        leaderboard_layout.setVisibility(View.VISIBLE);
                        stats_btn.setText("HIDE LEADERBOARD");
                    }



                }
                else if(leaderboard_layout.getVisibility() == View.VISIBLE)
                {
                    leaderboard_layout.setVisibility(View.GONE);
                    stats_btn.setText("VIEW LEADERBOARD");
                }

            }
        });

        prof_btn = v.findViewById(R.id.prof_btn);
        if(getArguments().getBoolean("is_prof_of")) {
            prof_btn.setVisibility(View.VISIBLE);
            prof_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.chooseCourseView(CourseActionDefs.REVIEW_QUESTIONS, s_course, 0);
                }
            });
        }
        else
            prof_btn.setVisibility(View.GONE);


        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        synchronized (CourseSelectLock.lock) {
            CourseSelectLock.pressed = false;
        }
    }

    public interface SelectedCourseDialogListener {
        void chooseCourseView(int action, String course, int rank);
    }
}
