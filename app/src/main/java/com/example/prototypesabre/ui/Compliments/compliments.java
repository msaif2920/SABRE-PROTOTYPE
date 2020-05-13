package com.example.prototypesabre.ui.Compliments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

public class compliments extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ;
    ArrayList<String> compliments = new ArrayList<>();
    ListView listOfCompliments;
    ArrayAdapter<String> adapterlist;
    ArrayList<String> emails = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_evaluate_compliment, container, false);

        listOfCompliments = root.findViewById(R.id.compimentListListView);
        adapterlist = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, compliments);
        listOfCompliments.setAdapter(adapterlist);
        fetchCompliments();

        listOfCompliments.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                db.collection("Compliment").document(emails.get(position))
                        .delete();
                compliments.remove(position);
                adapterlist.notifyDataSetChanged();

                return true;
            }
        });


        return root;
    }


    public void fetchCompliments() {
        db.collection("Compliment").orderBy("Compliment Times", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String info =
                                        "Compliment Times:" + document.get("Compliment Times") + "\n" +
                                                "For:" + document.get("For") + "\n" +
                                                "Sent By:" + document.get("Sent By") + "\n\n" +
                                                "Message:" + document.get("Message");

                                emails.add((String) document.get("For"));


                                compliments.add(info);
                                adapterlist.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }
}