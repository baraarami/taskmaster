package taskmaster.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.taskmaster.app.MainActivity;
import com.taskmaster.app.R;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "Login Activity";

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_login);

        EditText emailInput = findViewById(R.id.login_emailInput);
        EditText passwordInput = findViewById(R.id.login_passwordInput);
        Button loginBtn = findViewById(R.id.login_btn);
        Button signupBtn = findViewById(R.id.login_signupBtn);

        loginBtn.setOnClickListener(v ->{
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            if (!email.isEmpty() && ! password.isEmpty()){
                signIn(email,password);
            }else{
                Toast.makeText(LoginActivity.this,"please enter your email and password !! " , Toast.LENGTH_SHORT.show());
            }
        });
            signupBtn.setOnClickListener(v -> {
                Intent goToSignUp = new Intent(LoginActivity.this , SignUpActivity.class);
                startActivity(goToSignUp);
            });
    }

    public void signIn(String username , String password){
        Amplify.Auth,signIn(username , password , success -> {
            Log.i(TAG, "signIn: worked" + success.toString());
            Intent goToMain = new Intent(LoginActivity.this , MainActivity.class);
            goToMain.putExtra("username " , username);
            startActivity(goToMain);},
                error -> Log.e(TAG, "signIn: failed" + error.toString()));
    }

}

