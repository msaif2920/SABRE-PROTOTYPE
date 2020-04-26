package com.example.prototypesabre.AuthenticatedUserFragment.CreateGroup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.prototypesabre.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateGroup extends Fragment {
    FirebaseFirestore db;
    EditText groupName, descriptionGroup, Email;
    TextView checkAvail;
    Button inviteButton, doneButton, createGroup;

    public static final String TAG = "DOCUMENT";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        View root = inflater.inflate(R.layout.authenticated_user_fragment_create_group, container, false);
        groupName = root.findViewById(R.id.groupName);
        descriptionGroup = root.findViewById(R.id.descriptionGroup);
        Email = root.findViewById(R.id.emailInvite);
        checkAvail = root.findViewById(R.id.checkNameAvilavility);
        inviteButton = root.findViewById(R.id.Invite);
        createGroup = root.findViewById(R.id.createGroup);
        doneButton = root.findViewById(R.id.doneButton);


        //invite using email address
        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addData(groupName.getText().toString(), descriptionGroup.getText().toString(), Email.getText().toString().trim().toUpperCase());
                doneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        });


        //Takes it to group Activity
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        //creates the group database
        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!groupName.getText().toString().trim().isEmpty() && !descriptionGroup.getText().toString().trim().isEmpty()) {
                    Email.setAlpha(1);
                    inviteButton.setAlpha(1);

                } else {
                    Toast.makeText(getContext(), "Please Enter Group name and Description", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }


    //this is for inviting individuals, which adds data to their sub-collection
    public void addData(String groupName, String Description, String email) {
        Map<String, Object> groupData = new HashMap<>();
        groupData.put("Group Name", groupName);
        groupData.put("Group Description", Description);
        groupData.put("Email", email);

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
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                        Toast.makeText(getContext(), "Successfully Invited " + email, Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                        Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(getContext(), "No Such user Exists", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


    }
}
