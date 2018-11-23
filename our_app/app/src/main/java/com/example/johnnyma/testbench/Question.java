package com.example.johnnyma.testbench;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;

public class Question {
    private String id;
    private String body;
    private String correctAnswer;
    private String incorrectAnswer1;
    private String incorrectAnswer2;
    private String incorrectAnswer3;
    private int rating;
    private boolean verified;
    private boolean reported;
    private String json_string;

    public Question(JSONObject questionJSON) {
        Log.d("question constructor", questionJSON.toString());
        try {
            id = questionJSON.getString("_id");
            Log.i("id", id);
            body = questionJSON.getString("question_text");
            Log.i("question_text", body);
            correctAnswer = questionJSON.getString("correct_answer");
            Log.i("correct_answer", correctAnswer);
            incorrectAnswer1 = questionJSON.getString("incorrect_answer_1");
            Log.i("incorrect_answer_1", incorrectAnswer1);
            incorrectAnswer2 = questionJSON.getString("incorrect_answer_2");
            Log.i("incorrect_answer_2", incorrectAnswer2);
            incorrectAnswer3 = questionJSON.getString("incorrect_answer_3");
            Log.i("incorrect_answer_3", incorrectAnswer3);
            verified = questionJSON.getBoolean("verified");
            Log.i("verified", Boolean.toString(verified));
            reported = questionJSON.getBoolean("reported");
            Log.i("reported", Boolean.toString(reported));
            json_string = questionJSON.toString();
            Log.i("json", json_string);
            rating = (int) questionJSON.getDouble("rating");
            Log.i("rating", Integer.toString(rating));
        } catch (JSONException e) {
            rating = -1;
            return;
        }
    }

    public String getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getIncorrectAnswer1() {
        return incorrectAnswer1;
    }

    public String getIncorrectAnswer2() {
        return incorrectAnswer2;
    }

    public String getIncorrectAnswer3() {
        return incorrectAnswer3;
    }

    public int getRating() {
        return rating;
    }

    public boolean isVerified() {
        return verified;
    }

    public boolean isReported() {
        return reported;
    }

    public String getJSONString() {return json_string;}
}