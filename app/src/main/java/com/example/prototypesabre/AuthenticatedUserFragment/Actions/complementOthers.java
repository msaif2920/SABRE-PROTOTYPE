package com.example.prototypesabre.AuthenticatedUserFragment.Actions;

import android.os.Bundle;

import com.example.prototypesabre.AuthenticatedUserFragment.Chat.connectWithOthers;
import com.example.prototypesabre.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class complementOthers extends AppCompatActivity {

    Button sendCompliment;
    EditText emailReceivingCompliment, complimentEditText;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Long times = 0L;
    DocumentReference ref;
    Boolean exists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authenticated_user_complement_others);

        sendCompliment = findViewById(R.id.sendComplimentButton);
        emailReceivingCompliment = findViewById(R.id.emailEditText);
        complimentEditText = findViewById(R.id.complimentEditText);


        getSupportActionBar().setTitle("Give Compliments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }


    public void sendCompliment(View view) {

        if (emailReceivingCompliment.getText().toString().isEmpty()
                || complimentEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please Enter Appropriate values", Toast.LENGTH_SHORT).show();
        } else {
            checkIfUserExists(emailReceivingCompliment.getText().toString().toUpperCase(), new FirebaseCallback() {
                @Override
                public void onCallback(Boolean exist) {
                    if (exists) {
                        addComplimentToDatabase(emailReceivingCompliment.getText().toString().toUpperCase(),
                                complimentEditText.getText().toString());
                        emailReceivingCompliment.setText("");
                        complimentEditText.setText("");
                    } else {
                        Toast.makeText(complementOthers.this, "No User Exists With That Email Address", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
    }


    public void addComplimentToDatabase(String sendTo, String message) {
        ref = db.collection("Compliment").document(sendTo);

        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        times = (Long) document.get("Compliment Times");
                        if (document.getData().keySet().contains(getCurrentUserEmail())) {

                            try {
                                times--;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Map<String, Object> compliment = new HashMap<>();
                    compliment.put("Message", message);
                    compliment.put("Compliment Times", times + 1);
                    compliment.put("Time", FieldValue.serverTimestamp());
                    compliment.put("Sent By", getCurrentUserEmail());
                    compliment.put("For", sendTo);

                    db.collection("Compliment").document(sendTo)
                            .set(compliment, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Successfully sent compliment", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    public String getCurrentUserEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return (user.getEmail().toUpperCase().trim());
        } else {
            return "";
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return exists;
    }

    interface FirebaseCallback {

        void onCallback(Boolean exist);
    }
}
