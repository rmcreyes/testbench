package com.example.johnnyma.testbench;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.example.johnnyma.testbench.MatchmakingService.LocalBinder;
import com.github.nkzawa.socketio.client.Socket;

public class MatchmakingActivity extends AppCompatActivity implements StartDialog.StartDialogListener {
    public static final String TAG = "MatchmakingActivity"; //tag for sending info through intents
    private String courseID;
    private MatchmakingService my_service;
    private boolean is_bound = false;
    private int timeout = 0; //timeout counter used in Runnable runnable
    private Handler handler = new Handler();
    private String usernamePlayer;
    // these must be stored/obtained somewhere,
    private int playerRank = 99;
    private Socket socket;
    private String opponentUsername;
    private int opponentRank;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(my_service.isMatchFound()){
                opponentUsername = my_service.opponentUsername;
                opponentRank = my_service.opponentRank;
                showStartDialog();
                //exit match making activity and stop service
                //TODO Dont just stop it, destroy it
                //stopService(new Intent(getApplicationContext(),MatchmakingService.class));
                //finish();
            }
            else if(timeout >= 150){ //if timeout > 150 then that means 15 seconds has passed
                stopService(new Intent(getApplicationContext(),MatchmakingService.class));
                Toast.makeText(getApplicationContext(), "Matchmaking has timed out after 15 seconds", Toast.LENGTH_SHORT).show();
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
        usernamePlayer = starting_intent.getStringExtra("name");

        socket = SocketHandler.getSocket();

        //start the service
        Intent service_intent = new Intent(this, MatchmakingService.class);
        service_intent.putExtra(TAG, this.courseID);
        service_intent.putExtra("name", usernamePlayer);
        startService(service_intent);
        boolean bounded = bindService(service_intent, my_connection, Context.BIND_AUTO_CREATE);

        //check if service is bound
        //String meme = Boolean.toString(bounded);
        //Toast.makeText(this, meme, Toast.LENGTH_LONG).show();

        //make a handler to run and check if match has been found
        handler.postDelayed(runnable, 500);
    }

    //cancel match TODO
    public void cancelButton(View view){
        //String num = my_service.getRandomNumber();
        //Toast.makeText(this, num , Toast.LENGTH_SHORT).show();

        my_service.setFound();
        //stop the handler and stop the service
        /*
        handler.removeCallback(runnable)
        stopService(new Intent(this, MatchmakingService.class));
        finish();
        */
    }

    private ServiceConnection my_connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LocalBinder binder = (LocalBinder) iBinder;
            my_service = binder.getService();
            is_bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            is_bound = false;
        }
    };

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
            intent.putExtra("player_name", usernamePlayer);
            intent.putExtra("player_rank", playerRank);
            intent.putExtra("opponent_name", opponentUsername);
            intent.putExtra("opponent_name", opponentUsername);
            intent.putExtra("opponent_rank", opponentRank);
            intent.putExtra("questions", my_service.questions.toString());
            // add intent extras necessary for game
            startActivity(intent);
        } else {
            //cancel
            socket.disconnect();
            Intent intent = new Intent(MatchmakingActivity.this, CourseSelectActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
