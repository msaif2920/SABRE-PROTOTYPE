package com.example.prototypesabre.AuthenticatedUserFragment.Group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prototypesabre.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class commentAdapter extends RecyclerView.Adapter<commentAdapter.ViewHolder> {


    ArrayList<String> commentPersonImage = new ArrayList<>();
    ArrayList<String> comments = new ArrayList<>();
    ArrayList<String> commentPersonName = new ArrayList<>();
    Context ct;

    public commentAdapter(ArrayList<String> commentPersonImage, ArrayList<String> comments, ArrayList<String> commentPersonName, Context ct) {
        this.commentPersonImage = commentPersonImage;
        this.comments = comments;
        this.commentPersonName = commentPersonName;
        this.ct = ct;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ct);
        View view = inflater.inflate(R.layout.row_group_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.commentorName.setText(commentPersonName.get(position));
        holder.comment.setText(comments.get(position));
        try {
            Picasso.with(ct).load(commentPersonImage.get(position)).into(holder.commentorImage);
        } catch (Exception e) {
            holder.commentorImage.setImageResource(R.drawable.profile_placeholder);
        }


    }


    @Override
    public int getItemCount() {
        return comments.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView commentorImage;
        TextView commentorName, comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            commentorImage = itemView.findViewById(R.id.commentatorImage);
            comment = itemView.findViewById(R.id.commentTextView);
            commentorName = itemView.findViewById(R.id.nameTextViewComments);
        }
    }
}
