package com.example.johnnyma.testbench;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import java.util.concurrent.ExecutionException;

/*
 Activity to control user profile.
 */
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
    private String user_json;
    private String name;

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

        if(extras.containsKey("name")) {
            name = intent.getStringExtra("name");
        }

        if(extras.containsKey("profile_pic_url")) {
            profile_pic_url = intent.getStringExtra("profile_pic_url");
            Picasso.with(this).load(profile_pic_url)
                    .transform(new ProfilePicTransformation(200, 0,Color.WHITE))
                    .into(profile_pic);
        }
        username_text.setText(username);
        email_text.setText(email);
        alias_text.setText(alias);
        user_type_text.setText(isProf ? "Professor" : "Student");

        /*
        enter profile edit mode
         */
        settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edit_button_username.setVisibility(View.VISIBLE);
                edit_button_alias.setVisibility(View.VISIBLE);
                if(!isProf)
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
                closeEdit();
            }
        });

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            try {
                user_json = new OkHttpTask().execute(OkHttpTask.EDIT_PROFILE, alias_edittext.getText().toString(), username_edittext.getText().toString()).get();
            } catch (InterruptedException ed) {
                user_json = null;
            } catch (ExecutionException ed) {
                user_json = null;
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
                    closeEdit();
                }
            }
            }
        });

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


    /*
    exit profile edit mode
     */
    private void closeEdit() {
        edit_button_username.setVisibility(View.INVISIBLE);
        edit_button_alias.setVisibility(View.INVISIBLE);
        edit_button_usertype.setVisibility(View.INVISIBLE);

        alias_text.setText(alias_edittext.getText());
        alias_text.setVisibility(View.VISIBLE);
        alias_edittext.setVisibility(View.INVISIBLE);
        if(err_text.getVisibility() == View.GONE)
            username_text.setText(username_edittext.getText());
        username_text.setVisibility(View.VISIBLE);
        username_edittext.setVisibility(View.INVISIBLE);
        close_settings_btn.setVisibility(View.GONE);
        settings_btn.setVisibility(View.VISIBLE);

        submit_btn.setVisibility(View.GONE);
        logout_btn.setVisibility(View.VISIBLE);
        err_text.setVisibility(View.GONE);
    }

    /*
    Alert the user about User Type options before sending them to a professor verification page.
     */
    private void promptUserTypeChange()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setMessage("The only other option is to have a Professor user status. Continue?")
                .setCancelable(true)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       Intent intent_prof = new Intent(UserProfileActivity.this, EmailActivity.class);
                        intent_prof.putExtra("email",email);
                        intent_prof.putExtra("name",name);
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
        //on back pressed, exit edit mode if applicable
        if (edit_mode) {
            closeEdit();
            edit_mode =false;
        } else {
            super.onBackPressed();
        }
    }
}
