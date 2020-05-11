package com.example.prototypesabre.AuthenticatedUserFragment.Group;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.prototypesabre.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MembersInviteOthers extends AppCompatActivity {

    String groupName, currentUserEmail, currentUserName, groupDescription;
    private EditText invitedPerson;
    // private Button doneButton, inviteButton;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authenticated_user_group_invite_others);

        invitedPerson = findViewById(R.id.groupMembersEmailInvite);
       /* doneButton = findViewById(R.id.groupMembersDoneInvitingButton);
        inviteButton = findViewById(R.id.groupMembersInvitesButton);*/

        // getActionBar().setTitle("Invite Others");


        Bundle extras = getIntent().getExtras();
        groupName = extras.getString("Group Name");
        currentUserEmail = extras.getString("Current User");
        groupDescription = extras.getString("Group Description");
        getCurrentUserName();


    }


    public void getCurrentUserName() {
        db.collection("Users")
                .document(currentUserEmail)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUserName = documentSnapshot.get("Name").toString();
            }
        });
    }


    public void addData(String email) {
        Map<String, Object> groupData = new HashMap<>();
        groupData.put("Invited by", currentUserName);
        groupData.put("Contact", currentUserEmail);
        groupData.put("Group Name", groupName);
        groupData.put("Group Description", groupDescription);
        groupData.put("Time", FieldValue.serverTimestamp());

        DocumentReference docRef = db.collection("Users").document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        db.collection("Users").document(email).collection("Group Invite").document(groupName)
                                .set(groupData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("SUCCESS", "DocumentSnapshot successfully written!");
                                        Toast.makeText(getApplicationContext(), "Successfully Invited " + email, Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("FAILURE", "Error writing document", e);
                                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(getApplicationContext(), "No Such user Exists", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("Failed", "get failed with ", task.getException());
                }
            }
        });


    }

    public void invite(View view) {
        if (invitedPerson.getText().toString().isEmpty()) {
            Toast.makeText(MembersInviteOthers.this, "Enter an email address first", Toast.LENGTH_SHORT).show();
        } else {
            addData(invitedPerson.getText().toString().trim().toUpperCase());
            invitedPerson.setText("");
        }
    }

    public void done(View view) {
        finish();
    }

}
