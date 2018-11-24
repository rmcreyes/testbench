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
 * Created by JohnnyMa on 2018-11-24.
 */
public class GameplayActivityTest {
    @Rule
    public ActivityTestRule<GameplayActivity> GameplayActivityTestRule = new ActivityTestRule<GameplayActivity>(GameplayActivity.class){
        @Override
        protected Intent getActivityIntent(){
            Intent intent = new Intent();
            intent.putExtra("course", "CPEN311");
            intent.putExtra("player_name", "Bob");
            intent.putExtra("player_rank", 2);
            intent.putExtra("player_avatar", 2);

            intent.putExtra("opponent_name", "Tom");
            intent.putExtra("opponent_avatar", 3);
            intent.putExtra("opponent_rank", 3);
            return intent;
        }
    };

    @Test
    public void setupTest(){
        Espresso.onView(withText("Bob")).check(matches(isDisplayed()));
        Espresso.onView(withText("Tom")).check(matches(isDisplayed()));
        Espresso.onView(withText("CPEN 311")).check(matches(isDisplayed()));
        Espresso.onView(withText("Question 1 of 7")).check(matches(isDisplayed()));
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

}