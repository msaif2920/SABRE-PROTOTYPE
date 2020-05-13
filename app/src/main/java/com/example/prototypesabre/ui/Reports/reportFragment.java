package com.example.prototypesabre.ui.Reports;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.prototypesabre.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class reportFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<String> reports = new ArrayList<>();
    ArrayList<String> Message = new ArrayList<>();
    ArrayList<String> from = new ArrayList<>();
    ArrayList<String> to = new ArrayList<>();
    ArrayList<String> Document = new ArrayList<>();
    ArrayAdapter<String> adapterReport;
    ListView reportsList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_reports, container, false);
        reportsList = root.findViewById(R.id.listOfReports);
        adapterReport = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, reports);
        reportsList.setAdapter(adapterReport);
        fetchReports();

        reportsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), reviewComplain.class);
                intent.putExtra("Message", Message.get(position));
                intent.putExtra("From", from.get(position));
                intent.putExtra("Against", to.get(position));
                intent.putExtra("Document", Document.get(position));
                startActivity(intent);
            }
        });
        return root;
    }

    public void fetchReports() {
        db.collection("Complains").orderBy("Time", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String complains = "From:" + document.get("Complain From") + "\n\n" +
                                        "Email Or Individual: " + document.get("Email or Group");

                                Message.add(document.get("Complain").toString());
                                from.add(document.get("Complain From").toString());
                                to.add(document.get("Email or Group").toString());
                                Document.add(document.getId());

                                reports.add(complains);
                                adapterReport.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getContext(), "Something went wring", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}