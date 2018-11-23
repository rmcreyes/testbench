package com.example.johnnyma.testbench;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class ScoreActivity extends AppCompatActivity {

    private TextView win_or_lose;
    private TextView playerScore;
    private TextView opponentScore;
    private TextView playerName;
    private TextView opponentName;
    private ImageView playerAvatar;
    private ImageView opponentAvatar;
    private TextView playerRank;
    private TextView opponentRank;
    private TextView avg_reponse_time_txt;
    private TextView correctness_rate_txt;



    private String course_subject;
    private int course_number;

    private int player_score = 0;
    private int opponent_score = 0;
    private String player_name;
    private String opponent_name;
    private int player_avatar;
    private int opponent_avatar;
    private int player_rank;
    private int opponent_rank;
    private double total_response_time;
    private int total_num_correct;
    private Bundle extras;
    private float new_response_time;
    private float new_num_correct;
    private int TOTAL_QUESTIONS = 7;
    //private int won_game;
    private int level_progress =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        win_or_lose = findViewById(R.id.win_or_lose);
        playerAvatar = findViewById(R.id.playerAvatar);
        opponentAvatar = findViewById(R.id.opponentAvatar);
        playerName = findViewById(R.id.playerName);
        opponentName = findViewById(R.id.opponentName);
        playerScore = findViewById(R.id.playerScore);
        opponentScore = findViewById(R.id.opponentScore);
        playerRank = findViewById(R.id.playerRank);
        opponentRank = findViewById(R.id.opponentRank);
        avg_reponse_time_txt = findViewById(R.id.avg_reponse_time);
        correctness_rate_txt = findViewById(R.id.correctness_rate);

        Intent starting_intent = getIntent();
        extras = starting_intent.getExtras();

        if(extras.containsKey("player_score")){
            player_score = starting_intent.getIntExtra("player_score",999);
            playerScore.setText("Score: " + player_score);
        }
        if(extras.containsKey("opponent_score")){
            opponent_score =  starting_intent.getIntExtra("opponent_score",999);
            opponentScore.setText("Score: " + opponent_score);
        }

        if(extras.containsKey("player_rank")){
            player_rank =  starting_intent.getIntExtra("player_rank",999);
            playerRank.setText("Rank: " + player_rank);
        }
        if(extras.containsKey("opponent_rank")){
            opponent_rank = starting_intent.getIntExtra("opponent_rank",999);
            opponentRank.setText("Rank: " + opponent_rank);
        }

        if(extras.containsKey("player_name")){
            player_name = starting_intent.getStringExtra("player_name");
            playerName.setText(player_name);
        }
        if(extras.containsKey("opponent_name")){
            opponent_name = starting_intent.getStringExtra("opponent_name");
            opponentName.setText(opponent_name);
        }

        if(extras.containsKey("player_avatar")){
            player_avatar = starting_intent.getIntExtra("player_avatar",999);
            setPlayerAvatar();
        }
        if(extras.containsKey("opponent_avatar")){
            opponent_avatar = starting_intent.getIntExtra("opponent_avatar",999);
            setOpponentAvatar();
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

        //evaulate new percentages:
        new_response_time = (float) total_response_time /TOTAL_QUESTIONS;
        new_num_correct = (float) total_num_correct / TOTAL_QUESTIONS;


//        new_response_time = (float)0.67;
//        new_num_correct = (float)0.67;

        //evaulation of win or lose
        if(player_score > opponent_score){
            win_or_lose.setText("YOU WIN");
            level_progress = 3;
        }
        else if (player_score < opponent_score){
            win_or_lose.setText("YOU LOSE");
            level_progress = 0;
        }
        else{
            win_or_lose.setText("TIE");
            level_progress = 2;

        }


        avg_reponse_time_txt.setText("The avg resp time is: " + new_response_time);
        correctness_rate_txt.setText("The correctness rate is: " + new_num_correct);

        //if the stat does not exist, make a new one

        if(updateStat(new_num_correct,new_response_time,level_progress,
        course_subject,course_number) != 0) {
            //add the stat first, then try to update it
            addStatFirst(new_num_correct,new_response_time,level_progress,
            course_subject,course_number);
        }


//        String json_stat_http = null;
//        try {
//            json_stat_http = new OkHttpTask().execute(OkHttpTask.GET_USER_STAT, course_subject, Integer.toString(course_number)).get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }



    }


    protected void setPlayerAvatar(){
        Log.i("avatar p", Integer.toString(player_avatar));
        switch(player_avatar) {
            case 0:
                playerAvatar.setImageResource(R.drawable.penguin_avatar);
                break;
            case 1:
                playerAvatar.setImageResource(R.drawable.mountain_avatar);
                break;
            case 2:
                playerAvatar.setImageResource(R.drawable.rocket_avatar);
                break;
            case 3:
                playerAvatar.setImageResource(R.drawable.frog_avatar);
                break;
            case 4:
                playerAvatar.setImageResource(R.drawable.thunderbird_avatar);
                break;
            case 5:
                playerAvatar.setImageResource(R.drawable.cupcake_avatar);
                break;
        }
    }

    protected void setOpponentAvatar(){
        Log.i("avatar o", Integer.toString(opponent_avatar));
        switch(opponent_avatar) {
            case 0:
                opponentAvatar.setImageResource(R.drawable.penguin_avatar);
                break;
            case 1:
                opponentAvatar.setImageResource(R.drawable.mountain_avatar);
                break;
            case 2:
                opponentAvatar.setImageResource(R.drawable.rocket_avatar);
                break;
            case 3:
                opponentAvatar.setImageResource(R.drawable.frog_avatar);
                break;
            case 4:
                opponentAvatar.setImageResource(R.drawable.thunderbird_avatar);
                break;
            case 5:
                opponentAvatar.setImageResource(R.drawable.cupcake_avatar);
                break;
        }
    }

    public void done(View view){
        Intent intent = new Intent(ScoreActivity.this, CourseSelectActivity.class);
        startActivity(intent);
    }

    protected int updateStat(float correctness_rate,float response_time,int level_progress,
                               String course_subject,int course_number) {
        String json_stat_http = null;
        try {
            json_stat_http = new OkHttpTask().execute(OkHttpTask.UPDATE_USER_STAT, course_subject, Integer.toString(course_number),Float.toString(correctness_rate),Float.toString(response_time),Integer.toString(level_progress)).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Log.d("BELHTDFG","string 1: "+ json_stat_http);
        Log.d("BELHTDFG","course_subject: "+ course_subject);
        Log.d("BELHTDFG","course_number: "+ course_number);
        if(json_stat_http.equals("400") || json_stat_http.equals("409"))
        {
            return Integer.parseInt(json_stat_http);
        }
        return 0;
    }

    protected void addStatFirst(float correctness_rate,float response_time,int level_progress,
                                String course_subject,int course_number) {
        String json_stat_http = null;
        try {
            json_stat_http = new OkHttpTask().execute(OkHttpTask.ADD_STAT_TO_USER, course_subject, Integer.toString(course_number),Float.toString(correctness_rate),Float.toString(response_time),Integer.toString(level_progress)).get();
        } catch (InterruptedException e) {
            json_stat_http = null;
            e.printStackTrace();
        } catch (ExecutionException e) {
            json_stat_http = null;
            e.printStackTrace();
        }

        Log.d("BELHTDFG","string 2: "+ json_stat_http);

    }


}
