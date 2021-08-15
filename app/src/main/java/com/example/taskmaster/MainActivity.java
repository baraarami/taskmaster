package com.example.taskmaster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button addTask , allTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    addTask = findViewById(R.id.button2);
    allTasks = findViewById(R.id.button);

    }

    public void addTask(View view){
        Intent add = new Intent(MainActivity.this , AddTask.class);
        startActivity(add);
    }

    public void allTasks(View view){
        Intent all = new Intent(MainActivity.this , AllTasks.class);
        startActivity(all);
    }

}