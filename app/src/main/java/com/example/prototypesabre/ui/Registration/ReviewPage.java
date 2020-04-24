package com.example.prototypesabre.ui.Registration;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prototypesabre.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ReviewPage extends AppCompatActivity {

    TextView textInfo;
    private FirebaseFirestore db;
    String info = "";
    String Email;
    private Button approveButton;
    private Button denyButton;
    private EditText point;
    private String result;
    private String mailto, Name, Interest, Credential, Reference;
    private String subject;
    private FirebaseAuth mAuth;

    String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_page);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Intent intent = getIntent();
        Email = intent.getStringExtra("Email").trim();
        Log.i("LOL", Email);

        textInfo = findViewById(R.id.infoTextView);
        approveButton = findViewById(R.id.approveButton);
        denyButton = findViewById(R.id.denyButton);
        point = findViewById(R.id.pointEditText);

        //  approveButton.setEnabled(false);


        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Registration").document(Email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        mailto = document.get("Email").toString();
                        Name = document.get("Name").toString();
                        Interest = document.get("Interest").toString();
                        Credential = document.get("Credential").toString();
                        Reference = document.get("Reference").toString();
                        info = "Name: " + mailto + "\n"
                                + "Email: " + Email + "\n"
                                + "Interest: " + Interest + "\n"
                                + "Credential: " + Credential + "\n"
                                + "Reference: " + Reference;


                        textInfo.setText(info);
                    }
                } else {
                    Log.d("DATA", "get failed with ", task.getException());
                }
            }
        });

        point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approveButton.setEnabled(true);

            }
        });

    }

    public void sendData(String Name, String Email, String Interest, String Credential, String Reference, String point) {
        Map<String, Object> register = new HashMap<>();
        register.put("Name", Name);
        register.put("Email", Email);
        register.put("Interest", Interest);
        register.put("Credential", Credential);
        register.put("Reference", Reference);
        register.put("Point", point);
        register.put("Login", "1");


        db.collection("Users").document(Email)
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

    protected void sendEmail(String To, String Subject, String text) {
        Log.i("Send email", "");
        String[] TO = {To};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, Subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, text);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ReviewPage.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void send(View view) {
        String user_point = point.getText().toString();
        switch (view.getId()) {
            case R.id.approveButton:

                String password = new Random().ints(7, 33, 122).collect(StringBuilder::new,
                        StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString();

                result = "Congratulations! You are approved. Welcome to the SABRE community. You will start"
                        + " with initial point of " + user_point + ". This is your initial password " + password +
                        ". You will have to change the password when you login for the first time. Have a productive time" +
                        "\n Sincerely, \n Samin Saif";

                subject = "Registration approved";
                // Initialize Firebase Auth
                mAuth = FirebaseAuth.getInstance();

                mAuth.createUserWithEmailAndPassword(mailto, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    db.collection("Registration").document(Email)
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("Success", "DocumentSnapshot successfully deleted!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("Failed", "Error deleting document", e);
                                                }
                                            });
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.i("SUCCESS!", "createUserWithEmail:success");
                                    //   FirebaseUser user = mAuth.getCurrentUser();
                                    sendData(Name, mailto, Interest, Credential, Reference, user_point);
                                    sendEmail(mailto, subject, result);

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(getApplicationContext(), "SOMETHING WENT WRONG", Toast.LENGTH_LONG).show();
                                }

                                // ...
                            }
                        });


                break;

            case R.id.denyButton:
                result = "Sorry your registration wasn't approved. If this is your second try you are blocklisted and cannot"
                        + " and cannot resubmit your application. However, if it was your first attempt, you can try to " +
                        " register again. This time under interest section also add why I should reconsider my decision."
                        + "\n \n Thank you for applying. \n Sincerly \n Samin Saif";
                subject = "Registration fail";
                sendEmail(mailto, subject, result);


        }

    }
}
