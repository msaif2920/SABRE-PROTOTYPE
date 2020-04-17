package com.example.prototypesabre.ui.users;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prototypesabre.R;

import java.util.ArrayList;

public class adapter extends RecyclerView.Adapter<adapter.MyViewHolder> {
    Context ct;
    ArrayList<String> emails = new ArrayList<String>();
    ArrayList<String> name = new ArrayList<String>();
    ArrayList<String> point = new ArrayList<String>();

    public adapter(Context ct, ArrayList<String> email, ArrayList<String> names, ArrayList<String> points) {

        this.ct = ct;
        emails = email;
        name = names;
        point = points;
    }

    @NonNull
    @Override
    public adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ct);
        View view = inflater.inflate(R.layout.row_user_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapter.MyViewHolder holder, int position) {
        holder.emailTextView.setText(emails.get(position));
        holder.pointTextView.setText("Point: " + point.get(position));
        holder.nameTextView.setText(name.get(position));

        holder.userListLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ct, UserInfoActivity.class);
                intent.putExtra("Email", emails.get(position));
                ct.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return emails.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView, pointTextView, emailTextView;
        ImageView profileImageView;
        ConstraintLayout userListLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            pointTextView = itemView.findViewById(R.id.pointTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            userListLayout = itemView.findViewById(R.id.userlistLayout);
        }
    }
}
