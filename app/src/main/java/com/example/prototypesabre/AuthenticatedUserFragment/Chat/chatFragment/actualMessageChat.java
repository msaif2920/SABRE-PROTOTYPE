package com.example.prototypesabre.AuthenticatedUserFragment.Chat.chatFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.prototypesabre.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class actualMessageChat extends AppCompatActivity {

    RecyclerView oneToOneChatRecylerview;
    String connectedEmail, documentNumber;
    EditText newMessage;

    ArrayList<String> messages = new ArrayList<>();
    ArrayList<String> postedBy = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference ref;
    DocumentReference currentUserRef, connectedUserRef;
    oneToOneMessageAdapter adapter;

    SwipeRefreshLayout refreshChat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authenticated_user_message_chat);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Bundle extras = getIntent().getExtras();
        connectedEmail = extras.getString("Email");
        documentNumber = extras.getString("DocumentNumber");

        refreshChat = findViewById(R.id.refreshChat);

        ref = db.collection("Chat").document(documentNumber)
                .collection("Message");
        currentUserRef = db.collection("Users").document(getCurrentUser())
                .collection("Connection").document(documentNumber);

        connectedUserRef = db.collection("Users").document(connectedEmail)
                .collection("Connection").document(documentNumber);

        oneToOneChatRecylerview = findViewById(R.id.listOfMessageOneToOne);

        newMessage = findViewById(R.id.newMessage);


        fetchInfo(new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList list, ArrayList list2) {
                adapter = new oneToOneMessageAdapter(messages, postedBy, getApplicationContext(), getCurrentUser(), documentNumber);
                oneToOneChatRecylerview.setAdapter(adapter);
                // oneToOneChatRecylerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                linearLayoutManager.setStackFromEnd(true);
                oneToOneChatRecylerview.setLayoutManager(linearLayoutManager);

            }
        });

        refreshChat.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                finish();
                startActivity(getIntent());
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


    public void fetchInfo(final FirebaseCallback firebaseCallback) {
        ref.orderBy("Time", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        messages.add(document.get("Message").toString());
                        Log.i("CHEKINGG", document.get("Sent By").toString());
                        postedBy.add(document.get("Sent By").toString());
                        firebaseCallback.onCallback(messages, postedBy);
                    }
                } else {
                    Toast.makeText(actualMessageChat.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    interface FirebaseCallback {

        void onCallback(ArrayList list, ArrayList list2);
    }

    public void sendMessage(View view) {
        if (newMessage.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter your message", Toast.LENGTH_SHORT).show();
        } else {
            Map<String, Object> messages = new HashMap<>();
            messages.put("Message", newMessage.getText().toString());
            messages.put("Sent By", getCurrentUser());
            messages.put("Time", FieldValue.serverTimestamp());
            ref.document().set(messages);

            currentUserRef.update("Time", FieldValue.serverTimestamp());
            currentUserRef.update("Update", FieldValue.increment(1));
            currentUserRef.update("Recent Message", newMessage.getText().toString());

            connectedUserRef.update("Time", FieldValue.serverTimestamp());
            connectedUserRef.update("Update", FieldValue.increment(1));
            connectedUserRef.update("Recent Message", newMessage.getText().toString());
            newMessage.setText("");
            finish();
            startActivity(getIntent());

        }


    }


}
