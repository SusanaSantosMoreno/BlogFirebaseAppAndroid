package com.example.susanasantosmoreno.blogfirebaseapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.susanasantosmoreno.blogfirebaseapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class loginActivity extends AppCompatActivity {

    private EditText userMail;
    private EditText userPassword;
    private Button loginBtn;
    private ProgressBar loginProgressBar;
    private Button loginSignUpBtn;

    private FirebaseAuth mAuth;
    private Intent homeActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //init views
        userMail = findViewById(R.id.loginMail);
        userPassword = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginButton);
        loginProgressBar = findViewById(R.id.loginProgressBar);
        loginSignUpBtn = findViewById(R.id.signUpButton);
        mAuth = FirebaseAuth.getInstance();
        homeActivity = new Intent(this, HomeActivity.class);

        loginProgressBar.setVisibility(View.INVISIBLE);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginProgressBar.setVisibility(View.VISIBLE);
                loginBtn.setVisibility(View.INVISIBLE);

                final String mail = userMail.getText().toString();
                final String password = userPassword.getText().toString();

                if(mail.isEmpty() || password.isEmpty()){
                    showMessage("Please verify all fields");
                    loginProgressBar.setVisibility(View.INVISIBLE);
                    loginBtn.setVisibility(View.VISIBLE);
                }else{
                    signIn(mail, password);
                }
            }
        });

        loginSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerActivity = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(registerActivity);
                finish();
            }
        });
    }

    private void signIn(String mail, String password) {
        mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener
                (new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            loginProgressBar.setVisibility(View.INVISIBLE);
                            loginBtn.setVisibility(View.VISIBLE);
                            updateUI();
                        }else{
                            showMessage(task.getException().getMessage());
                            loginProgressBar.setVisibility(View.INVISIBLE);
                            loginBtn.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void updateUI() {
        startActivity(homeActivity);
        finish();
    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null){
            // the user is already connected
            updateUI();
        }
    }
}
