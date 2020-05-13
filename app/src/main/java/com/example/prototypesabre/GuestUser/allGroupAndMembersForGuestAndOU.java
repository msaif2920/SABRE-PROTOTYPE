package com.example.prototypesabre.GuestUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.prototypesabre.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class allGroupAndMembersForGuestAndOU extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ListView topThreeGroup, topFiveProfile;
    ArrayAdapter<String> topThree, topFive;
    ArrayList<String> topThreeProjects = new ArrayList<>();
    ArrayList<String> topFiveMembers = new ArrayList<>();
    ArrayList<String> email = new ArrayList<>();
    ArrayList<String> groupName = new ArrayList<>();
    ArrayList<String> groupDescription = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_more_guest_user);

        topThreeGroup = findViewById(R.id.groupsGuestCheck);
        topFiveProfile = findViewById(R.id.allmembers);

        topThree = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, topThreeProjects);
        topFive = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, topFiveMembers);
        topThreeGroup.setAdapter(topThree);
        topFiveProfile.setAdapter(topFive);


        fetchGroups();
        fetchMembers();

        topFiveProfile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), member_info_activity_all.class);
                intent.putExtra("Email", email.get(position));
                startActivity(intent);
            }
        });

        topThreeGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), groupPostAllUser.class);
                intent.putExtra("Group Name", groupName.get(position));
                intent.putExtra("Group Description", groupDescription.get(position));
                startActivity(intent);
            }
        });
    }

    public void fetchGroups() {
        db.collection("Groups").orderBy("Total Point", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        topThreeProjects.add(document.getId());
                        topThree.notifyDataSetChanged();
                        groupDescription.add(document.get("Group Description").toString());
                        groupName.add(document.getId());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void fetchMembers() {
        db.collection("Users").orderBy("Point", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {


                        topFiveMembers.add("RANK: " + document.get("Rank") + "\n"
                                + "Name: " + document.get("Name"));
                        topFive.notifyDataSetChanged();
                        email.add(document.get("Email").toString());

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
