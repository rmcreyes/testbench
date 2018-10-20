package com.example.johnnyma.testbench;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.example.johnnyma.testbench.matchmakingService.LocalBinder;

import java.net.URISyntaxException;


public class MatchmakingActivity extends AppCompatActivity {
    public static final String TAG = "MatchmakingActivity"; //tag for sending info through intents
    private String courseID;
    matchmakingService my_service;
    boolean is_bound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchmaking);
        Intent starting_intent = getIntent();
        this.courseID = starting_intent.getStringExtra(CourseActivity.TAG).replaceAll("\\s+","").toUpperCase();
        Intent service_intent = new Intent(this, matchmakingService.class);
        bindService(service_intent, my_connection, Context.BIND_AUTO_CREATE);
    }

    public void cancelButton(View view){
        String num = Integer.toString(my_service.getRandomNumber());
        Toast.makeText(this, num , Toast.LENGTH_SHORT).show();
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
}
