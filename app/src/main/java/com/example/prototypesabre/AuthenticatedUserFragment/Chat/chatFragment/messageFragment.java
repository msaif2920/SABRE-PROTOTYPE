package com.example.prototypesabre.AuthenticatedUserFragment.Chat.chatFragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

/**
 * A simple {@link Fragment} subclass.
 */
public class messageFragment extends Fragment {


    RecyclerView individualMessageRecylerView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<String> messageSentBy = new ArrayList<>();
    ArrayList<String> recentMessage = new ArrayList<>();
    ArrayList<Long> update = new ArrayList<>();
    ArrayList<String> image = new ArrayList<>();
    ArrayList<String> connectedToEmail = new ArrayList<>();
    ArrayList<String> documentNumber = new ArrayList<>();
    SwipeRefreshLayout refreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.authenticated_user_fragment_message, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        individualMessageRecylerView = root.findViewById(R.id.individualMessageRecylerView);

        fetchRecylerInfo(new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList<String> list1, ArrayList<String> list2, ArrayList<String> list3, ArrayList<Long> list4, ArrayList<String> list5
                    , ArrayList<String> list6) {
                chatAdapter adapter = new chatAdapter(messageSentBy, recentMessage, update, image, getContext(), connectedToEmail, documentNumber);
                individualMessageRecylerView.setAdapter(adapter);
                individualMessageRecylerView.setLayoutManager(new LinearLayoutManager(getContext()));


            }
        });
        refreshLayout = root.findViewById(R.id.refreshPage);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivity().finish();
                startActivity(getActivity().getIntent());
            }
        });

        return root;
    }

    public void fetchRecylerInfo(final FirebaseCallback firebaseCallback) {
        db.collection("Users").document(getCurrentUser())
                .collection("Connection").orderBy("Time", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        messageSentBy.add(document.get("Name").toString());
                        recentMessage.add(document.get("Recent Message").toString());
                        update.add((Long) document.get("Update"));
                        image.add(document.get("Profile Image").toString());
                        connectedToEmail.add(document.get("Email").toString());
                        documentNumber.add(document.getId());
                        firebaseCallback.onCallback(messageSentBy, recentMessage, image, update, connectedToEmail, documentNumber);
                    }
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

    interface FirebaseCallback {

        void onCallback(ArrayList<String> list1, ArrayList<String> list2, ArrayList<String> list3, ArrayList<Long> list4, ArrayList<String> list5,
                        ArrayList<String> list6);
    }

}
