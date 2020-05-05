package com.example.prototypesabre;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class firstTimelogin extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button chooseButton;
    private Button uploadButton;
    private Button submitButton;
    private ImageView profileImageView;
    private EditText passwordEditText, repeatPasswoedEditText;
    private Uri imageURi = null;
    firebaseFunction Firebasefunction = new firebaseFunction();


    String email = "";

    StorageReference objectStorageRefrences;
    FirebaseFirestore objectFirebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_timelogin);
        Bundle extras = getIntent().getExtras();
        email = extras.getString("Email");
        imageURi = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + getResources().getResourcePackageName(R.drawable.profile_placeholder)
                + '/' + getResources().getResourceTypeName(R.drawable.profile_placeholder) + '/' + getResources().getResourceEntryName(R.drawable.profile_placeholder));

        chooseButton = findViewById(R.id.chooseButton);
        submitButton = findViewById(R.id.submitButton);
        profileImageView = findViewById(R.id.uploadProfileImageView);
        passwordEditText = findViewById(R.id.passwordEditText);
        repeatPasswoedEditText = findViewById(R.id.repeatPasswordEdittext);
        objectFirebaseFirestore = FirebaseFirestore.getInstance();
        objectStorageRefrences = FirebaseStorage.getInstance().getReference("ProfileImage");


        //choosing an image
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openFileChoser();


            }
        });


    }

    public void submit(View view) {

        if (passwordEditText.getText().toString().equals(repeatPasswoedEditText.getText().toString())) {
            //   changePassword(passwordEditText.getText().toString());


            String newPassword = passwordEditText.getText().toString().trim();
            String repeatedPassword = repeatPasswoedEditText.getText().toString().trim();
            if (newPassword.equals(repeatedPassword) && (newPassword.length() > 6)) {
                uploadFile();
                changePassword(newPassword);
                Firebasefunction.updateField("Users", email.trim(), "Login", "2", getApplicationContext());
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Failed, make sure both password are same. Make sure the password length is at least 7 character", Toast.LENGTH_LONG).show();
            }

        }
    }


    //This opens the Android gallery top upload file
    private void openFileChoser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

    }


    //here we check if choosing image was successful and set it to imageview
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageURi = data.getData();
            profileImageView.setImageURI(imageURi);

        }
    }


    private void uploadFile() {

        String nameofImg = email + "." + "jpg";// getFileExtension(mImageUri);
        final StorageReference imageRef = objectStorageRefrences.child(nameofImg);

        UploadTask objectUploadTask = imageRef.putFile(imageURi);
        objectUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "FAILED", Toast.LENGTH_SHORT).show();
                }
                return imageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Firebasefunction.updateField("Users", email.trim(), "Links", task.getResult().toString(), getApplicationContext());
                }

            }
        });
    }

    public void changePassword(String password) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String newPassword = password;

        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i("DONE", "User password updated.");
                        }
                    }
                });


    }
}
