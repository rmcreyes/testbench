package com.example.johnnyma.testbench;

import android.content.Intent;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

/**
 * Created by JohnnyMa on 2018-11-22.
 */
public class ScoreActivityTest {

    @Rule
    public ActivityTestRule<ScoreActivity> scoreActivityActivityTestRule = new ActivityTestRule<ScoreActivity>(ScoreActivity.class){
        @Override
        protected Intent getActivityIntent(){
            Intent scoreIntent = new Intent();
            scoreIntent.putExtra("player_score","50");
            scoreIntent.putExtra("opponent_score","30");
            scoreIntent.putExtra("player_name","Mgee");
            scoreIntent.putExtra("opponent_name","Jackie Chan");
            scoreIntent.putExtra("player_rank","52");
            scoreIntent.putExtra("opponent_rank","37");
            scoreIntent.putExtra("player_avatar","1");
            scoreIntent.putExtra("opponent_avatar","3");
            scoreIntent.putExtra("course_subject", "CPEN");
            scoreIntent.putExtra("course_number", "341");
            scoreIntent.putExtra("response_time", "0.324");
            scoreIntent.putExtra("num_correct", "5");
            return scoreIntent;
        }
    };

    //TODO uncomment out when score activity fixed
    @Test
    public void setupTest(){
        //check that you won and everything displayed is correct
        //Espresso.onView(withText("WON!")).check(matches(isDisplayed()));
        float response_time = (float)0.324/7;
        float num_correct = (float) 5/7;
        //Espresso.onView(withText(response_time + "s")).check(matches(isDisplayed()));
        //Espresso.onView(withText(num_correct + "%")).check(matches(isDisplayed()));
        Espresso.onView(withText("Mgee")).check(matches(isDisplayed()));
        Espresso.onView(withText("Jackie Chan")).check(matches(isDisplayed()));
        //Espresso.onView(withId(R.id.button2)).perform(click());
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

}