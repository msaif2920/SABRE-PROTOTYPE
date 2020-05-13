package com.example.prototypesabre;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisternowActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    EditText Name;
    EditText Email;
    EditText Interest;
    EditText Credential;
    EditText Reference;

    Boolean initial = false;

    ArrayList<String> blockList = new ArrayList<>();

    String name = null, email = null, interest = null, credential = null, reference = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registernow);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        Name = findViewById(R.id.nameEditText);
        Email = findViewById(R.id.emailEditText);
        Interest = findViewById(R.id.interestEditText);
        Credential = findViewById(R.id.credentialEditText);
        Reference = findViewById(R.id.referenceEditText);

        db = FirebaseFirestore.getInstance();

        getBlockList();
    }

    public void submit(View view) {

        if (!initial) {
            Toast.makeText(this, "Email Address not Validated", Toast.LENGTH_SHORT).show();
        } else {


            boolean check = true;
            name = Name.getText().toString();
            if (name.trim().isEmpty()) {
                check = false;
            }
            email = Email.getText().toString().toUpperCase().trim();
            if (email.trim().isEmpty()) {
                check = false;
            }
            interest = Interest.getText().toString();
            if (interest.trim().isEmpty()) {
                check = false;
            }
            credential = Credential.getText().toString();
            if (credential.trim().isEmpty()) {
                check = false;
            }
            reference = Reference.getText().toString();
            if (reference.trim().isEmpty()) {
                check = false;
            }

            if (!check) {
                Toast.makeText(getApplicationContext(), "Fill in all the fields", Toast.LENGTH_SHORT).show();
            } else {
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure you want submit?")
                        .setMessage("Only proceed if all your information is correct.Super user will review your information and email you if he excepted your information or not ")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendData(name, email, interest, credential, reference);
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "You can fix your information and submit", Toast.LENGTH_SHORT).show();
                            }
                        }).show();

            }
        }

    }

    public void sendData(String Name, String Email, String Interest, String Credential, String Reference) {


        Map<String, Object> register = new HashMap<>();
        register.put("Name", Name);
        register.put("Email", Email);
        register.put("Interest", Interest);
        register.put("Credential", Credential);
        register.put("Reference", Reference);


        db.collection("Registration").document(Email)
                .set(register)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("PASS", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("FAIL", "Error writing document", e);
                    }
                });

    }

    public void getBlockList() {
        db.collection("Block List").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        blockList.add(document.getId());
                        Log.i("BLOCKED", document.getId());
                    }
                } else {

                }
            }
        });

    }

    boolean checkIfBlockListed(String email) {
        for (int i = 0; i < blockList.size() - 1; i++) {
            if (email == blockList.get(i)) {
                return false;
            }
        }
        return true;

    }

    public void validateEmail(View view) {
        if (Email.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please add your email", Toast.LENGTH_SHORT).show();
        } else {
            if (!checkIfBlockListed(Email.getText().toString().toUpperCase())) {
                Toast.makeText(this, "You are blocked", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Validated", Toast.LENGTH_SHORT).show();
                initial = true;
            }

        }


    }
}

