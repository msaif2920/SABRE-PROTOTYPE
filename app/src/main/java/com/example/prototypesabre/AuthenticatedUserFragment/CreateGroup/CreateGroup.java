package com.example.prototypesabre.AuthenticatedUserFragment.CreateGroup;

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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class CreateGroup extends Fragment {
    FirebaseFirestore db;
    EditText groupName, descriptionGroup, Email;
    TextView checkAvail;
    Button inviteButton, doneButton, createGroup, checkGroupNameAviability;
    String currentUser, currentUserName, currentUserImageLink;
    Long currentUserPoint;


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
        checkGroupNameAviability = root.findViewById(R.id.checkGroupAvilability);

        createGroup.setEnabled(false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            currentUser = user.getEmail().toUpperCase().trim();
            DocumentReference docRef = db.collection("Users").document(currentUser);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            currentUserName = document.get("Name").toString();
                            currentUserPoint = (Long) document.get("Point");
                            currentUserImageLink = document.get("Links").toString();
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

        } else {
            // No user is signed in
        }

        checkGroupNameAviability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!groupName.getText().toString().trim().isEmpty()) {

                    checkIfNameIsAvailable(groupName.getText().toString().toUpperCase().trim());

                } else {
                    Toast.makeText(getContext(), "Please enter a name for your group", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //invite using email address
        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addData(groupName.getText().toString().toUpperCase(), descriptionGroup.getText().toString(), Email.getText().toString().trim().toUpperCase());
                Email.setText("");
                doneButton.setAlpha(1);
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
                    createGroup.setEnabled(false);
                    createGroup.setAlpha(0);
                    groupName.setEnabled(false);
                    descriptionGroup.setEnabled(false);

                    createGroup(groupName.getText().toString().toUpperCase(), descriptionGroup.getText().toString());

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
        groupData.put("Invited by", currentUserName);
        groupData.put("Contact", currentUser);
        groupData.put("Group Name", groupName);
        groupData.put("Group Description", Description);
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

    public void createGroup(String groupName, String groupDescription) {
        Map<String, Object> group = new HashMap<>();
        Map<String, Object> info = new HashMap<>();
        Map<String, Object> groupChat = new HashMap<>();


        groupChat.put("Message", "Welcome");
        groupChat.put("Time", FieldValue.serverTimestamp());
        groupChat.put("Sent by", currentUserName);


        group.put("Name", currentUserName);
        group.put("Contact", currentUser);
        group.put("Profile image", currentUserImageLink);
        group.put("Point", currentUserPoint);

        info.put("Group Description", groupDescription);
        info.put("Group Name", groupName);
        info.put("Total Point", currentUserPoint);


        db.collection("Groups").document(groupName).collection("Group Chat").document()
                .set(groupChat);




        //creating group database
        db.collection("Groups").document(groupName).collection("Members").document(currentUser)
                .set(group)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Successfully created the group", Toast.LENGTH_SHORT).show();
                        //adding group info to the creator of the group
                        db.collection("Users").document(currentUser).collection("Groups").document(groupName)
                                .set(info)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                        Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed Creating the Group. Try Again", Toast.LENGTH_SHORT).show();
                    }
                });


        //adding more information to the field of group
        info.put("Created By", currentUserName);
        info.put("Contact", currentUser);
        info.put("Image Number", 0);

        db.collection("Groups").document(groupName)
                .set(info);


    }

    public void checkIfNameIsAvailable(String name) {

        db.collection("Groups")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.i("NAMEEE", document.getId() + " and" + name);
                                if (name.equals(document.get("Group Name"))) {
                                    checkAvail.setTextColor(ContextCompat.getColor(getContext(), R.color.colorRed));
                                    checkAvail.setText("This group name is already been used");
                                    // checkAvail.setTextColor(ContextCompat.getColor(getContext(), R.color.design_default_color_error));
                                    createGroup.setEnabled(false);
                                    break;

                                } else {
                                    checkAvail.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
                                    checkAvail.setText("Group name is available for use");
                                    // checkAvail.setTextColor(ContextCompat.getColor(getContext(), R.color.common_google_signin_btn_tint));
                                    createGroup.setEnabled(true);
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }


}
