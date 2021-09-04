package com.taskmaster.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.amplifyframework.core.Amplify;
import com.bumptech.glide.Glide;
import com.taskmaster.R;

import java.io.File;
import java.net.URL;
import java.util.Objects;

public class TaskDetail extends AppCompatActivity {

  private static final String TAG = "TaskDetail";
  private URL url = null;
  private Handler handler;

  @SuppressLint("RestrictedApi")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_task_detail);

    Objects.requireNonNull(getSupportActionBar()).setDefaultDisplayHomeAsUpEnabled(true);

    String taskTitle = getIntent().getStringExtra(MainActivity.TASK_TITLE);
    TextView taskTitleID = findViewById(R.id.taskDetailTitle);
    taskTitleID.setText(taskTitle);

    String taskBody = getIntent().getStringExtra(MainActivity.TASK_BODY);
    TextView taskBodyID = findViewById(R.id.taskDetails);
    taskBodyID.setText(taskBody);

    String taskState = getIntent().getStringExtra(MainActivity.TASK_STATUS);
    TextView taskStateID = findViewById(R.id.taskDetailState);
    taskStateID.setText(taskState);

    Intent intent = getIntent();
    String fileName = intent.getExtras().get(MainActivity.TASK_FILE).toString();

    getFileFromS3Storage(fileName);


    ImageView imageView = findViewById(R.id.taskDetailImg);

    handler = new Handler(Looper.getMainLooper(),
        message -> {

          String linkedText = String.format("<a href=\"%s\">download File</a> ", url);

          TextView link = findViewById(R.id.taskDetailLink);
          link.setText(Html.fromHtml(linkedText));
          link.setMovementMethod(LinkMovementMethod.getInstance());

          Glide.with(getBaseContext())
              .load(url.toString())
              .placeholder(R.drawable.ic_pictures)
              .error(R.drawable.ic_pictures)
              .centerCrop()
              .into(imageView);
          return false;
        });
  }

  private void getFileFromS3Storage(String key) {
    Amplify.Storage.downloadFile(
        key,
        new File(getApplicationContext().getFilesDir() + key),
        result -> {
          Log.i(TAG, "Successfully downloaded: " + result.getFile().getAbsoluteFile());
        },
        error -> Log.e(TAG,  "Download Failure", error)
    );

    Amplify.Storage.getUrl(
        key,
        result -> {
          Log.i(TAG, "Successfully generated: " + result.getUrl());
          url= result.getUrl();
          handler.sendEmptyMessage(1);
        },
        error -> Log.e(TAG, "URL generation failure", error)
    );
  }
}
