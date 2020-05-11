package com.example.prototypesabre.AuthenticatedUserFragment.Group;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.prototypesabre.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class GroupMembers extends AppCompatActivity {

    ArrayList<String> Name = new ArrayList<>();
    ArrayList<String> contact = new ArrayList<>();
    ArrayList<String> personImage = new ArrayList<>();
    ArrayList<String> point = new ArrayList<>();

    RecyclerView groupMemberRecyler;

    String groupName;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authenticated_user_group_members);

        groupMemberRecyler = findViewById(R.id.groupMemberRecylerView);

        Bundle extras = getIntent().getExtras();
        groupName = extras.getString("Group Name");

        getUserInfo(new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList list, ArrayList list2, ArrayList list3, ArrayList lis4) {
                adapterMembers membersAdapters = new adapterMembers(Name, contact, personImage, point, getApplicationContext());
                groupMemberRecyler.setAdapter(membersAdapters);
                groupMemberRecyler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }
        });
    }


    public void getUserInfo(final FirebaseCallback firebaseCallback) {

        db.collection("Groups").document(groupName)
                .collection("Members")
                .orderBy("Point", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {
                                    Name.add(document.get("Name").toString());
                                    contact.add(document.get("Contact").toString());
                                    personImage.add(document.get("Profile image").toString());
                                    point.add(document.get("Point").toString());
                                } catch (Exception e) {

                                }

                                firebaseCallback.onCallback(Name, contact, personImage, point);
                            }
                        } else {
                            Toast.makeText(GroupMembers.this, "Something went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


    interface FirebaseCallback {

        void onCallback(ArrayList list, ArrayList list2, ArrayList list3, ArrayList lis4);
    }
}
