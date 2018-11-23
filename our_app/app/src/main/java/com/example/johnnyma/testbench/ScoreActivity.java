package com.example.johnnyma.testbench;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

    private String course_subject;
    private String course_number;

    private int player_score = 0;
    private int opponent_score = 0;
    private String player_name;
    private String opponent_name;
    private int player_avatar;
    private int opponent_avatar;
    private int player_rank;
    private int opponent_rank;
    private Bundle extras;
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
            course_number = starting_intent.getStringExtra("course_number");
        }

        if(player_score > opponent_score){
            win_or_lose.setText("YOU WIN");
        }
        else if (player_score < opponent_score){
            win_or_lose.setText("YOU LOSE");
        }
        else{
            win_or_lose.setText("TIE");
        }

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
        new OkHttpTask().execute("UPDATE_USER_STAT", course_subject, course_number,
                "1", "1", "10"); //TODO change correctness rate and progress
        finish();
    }
}