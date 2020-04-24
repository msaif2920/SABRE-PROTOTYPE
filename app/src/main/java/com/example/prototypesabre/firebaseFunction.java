package com.example.prototypesabre;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class firebaseFunction {
    String fieldValue = "";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    public String getAFieldValue(String Collection, String Document, String Field, Context ct) {

        DocumentReference docRef = db.collection(Collection).document(Document);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        fieldValue = document.get(Field).toString();
                        Log.i("TIMEE", fieldValue);
                    } else {
                        Toast.makeText(ct, "No such document exists", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ct, "No Internet Connection failed to retrieve data", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return fieldValue;

    }

    public void updateField(String Collection, String Document, String Field, String newVal, Context ct) {
        DocumentReference Ref = db.collection(Collection).document(Document);


        Ref
                .update(Field, newVal)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Success", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("FAILED", "Error updating document", e);
                    }
                });
    }

    public void changePassword(String password) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String newPassword = password;

        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i("DONE", "User password updated.");
                        }
                    }
                });
    }

}
