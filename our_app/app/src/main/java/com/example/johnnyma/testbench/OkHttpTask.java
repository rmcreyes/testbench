package com.example.johnnyma.testbench;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
        String request_type = strings[0];
        RequestBody body;
        JSONObject reqBody = new JSONObject();
        // build the request differently depending on the request type
        switch(request_type) {
            case GET_USER_DETAILS:
                //Log.d("BELHTDFG","email: " + strings[1]);
                builder.url(IP + "/api/user/email/?email=" + strings[1])
                        .addHeader("Authorization", GlobalTokens.JWT_KEY);
                break;
            case GET_USER_COURSES:
                builder.url(IP + "/api/getcourses/?id=" + GlobalTokens.USER_ID)
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
            case ADD_QUESTION:
                body = RequestBody.create(JSON, strings[1]);
                //Toast.makeText(AddQuestionActivity.this, "HEHEHEH", Toast.LENGTH_SHORT).show();
                builder.url(IP + "/api/question").post(body);
                break;
        }
        return builder.build();
    }
}
