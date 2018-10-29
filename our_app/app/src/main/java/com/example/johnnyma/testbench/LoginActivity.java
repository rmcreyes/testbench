package com.example.johnnyma.testbench;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
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

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Retrieving data...");
                progressDialog.show();

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        progressDialog.dismiss();
                        try {
                            // retrieve relevant Facebook account information for use in the main activity,
                            // and send it in the intent
                            URL profile_pic = new URL("https://graph.facebook.com/"+object.getString("id")+"/picture?width=250&height=250");
                            String profile_pic_url = profile_pic.toString();
                            String email = object.getString("email");
                            String name = object.getString("first_name");
                            Intent intent = new Intent(LoginActivity.this, CourseSelectActivity.class);
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
                                Toast.makeText(LoginActivity.this, toToast, Toast.LENGTH_SHORT).show();
                            startActivity(intent);
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

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, "Login error, please try again", Toast.LENGTH_SHORT).show();
            }
        });

        if(AccessToken.getCurrentAccessToken() != null) {
            // TODO: come up with protocol for missing access token
        }

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
}
