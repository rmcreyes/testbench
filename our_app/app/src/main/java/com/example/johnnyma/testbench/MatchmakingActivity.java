package com.example.johnnyma.testbench;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import pl.droidsonroids.gif.GifImageView;

public class MatchmakingActivity extends AppCompatActivity implements StartDialog.StartDialogListener {
    public static final String TAG = "MatchmakingActivity"; //tag for sending info through intents
    private String courseID;
    private boolean match_found = false;
    private int timeout = 0; //timeout counter used in Runnable runnable
    private Handler handler = new Handler();

    private String alias;
    private String username;


    private TextView textview;
    private GifImageView loading_gif;
    private Button cancel_btn;

    private JSONArray questions;

    // these must be stored/obtained somewhere,
    private int playerRank;
    private Socket socket;
    private String opponentAlias;
    private String opponentUsername;
    private int opponentRank;
    {
        try {
            socket = IO.socket("http://40.78.64.46:3300/");
            SocketHandler.setSocket(socket);
        } catch (URISyntaxException e){}
    }
    //Declare timer
    CountDownTimer cTimer = null;

    //start timer function
    void startTimer() {
        cTimer = new CountDownTimer(15000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                Toast.makeText(getApplicationContext(), "Matchmaking has timed out after 15 seconds", Toast.LENGTH_SHORT).show();
                socket.emit("stop_waiting");
                socket.disconnect();
                finish();
            }
        };
        cTimer.start();
    }


    //cancel timer
    void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchmaking);
        Intent starting_intent = getIntent();
        courseID = starting_intent.getStringExtra(CourseSelectActivity.TAG).replaceAll("\\s+","").toUpperCase();
        alias = starting_intent.getStringExtra("alias");
        username = starting_intent.getStringExtra("username");

        playerRank = starting_intent.getIntExtra("rank", 1);

        textview = findViewById(R.id.textview);
        loading_gif = findViewById(R.id.loading_gif);
        cancel_btn = findViewById(R.id.cancelButton);
        SocketHandler.setSocket(socket);
        socket.connect();
        queueForGame();

        socket.on("game_made", onGameMade);
        socket.on("get_json_opponent", getJSONOpponent);
        socket.on("broadcast_leave", opponentLeft);

        startTimer();
    }

    public void cancelButton(View view){
        cancelTimer();
        socket.emit("stop_waiting");
        socket.disconnect();
        finish();
    }


    public void showStartDialog() {
        StartDialog startDialog = new StartDialog();
        Bundle args = new Bundle();
        args.putString("opponent_alias", opponentAlias);
        args.putString("opponent_rank", Integer.toString(opponentRank));
        startDialog.setArguments(args);
        startDialog.show(getFragmentManager(), "start game dialog");
    }

    @Override
    public void startOrCancel(boolean start) {
        Toast.makeText(getApplicationContext(), "start or cancel pressed", Toast.LENGTH_SHORT).show();
        if (start) {
            // start game
            Toast.makeText(this, "start pressed", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MatchmakingActivity.this, GameplayActivity.class);
            intent.putExtra("course", courseID);
            intent.putExtra("alias", alias);
            intent.putExtra("leaderboard_name", username);
            intent.putExtra("player_rank", playerRank);
            intent.putExtra("opponent_alias", opponentAlias);
            intent.putExtra("opponent_leaderboard_name", opponentUsername);
            intent.putExtra("opponent_rank", opponentRank);
            intent.putExtra("questions", questions.toString());
            // add intent extras necessary for game
            finish();
            startActivity(intent);
        } else {
            //cancel
            cancelTimer();
            socket.emit("leave_early", "leave_early");
            socket.disconnect();
            finish();
        }
    }

    /*
     * sends user info to socket to matchmake with another player
     */
    public void queueForGame() {
        JSONObject info = new JSONObject();
        try {
            info.put("alias", alias);
            info.put("username", username);
            info.put("course_subject",  courseID.substring(0, 4));
            info.put("course_number", Integer.valueOf(courseID.substring(4)));
            info.put("rank", playerRank);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("queue_for_game", info.toString());
    }
    /*
     * sends user info to socket after matchmaking, to give opponent relevant info
     */
    public void sendJSONOpponent() {
        JSONObject info = new JSONObject();
        try {
            info.put("username", alias);
            info.put("course_subject",  courseID.substring(0, 4));
            info.put("course_number", Integer.valueOf(courseID.substring(4)));
            info.put("rank", playerRank);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("send_json_opponent", info.toString());
    }
    /*
     * receive questions from socket after match made
     */
    public Emitter.Listener onGameMade = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable(){
                @Override
                public void run() {
                    try {
                        questions = new JSONArray((String) args[0]);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendJSONOpponent();
                }
            });
        }
    };

    /*
     * receives opponent info in json format after match made
     */
    public Emitter.Listener getJSONOpponent = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable(){
                @Override
                public void run(){
                    try {
                        JSONObject data = new JSONObject((String) args[0]);
                        opponentAlias = data.getString("alias");
                        opponentUsername = data.getString("username");
                        opponentRank = Integer.parseInt(data.getString("rank"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    cancelTimer();
                    textview.setVisibility(View.INVISIBLE);
                    loading_gif.setVisibility(View.INVISIBLE);
                    cancel_btn.setVisibility(View.INVISIBLE);
                    showStartDialog();
                }
            });
        }
    };

    /*
     * if player chooses to exit, must properly cancel match
     */
    @Override
    public void onBackPressed() {
        cancelTimer();
        socket.disconnect();
        finish();
    }
    /*
     * if opponent left, must exit activity (no longer matched)
     */
    public Emitter.Listener opponentLeft = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            cancelTimer();
            SocketHandler.setDisconnected(true);
            socket.disconnect();
            finish();
        }
    };

}
