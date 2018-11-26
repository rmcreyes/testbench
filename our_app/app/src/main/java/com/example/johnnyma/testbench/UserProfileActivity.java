package com.example.johnnyma.testbench;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class UserProfileActivity extends AppCompatActivity {
    private Button settings_btn;
    private Button logout_btn;
    private ImageView profile_pic;
    private String profile_pic_url;
    private boolean isProf;
    private String username;
    private String email;
    private String alias;
    private TextView username_text;
    private TextView email_text;
    private TextView alias_text;
    private TextView user_type_text;
    private EditText alias_edittext;
    private EditText username_edittext;

    private ImageView edit_button_username;
    private ImageView edit_button_alias;
    private Button edit_button_usertype;
    private Button submit_btn;
    private Button close_settings_btn;

    private TextView err_text;
    private boolean edit_mode;
    private Intent intent;
    private Bundle extras;
    private JSONObject u_json;
    private String user_json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);
        settings_btn = (Button) findViewById(R.id.settings_btn);
        logout_btn = (Button) findViewById(R.id.logout_btn);
        profile_pic = findViewById(R.id.profile_photo);
        username_text =  findViewById(R.id.username_text);
        email_text =  findViewById(R.id.email_text);
        alias_text =  findViewById(R.id.alias_text);
        user_type_text =  findViewById(R.id.user_type_text);
        edit_mode = false;

        edit_button_username = findViewById(R.id.edit_button_username);
        edit_button_alias = findViewById(R.id.edit_button_alias);
        edit_button_usertype = findViewById(R.id.edit_button_usertype);
        alias_edittext = findViewById(R.id.alias_edittext);
        username_edittext = findViewById(R.id.username_edittext);
        submit_btn = findViewById(R.id.submit_btn);
        close_settings_btn = findViewById(R.id.close_settings_btn);
        err_text = findViewById(R.id.err_text);

        intent = getIntent();
        extras = intent.getExtras();


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();



//        try {
//            user_json = new OkHttpTask().execute(OkHttpTask.GET_USER_DETAILS, email).get();
//            Toast.makeText(UserProfileActivity.this, user_json, Toast.LENGTH_SHORT).show();
//            u_json = new JSONObject(user_json.substring(1, user_json.length()-1));
//            username = u_json.getString("username");
//        } catch (InterruptedException ed) {
//            user_json = null;
//            Log.d("BELHTDFG","InterruptedException");
//        } catch (ExecutionException ed) {
//            user_json = null;
//            Log.d("BELHTDFG","ExecutionException");
//        } catch (JSONException e1) {
//            e1.printStackTrace();
//        }

        if(extras.containsKey("email")) {
            email = intent.getStringExtra("email");
        }
        if(extras.containsKey("isProf")) {
            isProf = intent.getBooleanExtra("isProf",true);
        }

        if(extras.containsKey("username")) {
            username = intent.getStringExtra("username");
        }



        if(extras.containsKey("alias")) {
            alias = intent.getStringExtra("alias");
        }

        username_text.setText(username);
        email_text.setText(email);
        alias_text.setText(alias);
        user_type_text.setText(isProf ? "Professor" : "Student");

        settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edit_button_username.setVisibility(View.VISIBLE);
                edit_button_alias.setVisibility(View.VISIBLE);
                edit_button_usertype.setVisibility(View.VISIBLE);

                alias_edittext.setText(alias_text.getText());
                alias_text.setVisibility(View.INVISIBLE);
                alias_edittext.setVisibility(View.VISIBLE);

                username_edittext.setText(username_text.getText());
                username_text.setVisibility(View.INVISIBLE);
                username_edittext.setVisibility(View.VISIBLE);
                close_settings_btn.setVisibility(View.VISIBLE);
                settings_btn.setVisibility(View.GONE);

                submit_btn.setVisibility(View.VISIBLE);
                logout_btn.setVisibility(View.GONE);
                edit_mode = true;
            }
        });

        close_settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_mode = false;
                closeEdit(false);
            }
        });

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    user_json = new OkHttpTask().execute(OkHttpTask.EDIT_PROFILE, alias_edittext.getText().toString(), username_edittext.getText().toString()).get();
                } catch (InterruptedException ed) {
                    user_json = null;
                    Log.d("BELHTDFG","InterruptedException");
                } catch (ExecutionException ed) {
                    user_json = null;
                    Log.d("BELHTDFG","ExecutionException");
                }

                if(user_json!=null) {
                    if(user_json.equals("409"))
                    {
                        err_text.setVisibility(View.VISIBLE);
                    } else {
                        err_text.setVisibility(View.GONE);
                        Snackbar.make(findViewById(android.R.id.content), "Changes Successful", Snackbar.LENGTH_LONG)
                        .show();
                        edit_mode = false;
                        closeEdit(true);
                    }
                }

            }
        });
        if(extras.containsKey("profile_pic_url")) {
            profile_pic_url = intent.getStringExtra("profile_pic_url");
            Picasso.with(this).load(profile_pic_url)
                    .transform(new ProfilePicTransformation(200, 0,Color.BLACK))
                    .into(profile_pic);
        }
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                Intent intent_login = new Intent(UserProfileActivity.this, LoginActivity.class);
                intent_login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent_login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent_login.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent_login);
            }
        });

        edit_button_usertype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptUserTypeChange();
            }
        });
    };
    private void closeEdit(boolean save) {
        edit_button_username.setVisibility(View.INVISIBLE);
        edit_button_alias.setVisibility(View.INVISIBLE);
        edit_button_usertype.setVisibility(View.INVISIBLE);
        if(save) {
            alias_text.setText(alias_edittext.getText());
            username_text.setText(username_edittext.getText());
        }
        alias_text.setVisibility(View.VISIBLE);
        alias_edittext.setVisibility(View.INVISIBLE);

        username_text.setVisibility(View.VISIBLE);
        username_edittext.setVisibility(View.INVISIBLE);
        close_settings_btn.setVisibility(View.GONE);
        settings_btn.setVisibility(View.VISIBLE);

        submit_btn.setVisibility(View.GONE);
        logout_btn.setVisibility(View.VISIBLE);
        err_text.setVisibility(View.GONE);
    }
    private void promptUserTypeChange()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setMessage("The only other option is to have a Professor user status. Continue?")
                .setCancelable(true)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       Intent intent_prof = new Intent(UserProfileActivity.this, EmailActivity.class);
                        startActivity(intent_prof);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();


    }

    @Override
    public void onBackPressed() {
        if (edit_mode) {
            closeEdit(false);
            edit_mode =false;
        } else {
            super.onBackPressed();
        }

    }


}
