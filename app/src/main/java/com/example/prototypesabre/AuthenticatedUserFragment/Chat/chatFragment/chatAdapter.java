package com.example.prototypesabre.AuthenticatedUserFragment.Chat.chatFragment;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prototypesabre.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class chatAdapter extends RecyclerView.Adapter<chatAdapter.ViewHolder> {

    ArrayList<String> messageSentBy = new ArrayList<>();
    ArrayList<String> recentMessage = new ArrayList<>();
    ArrayList<Long> update = new ArrayList<>();
    ArrayList<String> image = new ArrayList<>();
    ArrayList<String> connectedToEmail = new ArrayList<>();
    ArrayList<String> documentNumber = new ArrayList<>();


    Context ct;

    public chatAdapter(ArrayList<String> messageSentBy, ArrayList<String> recentMessage, ArrayList<Long> update, ArrayList<String> image, Context ct,
                       ArrayList<String> emails, ArrayList<String> documentNumbers) {
        this.messageSentBy = messageSentBy;
        this.recentMessage = recentMessage;
        this.update = update;
        this.image = image;
        this.ct = ct;
        connectedToEmail = emails;
        documentNumber = documentNumbers;

    }

    @NonNull
    @Override
    public chatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ct);
        View view = inflater.inflate(R.layout.row_chat_list, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull chatAdapter.ViewHolder holder, int position) {
        holder.chatContent.setText(recentMessage.get(position));
        holder.chatterNameTextView.setText(messageSentBy.get(position));
        Picasso.with(ct).load(image.get(position)).into(holder.chatterImageView);

        if (update.get(position) > 0) {
            holder.numberOfNewTextView.setText(update.get(position).toString());
        }

        holder.openMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ct, actualMessageChat.class);
                intent.putExtra("Email", connectedToEmail.get(position));
                intent.putExtra("DocumentNumber", documentNumber.get(position));
                ct.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return messageSentBy.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView chatterNameTextView, numberOfNewTextView, chatContent;
        ImageView chatterImageView;
        ConstraintLayout openMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            chatterImageView = itemView.findViewById(R.id.chatterImageView);
            chatterNameTextView = itemView.findViewById(R.id.chatterNameTextView);
            numberOfNewTextView = itemView.findViewById(R.id.numberOfNewTextView);
            chatContent = itemView.findViewById(R.id.chatContent);
            openMessage = itemView.findViewById(R.id.openMessage);
        }
    }

}
