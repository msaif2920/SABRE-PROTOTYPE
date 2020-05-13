package com.example.prototypesabre.AuthenticatedUserFragment.Group;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.prototypesabre.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class removeGroupMember extends AppCompatActivity {

    EditText name, email, message;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_group_member);

        name = findViewById(R.id.nameToRemove);
        email = findViewById(R.id.emailToRemove);
        message = findViewById(R.id.reasonToRemove);
    }

    public void submitRemoval(View view) {

    }
}
