package com.example.johnnyma.testbench;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.provider.CalendarContract;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static android.view.View.GONE;

/*
 An activity used to show final score of game and game stats,
 and allow users to rate and report questions
 */
public class ScoreActivity extends AppCompatActivity {

    private TextView win_or_lose;
    private TextView avg_reponse_time_txt;
    private TextView correctness_rate_txt;

    private TextView winnerScore;
    private TextView loserScore;
    private TextView winnerName;
    private TextView loserName;
    private ImageView winnerAvatar;
    private ImageView loserAvatar;
    private TextView winnerUsername;
    private TextView loserUsername;

    private String course_subject;
    private int course_number;

    private int player_score = 0;
    private int opponent_score = 0;
    private String player_name;
    private String opponent_name;
    private int player_avatar;
    private int opponent_avatar;
    private String player_username;
    private String opponent_username;

    //raw game stats
    private double total_response_time;
    private int total_num_correct;
    //processed game stats
    private float new_response_time;
    private float new_num_correct;
    private int level_progress =0;

    private Bundle extras;

    private final int TOTAL_QUESTIONS = 7;
    private boolean won_game;

    private Button done_btn;

    //rating buttons
    private Button rating_1_0;
    private Button rating_1_1;
    private Button rating_1_2;
    private Button rating_1_3;
    private Button rating_1_4;
    private Button rating_1_5;

    private Button rating_2_0;
    private Button rating_2_1;
    private Button rating_2_2;
    private Button rating_2_3;
    private Button rating_2_4;
    private Button rating_2_5;

    private Button rating_3_0;
    private Button rating_3_1;
    private Button rating_3_2;
    private Button rating_3_3;
    private Button rating_3_4;
    private Button rating_3_5;

    private Button rating_4_0;
    private Button rating_4_1;
    private Button rating_4_2;
    private Button rating_4_3;
    private Button rating_4_4;
    private Button rating_4_5;

    private Button rating_5_0;
    private Button rating_5_1;
    private Button rating_5_2;
    private Button rating_5_3;
    private Button rating_5_4;
    private Button rating_5_5;

    private Button rating_6_0;
    private Button rating_6_1;
    private Button rating_6_2;
    private Button rating_6_3;
    private Button rating_6_4;
    private Button rating_6_5;

    private Button rating_7_0;
    private Button rating_7_1;
    private Button rating_7_2;
    private Button rating_7_3;
    private Button rating_7_4;
    private Button rating_7_5;

    private Button submit_btn;

    //report buttons
    private Button report_q1;
    private Button report_q2;
    private Button report_q3;
    private Button report_q4;
    private Button report_q5;
    private Button report_q6;
    private Button report_q7;

    private TextView question_text_1;
    private TextView question_text_2;
    private TextView question_text_3;
    private TextView question_text_4;
    private TextView question_text_5;
    private TextView question_text_6;
    private TextView question_text_7;
    private TextView rate_title_txt;
    private LinearLayout rate_layout;

    private TextView ranked_up_txt;

    private String json_arr_str;
    private JSONArray questions;
    private ArrayList<ArrayList<Button>> question_button_arrays;
    private HashMap<Integer,Button> selected_button;
    private HashMap<Button,Integer> all_buttons;
    private HashMap<Button,Integer> report_buttons;

    private int ques_arr = 0;
    private int button_elem = 0;
    private Button curr_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        done_btn = findViewById(R.id.done_btn);
        ranked_up_txt = findViewById(R.id.ranked_up_txt);
        rate_layout = findViewById(R.id.rate_layout);
        rate_title_txt = findViewById(R.id.rate_title_txt);
        win_or_lose = findViewById(R.id.win_or_lose);

        winnerUsername = findViewById(R.id.winnerUsername);
        loserUsername = findViewById(R.id.loserUsername);

        winnerAvatar = findViewById(R.id.winnerAvatar);
        loserAvatar = findViewById(R.id.loserAvatar);
        winnerName = findViewById(R.id.winnerName);
        loserName = findViewById(R.id.loserName);
        winnerScore = findViewById(R.id.winnerScore);
        loserScore = findViewById(R.id.loserScore);
        avg_reponse_time_txt = findViewById(R.id.avg_reponse_time);
        correctness_rate_txt = findViewById(R.id.correctness_rate);

        submit_btn = findViewById(R.id.submit_btn);

        //question textviews
        question_text_1 = findViewById(R.id.question_text_1);
        question_text_2 = findViewById(R.id.question_text_2);
        question_text_3 = findViewById(R.id.question_text_3);
        question_text_4 = findViewById(R.id.question_text_4);
        question_text_5 = findViewById(R.id.question_text_5);
        question_text_6 = findViewById(R.id.question_text_6);
        question_text_7 = findViewById(R.id.question_text_7);
        ArrayList<TextView> question_txts = new ArrayList<TextView>
                (Arrays.asList(question_text_1,question_text_2,question_text_3,question_text_4,
                        question_text_5,question_text_6,question_text_7));

        //report buttons
        report_q1 = findViewById(R.id.report_q1);
        report_q2 = findViewById(R.id.report_q2);
        report_q3 = findViewById(R.id.report_q3);
        report_q4 = findViewById(R.id.report_q4);
        report_q5 = findViewById(R.id.report_q5);
        report_q6 = findViewById(R.id.report_q6);
        report_q7 = findViewById(R.id.report_q7);


        //rating buttons
        rating_1_0 = findViewById(R.id.rating_1_0);
        rating_1_1 = findViewById(R.id.rating_1_1);
        rating_1_2 = findViewById(R.id.rating_1_2);
        rating_1_3 = findViewById(R.id.rating_1_3);
        rating_1_4 = findViewById(R.id.rating_1_4);
        rating_1_5 = findViewById(R.id.rating_1_5);
        ArrayList<Button> question_button_1 = new ArrayList<Button>
                (Arrays.asList(rating_1_0,rating_1_1,rating_1_2,rating_1_3,rating_1_4,rating_1_5));

        rating_2_0 = findViewById(R.id.rating_2_0);
        rating_2_1 = findViewById(R.id.rating_2_1);
        rating_2_2 = findViewById(R.id.rating_2_2);
        rating_2_3 = findViewById(R.id.rating_2_3);
        rating_2_4 = findViewById(R.id.rating_2_4);
        rating_2_5 = findViewById(R.id.rating_2_5);
        ArrayList<Button> question_button_2 = new ArrayList<Button>
                (Arrays.asList(rating_2_0,rating_2_1,rating_2_2,rating_2_3,rating_2_4,rating_2_5));

        rating_3_0 = findViewById(R.id.rating_3_0);
        rating_3_1 = findViewById(R.id.rating_3_1);
        rating_3_2 = findViewById(R.id.rating_3_2);
        rating_3_3 = findViewById(R.id.rating_3_3);
        rating_3_4 = findViewById(R.id.rating_3_4);
        rating_3_5 = findViewById(R.id.rating_3_5);
        ArrayList<Button> question_button_3 = new ArrayList<Button>
                (Arrays.asList(rating_3_0,rating_3_1,rating_3_2,rating_3_3,rating_3_4,rating_3_5));

        rating_4_0 = findViewById(R.id.rating_4_0);
        rating_4_1 = findViewById(R.id.rating_4_1);
        rating_4_2 = findViewById(R.id.rating_4_2);
        rating_4_3 = findViewById(R.id.rating_4_3);
        rating_4_4 = findViewById(R.id.rating_4_4);
        rating_4_5 = findViewById(R.id.rating_4_5);
        ArrayList<Button> question_button_4 = new ArrayList<Button>
                (Arrays.asList(rating_4_0,rating_4_1,rating_4_2,rating_4_3,rating_4_4,rating_4_5));

        rating_5_0 = findViewById(R.id.rating_5_0);
        rating_5_1 = findViewById(R.id.rating_5_1);
        rating_5_2 = findViewById(R.id.rating_5_2);
        rating_5_3 = findViewById(R.id.rating_5_3);
        rating_5_4 = findViewById(R.id.rating_5_4);
        rating_5_5 = findViewById(R.id.rating_5_5);
        ArrayList<Button> question_button_5 = new ArrayList<Button>
                (Arrays.asList(rating_5_0,rating_5_1,rating_5_2,rating_5_3,rating_5_4,rating_5_5));

        rating_6_0 = findViewById(R.id.rating_6_0);
        rating_6_1 = findViewById(R.id.rating_6_1);
        rating_6_2 = findViewById(R.id.rating_6_2);
        rating_6_3 = findViewById(R.id.rating_6_3);
        rating_6_4 = findViewById(R.id.rating_6_4);
        rating_6_5 = findViewById(R.id.rating_6_5);
        ArrayList<Button> question_button_6 = new ArrayList<Button>
                (Arrays.asList(rating_6_0,rating_6_1,rating_6_2,rating_6_3,rating_6_4,rating_6_5));

        rating_7_0 = findViewById(R.id.rating_7_0);
        rating_7_1 = findViewById(R.id.rating_7_1);
        rating_7_2 = findViewById(R.id.rating_7_2);
        rating_7_3 = findViewById(R.id.rating_7_3);
        rating_7_4 = findViewById(R.id.rating_7_4);
        rating_7_5 = findViewById(R.id.rating_7_5);
        ArrayList<Button> question_button_7 = new ArrayList<Button>
                (Arrays.asList(rating_7_0,rating_7_1,rating_7_2,rating_7_3,rating_7_4,rating_7_5));

        question_button_arrays = new ArrayList<ArrayList<Button>>
                (Arrays.asList(question_button_1,question_button_2,question_button_3,question_button_4,
                        question_button_5,question_button_6,question_button_7));

        //matches rating button to the question they pertain to
        all_buttons = new HashMap<Button,Integer>();
        for(ques_arr = 0 ; ques_arr < question_button_arrays.size(); ques_arr++) {
            for(button_elem = 0; button_elem < question_button_arrays.get(ques_arr).size(); button_elem++) {
                curr_btn =  question_button_arrays.get(ques_arr).get(button_elem);
                all_buttons.put(curr_btn,ques_arr);
            }
        }

        //matches report button to the question they pertain to
        report_buttons = new HashMap<Button,Integer>();
        report_buttons.put(report_q1,0);
        report_buttons.put(report_q2,1);
        report_buttons.put(report_q3,2);
        report_buttons.put(report_q4,3);
        report_buttons.put(report_q5,4);
        report_buttons.put(report_q6,5);
        report_buttons.put(report_q7,6);

        //stores the rating button selected for corresponding question
        selected_button = new HashMap<Integer,Button>();


        for (Map.Entry<Button, Integer> entry : all_buttons.entrySet())
        {
            entry.getKey().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int question_num = all_buttons.get(((Button) view));
                    Button prev_selection = selected_button.get(question_num);
                    if(prev_selection != null) {
                        //reset previously pressed button to initial colour
                        prev_selection.setBackgroundTintList(ScoreActivity.this.getResources().getColorStateList(getOriginalColor(prev_selection), null));
                    }
                    //set selected button to yellow and add it to selected ratings
                    ((Button) view).setBackgroundTintList(ScoreActivity.this.getResources().getColorStateList(R.color.colorAccent, null));
                    selected_button.put(question_num,((Button) view));

                }
            });
        }

        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SocketHandler.setDisconnected(false);
                finish();

            }
        });

        Intent starting_intent = getIntent();
        extras = starting_intent.getExtras();

        if(extras.containsKey("questions")){
            json_arr_str = starting_intent.getStringExtra("questions");
            try {
                questions = new JSONArray(json_arr_str);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //set question textviews to in-game questions
            for(int i =0 ; i < question_txts.size(); i++)
            {
                try {
                    question_txts.get(i).setText(questions.getJSONObject(i).getString("question_text"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //return all ratings in hashmap
                for (Map.Entry<Integer, Button> entry : selected_button.entrySet()) {
                    String q_id = null;
                    System.out.println(entry.getKey() + "/" + entry.getValue());
                    if (entry.getKey() != null) {
                        try {
                            q_id = questions.getJSONObject(entry.getKey()).getString("_id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            new OkHttpTask().execute(OkHttpTask.UPDATE_QUESTION_RATING, q_id, entry.getValue().getText().toString()).get();
                        } catch (InterruptedException e) {
                        } catch (ExecutionException e) {
                        }
                    }

                }
                rate_title_txt.setVisibility(View.GONE);
                rate_layout.setVisibility(View.GONE);
                Snackbar.make(findViewById(android.R.id.content), "Thanks! Your feedback has been recorded.", Snackbar.LENGTH_LONG)
                        .show();
            }
        });

        for (Map.Entry<Button, Integer> entry : report_buttons.entrySet())
        {
            int entry_val =entry.getValue();
            entry.getKey().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        //prompt the user with an alert dialog directed towards specific question before sending report
                        promptReport((Button) view,questions.getJSONObject(report_buttons.get(((Button) view))).getString("question_text"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        if(extras.containsKey("player_score")){
            player_score = starting_intent.getIntExtra("player_score",999);
        }
        if(extras.containsKey("opponent_score")){
            opponent_score =  starting_intent.getIntExtra("opponent_score",999);
        }
        //won or tied game
        won_game = player_score >= opponent_score;
        winnerScore.setText((won_game ? player_score:opponent_score) + " pts");
        loserScore.setText((!won_game ? player_score:opponent_score) + " pts");

        if(extras.containsKey("leaderboard_name")){
            player_username = starting_intent.getStringExtra("leaderboard_name");
        }
        if(extras.containsKey("opponent_leaderboard_name")){
            opponent_username =  starting_intent.getStringExtra("opponent_leaderboard_name");
        }
        won_game = player_score >= opponent_score;
        winnerUsername.setText(won_game ? player_username:opponent_username);
        loserUsername.setText(!won_game ? player_username:opponent_username);


        if(extras.containsKey("player_name")){
            player_name = starting_intent.getStringExtra("player_name");
        }
        if(extras.containsKey("opponent_name")){
            opponent_name = starting_intent.getStringExtra("opponent_name");
        }

        winnerName.setText(won_game ? player_name:opponent_name);
        loserName.setText(!won_game ? player_name:opponent_name);

        if(extras.containsKey("player_rank")){
            player_avatar = starting_intent.getIntExtra("player_rank",999);
        }

        if(extras.containsKey("opponent_rank")){
            opponent_avatar = starting_intent.getIntExtra("opponent_rank",999);
        }

        if(won_game) {
            setAvatar(winnerAvatar, player_avatar);
            setAvatar(loserAvatar, opponent_avatar);
        } else {
            setAvatar(winnerAvatar, opponent_avatar);
            setAvatar(loserAvatar, player_avatar);
        }

        if(extras.containsKey("course_subject")){
            course_subject = starting_intent.getStringExtra("course_subject");
        }
        if(extras.containsKey("course_number")){
            course_number = starting_intent.getIntExtra("course_number",0);
        }
        if(extras.containsKey("response_time")){
            total_response_time = starting_intent.getDoubleExtra("response_time",0);
        }
        if(extras.containsKey("num_correct")){
            total_num_correct =  starting_intent.getIntExtra("num_correct",999);
        }

        //evaulate per-question response times and per-game correctness rates
        new_response_time = (float) total_response_time /TOTAL_QUESTIONS;
        new_num_correct = (float) total_num_correct / TOTAL_QUESTIONS;

        avg_reponse_time_txt.setText(String.format("%.2f", new_response_time) + "s");
        correctness_rate_txt.setText(String.format("%.1f", new_num_correct * 100) + "%");

        //evaulation of win or lose and progress gained
        if(player_score == opponent_score) {
            win_or_lose.setText("TIED!");
            level_progress = 2;
        }
        else if(won_game){
            win_or_lose.setText("WON!");
            level_progress = 3;
        }
        else {
            win_or_lose.setText("LOST!");
            level_progress = 0;
        }

        try {
            //update the stat
            //if the stat does not exist, make a new one
            if(updateStat(new_num_correct,new_response_time,level_progress,
            course_subject,course_number) != 0) {
                //add the stat first, then try to update it
                addStatFirst(new_num_correct,new_response_time,level_progress,
                course_subject,course_number);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
    get in-game avatar
     */
    protected void setAvatar(ImageView avatar,int avatar_val){
        Log.i("avatar o", Integer.toString(opponent_avatar));
        switch(avatar_val % 6) {
            case 0:
                avatar.setImageResource(R.drawable.penguin_avatar);
                break;
            case 1:
                avatar.setImageResource(R.drawable.mountain_avatar);
                break;
            case 2:
                avatar.setImageResource(R.drawable.rocket_avatar);
                break;
            case 3:
                avatar.setImageResource(R.drawable.frog_avatar);
                break;
            case 4:
                avatar.setImageResource(R.drawable.thunderbird_avatar);
                break;
            case 5:
                avatar.setImageResource(R.drawable.cupcake_avatar);
                break;
        }
    }

    /*
    get the original colour of a rating button
     */
    protected int getOriginalColor(Button button){
        int contents = Integer.parseInt(button.getText().toString());
        switch(contents %6) {
            case 0:
                return R.color.blueshade0;
            case 1:
                return R.color.blueshade1;
            case 2:
                return R.color.blueshade2;
            case 3:
                return R.color.blueshade3;
            case 4:
                return R.color.blueshade4;
            case 5:
                return R.color.blueshade5;
        }
        return 0;
    }

    @Override
    public void onBackPressed() {
        SocketHandler.setDisconnected(false);
        finish();
    }
    /*
    update the user stat
     */
    protected int updateStat(float correctness_rate,float response_time,int level_progress,
                               String course_subject,int course_number) throws JSONException {
        String json_stat_http = null;
        try {
            json_stat_http = new OkHttpTask().execute(OkHttpTask.UPDATE_USER_STAT, course_subject, Integer.toString(course_number),Float.toString(correctness_rate),Float.toString(response_time),Integer.toString(level_progress)).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(json_stat_http == null)
        {
            return 400;
        }
        if(json_stat_http.equals("400") || json_stat_http.equals("409"))
        {
            return Integer.parseInt(json_stat_http);
        }

        JSONObject ques = new JSONObject(json_stat_http);
        //if the user ranked up, display it on the screen
        if(ques.getBoolean("ranked_up")) {
            ranked_up_txt.setVisibility(View.VISIBLE);
        }
        return 0;
    }

    /*
    Make a stat first, then update it with the information. By default, the stat adding
    API also updates the stat with the initial information
     */
    protected void addStatFirst(float correctness_rate,float response_time,int level_progress,
                                String course_subject,int course_number) {

        try {
            new OkHttpTask().execute(OkHttpTask.ADD_STAT_TO_USER, course_subject, Integer.toString(course_number),Float.toString(correctness_rate),Float.toString(response_time),Integer.toString(level_progress)).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    /*
    Ask the user whether they want to report a question and proceed to set it
    as reported on confirmation.
     */
    private void promptReport(final Button report_btn, String question_txt)
    {
        final int question_num = report_buttons.get(report_btn);
        AlertDialog.Builder builder = new AlertDialog.Builder(ScoreActivity.this);
        builder.setMessage("Are you sure you want to report Question " + (question_num + 1) + " ("
                            + question_txt + ")?")
                .setCancelable(true)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String json_rate_http = null;
                        try {
                            json_rate_http = new OkHttpTask().execute(OkHttpTask.SET_QUESTION_REPORTED_STATUS, questions.getJSONObject(question_num).getString("_id"), "true").get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            json_rate_http = null;
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                            json_rate_http = null;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            json_rate_http = null;
                        }
                        if(json_rate_http != null) {
                            Snackbar.make(findViewById(android.R.id.content), "Question Reported", Snackbar.LENGTH_LONG)
                                    .setActionTextColor(Color.YELLOW)
                                    .show();

                            report_btn.setVisibility(GONE);
                        }
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }
}
