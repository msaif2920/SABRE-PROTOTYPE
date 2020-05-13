package com.example.prototypesabre.AuthenticatedUserFragment.GroupRequest;

import android.content.Intent;
import android.os.Bundle;
import android.os.StatFs;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.prototypesabre.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class GroupRequest extends Fragment {
    ArrayList<String> groupRequest = new ArrayList<>();
    String currentUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    static ListView listOfRequest;
    ConstraintLayout layout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            currentUser = user.getEmail().toUpperCase().trim();

        } else {
            // No user is signed in
        }


        View root = inflater.inflate(R.layout.authenticated_user_fragment_group_request, container, false);

        layout = root.findViewById(R.id.listviewConstrained);

        listOfRequest = root.findViewById(R.id.groupRequest);
        ArrayAdapter<String> adapterlist = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, groupRequest);
        listOfRequest.setAdapter(adapterlist);
        db.collection("Users").document(currentUser).collection("Group Invite").orderBy("Time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                groupRequest.add(document.getId() + "\n" + "Invited by " + document.get("Invited by"));
                                ((BaseAdapter) listOfRequest.getAdapter()).notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        listOfRequest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = (listOfRequest.getItemAtPosition(position)).toString();
                String[] parts = text.split("\n");
                String part1 = parts[0].trim();
                Intent intent = new Intent(getContext(), GroupReviewActivity.class);
                intent.putExtra("Group Name", part1);
                startActivity(intent);
            }
        });


        return root;
    }

    @Override
    public void onStart() {
        ((BaseAdapter) listOfRequest.getAdapter()).notifyDataSetChanged();
        super.onStart();
    }
}

