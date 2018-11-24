package com.example.johnnyma.testbench;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    private String playerUsername;


    private TextView textview;
    private GifImageView loading_gif;
    private Button cancel_btn;

    private JSONArray questions;

    // these must be stored/obtained somewhere,
    private int playerRank;
    private Socket socket;
    private String opponentUsername;
    private int opponentRank;
    {
        try {
            socket = IO.socket("http://40.78.64.46:3300/");
            SocketHandler.setSocket(socket);
        } catch (URISyntaxException e){}
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(match_found){
                textview.setVisibility(View.INVISIBLE);
//                loading_gif.setVisibility(View.INVISIBLE);
                cancel_btn.setVisibility(View.INVISIBLE);
                showStartDialog();

            }
            else if(timeout >= 150){ //if timeout > 150 then that means 15 seconds has passed
                Toast.makeText(getApplicationContext(), "Matchmaking has timed out after 15 seconds", Toast.LENGTH_SHORT).show();
                socket.disconnect();
                finish();
            }
            else {
                timeout++;
                handler.postDelayed(this, 100);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchmaking);
        Intent starting_intent = getIntent();
        courseID = starting_intent.getStringExtra(CourseSelectActivity.TAG).replaceAll("\\s+","").toUpperCase();
        playerUsername = starting_intent.getStringExtra("name");
        playerRank = starting_intent.getIntExtra("rank", 1);

        textview = findViewById(R.id.textview);
//        loading_gif = findViewById(R.id.loading_gif);
        cancel_btn = findViewById(R.id.cancelButton);
        SocketHandler.setSocket(socket);
        socket.connect();
        queueForGame();

        socket.on("game_made", onGameMade);
        socket.on("get_json_opponent", getJSONOpponent);
        socket.on("broadcast_leave", opponentLeft);

        handler.postDelayed(runnable, 500);
    }

    public void cancelButton(View view){
        socket.disconnect();

        finish();
    }


    public void showStartDialog() {
        StartDialog startDialog = new StartDialog();
        Bundle args = new Bundle();
        args.putString("opponent_username", opponentUsername);
        args.putString("opponent_rank", Integer.toString(opponentRank));
        startDialog.setArguments(args);
        startDialog.show(getSupportFragmentManager(), "start game dialog");
    }

    @Override
    public void startOrCancel(boolean start) {
        Toast.makeText(getApplicationContext(), "start or cancel pressed", Toast.LENGTH_SHORT).show();
        if (start) {
            // start game
            Toast.makeText(getApplicationContext(), "start pressed", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MatchmakingActivity.this, GameplayActivity.class);
            intent.putExtra("course", courseID);
            intent.putExtra("player_name", playerUsername);
            intent.putExtra("player_rank", playerRank);
            intent.putExtra("opponent_name", opponentUsername);
            intent.putExtra("opponent_name", opponentUsername);
            intent.putExtra("opponent_rank", opponentRank);
            intent.putExtra("questions", questions.toString());
            // add intent extras necessary for game
            finish();
            startActivity(intent);
        } else {
            //cancel
            socket.disconnect();
            finish();
        }
    }


    public void queueForGame() {
        JSONObject info = new JSONObject();
        try {
            info.put("username", playerUsername);
            info.put("course_subject",  courseID.substring(0, 4));
            info.put("course_number", Integer.valueOf(courseID.substring(4)));
            info.put("rank", playerRank);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("queue_for_game", info.toString());
    }

    public void sendJSONOpponent() {
        JSONObject info = new JSONObject();
        try {
            info.put("username", playerUsername);
            info.put("course_subject",  courseID.substring(0, 4));
            info.put("course_number", Integer.valueOf(courseID.substring(4)));
            info.put("rank", playerRank);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("send_json_opponent", info.toString());
    }

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



    public Emitter.Listener getJSONOpponent = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable(){
                @Override
                public void run(){
                    try {
                        JSONObject data = new JSONObject((String) args[0]);
                        opponentUsername = data.getString("username");
                        opponentRank = Integer.parseInt(data.getString("rank"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    match_found = true;
                }
            });
        }
    };

    @Override
    public void onBackPressed() {
        socket.disconnect();
        finish();
    }
    public Emitter.Listener opponentLeft = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            AlertDialog.Builder builder = new AlertDialog.Builder(MatchmakingActivity.this);
            builder.setMessage("You opponent disconnected. You will be brought back to the main page.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            socket.disconnect();
                            finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    };

}
