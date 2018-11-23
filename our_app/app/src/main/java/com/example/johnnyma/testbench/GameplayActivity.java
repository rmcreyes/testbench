package com.example.johnnyma.testbench;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
    int player_avatar;
    int opponent_avatar;
    int currentQuestion = 1;
    int answer_time = 0;
    boolean answered = false;
    boolean turn_ended = false;
    int player_rank;
    int opponent_rank;
    int num_false = 0;
    int correct_loc;
    int correctlyAnswered = 0;
    String round_winner = "";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);
        Intent starting_intent = getIntent();

        course = starting_intent.getStringExtra("course");

        courseHeader = findViewById(R.id.course_header);
        courseHeader.setText(course.substring(0,4)+ " " + course.substring(4, 7));

        player_name = starting_intent.getStringExtra("player_name");
        playerName = findViewById(R.id.player_name);
        playerName.setText(player_name);

        player_avatar = starting_intent.getIntExtra("player_rank", 1);
        player_rank = starting_intent.getIntExtra("player_rank", 1);
        playerAvatar = findViewById(R.id.player_avatar);
        setPlayerAvatar();

        opponent_name = starting_intent.getStringExtra("opponent_name");
        opponentName = findViewById(R.id.opponent_name);
        opponentName.setText(opponent_name);

        opponent_avatar = starting_intent.getIntExtra("opponent_rank", 1);
        opponent_rank = starting_intent.getIntExtra("opponent_rank", 1);
        opponentAvatar = findViewById(R.id.opponent_avatar);
        setOpponentAvatar();


        player_score = 0;
        playerScore = findViewById(R.id.player_score);
        playerScore.setText("Score: " + player_score);

        opponent_score = 0;
        opponentScore = findViewById(R.id.opponent_score);
        opponentScore.setText("Score: " + opponent_score);

        body = findViewById(R.id.question_body);

        questionHeader = findViewById(R.id.question_num);

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
        waitForQuestion();
    }
    protected void setPlayerAvatar(){
        switch(player_avatar % 6) {
            case 0:
                playerAvatar.setImageResource(R.drawable.penguin_avatar);
                break;
            case 1:
                playerAvatar.setImageResource(R.drawable.mountain_avatar);
                break;
            case 2:
                playerAvatar.setImageResource(R.drawable.rocket_avatar);
                break;
            case 3:
                playerAvatar.setImageResource(R.drawable.frog_avatar);
                break;
            case 4:
                playerAvatar.setImageResource(R.drawable.thunderbird_avatar);
                break;
            case 5:
                playerAvatar.setImageResource(R.drawable.cupcake_avatar);
                break;
        }
    }

    protected void setOpponentAvatar(){
        switch(opponent_avatar % 6) {
            case 0:
                opponentAvatar.setImageResource(R.drawable.penguin_avatar);
                break;
            case 1:
                opponentAvatar.setImageResource(R.drawable.mountain_avatar);
                break;
            case 2:
                opponentAvatar.setImageResource(R.drawable.rocket_avatar);
                break;
            case 3:
                opponentAvatar.setImageResource(R.drawable.frog_avatar);
                break;
            case 4:
                opponentAvatar.setImageResource(R.drawable.thunderbird_avatar);
                break;
            case 5:
                opponentAvatar.setImageResource(R.drawable.cupcake_avatar);
                break;
        }
    }


    protected void randomizeAnswers(Question q){
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

    protected void waitForQuestion() {
        num_false = 0;
        resetButtonColors();
        Bundle args = new Bundle();
        if (currentQuestion > 7) {
            endGame();
        } else {
            args.putString("next_q_msg", "Get Ready for \n  Question " + currentQuestion + "!");
            args.putString("round_winner", round_winner);
            args.putInt("winner_avatar", round_winner.equals(player_name) ? player_avatar : opponent_avatar);
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        LoadingQuestionFragment loadingQuestionFragment = new LoadingQuestionFragment();
        loadingQuestionFragment.setArguments(args);
        ft.add(R.id.fragment_container, loadingQuestionFragment, "loading_question");
        ft.commit();
        if(currentQuestion < 8) {
            questionHeader.setText("Question " + currentQuestion + " of 7");
            socket.emit("ready_next");
        } else {
            endGame();
        }
        // TODO: find a better way to do this
        Toast.makeText(GameplayActivity.this, "shit", Toast.LENGTH_LONG).show();
    }
    protected void resetButtonColors(){
        answer1.setBackgroundTintList(GameplayActivity.this.getResources().getColorStateList(R.color.colorPrimary, null));
        answer1.setTextColor(Color.parseColor("#ffffff"));
        answer2.setBackgroundTintList(GameplayActivity.this.getResources().getColorStateList(R.color.colorPrimary, null));
        answer2.setTextColor(Color.parseColor("#ffffff"));
        answer3.setBackgroundTintList(GameplayActivity.this.getResources().getColorStateList(R.color.colorPrimary, null));
        answer3.setTextColor(Color.parseColor("#ffffff"));
        answer4.setBackgroundTintList(GameplayActivity.this.getResources().getColorStateList(R.color.colorPrimary, null));
        answer4.setTextColor(Color.parseColor("#ffffff"));
    }
    protected void playQuestion() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        LoadingQuestionFragment loadingQuestionFragment = (LoadingQuestionFragment) fm.findFragmentByTag("loading_question");
        ft.remove(loadingQuestionFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.commit();
        answered = false;
        turn_ended = false;
        Log.d("playQuestion", "in playQuestion");

        body.setText(questions.get(currentQuestion - 1).getBody());
        // randomly assign questions to question buttons
        randomizeAnswers(questions.get(currentQuestion - 1));

        // start timer
        final int time = (int)System.currentTimeMillis();
        answer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!answered && !turn_ended) {
                    answered = true;
                    answer_time += System.currentTimeMillis() - time;
                    answerChosen(answer1, 1, (int)System.currentTimeMillis() - time);
                }
            }
        });

        answer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!answered && !turn_ended) {
                    answered = true;
                    answer_time += System.currentTimeMillis() - time;
                    answerChosen(answer2, 2, (int)System.currentTimeMillis() - time);

                }
            }
        });

        answer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!answered && !turn_ended) {
                    answered = true;
                    answer_time += System.currentTimeMillis() - time;
                    answerChosen(answer3, 3, (int)System.currentTimeMillis() - time);

                }

            }
        });

        answer4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!answered && !turn_ended) {
                    answered = true;
                    answer_time += System.currentTimeMillis() - time;
                    answerChosen(answer4, 4, (int) System.currentTimeMillis() - time);
                }
            }
        });
        final int timedQuestion = currentQuestion;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (!answered && !turn_ended && timedQuestion == currentQuestion) {
                    socket.emit("on_answer", "ANSWER_WRONG", 0);
                    answer_time += 10000;
                    answered = true;
                }
            }
        }, 10000);

    }

    protected void answerChosen(Button answer, int num, int time) {
        if (num == correct_loc) {
            socket.emit("on_answer", "ANSWER_RIGHT", calculateScore(time));
            answer.setBackgroundTintList(GameplayActivity.this.getResources().getColorStateList(R.color.colorButtonRightAnswer, null));
            answer.setTextColor(Color.parseColor("#0f2711"));
            int score = calculateScore(time);
        } else {
            socket.emit("on_answer", "ANSWER_WRONG", 0);
            answer.setBackgroundTintList(GameplayActivity.this.getResources().getColorStateList(R.color.colorButtonWrongAnswer, null));
            answer.setTextColor(Color.parseColor("#491212"));
        }
    }
    protected void endGame(){
        Log.d("response time (ms)", Integer.toString(answer_time));
        Log.d("number correct", Integer.toString(correctlyAnswered));
        Intent scoreIntent = new Intent(this, ScoreActivity.class);
        scoreIntent.putExtra("player_score",player_score);
        scoreIntent.putExtra("opponent_score",opponent_score);
        scoreIntent.putExtra("player_name",player_name);
        scoreIntent.putExtra("opponent_name",opponent_name);
        scoreIntent.putExtra("player_rank",player_rank);
        scoreIntent.putExtra("opponent_rank",opponent_rank);
        scoreIntent.putExtra("player_avatar",player_avatar);
        scoreIntent.putExtra("opponent_avatar",opponent_avatar);
        scoreIntent.putExtra("course_subject", course.substring(0,4));
        scoreIntent.putExtra("course_number", Integer.parseInt(course.substring(4,7)));
        scoreIntent.putExtra("response_time", answer_time/1000.0);
        scoreIntent.putExtra("num_correct", correctlyAnswered);
        startActivity(scoreIntent);
    }

    protected void parseQuestions(String questionsString) {
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
                    try {
                        JSONObject scores = new JSONObject((String) args[0]);
                        if (scores.getBoolean("correct")) {
                            if (scores.getString("user").equals(player_name)) {
                                player_score += scores.getInt("points");
                                correctlyAnswered++;
                                round_winner = player_name;
                            } else {
                                opponent_score += scores.getInt("points");
                                round_winner = opponent_name;
                            }
                            turn_ended = true;
                            playerScore.setText("Score: " + player_score);
                            opponentScore.setText("Score: " + opponent_score);
                            currentQuestion++;
                            if (currentQuestion > 7) {
                                endGame();
                            } else {
                                waitForQuestion();
                            }
                        } else {
                            num_false++;
                            if (num_false > 1) {
                                turn_ended = true;
                                currentQuestion++;
                                round_winner = "no winner";
                                if (currentQuestion > 7) {
                                    endGame();
                                } else {
                                    waitForQuestion();
                                }
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
            Log.d("readyQuestion", "in readyQuestion");

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.d("readyQuestion", "about to play question");
                    playQuestion();
                }
            }, 1000);
        }
    };
    public Emitter.Listener opponentLeft = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            AlertDialog.Builder builder = new AlertDialog.Builder(GameplayActivity.this);
            builder.setMessage("You opponent disconnected. You will be brought back to the main page.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(GameplayActivity.this, CourseSelectActivity.class);
                            startActivity(intent);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    };


    public Emitter.Listener getQuestions = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable(){
                @Override
                public void run() {
                    try {
                        JSONArray questions = new JSONArray((String) args[0]);
                        parseQuestions(questions.toString());
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };


    public Emitter.Listener popupEmoji = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View container = layoutInflater.inflate(R.layout.layout_emoji, null);
                    ImageView emojiImage = (ImageView) container.findViewById(R.id.emoji);
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

                    emojiPopup = new PopupWindow(container, 100, 100, false);
                    emojiPopup.showAtLocation(findViewById(android.R.id.content), Gravity.NO_GRAVITY, 500, 500); //TODO change location

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            emojiPopup.dismiss();
                        }
                    }, 2000);
                }
            });
        }
    };


    protected int calculateScore(int answerTime){
        return (int)((10000.0 - answerTime) / 10000.0 * 50.0 + 50) + (questions.get(currentQuestion - 1).isVerified() ? 50 : 0);
    }


    /*
        USED DURING ONCREATE
     */
    protected void setEmojiListeners(){
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



}
