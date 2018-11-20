package com.example.johnnyma.testbench;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.johnnyma.testbench.MatchmakingService.LocalBinder;
import com.github.nkzawa.socketio.client.Socket;

public class MatchmakingActivity extends AppCompatActivity {
    public static final String TAG = "MatchmakingActivity"; //tag for sending info through intents
    private String courseID;
    private MatchmakingService my_service;
    private boolean is_bound = false;
    private int timeout = 0; //timeout counter used in Runnable runnable
    private Button startButton;
    private Button cancelButton;
    private TextView userName_opponent;
    private TextView rank_opponent;
    private ImageView avatar_opponent;
    private Dialog startDialog;
    private Handler handler = new Handler();
    private String userName_player;
    // these must be stored/obtained somewhere,
    private int rank_player;
    private int avatar_player;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(my_service.isMatchFound()){
                showStartDialog(my_service.mSocket, my_service.opponentUsername, my_service.opponentRank, my_service.opponentPic);

                //exit match making activity and stop service
                //TODO Dont just stop it, destroy it
                stopService(new Intent(getApplicationContext(),MatchmakingService.class));
                finish();
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
        userName_player = starting_intent.getStringExtra("name");

        //start the service
        Intent service_intent = new Intent(this, MatchmakingService.class);
        service_intent.putExtra(TAG, this.courseID);
        service_intent.putExtra("name", userName_player);
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

    public void showStartDialog (final Socket socket, final String opponentUserName, final int opponentRank, final int opponentProfile) {
        startDialog = new Dialog(MatchmakingActivity.this);
        startDialog.setContentView(R.layout.dialog_start_game);
        startButton = startDialog.findViewById(R.id.start_btn);
        cancelButton = startDialog.findViewById(R.id.cancel_btn);
        userName_opponent = startDialog.findViewById(R.id.user_name);
        userName_opponent.setText(opponentUserName);
        rank_opponent = startDialog.findViewById(R.id.rank_field);
        rank_opponent.setText("Rank " + opponentRank);
        avatar_opponent = startDialog.findViewById(R.id.avatar);
        switch(opponentProfile) {
            case 0: avatar_opponent.setImageResource(R.drawable.penguin_avatar);
            case 1: avatar_opponent.setImageResource(R.drawable.mountain_avatar);
            case 2: avatar_opponent.setImageResource(R.drawable.rocket_avatar);
            case 3: avatar_opponent.setImageResource(R.drawable.frog_avatar);
            case 4: avatar_opponent.setImageResource(R.drawable.thunderbird_avatar);
            case 5: avatar_opponent.setImageResource(R.drawable.cupcake_avatar);
        }
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start game
                Intent intent = new Intent(MatchmakingActivity.this, GameplayActivity.class);
                intent.putExtra("course", courseID);
                intent.putExtra("player_name", userName_player);
                intent.putExtra("player_avatar", avatar_player);
                intent.putExtra("player_rank", rank_player);
                intent.putExtra("opponent_name", opponentUserName);
                intent.putExtra("opponent_avatar", opponentProfile);
                intent.putExtra("opponent_rank", opponentRank);
                intent.putExtra("questions", my_service.questions.toString());
                // add intent extras necessary for game
                startActivity(intent);
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socket.disconnect();
                startDialog.dismiss();
                Intent intent = new Intent(MatchmakingActivity.this, CourseSelectActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
