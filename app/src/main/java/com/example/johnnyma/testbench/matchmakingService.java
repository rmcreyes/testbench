package com.example.johnnyma.testbench;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.time.LocalTime;
import java.util.Random;

public class matchmakingService extends Service {

    private Socket mSocket;
    {
        try{
            mSocket = IO.socket("http://chat.socket.io");
        } catch (URISyntaxException e){}
    }

    private final IBinder binder = new LocalBinder();
    //private final IBinder socketBinder = new SocketBinder();
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
}
