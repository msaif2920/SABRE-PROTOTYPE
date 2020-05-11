package com.example.prototypesabre.AuthenticatedUserFragment.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prototypesabre.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class connectRequestReview extends AppCompatActivity {

    String documentId, connectUserEmail, currentUserEmail;
    String connectUserName, connectUserLink, currentUserName, currentUserLink;
    String message, nameProvided;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference currentUserRef;
    DocumentReference connectUserRef;
    DocumentReference chatRef, currentUserInfoRef, connectUserInfoRef, deleteDocRef;

    TextView connectUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authenticated_user_connect_review);
        connectUser = findViewById(R.id.connectRequestMessage);


        Bundle extras = getIntent().getExtras();
        getCurrentUser();
        connectUserEmail = extras.getString("Email");
        message = extras.getString("Message");
        nameProvided = extras.getString("Name");

        connectUser.setText("Name: " + nameProvided + "\n" + "Email: " + connectUserEmail
                + "\n" + "Message: " + message);

        chatRef = db.collection("Chat").document();
        documentId = chatRef.getId();
        currentUserRef = db.collection("Users").document(currentUserEmail).collection("Connection").document(documentId);
        connectUserRef = db.collection("Users").document(connectUserEmail).collection("Connection").document(documentId);
        connectUserInfoRef = db.collection("Users").document(connectUserEmail);
        currentUserInfoRef = db.collection("Users").document(currentUserEmail);
        deleteDocRef = db.collection("Users").document(currentUserEmail).collection("Connect Request")
                .document(connectUserEmail);


    }

    public void addDataTODatabases() {

        Map<String, Object> chatInfo = new HashMap<>();
        Map<String, Object> connectUserInfo = new HashMap<>();
        Map<String, Object> currentUserInfo = new HashMap<>();


        chatInfo.put("Time", FieldValue.serverTimestamp());
        chatInfo.put("User1", currentUserEmail);
        chatInfo.put("User2", connectUserEmail);

        connectUserInfo.put("Document", documentId);
        connectUserInfo.put("Name", connectUserName);
        connectUserInfo.put("Email", connectUserEmail);
        connectUserInfo.put("Profile Image", connectUserLink);
        connectUserInfo.put("Time", FieldValue.serverTimestamp());
        connectUserInfo.put("Update", 0);
        connectUserInfo.put("Recent Message", "");


        currentUserInfo.put("Document", documentId);
        currentUserInfo.put("Name", currentUserName);
        currentUserInfo.put("Email", currentUserEmail);
        currentUserInfo.put("Profile Image", currentUserLink);
        currentUserInfo.put("Time", FieldValue.serverTimestamp());
        currentUserInfo.put("Update", 0);
        currentUserInfo.put("Recent Message", "");


        // db.collection("Chat").document()
        chatRef.set(chatInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(connectRequestReview.this, "Successfully connected to " + connectUserName, Toast.LENGTH_SHORT).show();
                currentUserRef.set(connectUserInfo);
                connectUserRef.set(currentUserInfo);
                deleteDocRef.delete();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(connectRequestReview.this, "Failed!!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void getCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            currentUserEmail = (user.getEmail().toUpperCase().trim());
        } else {
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();

        }
    }

    public void fetchInfo(final FirebaseCallback firebaseCallback) {

        currentUserInfoRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        currentUserName = document.get("Name").toString();
                        currentUserLink = document.get("Links").toString();
                    }
                } else {
                    Toast.makeText(connectRequestReview.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }

                connectUserInfoRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                connectUserName = document.get("Name").toString();
                                connectUserLink = document.get("Links").toString();
                            }
                        }
                        firebaseCallback.onCallback(currentUserName, currentUserLink, connectUserName, connectUserLink);
                    }
                });

            }

        });
    }

    public void acceptConnect(View view) {
        fetchInfo(new FirebaseCallback() {
            @Override
            public void onCallback(String a, String b, String c, String d) {
                addDataTODatabases();
                finish();
            }
        });
    }


    interface FirebaseCallback {

        void onCallback(String a, String b, String c, String d);
    }
}

