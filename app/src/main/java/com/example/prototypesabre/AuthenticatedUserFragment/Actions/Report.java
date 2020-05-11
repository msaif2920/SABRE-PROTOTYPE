package com.example.prototypesabre.AuthenticatedUserFragment.Actions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.prototypesabre.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Report extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    EditText complainsEmailAddress, complainsEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authenticated_user_report);
        getSupportActionBar().setTitle("Complaints");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        complainsEditText = findViewById(R.id.complainEditText);
        complainsEmailAddress = findViewById(R.id.complainsEmailAddress);
    }


    private String getCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return (user.getEmail().toUpperCase().trim());
        } else {
            return "Anonymous User";
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

    public void sendComplain(View view) {
        if (complainsEditText.getText().toString().isEmpty() || complainsEmailAddress.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please fill in all the fields appropriately", Toast.LENGTH_SHORT).show();
        } else {
            complainDatabase(complainsEmailAddress.getText().toString(), getCurrentUser(), complainsEditText.getText().toString());

        }
    }

    public void complainDatabase(String emailOrGroup, String from, String complain) {

        Map<String, Object> complains = new HashMap<>();
        complains.put("Email/Group", emailOrGroup);
        complains.put("Complain From", from);
        complains.put("Complain", complain);
        complains.put("Time", FieldValue.serverTimestamp());
        db.collection("Complains").document()
                .set(complains).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(Report.this, "Complain Successfully Posted", Toast.LENGTH_SHORT).show();
                complainsEmailAddress.setText("");
                complainsEditText.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Report.this, "Whoops Something went wrong. Check Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

