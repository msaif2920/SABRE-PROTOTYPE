package com.example.prototypesabre.GuestUser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prototypesabre.R;
import com.example.prototypesabre.ui.users.adapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class adapterGroupPostsAll extends RecyclerView.Adapter<adapterGroupPostsAll.MyViewHolder> {
    ArrayList<String> linksProfile = new ArrayList<>();
    ArrayList<String> post = new ArrayList<>();
    ArrayList<String> imageLink = new ArrayList<>();
    ArrayList<String> posterName = new ArrayList<>();
    Context ct;

    public adapterGroupPostsAll(ArrayList<String> linksProfile, ArrayList<String> post, ArrayList<String> imageLink, ArrayList<String> posterName, Context ct) {
        this.linksProfile = linksProfile;
        this.post = post;
        this.imageLink = imageLink;
        this.posterName = posterName;
        this.ct = ct;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ct);
        View view = inflater.inflate(R.layout.row_group_content_all, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            Picasso.with(ct).load(imageLink.get(position)).into(holder.postImage);
            Picasso.with(ct).load(linksProfile.get(position)).into(holder.profile);

        } catch (Exception e) {

        }

        holder.name.setText(posterName.get(position));
        holder.post.setText(post.get(position));
    }


    @Override
    public int getItemCount() {
        return post.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView profile, postImage;
        TextView name, post;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profileImageAll);
            postImage = itemView.findViewById(R.id.imageAllLoad);
            name = itemView.findViewById(R.id.namePosterAll);
            post = itemView.findViewById(R.id.postAll);
        }
    }
}
