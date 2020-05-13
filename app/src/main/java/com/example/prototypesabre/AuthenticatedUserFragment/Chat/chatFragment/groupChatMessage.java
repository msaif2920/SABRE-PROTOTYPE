package com.example.prototypesabre.AuthenticatedUserFragment.Chat.chatFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.prototypesabre.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class groupChatMessage extends AppCompatActivity {

    String groupName, currentuserName;
    SwipeRefreshLayout refreshGroupChat;
    EditText groupChatMessageEditText;
    Button groupChatMessageSend;
    RecyclerView groupChatRecylerView;

    ArrayList<String> bannedWord = new ArrayList<>();

    ArrayList<String> message = new ArrayList<>();
    ArrayList<String> sentBy = new ArrayList<>();

    String finalMessage = "";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference blockWordRef = db.collection("Blocked Word");

    Boolean update = false;

    String[] parts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentiated_user_group_chat);

        Bundle extras = getIntent().getExtras();
        groupName = extras.getString("Group Name");
        getCurrentUserName();

        refreshGroupChat = findViewById(R.id.refreshGroupChat);
        groupChatMessageEditText = findViewById(R.id.groupChatMessageEditText);
        groupChatMessageSend = findViewById(R.id.groupChatMessageSend);
        groupChatRecylerView = findViewById(R.id.groupChatRecylerView);


        fetchGroupMessage(new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList list, ArrayList list2) {
                oneToOneMessageAdapter adapter = new oneToOneMessageAdapter(message, sentBy,
                        getApplicationContext(), currentuserName, "None");

                groupChatRecylerView.setAdapter(adapter);
                final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                linearLayoutManager.setStackFromEnd(true);
                groupChatRecylerView.setLayoutManager(linearLayoutManager);
            }
        });


        refreshGroupChat.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                finish();
                startActivity(getIntent());
            }
        });

        blockWordRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        bannedWord.add(document.getId());

                    }
                } else {

                }
            }
        });

    }

    public void fetchGroupMessage(final FirebaseCallback firebaseCallback) {

        db.collection("Groups").document(groupName)
                .collection("Group Chat").orderBy("Time", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                sentBy.add(document.get("Sent By").toString());
                                String z = document.get("Sent By").toString();
                                message.add(z + "\n" + document.get("Message").toString());
                                firebaseCallback.onCallback(message, sentBy);
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

    public void getCurrentUserName() {
        db.collection("Users").document(getCurrentUser())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentuserName = documentSnapshot.get("Name").toString();
            }
        });
    }

    public void sendGroupMessage(View view) {

        if (groupChatMessageEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter your message first", Toast.LENGTH_SHORT).show();
        } else {
            parts = (groupChatMessageEditText.getText().toString()).split("\\s+");

            if (checkIfWordIsBanned()) {
                db.collection("Users").document(getCurrentUser())
                        .update("Point", FieldValue.increment(-1));
            }

            Map<String, Object> sendMessage = new HashMap<>();
            for (int i = 0; i < parts.length; i++) {

            }
            sendMessage.put("Message", finalMessage);
            sendMessage.put("Sent By", currentuserName);
            sendMessage.put("Time", FieldValue.serverTimestamp());

            db.collection("Groups").document(groupName).
                    collection("Group Chat")
                    .add(sendMessage);

            groupChatMessageEditText.setText("");
            finish();
            startActivity(getIntent());
        }
    }

    interface FirebaseCallback {

        void onCallback(ArrayList list, ArrayList list2);
    }

    interface FirebaseCallback2 {

        void onCallback(Boolean val);
    }

    //sorry I will fix this later
    public Boolean checkIfWordIsBanned() {
        for (int i = 0; i < parts.length; i++) {
            for (int j = 0; j < bannedWord.size(); j++) {
                if (parts[i].equalsIgnoreCase(bannedWord.get(j))) {
                    parts[i] = "****";

                    update = true;
                }


            }
            finalMessage = finalMessage + parts[i] + " ";
        }
        return update;
    }
}
