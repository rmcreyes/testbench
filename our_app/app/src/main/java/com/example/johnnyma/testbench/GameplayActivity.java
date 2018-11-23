package com.example.johnnyma.testbench;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Random;

/*
 * TODO: add loading question dialog
 * TODO: add emoji-sending code
 */


public class GameplayActivity extends AppCompatActivity {
    Socket socket; // socket handle
    // handles for all layout elements
    Button incorrect1;
    Button incorrect2;
    Button incorrect3;
    Button correct;
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

    LoadingQuestionFragment loadingQuestionFragment;
    String course;
    int player_score;
    int opponent_score;
    String player_name;
    String opponent_name;
    int player_avatar;
    int opponent_avatar;
    int currentQuestion = 1;
    boolean answered = false;
    int answer_time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);
        Intent starting_intent = getIntent();

        course = starting_intent.getStringExtra("course");
        courseHeader = findViewById(R.id.course_header);
        courseHeader.setText(course);

        player_name = starting_intent.getStringExtra("player_name");
        playerName = findViewById(R.id.player_name);
        playerName.setText(player_name);

        player_avatar = starting_intent.getIntExtra("player_name", 0);
        playerAvatar = findViewById(R.id.player_avatar);
        setPlayerAvatar();

        opponent_name = starting_intent.getStringExtra("opponent_name");
        opponentName = findViewById(R.id.opponent_name);
        opponentName.setText(opponent_name);

        opponent_avatar = starting_intent.getIntExtra("opponent_rank", 0);
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

        parseQuestions(starting_intent.getStringExtra("questions"));

        socket = SocketHandler.getSocket();
        waitForQuestion();
    }
    protected void setPlayerAvatar(){
        switch(player_avatar % 6) {
            case 0:
                playerAvatar.setImageResource(R.drawable.penguin_avatar);
            case 1:
                playerAvatar.setImageResource(R.drawable.mountain_avatar);
            case 2:
                playerAvatar.setImageResource(R.drawable.rocket_avatar);
            case 3:
                playerAvatar.setImageResource(R.drawable.frog_avatar);
            case 4:
                playerAvatar.setImageResource(R.drawable.thunderbird_avatar);
            case 5:
                playerAvatar.setImageResource(R.drawable.cupcake_avatar);
        }
    }

    protected void setOpponentAvatar(){
        switch(opponent_avatar % 6) {
            case 0:
                opponentAvatar.setImageResource(R.drawable.penguin_avatar);
            case 1:
                opponentAvatar.setImageResource(R.drawable.mountain_avatar);
            case 2:
                opponentAvatar.setImageResource(R.drawable.rocket_avatar);
            case 3:
                opponentAvatar.setImageResource(R.drawable.frog_avatar);
            case 4:
                opponentAvatar.setImageResource(R.drawable.thunderbird_avatar);
            case 5:
                opponentAvatar.setImageResource(R.drawable.cupcake_avatar);
        }
    }


    protected void randomizeAnswers(Question q){
        ArrayList<Integer> answers = new ArrayList<>();
        Random random = new Random();
        int rand;
        while (answers.size() < 4) {
            rand = random.nextInt() % 4 + 1;
            if(answers.contains(rand))
                answers.add(rand);
        }
        switch (answers.indexOf(1)) {
            case 0:
                incorrect1 = findViewById(R.id.answer_1);
                break;
            case 1:
                incorrect1 = findViewById(R.id.answer_2);
                break;
            case 2:
                incorrect1 = findViewById(R.id.answer_3);
            case 3:
                incorrect1 = findViewById(R.id.answer_4);
        }
        incorrect1.setText(q.incorrectAnswer1);
        switch (answers.indexOf(2)) {
            case 0:
                incorrect2 = findViewById(R.id.answer_1);
                break;
            case 1:
                incorrect2 = findViewById(R.id.answer_2);
                break;
            case 2:
                incorrect2 = findViewById(R.id.answer_3);
            case 3:
                incorrect2 = findViewById(R.id.answer_4);
        }
        incorrect2.setText(q.incorrectAnswer2);
        switch (answers.indexOf(3)) {
            case 0:
                incorrect3 = findViewById(R.id.answer_1);
                break;
            case 1:
                incorrect3 = findViewById(R.id.answer_2);
                break;
            case 2:
                incorrect3 = findViewById(R.id.answer_3);
            case 3:
                incorrect3 = findViewById(R.id.answer_4);
        }
        incorrect3.setText(q.incorrectAnswer3);
        switch (answers.indexOf(4)) {
            case 0:
                correct = findViewById(R.id.answer_1);
                break;
            case 1:
                correct = findViewById(R.id.answer_2);
                break;
            case 2:
                correct = findViewById(R.id.answer_3);
            case 3:
                correct = findViewById(R.id.answer_4);
        }
        correct.setText(q.correctAnswer);
    }

    protected void endTurn(){
        socket.on("turn_over", turnOver);
    }

    protected void waitForQuestion() {
        // set fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putString("message", "Get Ready for \n  Question"+ currentQuestion +"!");
        loadingQuestionFragment = new LoadingQuestionFragment();
        loadingQuestionFragment.setArguments(args);
        fragmentTransaction.add(R.id.fragment_container, loadingQuestionFragment).commit();
        questionHeader.setText("Question " + currentQuestion + " of 7");
        if (currentQuestion > 7)
            endGame();
        else {
            socket.emit("ready_next");
            // TODO: find a better way to do this
            Toast.makeText(GameplayActivity.this, "shit", Toast.LENGTH_LONG).show();
            socket.on("start_question", readyQuestion);
        }
    }
    protected void playQuestion() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.remove(loadingQuestionFragment).commit();
        answered = false;
        body.setText(questions.get(currentQuestion - 1).body);
        // randomly assign questions to question buttons
        randomizeAnswers(questions.get(currentQuestion - 1));

        // start timer
        final int time = (int)System.currentTimeMillis();
        incorrect1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!answered) {
                    answer_time += System.currentTimeMillis() - time;
                    answered = true;
                    // add to event answer time
                    socket.emit("answer_wrong");
                    incorrect1.setBackgroundColor(0xd69191);
                    incorrect1.setTextColor(0x880000);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            endTurn();
                        }
                    }, 1000);
                }
            }
        });

        incorrect2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!answered) {
                    answer_time += System.currentTimeMillis() - time;
                    answered = true;
                    // add to event answer time
                    socket.emit("answer_wrong");
                    incorrect2.setBackgroundColor(0xd69191);
                    incorrect2.setTextColor(0x880000);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            endTurn();
                        }
                    }, 1000);
                }
            }
        });

        incorrect3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!answered) {
                    answer_time += System.currentTimeMillis() - time;
                    answered = true;
                    // add to event answer time
                    socket.emit("answer_wrong");
                    incorrect3.setBackgroundColor(0xd69191);
                    incorrect3.setTextColor(0x880000);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //end turn
                        }
                    }, 1000);
                }
            }
        });

        correct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!answered) {
                    answer_time += System.currentTimeMillis() - time;
                    answered = true;
                    correct.setBackgroundColor(0x885f89);
                    correct.setTextColor(0x29722f);
                    int score = calculateScore((int) System.currentTimeMillis() - time);
                    socket.emit("answer_right", score);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            endTurn();
                        }
                    }, 1000);

                }
            }
        });
        socket.on("turn_over", turnOver);
        while (System.currentTimeMillis() - time < 10000);
        socket.emit("answer_wrong");
        answer_time += 10000;
        endTurn();
        // update score based on contents attached to event
    }

    protected void endGame(){
        return;
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
                        player_score = scores.getInt(player_name);
                        opponent_score = scores.getInt(opponent_name);
                    } catch (JSONException e) {
                        return;
                    }
                    playerScore.setText("Score: " + player_score);
                    opponentScore.setText("Score: " + opponent_score);
                    currentQuestion++;
                    waitForQuestion();
                }
            });
        }
    };

    public Emitter.Listener readyQuestion = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            Log.d("readyQuestion", "in readyQuestion");

            // int time = (int)System.currentTimeMillis();
            // while(System.currentTimeMillis() - time < 3000);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable(){
                @Override
                public void run() {
                    Log.d("readyQuestion", "about to play question");
                    playQuestion();
                }
            });
        }
    };

    protected int calculateScore(int answerTime){
        return (10000 - answerTime) / 10000 * 500 + 500;
    }

    private class Question {
        String id;
        String body;
        String correctAnswer;
        String incorrectAnswer1;
        String incorrectAnswer2;
        String incorrectAnswer3;
        boolean profEndorsed;

        public Question(JSONObject questionJSON) {
            try {
                id = questionJSON.getString("id");
                body = questionJSON.getString("question_text");
                correctAnswer = questionJSON.getString("correct_answer");
                incorrectAnswer1 = questionJSON.getString("incorrect_answer_1");
                incorrectAnswer2 = questionJSON.getString("incorrect_answer_2");
                incorrectAnswer3 = questionJSON.getString("incorrect_answer_3");
                String endorsed = questionJSON.getString("professor_endorsed");
                profEndorsed = endorsed.equals("1") ? true : false;
            } catch (JSONException e) {
                return;
            }
        }
    }
}
