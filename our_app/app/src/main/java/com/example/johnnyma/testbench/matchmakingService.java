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

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;

public class matchmakingService extends Service {
    private String name;
    private int rank;
    private Socket mSocket;
    {
        try{
            mSocket = IO.socket("http://104.42.209.62:3300/");
        } catch (URISyntaxException e){}
    }

    private final IBinder binder = new LocalBinder();
    private String courseID;
    private boolean match_found = false;

    public matchmakingService(){
    }

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
        Random rand = new Random();
        ArrayList<String> names = new ArrayList<String>();
        names.add("Johnny");
        names.add("LightningMcQueen69");
        names.add("RobertoMartinCastroReyes");
        names.add("Andrea");
        names.add("PeenWeinerstein");
        names.add("MysteriousMongoose");

        name = names.get(rand.nextInt(6));
        rank = rand.nextInt(100) + 1;
        Toast.makeText(matchmakingService.this, "name: " + name + "\n rank: " + rank, Toast.LENGTH_LONG).show();
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

    //binder shit
    public class LocalBinder extends Binder {
        matchmakingService getService() {
            // Return this instance of LocalService so clients can call public methods
            return matchmakingService.this;
        }
    }

    public String getRandomNumber(){
        return this.courseID;
    }

    public boolean isMatch_found(){
        return match_found;
    }

    public void set_found(){
        this.match_found = true;
    }
    // JSON stuff
    public void queueForGame() {
        JSONObject info = new JSONObject();
        try {
            info.put("username", name);
            info.put("course", courseID);
            info.put("rank", rank);
            info.put("pic", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("queue_for_game", info.toString());
    }

    public void sendJSONOpponent() {
        JSONObject info = new JSONObject();
        try {
            info.put("username", name);
            info.put("course", courseID);
            info.put("rank", rank);
            info.put("pic", "0");
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
                    String username;
                    String course;
                    String rank;
                    String pic;
                    try {
                        JSONObject data = new JSONObject((String) args[0]);
                        username = data.getString("username");
                        course = data.getString("course");
                        rank = data.getString("rank");
                        pic = data.getString("pic");
                    } catch (JSONException e) {
                        return;
                    }
                    Toast.makeText(matchmakingService.this, "Opponent Username: " + username + "\n" + course + "\nRank: " + rank + "\npic: " + pic, Toast.LENGTH_SHORT).show();
                    match_found = true;
                }
            });
        }
    };
}