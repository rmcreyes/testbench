package com.example.johnnyma.testbench;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

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
    private Intent intent;
    private Bundle extras;

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
        intent = getIntent();
        extras = intent.getExtras();


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if(extras.containsKey("isProf")) {
            isProf = intent.getBooleanExtra("isProf",true);
        }

        if(extras.containsKey("username")) {
            username = intent.getStringExtra("username");
        }

        if(extras.containsKey("email")) {
            email = intent.getStringExtra("email");
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
                Intent intent_prof = new Intent(UserProfileActivity.this, SettingsActivity.class);
                startActivity(intent_prof);
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
    };


}
