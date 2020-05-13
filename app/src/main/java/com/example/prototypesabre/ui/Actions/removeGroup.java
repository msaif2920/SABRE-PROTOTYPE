package com.example.prototypesabre.ui.Actions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prototypesabre.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class removeGroup extends AppCompatActivity {

    ArrayList<String> currentMembers = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText groupName;
    Button removeGroup;
    Boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_remove_group);

        groupName = findViewById(R.id.groupNameRemove);
        removeGroup = findViewById(R.id.removeGroup);


    }

    public void removeGroup(View view) {


        if (groupName.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter group name", Toast.LENGTH_SHORT).show();
        } else if (!check) {
            Toast.makeText(this, "No group exist with that name. Validate first", Toast.LENGTH_LONG).show();
        } else {
            String nameGroup = groupName.getText().toString().toUpperCase().trim();

            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("ARE YOU SURE YOU WANT TO REMOVE GROUP " + groupName + " !")
                    .setMessage("Are you absolutely sure you want to proceed")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            for (int i = 0; i < currentMembers.size(); i++) {
                                db.collection("Users").document(currentMembers.get(i))
                                        .collection("Groups").document(nameGroup)
                                        .delete();

                                if (i == currentMembers.size() - 1) {
                                    db.collection("Groups").document(nameGroup).delete();
                                }
                            }

                            Toast.makeText(removeGroup.this, "Done", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();

        }
    }

    public void validate(View view) {
        if (groupName.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter group name", Toast.LENGTH_SHORT).show();
        } else {
            db.collection("Groups").document(groupName.getText().toString().toUpperCase()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    removeGroup.setEnabled(true);
                                    check = true;
                                    getCurrentMembers(groupName.getText().toString().toUpperCase());
                                    Toast.makeText(removeGroup.this, "Validated", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(removeGroup.this, "No group exist with that name", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                    });

       /*     getCurrentMembers(groupName.getText().toString().toUpperCase());
            Toast.makeText(this, "Validated", Toast.LENGTH_SHORT).show();*/

        }
    }


    public void getCurrentMembers(String groupName) {
        db.collection("Groups").document(groupName).
                collection("Members").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        currentMembers.add(document.getId());
                    }
                }
            }
        });


    }

}
