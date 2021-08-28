package com.taskmaster.app;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.taskmaster.view.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.contrib.RecyclerViewActions;

@RunWith(AndroidJUnit4.class)
public class AppEspressoTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityActivityScenarioRule= new ActivityScenarioRule<>(MainActivity.class);
    private Object RecyclerViewActions;


    @Test
    public void testTheMainActivity(){
        onView(withId(R.id.myTasks)).check(matches(withText("My Tasks")));
    }

    @Test
    public void testTheSettingsActivity(){
        onView(withId(R.id.settingsButton)).perform(click());
        onView(withId(R.id.settingsTitle)).check(matches(withText("Add Task")));
    }


    @Test
    public void testTheAddTaskActivity(){
        onView(withId(R.id.addTaskButton)).perform(click());
        onView(withId(R.id.AddATask)).check(matches(withText("Add Task")));
    }

    @Test
    public void testTheAllTaskActivity(){
        onView(withId(R.id.allTasksButton)).perform(click());
        onView(withId(R.id.allTasksLabel)).check(matches(withText("All Tasks")));

    }

    @Test
    public void testAddNewTask(){
        onView(withId(R.id.addTaskButton)).perform(click());

        onView(withId(R.id.newTaskName)).perform(typeText("New Task"), closeSoftKeyboard());
        onView(withId(R.id.newTaskBody)).perform(typeText("Task Details"), closeSoftKeyboard());

        onView(withId(R.id.newTaskSubmit)).perform(click());
        pressBack();
        onView(ViewMatchers.withId(R.id.List_tasks)).check(matches(isDisplayed()));
    }

    @Test
    public void testOpenTaskAtTaskDetail() throws InterruptedException {
        onView(withId(R.id.List_tasks)).check(matches(isDisplayed()));
        Thread.sleep(5000);
        onView(withId(R.id.List_tasks)).perform(RecyclerViewActions.actiononItemPosition(0 , click()));

        onView(withId(R.id.taskDetailTitle)).check(matches(withText("New Task")));
        onView(withId(R.id.taskDetails)).check(matches(withText("Task Details")));
        onView(withId(R.id.taskDetailState)).check(matches(withText("New")));

    }


    @Test
    public void testChangeUserName(){
        onView(withId(R.id.settingsButton)).perform(click());
        onView(withId(R.id.usernameInput)).perform(typeText("Baraa"), closeSoftKeyboard());
        onView(withId(R.id.usernameSaveButton)).perform(click());

        onView(withId(R.id.userTasksLabel)).check(matches(withText("Baraa's Tasks")));

    }

}

