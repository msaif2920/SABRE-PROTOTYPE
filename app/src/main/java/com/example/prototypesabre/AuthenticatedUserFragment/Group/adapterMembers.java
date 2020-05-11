package com.example.prototypesabre.AuthenticatedUserFragment.Group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prototypesabre.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class adapterMembers extends RecyclerView.Adapter<adapterMembers.ViewHolderMembers> {

    ArrayList<String> Name = new ArrayList<>();
    ArrayList<String> contact = new ArrayList<>();
    ArrayList<String> personImage = new ArrayList<>();
    ArrayList<String> point = new ArrayList<>();

    Context ct;

    public adapterMembers(ArrayList<String> name, ArrayList<String> contact, ArrayList<String> personImage, ArrayList<String> point, Context ct) {
        Name = name;
        this.contact = contact;
        this.personImage = personImage;
        this.point = point;
        this.ct = ct;
    }

    @NonNull
    @Override
    public ViewHolderMembers onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ct);
        View view = inflater.inflate(R.layout.row_group_member_list, parent, false);
        return new ViewHolderMembers(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderMembers holder, int position) {
        holder.nameMembers.setText(Name.get(position));
        holder.contactMembers.setText(contact.get(position));
        holder.groupMemberPoint.setText("Point:" + point.get(position));
        Picasso.with(ct).load(personImage.get(position)).into(holder.imageMembers);
    }

    @Override
    public int getItemCount() {
        return Name.size();
    }

    public class ViewHolderMembers extends RecyclerView.ViewHolder {
        ImageView imageMembers;
        TextView nameMembers, contactMembers, groupMemberPoint;

        public ViewHolderMembers(@NonNull View itemView) {
            super(itemView);

            imageMembers = itemView.findViewById(R.id.groupMemberListImageView);
            nameMembers = itemView.findViewById(R.id.groupMembersName);
            contactMembers = itemView.findViewById(R.id.groupMembersContact);
            groupMemberPoint = itemView.findViewById(R.id.groupMembersPoint);
        }
    }
}
