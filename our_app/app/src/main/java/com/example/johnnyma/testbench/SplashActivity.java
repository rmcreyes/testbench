package com.example.johnnyma.testbench;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Activity that routes to the appropriates first view.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        // user is logged in if access token is valid
        if(accessToken != null) {
            GlobalTokens.FACEBOOK_KEY = accessToken.getToken();
            GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    try {
                        String JWT_Json;
                        try {
                            JWT_Json = new OkHttpTask().execute(OkHttpTask.POST_USER_JWT, GlobalTokens.FACEBOOK_KEY).get();
                        } catch (InterruptedException e) {
                            JWT_Json = null;
                        } catch (ExecutionException e) {
                            JWT_Json = null;
                        }
                        if(JWT_Json != null) {
                            JSONObject jwt_raw = new JSONObject(JWT_Json);
                            GlobalTokens.JWT_KEY = jwt_raw.getString("token");
                            URL profile_pic = new URL("https://graph.facebook.com/"+object.getString("id")+"/picture?width=250&height=250");
                            String profile_pic_url = profile_pic.toString();
                            String email = object.getString("email");
                            String name = object.getString("first_name");


                            Intent intent = new Intent(SplashActivity.this, CourseSelectActivity.class);
                            intent.putExtra("profile_pic_url", profile_pic_url);
                            intent.putExtra("email", email);
                            intent.putExtra("name", name);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                            startActivity(intent);
                        } else {
                            //couldn't make first request to server
                            AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                            builder.setMessage("No server connection. Press OK to exit.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            finish();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }

                    } catch(MalformedURLException e) {
                        e.printStackTrace();
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

            // declare the keys to be present in the json string coming from
            // the login
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, email, first_name");
            request.setParameters(parameters);
            request.executeAsync();
        }

        // send user to login activity if they are not logged in
        else {
            //ensure that loginActivity can't go back
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }


    }
}
