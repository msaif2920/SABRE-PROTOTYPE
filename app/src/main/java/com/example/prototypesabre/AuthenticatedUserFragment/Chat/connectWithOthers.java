package com.example.prototypesabre.AuthenticatedUserFragment.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.prototypesabre.R;
import com.example.prototypesabre.ui.users.UsersFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class connectWithOthers extends AppCompatActivity {

    EditText connectNameEditText, connectEmailEditText,
            connectMessageEditText;
    boolean exists = false;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authenticated_user_connect_with_others);

        connectNameEditText = findViewById(R.id.nameConnectEditText);
        connectEmailEditText = findViewById(R.id.connectEmailEditText);
        connectMessageEditText = findViewById(R.id.connectMessageEditText);
    }


    public void addData(String Name, String Email, String Message) {

        Map<String, Object> createConnectRequest = new HashMap<>();

        createConnectRequest.put("Name", Name);
        createConnectRequest.put("Email", getCurrentUser());
        createConnectRequest.put("Message", Message);
        createConnectRequest.put("Time", FieldValue.serverTimestamp());

        db.collection("Users").document(Email)
                .collection("Connect Request")
                .document(getCurrentUser()).set(createConnectRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(connectWithOthers.this, "Successfully Sent request", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public Boolean checkIfUserExists(String Email, final FirebaseCallback firebaseCallback) {

        db.collection("Users").document(Email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        exists = true;
                    } else {
                        exists = false;
                    }
                    firebaseCallback.onCallback(exists);
                } else {
                    Toast.makeText(connectWithOthers.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return exists;
    }

    public void connect(View view) {
        checkIfUserExists(connectEmailEditText.getText().toString().toUpperCase(), new FirebaseCallback() {
            @Override
            public void onCallback(Boolean exist) {

                if (connectEmailEditText.getText().toString().isEmpty() ||
                        connectNameEditText.getText().toString().isEmpty() ||
                        connectMessageEditText.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    if (exists) {
                        addData(connectNameEditText.getText().toString(), connectEmailEditText.getText().toString().toUpperCase(),
                                connectMessageEditText.getText().toString());

                        connectMessageEditText.setText("");
                        connectEmailEditText.setText("");
                        connectNameEditText.setText("");

                    } else {
                        Toast.makeText(getApplicationContext(), "No such user exists!. Enter the correct email address", Toast.LENGTH_SHORT).show();
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

    interface FirebaseCallback {

        void onCallback(Boolean exist);
    }
}
