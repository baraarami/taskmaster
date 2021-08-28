package taskmaster.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.taskmaster.app.R;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import amplifyframework.datastore.generated.model.Team;
import taskmaster.adapter.TaskAdapter;
import taskmaster.model.Task;

public class MainActivity extends AppCompatActivity {

    public static final String Task_Title = " taskTitle";
    public static final  String Task_Body = "taskBody";
    public static final String Task_Status = "taskStatus";
    public static final String Task_File = "taskFile";
    private static final String TAG = "MainActivity";

    private static PinpointManager pinpointManager;

    private static List<Task> taskList = new ArrayList<>();

    private static TaskAdapter adapter;
    private Handler handler;
    private Team teamData = null ;
    private String teamNameData = null;
    private String currentUsername = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        taskList = new ArrayList<>();


     getPainpointManager(getApplicationContext());

    try{
        Amplify.addPlugin(new AWSCognitoAuthPlugin());
        Amplify.addPlugin(new AWSS3StoragePlugin());
        Amplify.addPlugin(new AWSApiPlugin());
        Amplify.configure(getApplicationContext());

        Log.i("Tutorial" , "Initialized Amplify");

    }catch(AmplifyException ex ){
        Log.e("Tutorial" , "could not initialize Amplify" , ex  );
    }

    if (Amplify.Auth.getCurrentUser()!= null){
        Log.i(TAG, "Auth" + Amplify.Auth.getCurrentUser().toString());
    }else {
        Log.i(TAG, "Auth : no user " + Amplify.Auth.getCurrentUser());
        Intent goToLogin = new Intent(this , LoginActivity.class);
        startActivity(goToLogin);
    }

    setContentView(R.layout.activity_main);

        RecyclerView taskRecyclerView = findViewById(R.id.List_tasks);

        handler = new Handler(Looper.getMainLooper(), message -> {
        listItemDeleted();
        return false;
    }) ;

        taskList = new ArrayList<>();

        adapter  = new TaskAdapter(taskList, new TaskAdapter.OnTaskItemClickListener() {
            @Override
            public void OnItemClicked(int position) {
                Intent goToDetailsIntent = new Intent(getApplicationContext() , TaskDetail.class);
                goToDetailsIntent.putExtra(Task_Title , taskList.get(position).getTitle());
                goToDetailsIntent.putExtra(Task_Body , taskList.get(position).getDescription());
                goToDetailsIntent.putExtra(Task_Status , taskList.get(position).getStatus());
                goToDetailsIntent.putExtra(Task_File , taskList.get(position).getFileName());
                startActivity(goToDetailsIntent);

            }

            @Override
            public void OnDeleteItem(int position) {
            Amplify.API.mutate(ModelMutation.delete(taskList.get(position)),
                    response -> Log.i(TAG, "item deleted from API :"),
                    error -> Log.e(TAG, "Delete failed",error );
            taskList.remove(position);
            listItemDeleted();

            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager((
                this,
                LinearLayoutManager.VERTICAL,false);

                taskRecyclerView.setLayoutManager(linearLayoutManager);
                taskRecyclerView.setAdapter(adapter);

        Button newTaskButton = findViewById(R.id.addTaskButton);
        newTaskButton.setOnClickListener(goToNewTaskCreator);

        Button allTaskButton = findViewById(R.id.allTasksButton);
        allTaskButton.setOnClickListener(goToAllTasks);

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


        @SuppressLint("SetTextI18n")
                @Override
        protected void onResume (){
            super.onResume();
            if (Amplify.Auth.getCurrent()!= null){
                TextView userNameText = (findViewById(R.id.userTasksLabel));
                userNameText.setText(Amplify.Auth.getCurrent().getUsername() + "'s Tasks");
            }else{
                Intent goToLogin = new Intent(this , LoginActivity.class);
                startActivity(goToLogin);
            }

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String username = preferences.getString("username" , "user") + "'s Tasks";
            String teamName = "your Team Name is :"+ preferences.getString("teamName" , "Choose your team");
            teamNameData = preferences.getString("teamName" , null);
            TextView teamNameLabel = findViewById(R.id.teamTaskLabel);
            teamNameLabel.setText(teamName);

            if (teamNameData != null){
                getTeamDetailFromAPIByName();
                try {
                    Thread.sleep(2000);
                }catch (InterruptedException ex){
                    ex.printStackTrace();
                }
                taskList.clear();
                Log.i(TAG, "************ selected team true ************");
                Log.i(TAG, teamNameData);
                getTaskDataFromAPIByTeam();
                }

        }
    }
     private final View.OnClickListener goToNewTaskCreator= v -> {
     Intent i = new Intent(getBaseContext(), AddTask.class);
     startActivity(i);
        };

        private final View.OnClickListener goToAllTasks= v -> {
            Intent i = new Intent(getBaseContext(), AllTasks.class);
            startActivity(i);
        };

        private final View.OnClickListener goToTaskDetail= v -> {
            Button makeTaskDetailsButton = findViewById(R.id.makeTaskDetailsButton);
            String buttonText = makeTaskDetailsButton.getText().toString();
            Intent i = new Intent(getBaseContext(), TaskDetail.class);
            i.putExtra(Task_Title , buttonText);
            startActivity(i);
        };

        private final View.OnClickListener goToTaskDetail1= v -> {
            Button makeTaskDetailsButton1 = findViewById(R.id.makeTaskDetailsButton1);
            String buttonText = makeTaskDetailsButton1.getText().toString();
            Intent i = new Intent(getBaseContext(), TaskDetail.class);
            i.putExtra(Task_Title , buttonText);
            startActivity(i);
        };


        private final View.OnClickListener goToTaskDetail2= v -> {
            Button makeTaskDetailsButton2 = findViewById(R.id.makeTaskDetailsButton2);
            String buttonText = makeTaskDetailsButton2.getText().toString();
            Intent i = new Intent(getBaseContext(), TaskDetail.class);
            i.putExtra(Task_Title , buttonText);
            startActivity(i);
        };

        private final View.OnClickListener goToSettings= v -> {
            Intent i = new Intent(getBaseContext(), Settings.class);
            startActivity(i);
        };


        public static void saveTaskToAPI(Task item){
            Amplify.API.mutate(ModelMutation.create(item),
                    success -> Log.i(TAG, "Saved item to API : " + success.getData().getTitle()),
                    error -> Log.e(TAG, "Could not save item to API/ dynamodb",reror ));

    }

    public void getTaskDataFromAPI(){
            Amplify.API.query(ModelQuery.list(Task.class),
                    response ->{
                for (Task task : response.getData()){
                    taskList.add(task);
                    Log.i(TAG, "getFrom api : the Task from api are =>" + task.toString());
                }
                handler.sendEmptyMessage(1);
                    },
                    error -> Log.e(TAG, "getFrom api :failed to get Task from api => ",error.toString()) );
        }

    public void getTeamDetailFromAPIByName(){
            Amplify.API.query(
                    MdelQuery.list(Team.class , Team.NAME.containes(teamNameData)),
                    response ->{
                        for (Team teamDetail : response.getData()){
                            Log.i(TAG, teamDetail.toString());
                            teamData=teamDetail;
                        }
                    },
                    error -> Log.e(TAG,"Query failure",error )
            );
    }

    public void getTaskDataFromAPIByTeam(){
        Log.i(TAG, "getTaskDataFromAPIByTeam: get task by team");

        Amplify.API.query(
                MdelQuery.list(Task.class , Task.TEAM.containes(teamData.getId())),
                response ->{
                    for (Task task : response.getData()){
                        Log.i(TAG, "task team id :" + task.getTeam().getId());
                        Log.i(TAG, "team id :" + teamData.getId());
                        taskList.add(task);

                        Log.i(TAG, "getFrom api by team : the task from api are =>" + task);
                    }
                    handler.sendEmptyMessage(1);
                },
                error -> Log.e(TAG, "getFrom api :failed to get Task from api => ",error.toString())
        );
    }

    public void getCurrentUser(){
            AuthUser authUser = Amplify.Auth.getCurrentUser();
            currentUsername = authUser.getUsername();
        Log.i(TAG, "getCurrentUser: " + authUser.toString());
        Log.i(TAG, "getCurrentUser: username" + authUser.getUsername());
        Log.i(TAG, "getCurrentUser: userID"+ authUser.getUserId());
    }


    public void logout(){
            Amplify.Auth.signOut(
                    () ->{
                        Log.i(TAG, "AuthQuickstart " , "Signed out successfully");
                        Intent goToLogin = new Intent(getBaseContext() , LoginActivity.class);
                       startActivity(goToLogin);
                    },
                    error -> Log.e(TAG, "AuthQuickstart",error.toString() )
            );
    }


    public static PinpointManager getPainpointManager(final  context applicationContext){
            if (pinpointManager == null){
                final AWSConfiguration awsConfig = new AWSConfiguration(applicationContext);
                AWSMobileClient.getInstance().initialize(applicationContext , awsConfig , new callback<UserStateDetails>(){
                    @Override
                    public void onResult(UserStateDetailes userStateDetailes){
                        Log.i("INIT" , userStateDetailes.getUserState().toString());
                    }

                    @Override
                    public void onError(Exception ex){
                        Log.e("INIT", "Initialization error " , ex );
                    }
                });


                PinpointConfiguration pinpointConfig = new PinpointConfiguration(
                        applicationContext,
                        AWSMobileClient.getInstance(),
                        awsConfig);

                pinpointConfiguration = new PinpoinManager(pinpointConfig);

                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new MediaPlayer.OnCompletionListener<String>(){
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
    private static void ListItemDeleted(){
            adapter.notifyDataSetChanged();
    }
    }





