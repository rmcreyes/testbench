package com.example.johnnyma.testbench;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import okhttp3.*;
import org.json.*;

import java.io.IOException;


public class HTTPService extends Service {
    OkHttpClient client;
    private final IBinder binder = new LocalBinder();
    private String name;
    public HTTPService(){
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        client = new OkHttpClient;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class LocalBinder extends Binder {
        HTTPSerivce getService() {
            // Return this instance of LocalService so clients can call public methods
            return HTTPService.this;
        }
    }
    // GET: should return JSON Object with name, rank, profile pic, id, courselist
    public JSONObject getUserDetails() throws IOException {
        Request request = new Request.Builder().url(" //TODO ").build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        JSONObject details = null;
        try {
           details = new JSONObject(response.body().string());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return details;
    }
    // PUT: should change profile name based on user id
    public void editProfileName(String userID, String name) throws IOException {
        Request request = new Request.Builder().url(" //TODO ").build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
    }
    // PUT: should change profile pic based on user id
    public void editProfilePic(String userID, int picNum) throws IOException {
        Request request = new Request.Builder().url(" //TODO ").build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
    }
    // PUT: should change rank based on user ID
    public void editUserRank(String userID, int rank) throws IOException {
        Request request = new Request.Builder().url(" //TODO ").build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
    }
    // DELETE: should delete user account based on id
    public void deleteUser(String userId) throws IOException {
        Request request = new Request.Builder().url(" //TODO ").build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
    }
    // POST: should add user account based on name and pic number
    // returns json object with user info, including newly-generated id,
    // rank, other stuff
    public JSONObject addUser(String name, int picNum) throws IOException {
        Request request = new Request.Builder().url(" //TODO ").build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        JSONObject details = null;
        try {
            details = new JSONObject(response.body().string());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return details;
    }
    // GET: should return user stats, based on user id and course id
    public JSONObject getUserStats(String userID, String courseID) throws IOException {
        Request request = new Request.Builder().url(" //TODO ").build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        JSONObject stats = null;
        try {
            stats = new JSONObject(response.body().string());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stats;
    }
    // GET: should return list of all courses in system
    public JSONArray getAllCourses()throws IOException {
        Request request = new Request.Builder().url(" //TODO ").build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        JSONArray courses = null;
        try {
            courses = new JSONArray(response.body().string());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return courses;
    }
    // POST: add new course to system
    public void addNewCourse(String courseID) throws IOException {
        Request request = new Request.Builder().url(" //TODO ").build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
    }
    // POST: should add a course to user's course list
    public void addUserCourse(String userID, String courseID)throws IOException {
        Request request = new Request.Builder().url(" //TODO ").build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
    }
    // GET: should return list of questions to client for game
    public JSONArray getQuestions() throws IOException {
        Request request = new Request.Builder().url(" //TODO ").build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        JSONArray questions = null;
        try {
            questions = new JSONArray(response.body().string());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return questions;
    }
    // POST: should send newly-created question to server to
    // be added to database,
    // returns string representing generated question id
    public void addQuestion(JSONObject question) throws IOException {
        Request request = new Request.Builder().url(" //TODO ").build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
    }
    // PUT: should set a question's report flag to true,
    // based on question id
    public void reportQuestion(String questionID) throws IOException {
        Request request = new Request.Builder().url(" //TODO ").build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
    }
    // PUT: should set a question's endorsed flag to true,
    // based on question id
    public void endorseQuestion(String questionID) throws IOException {
        Request request = new Request.Builder().url(" //TODO ").build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
    }
    // PUT: should update question's rating with a like or dislike,
    // based on question id
    public void rateQuestion(String questionID, int like) throws IOException {
        Request request = new Request.Builder().url(" //TODO ").build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
    }

}