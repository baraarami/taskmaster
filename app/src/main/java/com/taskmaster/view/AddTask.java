package com.taskmaster.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.room.Room;

import android.os.FileUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.taskmaster.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class AddTask extends AppCompatActivity {

  private static final String TAG = "AddTask";
  private String spinner_task_status = null;
  private String teamName = null;
  private Team teamData = null;
  static String pattern = "yyMMddHHmmssZ";
  @SuppressLint("SimpleDateFormat")
  static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
  private static String FileUploadName= simpleDateFormat.format(new Date());
  private static String fileUploadExtention = null;
  private static File uploadFile = null;

  @ReqiresApi(api = Build.VERSION_CODE.Q)
  @SuppressLint("RestrictedApi")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_task);

    if (Amplify.Auth.getCurrentUser() != null){
      android.util.Log.i(TAG, "Auth : " + Amplify.Auth.getCurrentUser().toString());
    }else{
      android.util.Log.i(TAG, "Auth : no user " + Amplify.Auth.getCurrentUser());
      Intent goToLogin = new Intent(this , LoginActivity.class);
      startActivity(goToLogin);
    }

    Spinner spinner = findViewById(R.id.spinner_status);

    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.task_status_array, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(adapter);

    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinner_task_status = (String) parent.getItemAtPosition(position);
        System.out.println(spinner_task_status);
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
        spinner_task_status = (String) parent.getItemAtPosition(0);
      }
    });

    Button uploadFile = findViewById(R.id.uploadFileBtn);
    uploadFile.setOnClickListener(v1 -> getFileFromDevice());

    TextView successLabel = findViewById(R.id.newTaskSubmitSuccess);
    successLabel.setVisibility(View.GONE);

    Button newTaskCreateButton = findViewById(R.id.newTaskSubmit);
    newTaskCreateButton.setOnClickListener(newTaskCreateListener);

    Objects.requireNonNull(getSupportActionBar()).setDefaultDisplayHomeAsUpEnabled(true);

  }

  private final View.OnClickListener newTaskCreateListener = v -> {
    String taskTitle = ((EditText) findViewById(R.id.newTaskName)).getText().toString();
    String taskBody = ((EditText) findViewById(R.id.newTaskBody)).getText().toString();
    String taskStatus = spinner_task_status;


    getTeamDetailFromAPIByName(teamName);

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    Task item = Task.builder().
        title(taskTitle).
        description(taskBody).
        team(teamData).
        status(taskStatus).
        fileName(FileUploadName +"."+ fileUploadExtention.split("/")[1]).
        build();

    saveTaskToAPI(item);

    TextView successLabel = findViewById(R.id.newTaskSubmitSuccess);
    successLabel.setVisibility(View.VISIBLE);
  };


  @RequiresApi(api = Build.VERSION_CODES.Q)
  public void handelSendImage(Intent intent){
    Uri imageUri = (Uri) intent.getPracelabelExtra(Intent.EXTRA_STREAM);
    if (imageUri != null){
      fileUploadExtention = getContentResolver().getType(imageUri);

      android.util.Log.i(TAG, "onActivityResult : " + fileUploadExtention);
      android.util.Log.i(TAG, "onActivityResult : returned from file explorer");
      android.util.Log.i(TAG, "onActivityResult : success choose image");

      uploadFile = new File(getApplicationContext().getFileDir() , "uplodeFile");

      try {
        InputStream inputStream = getContentResolver().openInputStream(imageUri);
        FileUtils.copy(inputStream , new FileOutputStream(uploadFile));
      }catch (Exeption ex){
        android.util.Log.e(TAG, "onActivityResult : file upload faild" + ex.toString());
      }
    }
  }

  public void saveTaskToAPI(Task item) {
    Amplify.Storage.uploadFile(
        FileUploadName +"."+ fileUploadExtention.split("/")[1],
        uploadFile,
        success -> {
          Log.i(TAG, "uploadFileToS3: succeeded " + success.getKey());
        },
        error -> {
          Log.e(TAG, "uploadFileToS3: failed " + error.toString());
        }
    );
    Amplify.API.mutate(ModelMutation.create(item),
        success -> Log.i(TAG, "Saved item to api : " + success.getData()),
        error -> Log.e(TAG, "Could not save item to API/dynamodb", error));
  }

  @RequiresApi(api = Build.VERSION_CODES.Q)
  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == 999 && resultCode == RESULT_OK) {
      Uri uri = data.getData();
      fileUploadExtention = getContentResolver().getType(uri);

      Log.i(TAG, "onActivityResult: mm is " +fileUploadExtention);
      Log.i(TAG, "onActivityResult: returned from file explorer");
      Log.i(TAG, "onActivityResult: => " + data.getData());
      Log.i(TAG, "onActivityResult:  data => " + data.getType());

      uploadFile = new File(getApplicationContext().getFilesDir(), "uploadFile");

      try {
        InputStream inputStream = getContentResolver().openInputStream(data.getData());
        FileUtils.copy(inputStream, new FileOutputStream(uploadFile));
      } catch (Exception exception) {
        Log.e(TAG, "onActivityResult: file upload failed" + exception.toString());
      }

    }
  }

  private void getFileFromDevice() {
    Intent upload = new Intent(Intent.ACTION_GET_CONTENT);
    upload.setType("*/*");
    upload = Intent.createChooser(upload, "Choose a File");
    startActivityForResult(upload, 999); // deprecated
  }

  @SuppressLint("NonConstantResourceId")
  public void onClickRadioButton(View view) {
    boolean checked = ((RadioButton) view).isChecked();
    switch (view.getId()) {
      case R.id.team1:
        if (checked)
          Log.i(TAG, "onClickRadioButton: team 1");
        teamName = "team 1";
        break;
      case R.id.team2:
        if (checked)
          Log.i(TAG, "onClickRadioButton: team 2");
        teamName = "team 2";
        break;
      case R.id.team3:
        if (checked)
          Log.i(TAG, "onClickRadioButton: team 3");
        teamName = "team 3";
        break;
    }
  }

  public void getTeamDetailFromAPIByName(String name) {
    Amplify.API.query(
        ModelQuery.list(Team.class, Team.NAME.contains(name)),
        response -> {
          for (Team teamDetail : response.getData()) {
            Log.i(TAG, teamDetail.getName());
            teamData = teamDetail;
          }
        },
        error -> Log.e(TAG, "Query failure", error)
    );
  }

}
