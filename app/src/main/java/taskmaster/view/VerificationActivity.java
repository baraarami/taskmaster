package taskmaster.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.taskmaster.app.R;

public class VerificationActivity extends AppCompatActivity {
    private static final String TAG = "VerificationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);


        EditText verificationInput = findViewById(R.id.vereficationInput);
        Button verificationBtn = findViewById(R.id.verifecationBtn);

        Intent intent = getIntent();
        String username = intent.getExtras().getString("username" ,"");
        String password = intent.getExtras().getString("password","");

        verificationBtn.setOnClickListener(v ->{
            String verification_number = verificationInput.getText().toString();
            verification(username , verification_number , password);
        });
    }

    private void verification(String username, String verificationNumber, String password) {

   Amplify.Auth.confirmSignUp(
           username,
           verificationNumber,
           success ->{
               Log.i(TAG, "verification: succeeded" + success.toString());
               Intent goToSignIn = new Intent(VerificationActivity.this , MainActivity.class);
               goToSignIn.putExtra("username" , username);
               startActivity(goToSignIn);
               silentSignIn(username, password);
           },
           error -> Log.e(TAG, "verification: failed" + error.toString()));
    }

    public void silentSignIn(String username, String password) {
        Amplify.Auth.signIn(
                username,
                password,
                success -> Log.i(TAG, "signIn: worked " + success.toString()),
                error -> Log.e(TAG, "signIn: failed" + error.toString()));
    }

}
