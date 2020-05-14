package com.example.prototypesabre.AuthenticatedUserFragment.GroupRequest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;

public class GroupReviewActivity extends AppCompatActivity {
    TextView groupInfo;
    Button acceptButton;
    String groupName;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String currentUser, Name, groupDescription, contact, invitedbBy, imageLink;
    long Point;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authenticated_group_invite_review_activity);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            currentUser = user.getEmail().toUpperCase().trim();

        } else {
            // No user is signed in
        }

        groupInfo = findViewById(R.id.groupInviteInfo);
        acceptButton = findViewById(R.id.acceptInviteButton);
        Bundle extras = getIntent().getExtras();
        groupName = extras.getString("Group Name");

        DocumentReference docRef = db.collection("Groups").document(groupName);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        groupName = document.getId().trim();
                        groupDescription = document.get("Group Description").toString();
                        invitedbBy = document.get("Created By").toString();
                        contact = document.get("Contact").toString();
                        String x = "Group Name: " + groupName + "\n"
                                + "Group Description: " + groupDescription + "\n"
                                + "Created by: " + invitedbBy + "\n"
                                + "Contact info: " + contact;
                        groupInfo.setText(x);
                        getVal();

                    } else {

                    }
                } else {
                    Toast.makeText(GroupReviewActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void acceptInvite(View view) {

        Map<String, Object> data = new HashMap<>();
        Map<String, Object> info = new HashMap<>();

        info.put("Group Description", groupDescription);
        info.put("Group Name", groupName);
        info.put("Contact", contact);
        info.put("Created By", invitedbBy);

        data.put("Point", Point);
        data.put("Name", Name);
        data.put("Profile image", imageLink);
        data.put("Contact", currentUser);

        db.collection("Groups").document(groupName).update("Total Point", FieldValue.increment(Point));

        db.collection("Groups").document(groupName).collection("Members").document(currentUser)
                .set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                db.collection("Users").document(currentUser).collection("Groups").document(groupName)
                        .set(info).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                        db.collection("Users").document(currentUser).collection("Group Invite")
                                .document(groupName)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(GroupReviewActivity.this, "Sucessfully added the group", Toast.LENGTH_SHORT).show();
                                        ((BaseAdapter) GroupRequest.listOfRequest.getAdapter()).notifyDataSetChanged();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(GroupReviewActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                finish();

            }
        });
    }

    public void getVal() {
        DocumentReference ref = db.collection("Users").document(currentUser);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Name = document.get("Name").toString();
                        Point = (long) document.get("Point");
                        imageLink = document.get("Links").toString();


                    } else {
                        Toast.makeText(GroupReviewActivity.this, "Ops", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(GroupReviewActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void deny(View view) {
        db.collection("Users").document(currentUser).collection("Group Invite")
                .document(groupName).delete();
        finish();
    }
}
