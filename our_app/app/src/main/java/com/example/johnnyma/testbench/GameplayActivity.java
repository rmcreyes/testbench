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
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class GameplayActivity extends AppCompatActivity  {

    // emoji encondings
    private static final int EMOJI_OK = 0;
    private static final int EMOJI_BIGTHINK = 1;
    private static final int EMOJI_FIRE = 2;
    private static final int EMOJI_POOP = 3;
    private static final int EMOJI_HUNNIT = 4;
    private static final int EMOJI_HEART = 5;
    private static final int EMOJI_CRYLAUGH = 6;

    // socket handle
    private Socket socket;

    // handles for all layout elements
    private Button answer1;
    private Button answer2;
    private Button answer3;
    private Button answer4;
    private TextView body;
    private TextView playerName;
    private TextView opponentName;
    private TextView playerScore;
    private TextView opponentScore;
    private TextView courseHeader;
    private TextView questionHeader;
    private ImageView playerAvatar;
    private ImageView opponentAvatar;
    private TextView loadingText;
    private TextView roundWinnerText;
    private TextView winText;
    private ImageView winnerAvatar;
    private ImageView emoji_bigthink;
    private ImageView emoji_crylaugh;
    private ImageView emoji_heart;
    private ImageView emoji_poop;
    private ImageView emoji_hunnit;
    private ImageView emoji_fire;
    private ImageView emoji_ok;
    private PopupWindow emojiPopup;
    private LayoutInflater layoutInflater;
    private CardView loading_card;
    private Handler handler;

    // holds all questions for the match
    private ArrayList<Question> questions;

    // values related to the current game and players
    private String course;
    private int player_score;
    private int opponent_score;
    private String player_name;
    private String opponent_name;
    private String opponent_leaderboard_name;
    private String leaderboard_name;
    private int player_rank;
    private int opponent_rank;

    // values related to the state of the game
    private int currentQuestion;
    private int answer_time;
    private boolean buttonsEnabled;
    private Object lock;
    private int num_false;
    private int correct_loc;
    private int correctlyAnswered;
    private long cur_q_time;
    private boolean emoji_displayed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);

        // set initial conditions for the game
        currentQuestion = 1;
        answer_time = 0;
        num_false = 0;
        correctlyAnswered = 0;
        buttonsEnabled = false;
        lock = new Object();
        cur_q_time = 0;
        emoji_displayed = false;
        handler = new Handler();
        player_score = 0;
        opponent_score = 0;

        // find all ui elements
        loading_card = findViewById(R.id.loading_card);
        loading_card.setVisibility(View.INVISIBLE);
        courseHeader = findViewById(R.id.course_header);
        playerName = findViewById(R.id.player_name);
        playerAvatar = findViewById(R.id.player_avatar);
        opponentName = findViewById(R.id.opponent_name);
        opponentAvatar = findViewById(R.id.opponent_avatar);
        playerScore = findViewById(R.id.player_score);
        opponentScore = findViewById(R.id.opponent_score);
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
        emoji_ok = findViewById(R.id.ok_emoji2);
        emoji_poop = findViewById(R.id.ok_emoji);
        emoji_bigthink = findViewById(R.id.bigthink_emoji);
        emoji_fire = findViewById(R.id.ok_emoji3);
        emoji_hunnit = findViewById(R.id.hunnit_emoji2);
        emoji_crylaugh = findViewById(R.id.crylaugh_emoji);
        emoji_heart = findViewById(R.id.heart_emoji);

        // get all info from intent
        Intent starting_intent = getIntent();
        course = starting_intent.getStringExtra("course");
        player_name = starting_intent.getStringExtra("alias");
        leaderboard_name = starting_intent.getStringExtra("leaderboard_name");
        opponent_leaderboard_name = starting_intent.getStringExtra("opponent_leaderboard_name");
        player_rank = starting_intent.getIntExtra("player_rank", 1);
        opponent_name = starting_intent.getStringExtra("opponent_alias");
        opponent_rank = starting_intent.getIntExtra("opponent_rank", 1);

        // configure display based on intent info
        courseHeader.setText(course.substring(0,4)+ " " + course.substring(4, 7));
        playerName.setText(player_name);
        setAvatar(playerAvatar,player_rank);
        opponentName.setText(opponent_name);
        setAvatar(opponentAvatar,opponent_rank);
        playerScore.setText("Score: " + player_score);
        opponentScore.setText("Score: " + opponent_score);
        parseQuestions(starting_intent.getStringExtra("questions"));

        //set emoji views and onclick listners for emojis
        setEmojiListeners();
        setSocketListeners();
        setButtonListeners();
        setInitialLoadView();
        waitForQuestion();
    }

    private void waitForQuestion() {
        yellowHighlightCorrect();
        num_false = 0;
        resetButtonColors();
        startTransition();
        if(currentQuestion <= 7) {
            questionHeader.setText("Question " + currentQuestion + " of 7");
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    socket.emit("ready_next");
                }
            }, 500);
        } else {
            endGame();
        }
    }

    private void playQuestion() {
        endTransition();
        enableButtons();
        if (currentQuestion > 7) {
            endGame();
        } else {
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
                        }, 1500);

                    }
                }
            }, 10000);
        }
    }
    private void setSocketListeners(){
        socket = SocketHandler.getSocket();
        socket.on("broadcast_emoji", popupEmoji);
        socket.on("turn_over", turnOver);
        socket.on("start_question", readyQuestion);
        socket.on("broadcast_leave", opponentLeft);
    }

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
        loadingText.setText("Get Ready for \n  Question " + (currentQuestion + 1) + "!");
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
        loading_card.setVisibility(View.VISIBLE);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        loading_card.startAnimation(slideUp);
    }

    private void endTransition() {
        Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        loading_card.startAnimation(slideDown);
        loading_card.setVisibility(View.INVISIBLE);
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

    private void yellowHighlightCorrect() {
        switch(correct_loc) {
            case 1: answer1.setBackgroundTintList(GameplayActivity.this.getResources().getColorStateList(R.color.colorAccent, null)); break;
            case 2: answer2.setBackgroundTintList(GameplayActivity.this.getResources().getColorStateList(R.color.colorAccent, null)); break;
            case 3: answer3.setBackgroundTintList(GameplayActivity.this.getResources().getColorStateList(R.color.colorAccent, null)); break;
            case 4: answer4.setBackgroundTintList(GameplayActivity.this.getResources().getColorStateList(R.color.colorAccent, null)); break;
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
        currentQuestion = 1;
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
                            waitForQuestion();
                        } else {
                            num_false++;
                            if (num_false > 1) {
                                yellowHighlightCorrect();
                                disableButtons();
                                currentQuestion++;
                                setInGameLoadView();
                                waitForQuestion();
                            }
                        }
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };

    public Emitter.Listener readyQuestion = new Emitter.Listener(){

        @Override
        public void call(final Object... args){
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    String msg = (String) args[0];
                    if(msg.equals("READY")) {
                        playQuestion();
                    } else {
                        socket.emit("ready_next");
                    }

                }
            }, 1000);
        }
    };
    public Emitter.Listener opponentLeft = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            SocketHandler.setDisconnected(true);
            socket.disconnect();
            finish();
        }
    };

    public Emitter.Listener popupEmoji = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
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
                        socket.disconnect();
                        finish();
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

}
