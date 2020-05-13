package com.example.prototypesabre.AuthenticatedUserFragment.Chat.chatFragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.prototypesabre.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class groupMessageFragment extends Fragment {

    ListView list;

    ArrayList<String> groupName = new ArrayList<>();
    ArrayAdapter<String> adapterlist;


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference getGroup;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.authenticated_user_fragment_group, container, false);

        list = root.findViewById(R.id.groupListChatListView);

        getGroup = db.collection("Users").document(getCurrentUser()).collection("Groups");
        adapterlist = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, groupName);
        list.setAdapter(adapterlist);
        fetchInfo();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), groupChatMessage.class);
                String text = (list.getItemAtPosition(position)).toString();
                intent.putExtra("Group Name", text);

                startActivity(intent);
            }
        });


        return root;
    }

    public void fetchInfo() {
        getGroup.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        groupName.add(document.getId());

                    }
                    adapterlist.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
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



