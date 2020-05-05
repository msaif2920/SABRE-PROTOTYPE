package com.example.prototypesabre;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class authenticatedUserForgotPassword extends AppCompatActivity {

    EditText enterEmail, repeatEmail;
    Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authenticated_user_forgot_password);

        enterEmail = findViewById(R.id.emailForgotPasswordEditText);
        repeatEmail = findViewById(R.id.repeatedForgotPasswordEditText);

        sendButton = findViewById(R.id.sendNewPassword);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = enterEmail.getText().toString();
                String repeatemail = repeatEmail.getText().toString();
                if (!email.isEmpty() && repeatemail.equals(email)) {
                    FirebaseAuth auth = FirebaseAuth.getInstance();

                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(authenticatedUserForgotPassword.this, "Sent a reset password links to " +
                                                "your email", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(authenticatedUserForgotPassword.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(authenticatedUserForgotPassword.this, "Please enter email in appropiate manner and " +
                            "make sure they match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
