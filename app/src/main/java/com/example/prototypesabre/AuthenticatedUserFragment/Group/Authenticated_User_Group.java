package com.example.prototypesabre.AuthenticatedUserFragment.Group;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prototypesabre.R;
import com.example.prototypesabre.ui.users.UsersFragment;
import com.example.prototypesabre.ui.users.adapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Map;

public class Authenticated_User_Group extends AppCompatActivity {
    static recylerAdapter myAdapter;
    FirebaseFirestore db;
    RecyclerView showGroupContent;
    SwipeRefreshLayout refreshLayout;

    ArrayList<Long> comments = new ArrayList<>();
    ArrayList<Long> dislike = new ArrayList<>();
    ArrayList<Long> like = new ArrayList<>();

    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> post = new ArrayList<>();
    ArrayList<String> userImageLink = new ArrayList<>();
    ArrayList<String> imageLink = new ArrayList<>();
    ArrayList<String> documentId = new ArrayList<>();

    ConstraintLayout layout;

    Button inviteMembers, currentMembers;
    TextView groupNameNoPost, groupDescriptionNoPost, postSomething;


    static String groupName, groupDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticated__user__group);


        db = FirebaseFirestore.getInstance();
        showGroupContent = findViewById(R.id.groupContentRecylerView);
        refreshLayout = findViewById(R.id.refreshGroupPost);
        layout = findViewById(R.id.noPostContraintLayout);

        groupNameNoPost = findViewById(R.id.groupNameNoPost);
        groupDescriptionNoPost = findViewById(R.id.groupDescriptionNoPost);
        inviteMembers = findViewById(R.id.inviteOthersNoPost);
        currentMembers = findViewById(R.id.checkMembersButtonNoPost);
        postSomething = findViewById(R.id.groupPostSomethingNoPost);


        Bundle extras = getIntent().getExtras();
        try {
            groupName = extras.getString("Group Name");
            groupDescription = extras.getString("Group Description");
        } catch (Exception e) {

        }
        groupNameNoPost.setText(groupName);
        groupDescriptionNoPost.setText(groupDescription);
        currentMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GroupMembers.class);
                intent.putExtra("Group Name", groupName);
                startActivity(intent);
            }
        });


        inviteMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setClickable(true);
                Intent intent = new Intent(getApplicationContext(), MembersInviteOthers.class);
                intent.putExtra("Group Name", groupName);
                intent.putExtra("Group Description", groupDescription);
                intent.putExtra("Current User", getCurrentUser());
                startActivity(intent);
            }
        });

        postSomething.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GroupPost.class);
                intent.putExtra("Group Name", groupName);

                startActivity(intent);
            }
        });


        getUserInfo(new FirebaseCallback() {

            @Override
            public void onCallback(ArrayList list, ArrayList list2, ArrayList list3, ArrayList list4, ArrayList list5, ArrayList list6, ArrayList list7, ArrayList list8) {


                if (comments.size() != 0) {
                    layout.setVisibility(View.GONE);
                    showGroupContent.setVisibility(View.VISIBLE);


                    myAdapter = new recylerAdapter(comments, dislike, like, name, post, userImageLink, imageLink, documentId, groupDescription, groupName, getApplicationContext());
                    showGroupContent.setAdapter(myAdapter);
                    showGroupContent.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    Log.i("COME", "HERE");

                } else {

                    Log.i("COME", "HERE");
                    layout.setVisibility(View.VISIBLE);
                    showGroupContent.setVisibility(View.GONE);


                }
            }
        });


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                finish();
                startActivity(getIntent());
            }
        });
    }

    public void getUserInfo(final FirebaseCallback firebaseCallback) {


        db.collection("Groups").document(groupName).collection("Group Content").orderBy("Time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {


                                comments.add((Long) document.get("Comment"));
                                dislike.add((Long) document.get("Dislike"));
                                like.add((Long) document.get("Like"));
                                name.add(document.get("Name").toString());
                                post.add(document.get("Post").toString());
                                userImageLink.add(document.get("User Image Links").toString());
                                imageLink.add(document.get("Image Link").toString());
                                documentId.add(document.getId());


                                firebaseCallback.onCallback(comments, dislike, like, name, post, userImageLink, imageLink, documentId);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


    public String getCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return (user.getEmail().toUpperCase().trim());
        } else {
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            return "";
        }
    }


    interface FirebaseCallback {

        void onCallback(ArrayList list, ArrayList list2, ArrayList list3, ArrayList list4, ArrayList list5,
                        ArrayList list6, ArrayList list7, ArrayList list8);
    }


    /*
      public void currentMemberNoPost(View view){
        Intent intent = new Intent(getApplicationContext(), GroupMembers.class);
        intent.putExtra("Group Name", groupName);
        startActivity(intent);
    }

    public void inviteMembersNoPost(View view){
        Intent intent = new Intent(getApplicationContext(), MembersInviteOthers.class);
        intent.putExtra("Group Name", groupName);
        intent.putExtra("Group Description", groupDescription);
        intent.putExtra("Current User", getCurrentUser());
        startActivity(intent);
    }


    public void postSomethingNoPost(View view){
        Intent intent = new Intent(getApplicationContext(), GroupPost.class);
        intent.putExtra("Group Name", groupName);

        startActivity(intent);
    }
     */

   
}
