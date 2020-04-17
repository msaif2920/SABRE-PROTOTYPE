package com.example.prototypesabre;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signInButton;
    String SupEmail = "";
    String supPassWord = "";
    String time = "";

    private FirebaseFirestore db;
    firebaseFunction Firebasefunction = new firebaseFunction();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();


        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        // Access a Cloud Firestore instance from your Activity
        // Access a Cloud Firestore instance from your Activity


    }

    public void register(View view) {
        Intent intent = new Intent(getApplicationContext(), RegisternowActivity.class);
        startActivity(intent);
    }

    public void enterGuestMode(View view) {
        //guest Mode page
    }

    public void signIn(View view) {
        Log.i("DATA", "HERE");
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();


        db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("SuperUser").document("Credential");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        SupEmail = (String) document.get("email");
                        supPassWord = (String) document.get("password");
                    }
                } else {
                    Log.d("DATA", "get failed with ", task.getException());
                }
            }
        });

        System.out.println(SupEmail + " " + supPassWord);

        if (email.equals("") || password.equals("")) {
            Toast.makeText(getApplicationContext(), "Please Enter your credentials", Toast.LENGTH_SHORT).show();

        } else if (SupEmail.equals(email) && supPassWord.equals(password)) {
            Intent intent = new Intent(getApplicationContext(), SuperUserActivity.class);

            startActivity(intent);
        } else {
            signInExistingUser(email, password);
        }
    }

    public void signInExistingUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Sign in Succesfull", "signInWithEmail:success");

                            FirebaseUser user = mAuth.getCurrentUser();
                            DocumentReference docRef = db.collection("Users").document(email);
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            if (document.get("Login").equals("1")) {
                                                Firebasefunction.updateField("Users", email.trim(), "Login", "2", getApplicationContext());
                                                Intent intent = new Intent(getApplicationContext(), firstTimelogin.class);
                                                startActivity(intent);
                                            }
                                        } else {
                                            Toast.makeText(getApplicationContext(), "No such document exists", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "No Internet Connection failed to retrieve data", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                            Intent intent = new Intent(getApplicationContext(), firstTimelogin.class);
                            startActivity(intent);


                                /*Intent intent = new Intent(getApplicationContext(), AuthenticateduserActivity.class);
                                startActivity(intent);
*/


                        } else {
                            Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
                            // ...
                        }

                        // ...
                    }
                });

    }


}
