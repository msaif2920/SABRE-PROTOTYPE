package com.example.prototypesabre.AuthenticatedUserFragment.Group;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.prototypesabre.R;

public class Authenticated_User_Group extends AppCompatActivity {

    TextView postEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticated__user__group);

        postEditText = findViewById(R.id.postEditText);

        postEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GroupPost.class);
                startActivity(intent);
            }
        });
    }
}
