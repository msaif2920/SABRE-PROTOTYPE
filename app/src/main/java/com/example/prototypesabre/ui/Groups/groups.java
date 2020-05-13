package com.example.prototypesabre.ui.Groups;

import android.content.Intent;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;

import com.example.prototypesabre.AuthenticatedUserFragment.Group.Authenticated_User_Group;
import com.example.prototypesabre.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class groups extends Fragment {


    ListView groupList;
    ArrayList<String> listOfGroup = new ArrayList<String>();
    ArrayList<String> groupDescription = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String currentUser = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.authenticated_user_group_list, container, false);

        groupList = root.findViewById(R.id.groupList);
        ArrayAdapter<String> adapterGroupList = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, listOfGroup);
        groupList.setAdapter(adapterGroupList);
        getListOfGroup();


        groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = (groupList.getItemAtPosition(position)).toString();
                String[] parts = text.split("\n");
                String part1 = parts[0].trim();
                Intent intent = new Intent(getContext(), Authenticated_User_Group.class);
                intent.putExtra("Group Name", part1);
                intent.putExtra("Group Description", groupDescription.get(position));
                startActivity(intent);
            }
        });


        return root;
    }

    public void getListOfGroup() {
        currentUser = getCurrentUser();

        db.collection("Groups")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                listOfGroup.add(document.getId());
                                groupDescription.add(document.get("Group Description").toString());
                                ((BaseAdapter) groupList.getAdapter()).notifyDataSetChanged();


                            }
                        } else {
                            Toast.makeText(getContext(), "Something went wrong fetching group info", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public String getCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return (user.getEmail().toUpperCase().trim());
        } else {
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            return "";
        }
    }

}


