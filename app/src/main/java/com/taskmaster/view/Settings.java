package com.taskmaster.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.taskmaster.R;

import java.util.Objects;

public class Settings extends AppCompatActivity {


  private static final String TAG = "Settings";
  private String teamName = null;

  @SuppressLint("RestrictedApi")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);

    Objects.requireNonNull(getSupportActionBar()).setDefaultDisplayHomeAsUpEnabled(true);

    Button saveUsernameButton = findViewById(R.id.usernameSaveButton);
    saveUsernameButton.setOnClickListener(updateUsernameListener);
  }

  private final View.OnClickListener updateUsernameListener = v -> {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    SharedPreferences.Editor editor = preferences.edit();
    EditText usernameField = findViewById(R.id.usernameInput);
    String username = usernameField.getText().toString();
    editor.putString("username", username);
    editor.putString("teamName", teamName);
    editor.apply();
    Intent i = new Intent(getBaseContext(), MainActivity.class);
    startActivity(i);
  };

  @SuppressLint("NonConstantResourceId")
  public void onClickChooseYourTeam(View view) {
    boolean checked = ((RadioButton) view).isChecked();
    switch (view.getId()) {

      case R.id.chooseTeam1:
        if (checked)
          Log.i(TAG, "onClickRadioButton: team 1");
        teamName = "team 1";
        break;
      case R.id.chooseTeam2:
        if (checked)
          Log.i(TAG, "onClickRadioButton: team 2");
        teamName = "team 2";
        break;
      case R.id.chooseTeam3:
        if (checked)
          Log.i(TAG, "onClickRadioButton: team 3");
        teamName = "team 3";
        break;
    }
  }

}
