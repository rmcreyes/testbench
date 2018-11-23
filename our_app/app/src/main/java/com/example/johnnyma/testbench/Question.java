package com.example.johnnyma.testbench;


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
        try {
            id = questionJSON.getString("id");
            body = questionJSON.getString("question_text");
            correctAnswer = questionJSON.getString("correct_answer");
            incorrectAnswer1 = questionJSON.getString("incorrect_answer_1");
            incorrectAnswer2 = questionJSON.getString("incorrect_answer_2");
            incorrectAnswer3 = questionJSON.getString("incorrect_answer_3");
            rating = questionJSON.getInt("rating");
            verified = questionJSON.getBoolean("verified");
            reported = questionJSON.getBoolean("reported");
            json_string = questionJSON.toString();
        } catch (JSONException e) {
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