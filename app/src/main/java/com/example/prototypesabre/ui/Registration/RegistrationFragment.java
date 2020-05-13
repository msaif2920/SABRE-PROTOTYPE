package com.example.prototypesabre.ui.Registration;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.prototypesabre.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RegistrationFragment extends Fragment {


    final ArrayList<String> userRequest = new ArrayList<String>();
    private FirebaseFirestore db;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_registration_super_user, container, false);
        final ListView listof = root.findViewById(R.id.list);

        //setting up the adapter
        listof.setAdapter(new ArrayAdapter<String>(root.getContext(), android.R.layout.simple_list_item_1, userRequest));


        db = FirebaseFirestore.getInstance();

        db.collection("Registration")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String x = (String) document.get("Name") + "\n" + (String) document.get("Email");

                                userRequest.add(x);
                                ((BaseAdapter) listof.getAdapter()).notifyDataSetChanged();


                            }
                        } else {
                            Toast.makeText(getActivity(), "Something Went Wrong", Toast.LENGTH_SHORT).show();

                        }
                    }
                });


        listof.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = (listof.getItemAtPosition(position)).toString();
                String[] parts = text.split("\n");
                String part1 = parts[1];
                Intent intent = new Intent(getActivity(), ReviewPage.class);
                intent.putExtra("Email", part1);
                startActivity(intent);

            }
        });


        return root;
    }
}