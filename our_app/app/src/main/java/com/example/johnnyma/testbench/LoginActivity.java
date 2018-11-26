package com.example.johnnyma.testbench;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * Activity from where the user logs in with Facebook.
 */
public class LoginActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private ProgressDialog progressDialog;
    private boolean exit = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        printKeyHash();


        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        // declare the permission we want from the user
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        //user facebook graph API to generate a Facebook Auth token
        //this is used to produce a JWT token from our server
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Retrieving data...");
                progressDialog.show();
                AccessToken loginAccessToken = loginResult.getAccessToken();
                GlobalTokens.FACEBOOK_KEY = loginAccessToken.getToken();

                GraphRequest request = GraphRequest.newMeRequest(loginAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        progressDialog.dismiss();
                        try {

                            Log.d("BELHTDFG","start!");
                            // retrieve relevant Facebook account information for use in the main activity,
                            // and send it in the intent
                            URL profile_pic = new URL("https://graph.facebook.com/"+object.getString("id")+"/picture?width=250&height=250");

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
                            }

                            //keep for home page
                            String profile_pic_url = profile_pic.toString();
                            String name = object.getString("first_name");
                            String email = object.getString("email");


                            Intent intent = new Intent(LoginActivity.this, CourseSelectActivity.class);
                            intent.putExtra("profile_pic_url", profile_pic_url);
                            intent.putExtra("email", email);
                            intent.putExtra("name", name);
                            //flags to ensure that the user cannot press back on the next activity
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                            startActivity(intent);
                        } catch(MalformedURLException e) {
                            Log.d("BELHTDFG","Malformed URL");
                            e.printStackTrace();
                        } catch(JSONException e) {
                            Log.d("BELHTDFG","JSONException");
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

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, "Login error, please try again", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * Used to find the machine specific KeyHash necessary for allowing that machine to develop
     * and test this app while allowing Facebook login
     */
    private void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.johnnyma.testbench", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }
}
