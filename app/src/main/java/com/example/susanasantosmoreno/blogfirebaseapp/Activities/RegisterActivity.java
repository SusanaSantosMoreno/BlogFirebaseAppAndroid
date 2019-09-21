package com.example.susanasantosmoreno.blogfirebaseapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.susanasantosmoreno.blogfirebaseapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {

    private static int REQUEST_CODE = 1;
    private static int REQUEST_CODE_GALLERY = 1;
    private Uri pickedImgUri;
    private FirebaseAuth mAuth;

    // views
    private ImageView userImage;
    private EditText userEmail;
    private EditText userName;
    private EditText userPassword;
    private EditText userPasswordRepeat;
    private ProgressBar loadingProgress;
    private Button regBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //init views
        userImage = findViewById(R.id.regUserImg);
        userEmail = findViewById(R.id.regMail);
        userName = findViewById(R.id.regName);
        userPassword = findViewById(R.id.regPassword);
        userPasswordRepeat = findViewById(R.id.regPasswordRep);
        regBtn = findViewById(R.id.regButton);
        loadingProgress = findViewById(R.id.regProgressBar);
        mAuth = FirebaseAuth.getInstance();

        loadingProgress.setVisibility(View.INVISIBLE);

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= 22){
                    checkAndRequestForPermission();
                }else{
                    openGallery();
                }
            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regBtn.setVisibility(View.INVISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);

                final String email = userEmail.getText().toString();
                final String name = userName.getText().toString();
                final String password = userPassword.getText().toString();
                final String passwordRepeat = userPasswordRepeat.getText().toString();

                if(email.isEmpty() || name.isEmpty() || password.isEmpty() ||
                        passwordRepeat.isEmpty() || !password.equals(passwordRepeat)){
                    showMessage("Please verify all fields");
                    regBtn.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                }else{
                    //create user account
                    createUserAccount(name, email, password);
                }
            }
        });
    }

    private void createUserAccount(final String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    showMessage("Account Created");
                    updateUserInfo(name, pickedImgUri, mAuth.getCurrentUser());
                }else{
                    showMessage("Account creation failed: " + task.getException().getMessage());
                    regBtn.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void updateUserInfo(final String name, final Uri pickedImgUri, final FirebaseUser currentUser) {
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_images");
        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener
                <UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profileUpdate).addOnCompleteListener
                                (new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    showMessage("Register Complete");
                                    updateUI();
                                }
                            }
                        });

                    }
                });
            }
        });
    }

    private void updateUI() {
        Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(homeActivity);
        finish();
    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void openGallery() {
        //TODO: open gallery intent and for user to pick an image
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("Image/*");
        startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY);
    }

    private void checkAndRequestForPermission(){
        if(ContextCompat.checkSelfPermission(RegisterActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(RegisterActivity.this,
                        "Please accept for required permission", Toast.LENGTH_LONG).show();
            }else{
                ActivityCompat.requestPermissions(RegisterActivity.this,
                        new String []{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE);
            }
        }else{
            openGallery();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_GALLERY && data != null){
            //the user has successfully picked an image
            pickedImgUri = data.getData();
            userImage.setImageURI(pickedImgUri);
        }
    }
}
