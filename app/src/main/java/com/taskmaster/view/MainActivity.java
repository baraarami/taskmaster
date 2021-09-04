package com.taskmaster.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration;
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.auth.AuthUser;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.taskmaster.R;
import com.taskmaster.adapter.TaskAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  public static final String TASK_TITLE = "taskTitle";
  public static final String TASK_BODY = "taskBody";
  public static final String TASK_STATUS = "taskStatus";
  public static final String TASK_FILE = "taskFile";
  private static final String TAG = "MainActivity";

  private static PinpointManager pinpointManager;

  private static List<Task> taskList = new ArrayList<>();

  private static TaskAdapter adapter;
  private Handler handler;
  private Team teamData = null;
  private String teamNameData = null;
  private String currentUsername = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    taskList = new ArrayList<>();

    // Initialize PinpointManager
    getPinpointManager(getApplicationContext());

    try {

      Amplify.addPlugin(new AWSCognitoAuthPlugin());
      Amplify.addPlugin(new AWSS3StoragePlugin());
      Amplify.addPlugin(new AWSApiPlugin());
      Amplify.configure(getApplicationContext());

      Log.i("Tutorial", "Initialized Amplify");
    } catch (AmplifyException e) {
      Log.e("Tutorial", "Could not initialize Amplify", e);
    }

    if (Amplify.Auth.getCurrentUser()!= null){
      Log.i(TAG, "Auth: " + Amplify.Auth.getCurrentUser().toString());
    }else {
      Log.i(TAG, "Auth:  no user " + Amplify.Auth.getCurrentUser());
      Intent goToLogin= new Intent(this,LoginActivity.class);
      startActivity(goToLogin);
    }

    setContentView(R.layout.activity_main);

    RecyclerView taskRecyclerView = findViewById(R.id.List_tasks);

    handler = new Handler(Looper.getMainLooper(),
        message -> {
          listItemDeleted();
          return false;
        });


    taskList = new ArrayList<>();

    adapter = new TaskAdapter(taskList, new TaskAdapter.OnTaskItemClickListener() {
      @Override
      public void onItemClicked(int position) {
        Intent goToDetailsIntent = new Intent(getApplicationContext(), TaskDetail.class);
        goToDetailsIntent.putExtra(TASK_TITLE, taskList.get(position).getTitle());
        goToDetailsIntent.putExtra(TASK_BODY, taskList.get(position).getDescription());
        goToDetailsIntent.putExtra(TASK_STATUS, taskList.get(position).getStatus());
        goToDetailsIntent.putExtra(TASK_FILE, taskList.get(position).getFileName());
        startActivity(goToDetailsIntent);
      }

      @Override
      public void onDeleteItem(int position) {
//        taskDao.delete(taskList.get(position));

        Amplify.API.mutate(ModelMutation.delete(taskList.get(position)),
            response -> Log.i(TAG, "item deleted from API:"),
            error -> Log.e(TAG, "Delete failed", error)
        );
        taskList.remove(position);
        listItemDeleted();
      }
    });

    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
        this,
        LinearLayoutManager.VERTICAL,
        false);

    taskRecyclerView.setLayoutManager(linearLayoutManager);
    taskRecyclerView.setAdapter(adapter);

    Button newTaskButton = findViewById(R.id.addTaskButton);
    newTaskButton.setOnClickListener(goToNewTaskCreator);

    Button allTasksButton = findViewById(R.id.allTasksButton);
    allTasksButton.setOnClickListener(goToAllTasks);

    Button makeTaskDetailsButton = findViewById(R.id.makeTaskDetailsButton);
    makeTaskDetailsButton.setOnClickListener(goToTaskDetail);

    Button makeTaskDetailsButton1 = findViewById(R.id.makeTaskDetailsButton1);
    makeTaskDetailsButton1.setOnClickListener(goToTaskDetail1);

    Button makeTaskDetailsButton2 = findViewById(R.id.makeTaskDetailsButton2);
    makeTaskDetailsButton2.setOnClickListener(goToTaskDetail2);

    Button settingsButton = findViewById(R.id.settingsButton);
    settingsButton.setOnClickListener(goToSettings);

    Button logout = findViewById(R.id.logoutButton);
    logout.setOnClickListener(v -> logout());

    getTaskDataFromAPI();

  }

  @SuppressLint("SetTextI18n")
  @Override
  protected void onResume() {
    super.onResume();

    if (Amplify.Auth.getCurrentUser()!= null){
      TextView userNameText = (findViewById(R.id.userTasksLabel));
      userNameText.setText(Amplify.Auth.getCurrentUser().getUsername()+ "'s Tasks");
    }else {
      Intent goToLogin= new Intent(this,LoginActivity.class);
      startActivity(goToLogin);
    }

    SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    String username = preference.getString("username", "user") + "'s Tasks";
    String teamName = "Your Team Name is: " + preference.getString("teamName", "Choose your team");
    teamNameData = preference.getString("teamName", null);
//    TextView userLabel = findViewById(R.id.userTasksLabel);
    TextView teamNameLabel = findViewById(R.id.teamTasksLabel);
//    userLabel.setText(username);
    teamNameLabel.setText(teamName);

    if (teamNameData!= null){
      getTeamDetailFromAPIByName();
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      taskList.clear();
      Log.i(TAG, "-----selected team true-------- ");
      Log.i(TAG, teamNameData);
      getTaskDataFromAPIByTeam();
    }

  }

  private final View.OnClickListener goToNewTaskCreator = v -> {
    Intent i = new Intent(getBaseContext(), AddTask.class);
    startActivity(i);
  };

  private final View.OnClickListener goToAllTasks = v -> {
    Intent i = new Intent(getBaseContext(), AllTasks.class);
    startActivity(i);
  };

  private final View.OnClickListener goToTaskDetail = v -> {
    Button makeTaskDetailsButton = findViewById(R.id.makeTaskDetailsButton);
    String buttonText = makeTaskDetailsButton.getText().toString();
    Intent i = new Intent(getBaseContext(), TaskDetail.class);
    i.putExtra(TASK_TITLE, buttonText);
    startActivity(i);
  };

  private final View.OnClickListener goToTaskDetail1 = v -> {
    Button makeTaskDetailsButton1 = findViewById(R.id.makeTaskDetailsButton1);
    String buttonText = makeTaskDetailsButton1.getText().toString();
    Intent i = new Intent(getBaseContext(), TaskDetail.class);
    i.putExtra(TASK_TITLE, buttonText);
    startActivity(i);
  };

  private final View.OnClickListener goToTaskDetail2 = v -> {
    Button makeTaskDetailsButton2 = findViewById(R.id.makeTaskDetailsButton2);
    String buttonText = makeTaskDetailsButton2.getText().toString();
    Intent i = new Intent(getBaseContext(), TaskDetail.class);
    i.putExtra(TASK_TITLE, buttonText);
    startActivity(i);
  };

  private final View.OnClickListener goToSettings = v -> {
    Intent i = new Intent(getBaseContext(), Settings.class);
    startActivity(i);
  };

  public static void saveTaskToAPI(Task item) {
    Amplify.API.mutate(ModelMutation.create(item),
        success -> Log.i(TAG, "Saved item to api : " + success.getData().getTitle()),
        error -> Log.e(TAG, "Could not save item to API/dynamodb", error));

  }

  public void getTaskDataFromAPI() {

    Amplify.API.query(ModelQuery.list(Task.class),
        response -> {
          for (Task task : response.getData()) {
            taskList.add(task);
            Log.i(TAG, "getFrom api: the Task from api are => " + task.toString());
          }
          handler.sendEmptyMessage(1);
        },
        error -> Log.e(TAG, "getFrom api: Failed to get Task from api => " + error.toString())
    );
  }


  public void getTeamDetailFromAPIByName() {
      Amplify.API.query(
          ModelQuery.list(Team.class, Team.NAME.contains(teamNameData)),
          response -> {
            for (Team teamDetail : response.getData()) {
              Log.i(TAG, teamDetail.toString());
              teamData = teamDetail;
            }
          },
          error -> Log.e(TAG, "Query failure", error)
      );
  }

  public  void  getTaskDataFromAPIByTeam(){
    Log.i(TAG, "getTaskDataFromAPIByTeam: get task by team");

    Amplify.API.query(ModelQuery.list(Task.class, Task.TEAM.contains(teamData.getId())),
        response -> {
          for (Task task : response.getData()) {

            Log.i(TAG, "task-team-id: " + task.getTeam().getId());
            Log.i(TAG, "team-id: "+ teamData.getId());
            taskList.add(task);

            Log.i(TAG, "getFrom api by team: the Task from api are => " + task);
          }
          handler.sendEmptyMessage(1);
        },
        error -> Log.e(TAG, "getFrom api: Failed to get Task from api => " + error.toString())
    );
  }

  public  void getCurrentUser() {
    AuthUser authUser = Amplify.Auth.getCurrentUser();
    currentUsername = authUser.getUsername();
    Log.i(TAG, "getCurrentUser: " + authUser.toString());
    Log.i(TAG, "getCurrentUser: username" + authUser.getUsername());
    Log.i(TAG, "getCurrentUser: userId" + authUser.getUserId());
  }

  public void logout(){
    Amplify.Auth.signOut(
        () ->{
          Log.i("AuthQuickstart", "Signed out successfully");
          Intent goToLogin = new Intent(getBaseContext(), LoginActivity.class);
          startActivity(goToLogin);
        } ,
        error -> Log.e("AuthQuickstart", error.toString())
    );
  }

  public static PinpointManager getPinpointManager(final Context applicationContext) {
    if (pinpointManager == null) {
      final AWSConfiguration awsConfig = new AWSConfiguration(applicationContext);
      AWSMobileClient.getInstance().initialize(applicationContext, awsConfig, new Callback<UserStateDetails>() {
        @Override
        public void onResult(UserStateDetails userStateDetails) {
          Log.i("INIT", userStateDetails.getUserState().toString());
        }

        @Override
        public void onError(Exception e) {
          Log.e("INIT", "Initialization error.", e);
        }
      });

      PinpointConfiguration pinpointConfig = new PinpointConfiguration(
          applicationContext,
          AWSMobileClient.getInstance(),
          awsConfig);

      pinpointManager = new PinpointManager(pinpointConfig);

      FirebaseMessaging.getInstance().getToken()
          .addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<String> task) {
              if (!task.isSuccessful()) {
                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                return;
              }
              final String token = task.getResult();
              Log.d(TAG, "Registering push notifications token: " + token);
              pinpointManager.getNotificationClient().registerDeviceToken(token);
            }
          });
    }
    return pinpointManager;
  }

  @SuppressLint("NotifyDataSetChanged")
  private static void listItemDeleted() {
    adapter.notifyDataSetChanged();
  }
}