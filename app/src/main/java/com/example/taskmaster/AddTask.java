package com.example.taskmaster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import androidx.navigation.fragment.NavHostFragment;

import com.example.taskmaster.databinding.FragmentFirstBinding;

public class AddTask extends AppCompatActivity {
    TextView submit;

    private FragmentFirstBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        submit=findViewById(R.id.textview4);
        submit.setVisibility(View.INVISIBLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent back = new Intent(getApplicationContext() , MainActivity.class);
        startActivity(back);
        return true;
    }
    public void submitTask(View view){
        submit.setVisibility(View.VISIBLE);
    }
}