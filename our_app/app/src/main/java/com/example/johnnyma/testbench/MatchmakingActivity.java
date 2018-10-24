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
import com.example.johnnyma.testbench.matchmakingService.LocalBinder;

import java.net.URISyntaxException;



public class MatchmakingActivity extends AppCompatActivity {
    public static final String TAG = "MatchmakingActivity"; //tag for sending info through intents
    private String courseID;
    matchmakingService my_service;
    boolean is_bound = false;
    int timeout = 0; //timeout counter used in Runnable runnable

    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(my_service.isMatch_found()){
                //TODO start the activity
                //Toast.makeText(getApplicationContext(), "Match Found", Toast.LENGTH_LONG).show();
                
                //exit match making activity and stop service
                //TODO DONT JUST STOP IT. DESTROY IT
                stopService(new Intent(getApplicationContext(),matchmakingService.class));
                finish();
            }
            else if(timeout >= 150){ //if timeout > 150 then that means 15 seconds has passed
                stopService(new Intent(getApplicationContext(),matchmakingService.class));
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
        this.courseID = starting_intent.getStringExtra(CourseActivity.TAG).replaceAll("\\s+","").toUpperCase();

        //start the service
        Intent service_intent = new Intent(this, matchmakingService.class);
        service_intent.putExtra(TAG, this.courseID);
        startService(service_intent);
        boolean bounded = bindService(service_intent, my_connection, Context.BIND_AUTO_CREATE);

        //check if service is bound
        //String meme = Boolean.toString(bounded);
        //Toast.makeText(this, meme, Toast.LENGTH_LONG).show();

        //make a handler to run and check if match has been found
        handler.postDelayed(runnable, 500);
    }

    //cancel match TODO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public void cancelButton(View view){

        //String num = my_service.getRandomNumber();
        //Toast.makeText(this, num , Toast.LENGTH_SHORT).show();

        my_service.set_found();
        //stop the handler and stop the service
        /*
        handler.removeCallback(runnable)
        stopService(new Intent(this, matchmakingService.class));
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
    // TODO disable back button cuz fuck that


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
