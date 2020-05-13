package com.example.prototypesabre.GuestUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.prototypesabre.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.FormatFlagsConversionMismatchException;

public class member_info_activity_all extends AppCompatActivity {

    TextView name, rank, allOtherInfo;
    ImageView profileImageView;
    String email, link, namePerson, rankInfo, interest, credential;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_info_all);

        name = findViewById(R.id.memberNameTextView);
        rank = findViewById(R.id.membersRank);
        allOtherInfo = findViewById(R.id.allInfoMembers);
        profileImageView = findViewById(R.id.memberImage);

        Bundle extras = getIntent().getExtras();
        email = extras.getString("Email");

        fetchUserInfo();

    }

    public void fetchUserInfo() {
        db.collection("Users").document(email).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        link = documentSnapshot.get("Links").toString();
                        credential = documentSnapshot.get("Credential").toString();
                        rankInfo = documentSnapshot.get("Rank").toString();
                        namePerson = documentSnapshot.get("Name").toString();
                        interest = documentSnapshot.get("Interest").toString();

                        name.setText(namePerson);
                        rank.setText("Rank: " + rankInfo);
                        allOtherInfo.setText("Contact: " + email + "\n"
                                + "Interests: " + interest + "\n" + "Credential: " + credential);

                        Picasso.with(getApplicationContext()).load(link).into(profileImageView);

                    }
                });
    }


}
