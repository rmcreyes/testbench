package com.example.johnnyma.testbench;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * AsyncTask used for sending HTTP request parallel to main UI thread
 */
public class OkHttpTask extends AsyncTask<String, Void, String> {

    // set of constants used for arguments to .execute() to identify
    // the intended HTTP request to use
    public static final String ADD_QUESTION = "ADD_QUESTION";
    public static final String GET_USER_DETAILS = "GET_USER_DETAILS";
    public static final String POST_USER_JWT = "POST_USER_JWT";
    public static final String GET_USER_COURSES = "GET_USER_COURSES";
    public static final String ADD_TO_USER_COURSES = "ADD_TO_USER_COURSES";
    public static final String SET_USERNAME = "SET_USERNAME";
    public static final String EDIT_PROFILE = "EDIT_PROFILE";
    public static final String GET_USER_STAT = "GET_USER_STAT";
    public static final String GET_USER_DETAILS_BY_ID = "GET_USER_DETAILS_BY_ID";
    public static final String DELETE_QUESTION = "DELETE_QUESTION";
    public static final String GET_LEADERBOARD = "GET_LEADERBOARD";
    public static final String GET_RANK = "GET_RANK";
    public static final String ADD_STAT_TO_USER = "ADD_STAT_TO_USER";
    public static final String UPDATE_USER_STAT = "UPDATE_USER_STAT";
    public static final String DELETE_STAT_FROM_USER = "DELETE_STAT_FROM_USER";
    public static final String UPDATE_QUESTION_RATING = "UPDATE_QUESTION_RATING";
    public static final String ADD_PROF_COURSE = "ADD_PROF_COURSE";
    public static final String DELETE_PROF_COURSE = "DELETE_PROF_COURSE";
    public static final String SET_PROF_STATUS = "SET_PROF_STATUS";
    public static final String SET_USER_REPORTED_STATUS = "SET_USER_REPORTED_STATUS";
    public static final String SET_QUESTION_REPORTED_STATUS = "SET_QUESTION_REPORTED_STATUS";
    public static final String SET_QUESTION_VERIFIED_STATUS = "SET_QUESTION_VERIFIED_STATUS";
    public static final String GET_PROF_COURSES = "GET_PROF_COURSES";
    public static final String GET_COURSE_QUESTIONS = "GET_COURSE_QUESTIONS";
    public static final String IP = "http://40.78.64.46:3300";
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");


    @Override
    protected String doInBackground(String... strings) {
        OkHttpClient client = new OkHttpClient();
        Request request = createRequest(strings);
        try {
            Response response = client.newCall(request).execute();
            if(response.code() !=200) {
                Log.d("BELHTDFG","error: "+ Integer.toString(response.code()));
                Log.d("BELHTDFG","error message: "+ response.body().string());
                return Integer.toString(response.code());
            }
            else
                //Log.d("BELHTDFG","resp: "+ response.body().string());
                return response.body().string();
        } catch (IOException e) {
            return null;
        }
    }

    private Request createRequest(String... strings) {
        Request.Builder builder = new Request.Builder();
        HttpUrl.Builder url_builder;
        String request_type = strings[0];
        RequestBody body;
        JSONObject reqBody = new JSONObject();
        // build the request differently depending on the request type
        switch(request_type) {
            case GET_USER_DETAILS:
                //Log.d("BELHTDFG","email: " + strings[1]);
                url_builder = HttpUrl
                        .parse(IP + "/api/user/email/").newBuilder()
                        .addQueryParameter("email",strings[1]);
                builder.url(url_builder.build())
                        .addHeader("Authorization", GlobalTokens.JWT_KEY);
                break;
            case GET_USER_DETAILS_BY_ID:
                url_builder = HttpUrl
                        .parse(IP + "/api/user/").newBuilder()
                        .addQueryParameter("id",GlobalTokens.USER_ID);
                builder.url(url_builder.build())
                        .addHeader("Authorization", GlobalTokens.JWT_KEY);
                break;
            case GET_USER_COURSES:
                url_builder = HttpUrl
                        .parse(IP + "/api/getcourses/").newBuilder()
                        .addQueryParameter("id",GlobalTokens.USER_ID);
                builder.url(url_builder.build())
                        .addHeader("Authorization", GlobalTokens.JWT_KEY);
                break;
            case GET_PROF_COURSES:
                url_builder = HttpUrl
                        .parse(IP + "/api/getprofcourses/").newBuilder()
                        .addQueryParameter("id",GlobalTokens.USER_ID);
                builder.url(url_builder.build())
                        .addHeader("Authorization", GlobalTokens.JWT_KEY);
                break;
            case GET_COURSE_QUESTIONS:
                url_builder = HttpUrl
                        .parse(IP + "/api/getcoursequestions/").newBuilder()
                        .addQueryParameter("course_subject",strings[1])
                        .addQueryParameter("course_number",strings[2]);
                builder.url(url_builder.build())
                        .addHeader("Authorization", GlobalTokens.JWT_KEY);
                break;
            case SET_USERNAME:
                //JSONObject courseAddBody = new JSONObject();
                try {
                    reqBody.put("username", strings[1]);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                body = RequestBody.create(JSON, reqBody.toString());
                builder.url(IP + "/api/user/username/"+ GlobalTokens.USER_ID)
                        .addHeader("Authorization", GlobalTokens.JWT_KEY)
                        .put(body);
                //builder.url(IP + "/api/getcourses/" + GlobalTokens.USER_ID)
                break;
            case ADD_TO_USER_COURSES:
                //JSONObject courseAddBody = new JSONObject();
                try {
                    reqBody.put("course_subject", strings[1].toUpperCase());
                    reqBody.put("course_number", strings[2]);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                body = RequestBody.create(JSON, reqBody.toString());
                builder.url(IP + "/api/addcourse/"+ GlobalTokens.USER_ID)
                        .addHeader("Authorization", GlobalTokens.JWT_KEY)
                        .put(body);
                //builder.url(IP + "/api/getcourses/" + GlobalTokens.USER_ID)
                break;
            case EDIT_PROFILE:
                try {
                    reqBody.put("alias", strings[1]);
                    reqBody.put("username", strings[2]);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                body = RequestBody.create(JSON, reqBody.toString());
                builder.url(IP + "/api/user/"+ GlobalTokens.USER_ID)
                        .addHeader("Authorization", GlobalTokens.JWT_KEY)
                        .put(body);
                break;
            case POST_USER_JWT:
                //JSONObject fbAuthBody = new JSONObject();
                try {
                    reqBody.put("access_token", strings[1]);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Log.d("BELHTDFG",reqBody.toString());
                body = RequestBody.create(JSON, reqBody.toString());
                builder.url(IP + "/users/oauth/facebook")
                        .post(body);
                break;

            case DELETE_QUESTION:
                builder.url(IP + "/api/question/" + strings[1] ).delete()
                        .addHeader("Authorization", GlobalTokens.JWT_KEY);
                break;
            case GET_LEADERBOARD:
                url_builder = HttpUrl
                        .parse(IP + "/api/sortedusers/").newBuilder()
                        .addQueryParameter("course_subject",strings[1])
                        .addQueryParameter("course_number",strings[2]);
                builder.url(url_builder.build())
                        .addHeader("Authorization", GlobalTokens.JWT_KEY);
                break;

            case GET_USER_STAT:
                url_builder = HttpUrl
                        .parse(IP + "/api/getstats/").newBuilder()
                        .addQueryParameter("id",GlobalTokens.USER_ID)
                        .addQueryParameter("course_subject",strings[1])
                        .addQueryParameter("course_number",strings[2]);
                builder.url(url_builder.build())
                        .addHeader("Authorization", GlobalTokens.JWT_KEY);
                break;
            case GET_RANK:
                url_builder = HttpUrl
                        .parse(IP + "/api/rank/").newBuilder()
                        .addQueryParameter("id",GlobalTokens.USER_ID)
                        .addQueryParameter("course_subject",strings[1])
                        .addQueryParameter("course_number",strings[2]);
                builder.url(url_builder.build())
                        .addHeader("Authorization", GlobalTokens.JWT_KEY);
                break;

            case ADD_STAT_TO_USER:
                try {
                    reqBody.put("course_subject", strings[1]);
                    reqBody.put("course_number", strings[2]);
                    reqBody.put("add_correctness_rate", Double.parseDouble(strings[3]));
                    reqBody.put("add_response_time", Double.parseDouble(strings[4]));
                    reqBody.put("level_progress", strings[5]);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                body = RequestBody.create(JSON, reqBody.toString());
                builder.url(IP + "/api/addnewstat/"+ GlobalTokens.USER_ID)
                        .addHeader("Authorization", GlobalTokens.JWT_KEY)
                        .put(body);
                break;

            case UPDATE_USER_STAT:
                try {
                    reqBody.put("course_subject", strings[1]);
                    reqBody.put("course_number", strings[2]);
                    reqBody.put("add_correctness_rate", Double.parseDouble(strings[3]));
                    reqBody.put("add_response_time", Double.parseDouble(strings[4]));
                    reqBody.put("level_progress", strings[5]);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                body = RequestBody.create(JSON, reqBody.toString());
                builder.url(IP + "/api/updatestat/"+ GlobalTokens.USER_ID)
                        .addHeader("Authorization", GlobalTokens.JWT_KEY)
                        .put(body);
                break;
            case DELETE_STAT_FROM_USER:
                try {
                    reqBody.put("course_subject", strings[1]);
                    reqBody.put("course_number", strings[2]);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                body = RequestBody.create(JSON, reqBody.toString());
                builder.url(IP + "/api/deletestat/"+ GlobalTokens.USER_ID)
                        .addHeader("Authorization", GlobalTokens.JWT_KEY)
                        .put(body);
                break;

            case UPDATE_QUESTION_RATING:
                try {
                    reqBody.put("new_rating", strings[2]);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                body = RequestBody.create(JSON, reqBody.toString());
                builder.url(IP + "/api/updaterating/"+ strings[1])
                        .addHeader("Authorization", GlobalTokens.JWT_KEY)
                        .put(body);
                break;

            case ADD_PROF_COURSE:
                try {
                    reqBody.put("course_subject", strings[1]);
                    reqBody.put("course_number", strings[2]);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                body = RequestBody.create(JSON, reqBody.toString());
                builder.url(IP + "/api/addprofessorcourse/"+ GlobalTokens.USER_ID)
                        .addHeader("Authorization", GlobalTokens.JWT_KEY)
                        .put(body);
                break;
            case DELETE_PROF_COURSE:
                try {
                    reqBody.put("course_subject", strings[1]);
                    reqBody.put("course_number", strings[2]);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                body = RequestBody.create(JSON, reqBody.toString());
                builder.url(IP + "/api/deleteprofcourse/"+ GlobalTokens.USER_ID)
                        .addHeader("Authorization", GlobalTokens.JWT_KEY)
                        .put(body);
                break;
            case SET_PROF_STATUS:
                try {
                    reqBody.put("is_professor", strings[1]);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                body = RequestBody.create(JSON, reqBody.toString());
                builder.url(IP + "/api/professor/"+ GlobalTokens.USER_ID)
                        .addHeader("Authorization", GlobalTokens.JWT_KEY)
                        .put(body);
                break;
            case SET_USER_REPORTED_STATUS:
                try {
                    reqBody.put("reported", strings[1]);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                body = RequestBody.create(JSON, reqBody.toString());
                builder.url(IP + "/api/user/reported/"+ GlobalTokens.USER_ID)
                        .addHeader("Authorization", GlobalTokens.JWT_KEY)
                        .put(body);
                break;
            case SET_QUESTION_REPORTED_STATUS:
                try {
                    reqBody.put("reported", strings[2]);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                body = RequestBody.create(JSON, reqBody.toString());
                builder.url(IP + "/api/question/reported/"+ strings[1])
                        .addHeader("Authorization", GlobalTokens.JWT_KEY)
                        .put(body);
                break;
            case SET_QUESTION_VERIFIED_STATUS:
                try {
                    reqBody.put("verified", strings[2]);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                body = RequestBody.create(JSON, reqBody.toString());
                builder.url(IP + "/api/question/verified/"+ strings[1])
                        .addHeader("Authorization", GlobalTokens.JWT_KEY)
                        .put(body);

            case ADD_QUESTION:
                try{
                    reqBody.put("question_text", strings[1]);
                    reqBody.put("correct_answer", strings[2]);
                    reqBody.put("incorrect_answer_1", strings[3]);
                    reqBody.put("incorrect_answer_2", strings[4]);
                    reqBody.put("incorrect_answer_3", strings[5]);
                    reqBody.put("creator_uID", strings[6]);
                    reqBody.put("verified", strings[7]);
                    reqBody.put("course_subject", strings[8]);
                    reqBody.put("course_number", strings[9]);
                } catch (JSONException e){
                    e.printStackTrace();
                }
                body = RequestBody.create(JSON, reqBody.toString());
                builder.url(IP + "/api/question").post(body).addHeader("Authorization", GlobalTokens.JWT_KEY);

                break;
        }
        return builder.build();
    }
}
