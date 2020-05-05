package com.example.prototypesabre.ui.users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.prototypesabre.MainActivity;
import com.example.prototypesabre.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;


public class UserInfoActivity extends AppCompatActivity {

    private String Email;
    int postion;
    private FirebaseFirestore db;
    private TextView userinfo;
    private EditText point;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        image = findViewById(R.id.userInfoProfiile);
        userinfo = findViewById(R.id.userInfoTextView);
        point = findViewById(R.id.pointEditText);


        Intent intent = getIntent();
        Email = intent.getStringExtra("Email").trim();
        postion = intent.getIntExtra("Position", 1);

        db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("Users").document(Email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        String mailto = document.get("Email").toString();
                        String Name = document.get("Name").toString();
                        String Interest = document.get("Interest").toString();
                        String Credential = document.get("Credential").toString();
                        String Reference = document.get("Reference").toString();
                        String info = "Name: " + Name + "\n"
                                + "Email: " + Email + "\n"
                                + "Interest: " + Interest + "\n"
                                + "Credential: " + Credential + "\n"
                                + "Reference: " + Reference;
                        userinfo.setText(info);
                        point.setText(document.get("Point").toString());
                        Picasso.with(getApplicationContext()).load(adapter.links.get(postion)).into(image);


                    } else {
                        Log.i("WRONG", "No such document");
                    }
                } else {
                    Log.i("FAILED", "get failed with ", task.getException());
                }
            }
        });
    }


}
