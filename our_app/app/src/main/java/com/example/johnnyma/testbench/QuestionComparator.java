package com.example.johnnyma.testbench;

import java.util.Comparator;

public class QuestionComparator implements Comparator<Question> {

    @Override
    public int compare(Question question, Question t1) {
        return question.getRating() > t1.getRating() ?
                1 :
                question.getRating() < t1.getRating() ?
                        -1 : 0;
    }
}
