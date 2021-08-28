package taskmaster.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.taskmaster.app.R;

import java.io.FileOutputStream;
import java.io.InputStream;

import amplifyframework.datastore.generated.model.Task;
import amplifyframework.datastore.generated.model.Team;

public class AddTask extends AppCompatActivity {
    private static final String TAG = "Add Task";
    private String spinner_task_status = null;
    private String teamName = null;
    private Team teamData = null;
    private String pattern = "bebomody";

    @SuppressLint("RestrictedApi")
    @Override
    protected void OnCreate (Bundle savedInstanceState){
        super.OnCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Spinner spinner= findViewById(R.id.spinner_status);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this ,
                R.array.task_status_array , android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                spinner_task_status = (String) parent.getItemAtPosition(position);
                System.out.println(spinner_task_status);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){
                spinner_task_status = (String) parent.getItemAtPosition(0);
            }
        });

        Button uploadFile = findViewById(R.id.uploadFileBtn);
        uploadFile.setOnClickListener(v1 -> getFileFromDevice());

        TextView successLabel = findViewById(R.id.newTaskSubmitSuccess);
        successLabel.setVisibility(View.GONE);

        Button newTaskCreateButton = findViewById(R.id.newTaskSubmit);
        newTaskCreateButton.setOnClickListener(newTaskCreateListener);

        Object.requireNonNull(getSupportActionBar()).setDefaultDisplayHomeAsUpEnabled(true);
    }

    private final View.OnClickListener newTaskCreateListener = v -> {
        String taskTitle = ((EditText) findViewById(R.id.newTaskName)).getText().toString();
        String taskBody = ((EditText) findViewById(R.id.newTaskBody)).getText().toString();
        String taskStatus = spinner_task_status;

        getTeamDetailFromeAPIByName (teamName);

        try {
            Thread.sleep(1500);
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }


        Task item = Task.builder().title(taskTitle).description(taskBody).team(teamData).status(taskStatus).fileName(FileUploadName + "."+ fileUploadExtention.splite("/")[1]).build();
        saveTaskToAPI(item);

        TextView successLabel = findViewById(R.id.newTaskSubmitSuccess);
        successLabel.setVisibility(View.VISIBLE);

    };

    public void saveTaskToAPI (Task item){
        Amplify.Storage.uploadFile(
                FileUploadName + "." + fileUploadExtention.splite("/")[1],
                uploadFile,
                success -> {
                    Log.e(TAG, "uploadFileToS3: successed " + success.getKey());
                },
                error ->{
                    Log.e(TAG, "uploadFileToS3: failed" + error.toString());
                }
        );

        Amplify.API.mutate(ModelMutation.create(item),
                success -> Log.e(TAG, "save item To API: " + success.getData());
                error -> Log.e(TAG, "Could not save item To API: " , error );

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode , int resultCode , @Nullable Intent data){
        super.onActivityResult(requestCode , resultCode , data );

        if (requestCode == 999 && resultCode == RESULT_OK){
            Uri uri = data.getData();
            fileUploadExtention = getCotentResolver().getType(uri);

            Log.e(TAG, "onActivityResult: bb is "+ fileUploadExtention );
            Log.e(TAG, "onActivityResult: return from file explorer" );
            Log.e(TAG, "onActivityResult: =>"+data.getData() );
            Log.e(TAG, "onActivityResult:  data =>"+data.getType() );

            uploadFile = new File (getApplicationContext().getFilesDir() , "uploadFile");

            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                FileUtils.copy(inputStream , new FileOutputStream(uploadFile));
            }catch (Exception ex){
                Log.e(TAG, "onActivityResult: file upload failed " + ex.toString() );
            }
        }

    }

    private void getFileFromDevice(){
        Intent upload = new Intent(Intent.ACTION_GET_CONTENT);
        upload.setType("*/*");
        upload = Intent.createChooser(upload , "choose a File ");
        startActivityForResult(upload,999);
    }


    @SuppressLint("NonConstantResourceId")
    public void onClickRadioButton(View view){
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()){
            case R.id.team1:
            if (checked)
                Log.e(TAG, "onClickRadioButton: team1" );
            teamName= "team 1 ";
            break;

            case R.id.team2:
                if (checked)
                    Log.e(TAG, "onClickRadioButton: team 2" );
                teamName = " team 2";
                break;

            case R.id.team3:
                if (checked)
                    Log.e(TAG, "onClickRadioButton: team 3" );
                teamName = " team 3";
                break;

        }
    }

    public void getTeamDetailFromeAPIByName(String name){
        Amplify.API.query(ModelQuery.list(Team.class , Team.NAME.contains(name)),
                response ->{
            for (Team teamDetail : response.getData()){
                Log.i(TAG, teamDetail.getName());
                teamData = teamDetail;
            }
                },
                error -> Log.e(TAG, "Query failure ", error )

                );
    }

}
