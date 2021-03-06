package com.example.johnnyma.testbench;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

/**
 * Activity from where the user picks a course to battle with,
 * add a question to, or view their stats in
 */
public class CourseSelectActivity extends AppCompatActivity implements SelectedCourseDialog.SelectedCourseDialogListener, CourseAddDialog.BottomSheetListener {

    public static final String TAG = "CourseSelectActivity"; //tag for sending info through intents
    private ListView CourseListView;
    private FloatingActionButton fab;
    private ImageButton profile_btn;
    private TextView name;
    private ImageView profile_pic;
    private String fb_name;
    private String profile_pic_url;
    private String email;
    private String alias;
    private boolean exit;
    private JSONObject u_json;
    private Intent intent;
    private Bundle extras;
    private boolean isProf;
    private String username;

    // each course header(eg. CPEN) is a key to a list of course codes (eg. 311, 321, 331)
    private Map<String, List<String>> Courses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_select);

        CourseSelectLock.lock = new Object();

        CourseListView = findViewById(R.id.list_view);
        profile_btn = findViewById(R.id.profile_btn);
        fab = findViewById(R.id.fab);
        name = findViewById(R.id.name);
        profile_pic = findViewById(R.id.profile_pic);
        intent = getIntent();
        extras = intent.getExtras();

        // coming from the LoginActivity, the intent may come with information relating to the user
        if(extras.containsKey("name")) {
            fb_name = intent.getStringExtra("name");
            name.setText(fb_name);
        }
        else
            name.setText("error");

        if(extras.containsKey("profile_pic_url")) {
            profile_pic_url = intent.getStringExtra("profile_pic_url");
            Picasso.with(this).load(profile_pic_url)
                    .transform(new ProfilePicTransformation(200, 0,Color.WHITE))
                    .into(profile_pic);
        }


        if(extras.containsKey("email")) {
            email = intent.getStringExtra("email");
        }

        // collect user info and load course feed
        refreshProfile();
        onCourseAdded();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CourseAddDialog bottomSheet = new CourseAddDialog();
                bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
            }
        });

        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to user profile
                try {
                    isProf = u_json.getBoolean("is_professor");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent userprofile = new Intent(CourseSelectActivity.this, UserProfileActivity.class);
                userprofile.putExtra("alias", alias);
                userprofile.putExtra("username", username);
                userprofile.putExtra("isProf", isProf);
                userprofile.putExtra("email", email);
                userprofile.putExtra("name", fb_name);
                userprofile.putExtra("profile_pic_url", profile_pic_url);
                startActivity(userprofile);
            }
        });

    }

    /**
     * Initializes and loads the map `Courses` with the courses tied to
     * the user's account.
     */
    private void fillCourses() {
        Courses = new TreeMap<String, List<String>>();

        String s_courses = null;
        try {
            s_courses = new OkHttpTask().execute(OkHttpTask.GET_USER_COURSES).get();
        } catch(InterruptedException e) {
            Toast.makeText(this, "error connecting to server", Toast.LENGTH_SHORT).show();
            return;
        } catch(ExecutionException e) {
            Toast.makeText(this, "error connecting to server", Toast.LENGTH_SHORT).show();
            return;
        }

        if(s_courses == null) {
            Toast.makeText(this, "error connecting to server", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONArray jsonArray = new JSONArray(s_courses);

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String course_subject = jsonObject.getString("course_subject");
                int course_number = jsonObject.getInt("course_number");

                if(Courses.keySet().contains(course_subject)) {
                    Courses.get(course_subject).add(Integer.toString(course_number));
                }
                else {
                    List<String> subject_codes = new ArrayList<String>();
                    subject_codes.add(Integer.toString(course_number));
                    Courses.put(course_subject, subject_codes);
                }
            }

            for(String subj : Courses.keySet())
                Collections.sort(Courses.get(subj));

        } catch (JSONException e) {
            Toast.makeText(this, "illegal response", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    /**
     * Abstract method implementation that allows SelectCourseActivity to
     * determine the action decided on in the SelectedCourseDialog
     * @param action - action to be taken with the course
     * @param course - course whose action is to be enacted
     */
    @Override
    public void chooseCourseView(int action, String course, int rank) {
        switch (action) {
            case CourseActionDefs.BATTLE:
                Intent intent = new Intent(this, MatchmakingActivity.class);
                intent.putExtra("rank",rank);
                intent.putExtra(TAG, course);
                intent.putExtra("alias", alias);
                intent.putExtra("username", username);
                startActivity(intent);
                break;

            case CourseActionDefs.ADD_QUESTION:
                Intent q_intent = new Intent(this, AddQuestionActivity.class);
                q_intent.putExtra("course",course);
                startActivity(q_intent);
                break;

            case CourseActionDefs.REVIEW_QUESTIONS:
                Intent r_intent = new Intent(this, ProfessorActivity.class);
                r_intent.putExtra("course", course);
                startActivity(r_intent);
                break;


            default: break;
        }

    }


    @Override
    public void onBackPressed() {
        if (exit) {
            finish();
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

    /**
     * Updates the view when a user adds a course to their account
     */
    @Override
    public void onCourseAdded() {
        fillCourses();

        CourseAdapter courseAdapter = new CourseAdapter(this, Courses, getSupportFragmentManager());
        CourseListView.setAdapter(courseAdapter);
    }


    /*
        When the user uses the app for the first time, they will not have a username.
        This alert will prompt them to enter a unique username.
     */
    private void promptUsername()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(CourseSelectActivity.this);

        LayoutInflater inflater=CourseSelectActivity.this.getLayoutInflater();
        View layout=inflater.inflate(R.layout.dialog_assign_username,null);
        alert.setView(layout);
        final EditText usernameInput=(EditText)layout.findViewById(R.id.username_text);
        final TextView error_text = (TextView) layout.findViewById(R.id.error_text);
        alert.setCancelable(false).setPositiveButton(android.R.string.ok, null);
        final AlertDialog dialog = alert.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String un_resp;
                        try {
                            un_resp = new OkHttpTask().execute(OkHttpTask.SET_USERNAME, usernameInput.getText().toString()).get();
                        } catch (InterruptedException e) {
                            un_resp = null;
                        } catch (ExecutionException e) {
                            un_resp = null;
                        }
                        if(un_resp != null) {
                            if(un_resp.equals("409"))
                            {
                                error_text.setVisibility(View.VISIBLE);
                                ColorStateList colorStateList = ColorStateList.valueOf(Color.RED);
                                ViewCompat.setBackgroundTintList(usernameInput, colorStateList);
                            } else {
                                username = usernameInput.getText().toString();
                                Toast.makeText(CourseSelectActivity.this, "username added!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }

                        }
                    }
                });
            }
        });
        dialog.show();

    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshProfile();

        if(SocketHandler.isDisconnected()) {
            SocketHandler.setDisconnected(false);
            Toast.makeText(this, "Your opponent has quit the game", Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * Recollects user's information and stores them in the class's attributes.
     */

    private void refreshProfile() {
        String user_json;
        try {
            user_json = new OkHttpTask().execute(OkHttpTask.GET_USER_DETAILS, email).get();
        } catch (InterruptedException e) {
            user_json = null;
        } catch (ExecutionException e) {
            user_json = null;
        }
        if (user_json != null) {
            try {
                u_json = new JSONObject(user_json.substring(1, user_json.length()-1));

                GlobalTokens.USER_ID = u_json.getString("_id");
                alias = u_json.getString("alias");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                username = u_json.getString("username");
            } catch (JSONException e) {
                promptUsername();
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(CourseSelectActivity.this);
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



    }
}


