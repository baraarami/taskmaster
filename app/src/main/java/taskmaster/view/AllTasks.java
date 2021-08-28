package taskmaster.view;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.taskmaster.app.R;

public class AllTasks extends AppCompatActivity {

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);

        Object.requireNonNull(getSupportActionBar()).setDefaultDisplayHomeAsUpEnabled(true);

    }
}
