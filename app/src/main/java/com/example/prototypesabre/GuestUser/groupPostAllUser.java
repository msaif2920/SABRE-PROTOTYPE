package com.example.prototypesabre.GuestUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.prototypesabre.R;
import com.example.prototypesabre.ui.users.UsersFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class groupPostAllUser extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<String> linksProfile = new ArrayList<>();
    ArrayList<String> post = new ArrayList<>();
    ArrayList<String> imageLink = new ArrayList<>();
    ArrayList<String> posterName = new ArrayList<>();

    String groupName, groupDescription;
    int positon = 0;

    RecyclerView groupPostsRecylerView;
    TextView groupNameTextView, groupDescriptionTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_post_all_user);

        Bundle extras = getIntent().getExtras();
        groupName = extras.getString("Group Name");

        Log.i("INFOO", groupName);

        groupDescription = extras.getString("Group Description");
        groupPostsRecylerView = findViewById(R.id.recylerviewgroupconetent);

        groupNameTextView = findViewById(R.id.groupNameAll);
        groupDescriptionTextView = findViewById(R.id.groupDescriptionAll);

        groupNameTextView.setText(groupName);
        groupDescriptionTextView.setText(groupDescription);

        fetchGroupContent(new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList list, ArrayList list2, ArrayList list3, ArrayList<String> list4) {
                adapterGroupPostsAll adapter = new adapterGroupPostsAll(linksProfile, post, imageLink, posterName, getApplicationContext());
                groupPostsRecylerView.setAdapter(adapter);
                groupPostsRecylerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }
        });

    }

    public void fetchGroupContent(final FirebaseCallback firebaseCallback) {
        db.collection("Groups").document(groupName).collection("Group Content")
                .whereEqualTo("Post Type", "Public")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        linksProfile.add(document.get("User Image Links").toString());
                        post.add(document.get("Post").toString());
                        posterName.add(document.get("Name").toString());
                        imageLink.add(document.get("Image Link").toString());
                        firebaseCallback.onCallback(linksProfile, post, posterName, imageLink);
                        Log.i("INFOOO", post.get(positon));
                        positon++;
                    }
                } else {

                }
            }
        });
    }


    interface FirebaseCallback {

        void onCallback(ArrayList list, ArrayList list2, ArrayList list3, ArrayList<String> list4);
    }
}
