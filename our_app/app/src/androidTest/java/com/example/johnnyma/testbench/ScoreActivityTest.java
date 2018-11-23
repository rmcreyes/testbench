package com.example.johnnyma.testbench;

import android.content.Intent;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
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
            return scoreIntent;
        }
    };

    @Test
    public void setupTest(){
        try{
            Thread.sleep(10000);
        } catch (Exception e){

        }
        Espresso.onView(withId(R.id.done)).perform(click());
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

}