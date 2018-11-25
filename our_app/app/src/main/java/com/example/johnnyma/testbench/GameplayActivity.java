package com.example.johnnyma.testbench;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/*
 * TODO: add loading question dialog
 */



public class GameplayActivity extends AppCompatActivity  {

    //emoji encondings
    public static final int EMOJI_OK = 0;
    public static final int EMOJI_BIGTHINK = 1;
    public static final int EMOJI_FIRE = 2;
    public static final int EMOJI_POOP = 3;
    public static final int EMOJI_HUNNIT = 4;
    public static final int EMOJI_HEART = 5;
    public static final int EMOJI_CRYLAUGH = 6;

    Socket socket; // socket handle
    // handles for all layout elements
    Button answer1;
    Button answer2;
    Button answer3;
    Button answer4;
    TextView body;
    TextView playerName;
    TextView opponentName;
    TextView playerScore;
    TextView opponentScore;
    TextView courseHeader;
    TextView questionHeader;
    ImageView playerAvatar;
    ImageView opponentAvatar;
    // all questions
    ArrayList<Question> questions;

    String course;
    int player_score;
    int opponent_score;
    String player_name;
    String opponent_name;
    String opponent_leaderboard_name;
    String leaderboard_name;

    int currentQuestion = 1;
    int answer_time = 0;

    boolean buttonsEnabled = false;
    private Object lock = new Object();
    int player_rank;
    int opponent_rank;
    int num_false = 0;
    int correct_loc;
    int correctlyAnswered = 0;
    long cur_q_time = 0;

    long time_event = 0;


    TextView loadingText;
    TextView roundWinnerText;
    TextView winText;
    ImageView winnerAvatar;
    private boolean timed_out;

    //emoji stuff
    ImageView emoji_bigthink;
    ImageView emoji_crylaugh;
    ImageView emoji_heart;
    ImageView emoji_poop;
    ImageView emoji_hunnit;
    ImageView emoji_fire;
    ImageView emoji_ok;
    private PopupWindow emojiPopup;
    private LayoutInflater layoutInflater;
    private boolean emoji_displayed = false;

    private FrameLayout fragment_container;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        time_event = 0;
        currentQuestion = 1;
        answer_time = 0;
        num_false = 0;
        correctlyAnswered = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);
        Intent starting_intent = getIntent();
        course = starting_intent.getStringExtra("course");
        fragment_container = findViewById(R.id.loading_card);
        fragment_container.setVisibility(View.INVISIBLE);
        courseHeader = findViewById(R.id.course_header);
        courseHeader.setText(course.substring(0,4)+ " " + course.substring(4, 7));
        player_name = starting_intent.getStringExtra("alias");
        leaderboard_name = starting_intent.getStringExtra("leaderboard_name");

        opponent_leaderboard_name = starting_intent.getStringExtra("opponent_leaderboard_name");
        playerName = findViewById(R.id.player_name);
        playerName.setText(player_name);
        player_rank = starting_intent.getIntExtra("player_rank", 1);
        playerAvatar = findViewById(R.id.player_avatar);
        setAvatar(playerAvatar,player_rank);
        opponent_name = starting_intent.getStringExtra("opponent_alias");
        opponentName = findViewById(R.id.opponent_name);
        opponentName.setText(opponent_name);
        opponent_rank = starting_intent.getIntExtra("opponent_rank", 1);
        opponentAvatar = findViewById(R.id.opponent_avatar);
        setAvatar(opponentAvatar,opponent_rank);
        player_score = 0;
        playerScore = findViewById(R.id.player_score);
        playerScore.setText("Score: " + player_score);
        opponent_score = 0;
        opponentScore = findViewById(R.id.opponent_score);
        opponentScore.setText("Score: " + opponent_score);
        body = findViewById(R.id.question_body);
        questionHeader = findViewById(R.id.question_num);
        loadingText = findViewById(R.id.loading_text);
        roundWinnerText = findViewById(R.id.winner);
        winText = findViewById(R.id.won_msg);
        winnerAvatar = findViewById(R.id.loading_pic);
        answer1 = findViewById(R.id.answer_1);
        answer2 = findViewById(R.id.answer_2);
        answer3 = findViewById(R.id.answer_3);
        answer4 = findViewById(R.id.answer_4);
        parseQuestions(starting_intent.getStringExtra("questions"));
        //set emoji views and onclick listners for emojis
        emoji_ok = findViewById(R.id.ok_emoji2);
        emoji_poop = findViewById(R.id.ok_emoji);
        emoji_bigthink = findViewById(R.id.bigthink_emoji);
        emoji_fire = findViewById(R.id.ok_emoji3);
        emoji_hunnit = findViewById(R.id.hunnit_emoji2);
        emoji_crylaugh = findViewById(R.id.crylaugh_emoji);
        emoji_heart = findViewById(R.id.heart_emoji);
        setEmojiListeners();
        socket = SocketHandler.getSocket();
        socket.on("broadcast_emoji", popupEmoji);
        socket.on("turn_over", turnOver);
        socket.on("start_question", readyQuestion);
        socket.on("broadcast_leave", opponentLeft);
        setButtonListeners();
        setInitialLoadView();
        //handler.postDelayed(eventCheck, 5000);
        waitForQuestion();
    }

    private Runnable eventCheck = new Runnable() {
        @Override
        public void run() {
            if(System.currentTimeMillis() - time_event > 20000){
                SocketHandler.setDisconnected(true);

                Log.i("disconnect", "fuck 2");
                socket.disconnect();
                finish();
            } else {
                handler.postDelayed(this, 5000);
            }
        }
    };

    private void setInitialLoadView() {
        roundWinnerText.setVisibility(View.INVISIBLE);
        winText.setVisibility(View.INVISIBLE);
        winnerAvatar.setImageResource(R.drawable.bigthink_emoji);
        loadingText.setText("Get Ready for Question " + currentQuestion + "!");
    }

    private void setInGameLoadView(){
        roundWinnerText.setVisibility(View.VISIBLE);
        winText.setVisibility(View.VISIBLE);
        roundWinnerText.setText("Nobody");
        winText.setText("won the round!");
        winnerAvatar.setImageResource(R.drawable.bigthink_emoji);
        loadingText.setText("Get Ready for Question " + currentQuestion + "!");
    }

    private void setInGameLoadView(String winner, int avatar) {
        roundWinnerText.setVisibility(View.VISIBLE);
        winText.setVisibility(View.VISIBLE);
        roundWinnerText.setText(winner);
        winText.setText("won the round!");
        switch (avatar % 6) {
            case 0:
                winnerAvatar.setImageResource(R.drawable.penguin_avatar);
                break;
            case 1:
                winnerAvatar.setImageResource(R.drawable.mountain_avatar);
                break;
            case 2:
                winnerAvatar.setImageResource(R.drawable.rocket_avatar);
                break;
            case 3:
                winnerAvatar.setImageResource(R.drawable.frog_avatar);
                break;
            case 4:
                winnerAvatar.setImageResource(R.drawable.thunderbird_avatar);
                break;
            case 5:
                winnerAvatar.setImageResource(R.drawable.cupcake_avatar);
                break;
        }
        loadingText.setText("Get Ready for \n  Question " + currentQuestion + "!");
    }
    private void enableButtons(){
        synchronized(lock) {
            buttonsEnabled = true;
        }
    }
    private void disableButtons() {
        synchronized (lock) {
            buttonsEnabled = false;
        }
    }
    private boolean buttonsEnabled() {
        synchronized(lock) {
            return buttonsEnabled;
        }
    }
    private void setButtonListeners() {
        answer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buttonsEnabled()) {
                    disableButtons();
                    answer_time += System.currentTimeMillis() - cur_q_time;
                    answerChosen(answer1, 1, System.currentTimeMillis() - cur_q_time);
                }
            }
        });

        answer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buttonsEnabled()) {
                    disableButtons();
                    answer_time += System.currentTimeMillis() - cur_q_time;
                    answerChosen(answer2, 2, System.currentTimeMillis() - cur_q_time);
                }
            }
        });

        answer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buttonsEnabled()) {
                    disableButtons();
                    answer_time += System.currentTimeMillis() - cur_q_time;
                    answerChosen(answer3, 3, System.currentTimeMillis() - cur_q_time);
                }
            }
        });

        answer4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buttonsEnabled()) {
                    disableButtons();
                    answer_time += System.currentTimeMillis() - cur_q_time;
                    answerChosen(answer4, 4, System.currentTimeMillis() - cur_q_time);
                }
            }
        });
    }

    private void setAvatar(ImageView avatar,int rank) {
        switch(rank % 6) {
            case 0:
                avatar.setImageResource(R.drawable.penguin_avatar);
                break;
            case 1:
                avatar.setImageResource(R.drawable.mountain_avatar);
                break;
            case 2:
                avatar.setImageResource(R.drawable.rocket_avatar);
                break;
            case 3:
                avatar.setImageResource(R.drawable.frog_avatar);
                break;
            case 4:
                avatar.setImageResource(R.drawable.thunderbird_avatar);
                break;
            case 5:
                avatar.setImageResource(R.drawable.cupcake_avatar);
                break;
        }
    }

    private void randomizeAnswers(Question q){
        Random random = new Random();
        int rand = random.nextInt(4) + 1;
        switch (rand) {
            case 1: {
                answer1.setText(q.getCorrectAnswer());
                correct_loc = 1;
                answer2.setText(q.getIncorrectAnswer1());
                answer3.setText(q.getIncorrectAnswer2());
                answer4.setText(q.getIncorrectAnswer3());
                break;
            }
            case 2: {
                answer2.setText(q.getCorrectAnswer());
                correct_loc = 2;
                answer1.setText(q.getIncorrectAnswer1());
                answer3.setText(q.getIncorrectAnswer2());
                answer4.setText(q.getIncorrectAnswer3());
                break;
            }
            case 3: {
                answer3.setText(q.getCorrectAnswer());
                correct_loc = 3;
                answer1.setText(q.getIncorrectAnswer1());
                answer2.setText(q.getIncorrectAnswer2());
                answer4.setText(q.getIncorrectAnswer3());
                break;
            }
            case 4: {
                answer4.setText(q.getCorrectAnswer());
                correct_loc = 4;
                answer1.setText(q.getIncorrectAnswer1());
                answer2.setText(q.getIncorrectAnswer2());
                answer3.setText(q.getIncorrectAnswer3());
                break;
            }
        }
    }

    private void startTransition() {
        fragment_container.setVisibility(View.VISIBLE);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        fragment_container.startAnimation(slideUp);
    }

    private void endTransition() {
        Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        fragment_container.startAnimation(slideDown);
        fragment_container.setVisibility(View.INVISIBLE);
    }


    private void waitForQuestion() {
        yellowHighlightCorrect();
        num_false = 0;

        resetButtonColors();
        startTransition();
        Log.d("num_questions","num questions is: " + questions.size());
        if(currentQuestion <= questions.size()) {
            questionHeader.setText("Question " + currentQuestion + " of " + questions.size());
            Log.d("wait for question", "emitting ready next");
            socket.emit("ready_next");
//            timed_out = true;
//            new Timer().schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    // this code will be executed after 2 seconds
//                    if(timed_out) {
//                        Log.i("disconnect","hmm");
//                        socket.disconnect();
//                        finish();
//                    }
//                }
//            }, 15000);
        } else {
            Log.d("endgame","ending game 1 with current question: " + currentQuestion + " and max " + questions.size());

            //socket.disconnect();
            endGame();
        }
    }
    private void resetButtonColors(){
        answer1.setBackgroundTintList(GameplayActivity.this.getResources().getColorStateList(R.color.colorPrimary, null));
        answer1.setTextColor(Color.parseColor("#ffffff"));
        answer2.setBackgroundTintList(GameplayActivity.this.getResources().getColorStateList(R.color.colorPrimary, null));
        answer2.setTextColor(Color.parseColor("#ffffff"));
        answer3.setBackgroundTintList(GameplayActivity.this.getResources().getColorStateList(R.color.colorPrimary, null));
        answer3.setTextColor(Color.parseColor("#ffffff"));
        answer4.setBackgroundTintList(GameplayActivity.this.getResources().getColorStateList(R.color.colorPrimary, null));
        answer4.setTextColor(Color.parseColor("#ffffff"));
    }

    private void playQuestion() {
        endTransition();
        enableButtons();
        Log.d("playQuestion", "in playQuestion");
        if (currentQuestion > questions.size()) currentQuestion = 1;
        body.setText(questions.get(currentQuestion - 1).getBody());
        // randomly assign questions to question buttons
        randomizeAnswers(questions.get(currentQuestion - 1));

        // start timer
        cur_q_time = System.currentTimeMillis();

        final int timedQuestion = currentQuestion;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (buttonsEnabled() && timedQuestion == currentQuestion) {
                    answer_time += 10000;
                    yellowHighlightCorrect();
                    disableButtons();
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            socket.emit("on_answer", "ANSWER_WRONG", 0);
                        }
                    },1500);

                }
            }
        }, 10000);

    }
    private void yellowHighlightCorrect() {
        switch(correct_loc) {
            case 1: answer1.setBackgroundTintList(GameplayActivity.this.getResources().getColorStateList(R.color.colorAccent, null)); break;
            case 2: answer2.setBackgroundTintList(GameplayActivity.this.getResources().getColorStateList(R.color.colorAccent, null)); break;
            case 3: answer3.setBackgroundTintList(GameplayActivity.this.getResources().getColorStateList(R.color.colorAccent, null)); break;
            case 4: answer2.setBackgroundTintList(GameplayActivity.this.getResources().getColorStateList(R.color.colorAccent, null)); break;
        }
    }
    private void answerChosen(Button answer, int num, long time) {
        if (num == correct_loc) {
            socket.emit("on_answer", "ANSWER_RIGHT", calculateScore((int)time));
            answer.setBackgroundTintList(GameplayActivity.this.getResources().getColorStateList(R.color.colorButtonRightAnswer, null));
            answer.setTextColor(Color.parseColor("#0f2711"));
        } else {
            socket.emit("on_answer", "ANSWER_WRONG", 0);
            yellowHighlightCorrect();
            answer.setBackgroundTintList(GameplayActivity.this.getResources().getColorStateList(R.color.colorButtonWrongAnswer, null));
            answer.setTextColor(Color.parseColor("#491212"));
        }
    }
    private void endGame(){
        socket.disconnect();
        Intent scoreIntent = new Intent(this, ScoreActivity.class);
        scoreIntent.putExtra("player_score",player_score);
        scoreIntent.putExtra("opponent_score",opponent_score);
        scoreIntent.putExtra("player_name",player_name);
        scoreIntent.putExtra("opponent_name",opponent_name);
        scoreIntent.putExtra("player_rank",player_rank);
        scoreIntent.putExtra("opponent_rank",opponent_rank);
        scoreIntent.putExtra("course_subject", course.substring(0,4));
        scoreIntent.putExtra("course_number", Integer.parseInt(course.substring(4,7)));
        scoreIntent.putExtra("response_time", answer_time/1000.0);
        scoreIntent.putExtra("num_correct", correctlyAnswered);
        scoreIntent.putExtra("questions", getIntent().getStringExtra("questions"));
        scoreIntent.putExtra("leaderboard_name", leaderboard_name);
        scoreIntent.putExtra("opponent_leaderboard_name", opponent_leaderboard_name);

        scoreIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(scoreIntent);
    }

    private void parseQuestions(String questionsString) {
        questions = new ArrayList<>();
        Log.d("Questions", questionsString);
        for (int i = 0; i < 7 ; i++) {
            try {
                JSONArray questionsJSON = new JSONArray(questionsString);
                questions.add(new Question(questionsJSON.getJSONObject(i)));
            } catch (JSONException e) {
                return;
            }
        }
    }

    public Emitter.Listener turnOver = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable(){
                @Override
                public void run() {
                    time_event = System.currentTimeMillis();
                    try {
                        JSONObject scores = new JSONObject((String) args[0]);
                        if (scores.getBoolean("correct")) {
                            if (scores.getString("user").equals(player_name)) {
                                player_score += scores.getInt("points");
                                correctlyAnswered++;
                                setInGameLoadView(player_name, player_rank);
                            } else {
                                opponent_score += scores.getInt("points");
                                setInGameLoadView(opponent_name, opponent_rank);
                                yellowHighlightCorrect();
                            }
                            disableButtons();
                            playerScore.setText("Score: " + player_score);
                            opponentScore.setText("Score: " + opponent_score);
                            currentQuestion++;
                            //if (currentQuestion > questions.size()) {

                                //Log.d("endgame","ending game 2");
                                //endGame();
                            //} else {
                                waitForQuestion();
                            //}
                        } else {
                            num_false++;
                            if (num_false > 1) {
                                yellowHighlightCorrect();
                                disableButtons();
                                currentQuestion++;
                               // if (currentQuestion > questions.size()) {
                                    //Log.d("endgame","ending game 3");
                                    //endGame();
                                //} else {
                                    setInGameLoadView();
                                    waitForQuestion();
                                //}
                            }
                        }
                    } catch (JSONException e) {
                        Log.d("fuck","json exception in turnover");
                        return;
                    }
                }
            });
        }
    };

    public Emitter.Listener readyQuestion = new Emitter.Listener(){

        @Override
        public void call(final Object... args){
            time_event = System.currentTimeMillis();
            Log.d("readyQuestion", "in readyQuestion");
            timed_out = false;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    String msg = (String) args[0];
                    if(msg.equals("READY")) {
                        Log.d("endgame","play question");
                        playQuestion();
                    } else {
                        Log.d("endgame","re-emit");
                        socket.emit("ready_next");
                    }

                }
            }, 1000);
        }
    };
    public Emitter.Listener opponentLeft = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            time_event = System.currentTimeMillis();
            Log.i("disconnect", "fuck");
            SocketHandler.setDisconnected(true);
            socket.disconnect();
            finish();
        }
    };

    public Emitter.Listener popupEmoji = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            time_event = System.currentTimeMillis();
            if(emoji_displayed){
                return;
            }
            emoji_displayed = true;
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View container = layoutInflater.inflate(R.layout.layout_emoji, null);
                    ImageView emojiImage = (ImageView) container.findViewById(R.id.emoji);
                    //ImageView avatar = (ImageView) container.findViewById(R.id.avatar);
                    //avatar.setImageDrawable(opponentAvatar.getDrawable());
                    switch((int) args[0]){
                        case EMOJI_OK:
                            emojiImage.setImageResource(R.drawable.ok_emoji);
                            break;
                        case EMOJI_BIGTHINK:
                            emojiImage.setImageResource(R.drawable.bigthink_emoji);
                            break;
                        case EMOJI_CRYLAUGH:
                            emojiImage.setImageResource(R.drawable.crylaugh_emoji);
                            break;
                        case EMOJI_FIRE:
                            emojiImage.setImageResource(R.drawable.fire_emoji);
                            break;
                        case EMOJI_HEART:
                            emojiImage.setImageResource(R.drawable.heart_emoji);
                            break;
                        case EMOJI_HUNNIT:
                            emojiImage.setImageResource(R.drawable.hunnit_emoji);
                            break;
                        case EMOJI_POOP:
                            emojiImage.setImageResource(R.drawable.poop_emoji);
                            break;
                    }

                    emojiPopup = new PopupWindow(container, 250, 250, false);
                    emojiPopup.setAnimationStyle(R.style.custom_animation);
                    int[] location_opp = new int[2];
                    try {
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                        r.play();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    opponentAvatar.getLocationOnScreen(location_opp);
                    emojiPopup.showAtLocation(findViewById(android.R.id.content), Gravity.NO_GRAVITY, location_opp[0], location_opp[1]); //TODO change location

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            emojiPopup.dismiss();
                            emoji_displayed = false;
                        }
                    }, 2000);
                }
            });
        }
    };


    private int calculateScore(int answerTime){
        return (int)((10000.0 - answerTime) / 10000.0 * 50.0 + 50) + (questions.get(currentQuestion - 1).isVerified() ? 50 : 0);
    }


    /*
        USED DURING ONCREATE
     */
    private void setEmojiListeners(){
        emoji_ok.setClickable(true);
        emoji_ok.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                socket.emit("send_emoji", EMOJI_OK);
            }
        });

        emoji_fire.setClickable(true);
        emoji_fire.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                socket.emit("send_emoji", EMOJI_FIRE);
            }
        });

        emoji_bigthink.setClickable(true);
        emoji_bigthink.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                socket.emit("send_emoji", EMOJI_BIGTHINK);
            }
        });
        emoji_crylaugh.setClickable(true);
        emoji_crylaugh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                socket.emit("send_emoji", EMOJI_CRYLAUGH);
            }
        });

        emoji_heart.setClickable(true);
        emoji_heart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                socket.emit("send_emoji", EMOJI_HEART);
            }
        });

        emoji_hunnit.setClickable(true);
        emoji_hunnit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                socket.emit("send_emoji", EMOJI_HUNNIT);
            }
        });

        emoji_poop.setClickable(true);
        emoji_poop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                socket.emit("send_emoji", EMOJI_POOP);
            }
        });
    }



    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameplayActivity.this);
        builder.setMessage("Are you sure you want to exit the game?")
                .setCancelable(true)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        socket.emit("leave_early");
                        Intent intent = new Intent(GameplayActivity.this, CourseSelectActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        Log.d("endgame","ending game 2");
//        socket.emit("leave_early");
//        socket.disconnect();
//        finish();
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.d("endgame","ending game 3");
//        socket.emit("leave_early");
//        socket.disconnect();
//        finish();
//    }
}
