package com.example.johnnyma.testbench;

import android.content.Intent;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

/**
 * Created by JohnnyMa on 2018-11-24.
 */
public class CourseSelectActivityTest {

    @Rule
    public ActivityTestRule<CourseSelectActivity> courseSelectActivityActivityTestRule =
            new ActivityTestRule<CourseSelectActivity>(CourseSelectActivity.class){
                @Override
                protected Intent getActivityIntent(){
                    Intent intent = new Intent();
                    intent.putExtra("name", "Johnny");
                    GlobalTokens.JWT_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJDb2RlV29ya3IiLCJzdWIiOiI1YmVkMjNkODIyNWUyYjdiM2MyYTBkZWEiLCJpYXQiOjE1NDI0ODg2NzUxNjYsImV4cCI6MTU0NTA4MDY3NTE2Nn0.HFjfsSAV8oaIVI6-FzbZl8mVxOA9hFdJEtZRKx3MBZE";
                    GlobalTokens.USER_ID = "5bf0c36bba9469097259c399";
                    return intent;
                }
            };

    private String courseName = "CPEN";
    private String courseID = "321";

    @Test
    public void testAddCourse(){
        //click on fab
        Espresso.onView(withId(R.id.fab)).perform(click());
        //check if the dialog box appears
        Espresso.onView(withText("ADD A COURSE!")).check(matches(isDisplayed()));
        //enter coursename and courseID
        Espresso.onView(withId(R.id.subj_text)).perform(typeText(courseName));
        Espresso.onView(withId(R.id.num_text)).perform(typeText(courseID));
        Espresso.closeSoftKeyboard();

        //click submit
        Espresso.onView(withId(R.id.submit_btn)).perform(click());
        Espresso.pressBack();
        //checks if the button is made
        Espresso.onView(withText(courseID)).check(matches(isDisplayed()));
        Espresso.onView(withText(courseName)).check(matches(isDisplayed()));
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

}