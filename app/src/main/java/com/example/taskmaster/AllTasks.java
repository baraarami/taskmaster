package com.example.taskmaster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


public class AllTasks extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

   @Override
    public boolean onOptionItemSelected(MenuItem item){
       Intent back = new Intent(getApplicationContext(), MainActivity.class);
       startActivity(back);
       return true;
   }

}