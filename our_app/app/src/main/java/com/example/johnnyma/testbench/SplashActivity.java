package com.example.johnnyma.testbench;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        if(accessToken != null) {
            Toast.makeText(this, "FUCK YEAH", Toast.LENGTH_SHORT).show();
            GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    try {
                        // retrieve relevant Facebook account information for use in the main activity,
                        // and send it in the intent
                        URL profile_pic = new URL("https://graph.facebook.com/"+object.getString("id")+"/picture?width=250&height=250");
                        String profile_pic_url = profile_pic.toString();
                        String email = object.getString("email");
                        String name = object.getString("first_name");
                        Intent intent = new Intent(SplashActivity.this, CourseSelectActivity.class);
                        intent.putExtra("profile_pic_url", profile_pic_url);
                        intent.putExtra("email", email);
                        intent.putExtra("name", name);

                        // testing use of HTTP requests with OkHttpTask
                        String toToast;
                        try {
                            toToast = new OkHttpTask().execute(OkHttpTask.GET_USER_DETAILS, "yeeter@yeet.net").get();
                        } catch (InterruptedException e) {
                            toToast = null;
                        } catch (ExecutionException e) {
                            toToast = null;
                        }
                        if(toToast != null)
                            Toast.makeText(SplashActivity.this, toToast, Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    } catch(MalformedURLException e) {
                        e.printStackTrace();
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, email, first_name");
            request.setParameters(parameters);
            request.executeAsync();
        }

        else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }


    }
}
