package com.example.prototypesabre.ui.users;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prototypesabre.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class UsersFragment extends Fragment {


    private UsersViewModel usersViewModel;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<String> emails = new ArrayList<String>();
    ArrayList<String> name = new ArrayList<String>();
    ArrayList<String> point = new ArrayList<String>();
    ArrayList<String> imageLink = new ArrayList<String>();

    private FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();

        usersViewModel =
                ViewModelProviders.of(this).get(UsersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_list_of_user, container, false);
        recyclerView = root.findViewById(R.id.user_list);

        getUserInfo(new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList list, ArrayList list2, ArrayList list3, ArrayList list4) {

                adapter myadapter = new adapter(getContext(), emails, name, point, imageLink);
                recyclerView.setAdapter(myadapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            }
        });


        return root;
    }

    public void getUserInfo(final FirebaseCallback firebaseCallback) {
        db.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int i = 0;
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                emails.add(document.get("Email").toString());
                                name.add(document.get("Name").toString());
                                point.add(document.get("Point").toString());
                                imageLink.add(document.get("Links").toString());

                                firebaseCallback.onCallback(emails, name, point, imageLink);


                            }
                        } else {
                            Log.d("INFO", "Error getting documents: ", task.getException());
                        }
                    }

                });


    }

    interface FirebaseCallback {

        void onCallback(ArrayList list, ArrayList list2, ArrayList list3, ArrayList<String> list4);
    }


}