 package com.example.johnnyma.testbench;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Random;

public class MatchmakingService extends Service {
    private String name;
    private int rank;
    private String opponentUsername;
    private int opponentRank;
    private JSONArray questions;
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://40.78.64.46:3300/");
            SocketHandler.setSocket(mSocket);
        } catch (URISyntaxException e){}
    }

    private final IBinder binder = new LocalBinder();
    //private final IBinder socketBinder = new SocketBinder();
    private String courseID;
    private boolean match_found = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return binder;
    }

    //start the socket
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSocket.connect();
        courseID = intent.getStringExtra(MatchmakingActivity.TAG);
        rank = intent.getIntExtra("rank",1);
        name = intent.getStringExtra("name");
        Random rand = new Random();


        Toast.makeText(MatchmakingService.this, "name: " + name + "\n rank: " + rank, Toast.LENGTH_LONG).show();
        queueForGame();

        mSocket.on("game_made", onGameMade);
        mSocket.on("get_json_opponent", getJSONOpponent);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mSocket.disconnect();
        super.onDestroy();
    }

    //binder
    public class LocalBinder extends Binder {
        MatchmakingService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MatchmakingService.this;
        }
    }

    protected String getOpponentUsername() {
        return opponentUsername;
    }
    protected int getOpponentRank() {
        return opponentRank;
    }
    protected JSONArray getQuestions() {
        return questions;
    }
    public boolean isMatchFound(){
        return match_found;
    }

    public void setFound(){
        this.match_found = true;
    }
    // JSON stuff
    public void queueForGame() {
        JSONObject info = new JSONObject();
        try {
            info.put("username", name);
            info.put("course_subject",  courseID.substring(0, 4));
            info.put("course_number", Integer.valueOf(courseID.substring(4)));
            info.put("rank", rank);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("queue_for_game", info.toString());
    }

    public void sendJSONOpponent() {
        JSONObject info = new JSONObject();
        try {
            info.put("username", name);
            info.put("course_subject",  courseID.substring(0, 4));
            info.put("course_number", Integer.valueOf(courseID.substring(4)));
            info.put("rank", rank);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("send_json_opponent", info.toString());
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
                        return;
                    }
                    match_found = true;
                }
            });
        }
    };

}