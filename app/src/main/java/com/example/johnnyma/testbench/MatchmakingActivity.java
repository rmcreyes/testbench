package com.example.johnnyma.testbench;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;


public class MatchmakingActivity extends AppCompatActivity {
    public static final String TAG = "MatchmakingActivity"; //tag for sending info through intents
    private String courseID;

    private Socket mSocket;
    {
        try{
            mSocket = IO.socket("http://chat.socket.io");
        } catch (URISyntaxException e){}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchmaking);
        Intent starting_intent = getIntent();
        this.courseID = starting_intent.getStringExtra(CourseActivity.TAG).replaceAll("\\s+","").toUpperCase();
        mSocket.connect();
    }
}
