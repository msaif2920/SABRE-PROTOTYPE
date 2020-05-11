package com.example.prototypesabre.AuthenticatedUserFragment.Chat.chatFragment;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prototypesabre.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class oneToOneMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<String> messages = new ArrayList<>();
    ArrayList<String> postedBy = new ArrayList<>();
    Context ct;
    String currentUser;
    String documentNumber;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public oneToOneMessageAdapter(ArrayList<String> messages, ArrayList<String> postedBy, Context ct,
                                  String currentUser, String documentNumber) {
        this.messages = messages;
        this.postedBy = postedBy;
        this.ct = ct;
        this.currentUser = currentUser;
        this.documentNumber = documentNumber;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == 0) {
            view = layoutInflater.inflate(R.layout.row_user_one_one_chat_left, parent, false);

            ViewHolderOne held = new ViewHolderOne(view);
            return held;
        } else {

            view = layoutInflater.inflate(R.layout.row_user_one_to_one_chat_right, parent, false);
            return new ViewHolderTwo(view);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (!postedBy.get(position).equals(currentUser)) {
            ViewHolderOne viewHolderOne = (ViewHolderOne) holder;
            viewHolderOne.leftSide.setText(messages.get(position));

        } else {
            ViewHolderTwo viewHolderTwo = (ViewHolderTwo) holder;
            viewHolderTwo.rightSide.setText(messages.get(position));
        }
        if (!documentNumber.equals("None")) {
            db.collection("Users").document(currentUser).collection("Connection")
                    .document(documentNumber).update("Update", 0);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (postedBy.get(position).equals(currentUser)) {
            return 1;
        } else {

            return 0;
        }
    }

    class ViewHolderOne extends RecyclerView.ViewHolder {
        TextView leftSide;

        public ViewHolderOne(@NonNull View itemView) {
            super(itemView);

            leftSide = itemView.findViewById(R.id.leftSideText);
        }
    }

    class ViewHolderTwo extends RecyclerView.ViewHolder {
        TextView rightSide;

        public ViewHolderTwo(@NonNull View itemView) {
            super(itemView);

            rightSide = itemView.findViewById(R.id.rightSideTextUser);
        }
    }

    public String getCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return (user.getEmail().toUpperCase().trim());
        } else {
            Toast.makeText(ct, "Something went wrong", Toast.LENGTH_SHORT).show();
            return "";
        }
    }


}
