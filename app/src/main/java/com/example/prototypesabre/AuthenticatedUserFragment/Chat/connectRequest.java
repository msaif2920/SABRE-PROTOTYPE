package com.example.prototypesabre.AuthenticatedUserFragment.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.prototypesabre.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class connectRequest extends AppCompatActivity {

    ArrayList<String> listOfRequest = new ArrayList<>();
    ArrayList<String> message = new ArrayList<>();
    ListView connectRequestListView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayAdapter<String> adapterlist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authenticated_user_connect_request);

        connectRequestListView = findViewById(R.id.connectRequestListView);

        adapterlist = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listOfRequest);
        connectRequestListView.setAdapter(adapterlist);

        fetchRequest();

        connectRequestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = (connectRequestListView.getItemAtPosition(position)).toString();
                String[] parts = text.split("\n");
                String part1 = parts[1].trim();
                String part = parts[0].trim();
                Intent intent = new Intent(getApplicationContext(), connectRequestReview.class);
                intent.putExtra("Email", part1);
                intent.putExtra("Name", part);
                intent.putExtra("Message", message.get(position));
                startActivity(intent);
            }
        });


    }

    public void fetchRequest() {
        db.collection("Users").document(getCurrentUser()).collection("Connect Request")
                .orderBy("Time", Query.Direction.ASCENDING)

                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                listOfRequest.add(document.get("Name").toString() + "\n"
                                        + document.get("Email").toString());

                                message.add(document.get("Message").toString());

                                adapterlist.notifyDataSetChanged();
                            }
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
}
