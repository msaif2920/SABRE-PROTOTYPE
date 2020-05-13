package com.example.prototypesabre.ui.Reports;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prototypesabre.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class reviewComplain extends AppCompatActivity {

    String Message, from, against, documents;

    TextView fromReport, toReport, actualReport;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.super_user_review_complain);


        Bundle extras = getIntent().getExtras();

        Message = extras.getString("Message");
        from = extras.getString("From");
        against = extras.getString("Against");
        documents = extras.getString("Document");

        fromReport = findViewById(R.id.fromReport);
        toReport = findViewById(R.id.toReport);
        actualReport = findViewById(R.id.actualReport);


        fromReport.setText("Complain From:" + from);
        toReport.setText("Complaint Against: " + against);
        actualReport.setText("Complaint: " + Message);


    }

    public void deleteReport(View view) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("ARE YOU SURE YOU WANT TO DELETE!")
                .setMessage("Did you evaluate the complaints and take proper steps")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.collection("Complains").document(documents)
                                .delete();

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
