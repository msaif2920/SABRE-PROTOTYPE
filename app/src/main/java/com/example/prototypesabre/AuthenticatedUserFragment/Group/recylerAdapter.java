package com.example.prototypesabre.AuthenticatedUserFragment.Group;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prototypesabre.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class recylerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Map<String, Object> likeDislikeStatus = new HashMap<>();
    ListenerRegistration listen;

    ArrayList<Long> comments = new ArrayList<>();
    ArrayList<Long> dislike = new ArrayList<>();
    ArrayList<Long> like = new ArrayList<>();

    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> post = new ArrayList<>();
    ArrayList<String> userImageLink = new ArrayList<>();
    ArrayList<String> imageLink = new ArrayList<>();
    ArrayList<String> documentId = new ArrayList<>();
    String groupDescription, groupName;
    Context ct;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference refUpdate, refSet;


    public recylerAdapter(ArrayList<Long> comments, ArrayList<Long> dislike, ArrayList<Long> like,
                          ArrayList<String> name, ArrayList<String> post, ArrayList<String> userImageLink,
                          ArrayList<String> imageLink, ArrayList<String> documentId, String groupDescription, String groupName, Context ct) {
        this.comments = comments;
        this.dislike = dislike;
        this.like = like;
        this.name = name;
        this.post = post;
        this.userImageLink = userImageLink;
        this.imageLink = imageLink;
        this.documentId = documentId;
        this.groupDescription = groupDescription;
        this.groupName = groupName;
        this.ct = ct;


    }


    /*
     *The start page
     * Starter view
     */
    class ViewHolderOne extends RecyclerView.ViewHolder {
        TextView groupNameTextView, groupDescriptionTextView, postSomethingTextView;
        Button membersActivityButton, inviteActivityButton;

        public ViewHolderOne(@NonNull View itemView) {
            super(itemView);

            groupNameTextView = itemView.findViewById(R.id.groupNameFromGroupTextView);
            groupDescriptionTextView = itemView.findViewById(R.id.groupFromGroupDesciptionTextView);
            membersActivityButton = itemView.findViewById(R.id.checkMembersGroupButton);
            inviteActivityButton = itemView.findViewById(R.id.inviteFromGroupButton);
            postSomethingTextView = itemView.findViewById(R.id.groupPostSomethingTextView);
        }
    }


    /*
     *With ImageView
     * Post and Image
     */

    class ViewHolderTwo extends RecyclerView.ViewHolder {

        ImageView posterImage, imageGroupPost;
        TextView profileName, groupPostTextView, likeUnlikeStatus;
        ImageButton likeButton, dislikeButton, commentButton;

        public ViewHolderTwo(@NonNull View itemView) {
            super(itemView);

            posterImage = itemView.findViewById(R.id.posterImage);
            imageGroupPost = itemView.findViewById(R.id.imageGroupPost);

            profileName = itemView.findViewById(R.id.profileName);
            groupPostTextView = itemView.findViewById(R.id.groupPostTextView);
            likeUnlikeStatus = itemView.findViewById(R.id.likeUnlikestatus);

            likeButton = itemView.findViewById(R.id.likeButton);
            dislikeButton = itemView.findViewById(R.id.dislikeButton);
            commentButton = itemView.findViewById(R.id.commentButton);
        }
    }

    /*
     *Without ImageView
     * Only Post
     */
    class ViewHolderThree extends RecyclerView.ViewHolder {

        ImageView posterImagewithoutImageview;
        TextView profileNameWithoutImageView, groupPostTextViewWithoutImageView, likeUnlikeStatusWithoutImageView;
        ImageButton likeButtonWithoutImageView, dislikeButtonWithoutImageView, commentButtonWithoutImageView;

        public ViewHolderThree(@NonNull View itemView) {
            super(itemView);

            posterImagewithoutImageview = itemView.findViewById(R.id.posterImageWithoutImageView);

            profileNameWithoutImageView = itemView.findViewById(R.id.profileNameWithoutImageView);
            groupPostTextViewWithoutImageView = itemView.findViewById(R.id.groupPostTextViewWithoutImageView);
            likeUnlikeStatusWithoutImageView = itemView.findViewById(R.id.likeUnlikestatusWithoutImageView);

            likeButtonWithoutImageView = itemView.findViewById(R.id.likeButtonWithoutImageView);
            dislikeButtonWithoutImageView = itemView.findViewById(R.id.dislikeButtonWithoutImageViewWithoutImageView);
            commentButtonWithoutImageView = itemView.findViewById(R.id.commentButtonWithoutImageView);
        }
    }


    /*
     *help us determine which viewholder class to run
     */
    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return 0;
        } else if (imageLink.get(position - 1).contains("None")) {
            return 2;
        } else {
            return 1;
        }
    }


    /*
     *inflating the layout for each viewholder
     * We determine which layout to infalter using viewtype
     * we get viewtype from getViewType
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(ct);
        View view;

        if (viewType == 0) {
            view = layoutInflater.inflate(R.layout.row_group_start, parent, false);

            ViewHolderOne held = new ViewHolderOne(view);
            return held;


        } else if (viewType == 1) {
            view = layoutInflater.inflate(R.layout.row_picture_group_post, parent, false);
            ViewHolderTwo held = new ViewHolderTwo(view);
            return held;


        } else if (viewType == 2) {
            view = layoutInflater.inflate(R.layout.row_group_post_without_image, parent, false);
            ViewHolderThree held = new ViewHolderThree(view);
            return held;
        }
        return null;
    }


    /*
     *This where all the action takes place
     * We set the text view , images here
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (position == 0) {
            ViewHolderOne viewHolderOne = (ViewHolderOne) holder;

            viewHolderOne.groupNameTextView.setText(groupName);
            viewHolderOne.groupDescriptionTextView.setText(groupDescription);


            viewHolderOne.postSomethingTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ct, GroupPost.class);
                    intent.putExtra("Group Name", groupName);

                    ct.startActivity(intent);
                }
            });

            viewHolderOne.membersActivityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ct, GroupMembers.class);
                    intent.putExtra("Group Name", groupName);
                    ct.startActivity(intent);

                }
            });

            viewHolderOne.inviteActivityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ct, MembersInviteOthers.class);
                    intent.putExtra("Group Name", groupName);
                    intent.putExtra("Group Description", groupDescription);
                    intent.putExtra("Current User", getCurrentUser());
                    ct.startActivity(intent);
                }
            });


        } else if (imageLink.get(position - 1).contains("None"))/* If the post contains no image*/ {

            ViewHolderThree viewHolderThree = (ViewHolderThree) holder;
            refSet = db.collection("Groups").document(groupName).collection("Group Content")
                    .document(documentId.get(viewHolderThree.getAdapterPosition() - 1)).collection("Like Dislike").document(getCurrentUser());

            //loading image for person who is posting
            Picasso.with(ct).load(userImageLink.get(position - 1)).into(((ViewHolderThree) holder).posterImagewithoutImageview);
            viewHolderThree.profileNameWithoutImageView.setText(name.get(position - 1));

            //here Updating button according to like Dislike status
            refSet.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String status = document.get("Status").toString();

                                    if (status.equals("None")) {
                                        viewHolderThree.likeButtonWithoutImageView.setImageResource(R.drawable.likeicon);
                                        viewHolderThree.dislikeButtonWithoutImageView.setImageResource(R.drawable.dislike);
                                    } else if (status.equals("Like")) {
                                        viewHolderThree.likeButtonWithoutImageView.setImageResource(R.drawable.likeiconunlike);
                                        viewHolderThree.dislikeButtonWithoutImageView.setImageResource(R.drawable.dislike);
                                    } else if (status.equals("Dislike")) {
                                        viewHolderThree.likeButtonWithoutImageView.setImageResource(R.drawable.likeicon);
                                        viewHolderThree.dislikeButtonWithoutImageView.setImageResource(R.drawable.dislikeicon);
                                    }
                                } else {
                                    viewHolderThree.likeButtonWithoutImageView.setImageResource(R.drawable.likeicon);
                                    viewHolderThree.dislikeButtonWithoutImageView.setImageResource(R.drawable.dislike);
                                }
                            } else {
                                Toast.makeText(ct, "No internet connection", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


            //setting up the group post and current like unlike status
            viewHolderThree.groupPostTextViewWithoutImageView.setText(post.get(position - 1));
            viewHolderThree.likeUnlikeStatusWithoutImageView.setText("Likes " + like.get(position - 1).toString() +
                    "\t\t\t\t\t\tDislikes " + dislike.get(position - 1).toString() +
                    "\t\t\t\t\t\tComments " + comments.get(position - 1));


            viewHolderThree.commentButtonWithoutImageView.setImageResource(R.drawable.commenticon);

            //updates using a listner whnever there is a change
            viewHolderThree.likeButtonWithoutImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //SnapShot listner for update
                    listen = db.collection("Groups").document(groupName).collection("Group Content")
                            .document(documentId.get(position - 1))
                            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                    if (e != null) {

                                        Toast.makeText(ct, "Something went Wrong", Toast.LENGTH_SHORT).show();
                                        Log.i("Error", e.getMessage());
                                        return;
                                    }
                                    if (documentSnapshot != null) {
                                        like.add(viewHolderThree.getAdapterPosition() - 1, documentSnapshot.getLong("Like"));
                                        dislike.add(viewHolderThree.getAdapterPosition() - 1, documentSnapshot.getLong("Dislike"));
                                        comments.add(viewHolderThree.getAdapterPosition() - 1, documentSnapshot.getLong("Comment"));


                                        viewHolderThree.likeUnlikeStatusWithoutImageView.setText("Likes " + like.get(position - 1).toString() +
                                                "\t\t\t\t\t\tDislikes " + dislike.get(position - 1).toString() +
                                                "\t\t\t\t\t\tComments " + comments.get(position - 1));
                                    }
                                }
                            });


                    refSet = db.collection("Groups").document(groupName).collection("Group Content")
                            .document(documentId.get(viewHolderThree.getAdapterPosition() - 1)).collection("Like Dislike").document(getCurrentUser());

                    refUpdate = db.collection("Groups").document(groupName).collection("Group Content")
                            .document(documentId.get(viewHolderThree.getAdapterPosition() - 1));


                    if ((viewHolderThree.dislikeButtonWithoutImageView.getDrawable().getConstantState() == ct.getResources().
                            getDrawable(R.drawable.dislikeicon).getConstantState())) {
                        viewHolderThree.dislikeButtonWithoutImageView.setImageResource(R.drawable.dislike);
                        viewHolderThree.likeButtonWithoutImageView.setImageResource(R.drawable.likeiconunlike);

                        likeDislikeStatus.put("Status", "Like");
                        refSet.set(likeDislikeStatus);
                        refUpdate.update("Like", FieldValue.increment(1));
                        refUpdate.update("Dislike", FieldValue.increment(-1));
                    } else if ((viewHolderThree.likeButtonWithoutImageView.getDrawable().getConstantState() == ct.getResources().
                            getDrawable(R.drawable.likeicon).getConstantState())) {
                        viewHolderThree.likeButtonWithoutImageView.setImageResource(R.drawable.likeiconunlike);
                        likeDislikeStatus.put("Status", "Like");

                        refSet.set(likeDislikeStatus);
                        refUpdate.update("Like", FieldValue.increment(1));


                    } else if ((viewHolderThree.likeButtonWithoutImageView.getDrawable().getConstantState() == ct.getResources().
                            getDrawable(R.drawable.likeiconunlike).getConstantState())) {
                        viewHolderThree.likeButtonWithoutImageView.setImageResource(R.drawable.likeicon);
                        refUpdate.update("Like", FieldValue.increment(-1));
                        likeDislikeStatus.put("Status", "None");
                        refSet.set(likeDislikeStatus);

                    }
                }
            });


            viewHolderThree.dislikeButtonWithoutImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refUpdate = db.collection("Groups").document(groupName).collection("Group Content")
                            .document(documentId.get(viewHolderThree.getAdapterPosition() - 1));

                    listen = db.collection("Groups").document(groupName).collection("Group Content")
                            .document(documentId.get(position - 1))
                            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                    if (e != null) {

                                        Toast.makeText(ct, "Something went Wrong", Toast.LENGTH_SHORT).show();
                                        Log.i("Error", e.getMessage());
                                        return;
                                    }
                                    if (documentSnapshot != null) {
                                        like.add(viewHolderThree.getAdapterPosition() - 1, documentSnapshot.getLong("Like"));
                                        dislike.add(viewHolderThree.getAdapterPosition() - 1, documentSnapshot.getLong("Dislike"));
                                        comments.add(viewHolderThree.getAdapterPosition() - 1, documentSnapshot.getLong("Comment"));


                                        viewHolderThree.likeUnlikeStatusWithoutImageView.setText("Likes " + like.get(position - 1).toString() +
                                                "\t\t\t\t\t\tDislikes " + dislike.get(position - 1).toString() +
                                                "\t\t\t\t\t\tComments " + comments.get(position - 1));
                                    }
                                }
                            });


                    if ((viewHolderThree.likeButtonWithoutImageView.getDrawable().getConstantState() == ct.getResources().
                            getDrawable(R.drawable.likeiconunlike).getConstantState())) {
                        viewHolderThree.dislikeButtonWithoutImageView.setImageResource(R.drawable.dislikeicon);
                        viewHolderThree.likeButtonWithoutImageView.setImageResource(R.drawable.likeicon);


                        likeDislikeStatus.put("Status", "Dislike");
                        //this helps us identify is user reacted to the post before
                        refSet.set(likeDislikeStatus);

                        //updating like and dislike number in the dataBase
                        refUpdate.update("Like", FieldValue.increment(-1));
                        refUpdate.update("Dislike", FieldValue.increment(1));
                    } else if ((viewHolderThree.dislikeButtonWithoutImageView.getDrawable().getConstantState() == ct.getResources().
                            getDrawable(R.drawable.dislike).getConstantState())) {
                        viewHolderThree.dislikeButtonWithoutImageView.setImageResource(R.drawable.dislikeicon);

                        likeDislikeStatus.put("Status", "Dislike");
                        refSet.set(likeDislikeStatus);

                        refUpdate.update("Dislike", FieldValue.increment(1));


                    } else if ((viewHolderThree.dislikeButtonWithoutImageView.getDrawable().getConstantState() == ct.getResources().
                            getDrawable(R.drawable.dislikeicon).getConstantState())) {
                        viewHolderThree.dislikeButtonWithoutImageView.setImageResource(R.drawable.dislike);


                        likeDislikeStatus.put("Status", "None");
                        refSet.set(likeDislikeStatus);
                        refUpdate.update("Dislike", FieldValue.increment(-1));


                    }
                }
            });


            viewHolderThree.commentButtonWithoutImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ct, comment_acitivity.class);
                    intent.putExtra("Group Name", groupName);
                    intent.putExtra("DocumentId", documentId.get(position - 1));
                    intent.putExtra("Current User", getCurrentUser());

                    ct.startActivity(intent);

                }
            });

        }



        /*
         *Set up recycler view with an image view
         */

        else {
            ViewHolderTwo viewHolderTwo = (ViewHolderTwo) holder;


            //Reference to user liked or Disliked status database
            refSet = db.collection("Groups").document(groupName).collection("Group Content")
                    .document(documentId.get(viewHolderTwo.getAdapterPosition() - 1)).collection("Like Dislike").document(getCurrentUser());


            //here Updating button according to like Dislike status
            refSet.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String status = document.get("Status").toString();

                                    if (status.equals("None")) {
                                        viewHolderTwo.likeButton.setImageResource(R.drawable.likeicon);
                                        viewHolderTwo.dislikeButton.setImageResource(R.drawable.dislike);
                                    } else if (status.equals("Like")) {
                                        viewHolderTwo.likeButton.setImageResource(R.drawable.likeiconunlike);
                                        viewHolderTwo.dislikeButton.setImageResource(R.drawable.dislike);
                                    } else if (status.equals("Dislike")) {
                                        viewHolderTwo.likeButton.setImageResource(R.drawable.likeicon);
                                        viewHolderTwo.dislikeButton.setImageResource(R.drawable.dislikeicon);
                                    }
                                } else {
                                    viewHolderTwo.likeButton.setImageResource(R.drawable.likeicon);
                                    viewHolderTwo.dislikeButton.setImageResource(R.drawable.dislike);
                                }
                            } else {
                                Toast.makeText(ct, "No internet connection", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


            //Image for user profile and the post
            Picasso.with(ct).load(userImageLink.get(position - 1)).into(((ViewHolderTwo) holder).posterImage);
            Picasso.with(ct).load(imageLink.get(position - 1)).into(((ViewHolderTwo) holder).imageGroupPost);


            /*
            Setting up Posters name and their post
             */
            viewHolderTwo.profileName.setText(name.get(position - 1));
            viewHolderTwo.groupPostTextView.setText(post.get(position - 1));



            /*
            Showing the current like and Dislikes
             */
            viewHolderTwo.likeUnlikeStatus.setText("Likes " + like.get(position - 1).toString() +
                    "\t\t\t\t\t\tDislikes " + dislike.get(position - 1).toString() +
                    "\t\t\t\t\t\tComments " + comments.get(position - 1));


            //Initialize comment button
            viewHolderTwo.commentButton.setImageResource(R.drawable.commenticon);



            /*
             *Like a post
             * Complicated with snapshot listener updates realtime
             */
            viewHolderTwo.likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //SnapShot listner for update
                    listen = db.collection("Groups").document(groupName).collection("Group Content")
                            .document(documentId.get(position - 1))
                            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                    if (e != null) {

                                        Toast.makeText(ct, "Something went Wrong", Toast.LENGTH_SHORT).show();
                                        Log.i("Error", e.getMessage());
                                        return;
                                    }
                                    if (documentSnapshot != null) {
                                        like.add(viewHolderTwo.getAdapterPosition() - 1, documentSnapshot.getLong("Like"));
                                        dislike.add(viewHolderTwo.getAdapterPosition() - 1, documentSnapshot.getLong("Dislike"));
                                        comments.add(viewHolderTwo.getAdapterPosition() - 1, documentSnapshot.getLong("Comment"));


                                        viewHolderTwo.likeUnlikeStatus.setText("Likes " + like.get(position - 1).toString() +
                                                "\t\t\t\t\t\tDislikes " + dislike.get(position - 1).toString() +
                                                "\t\t\t\t\t\tComments " + comments.get(position - 1));
                                    }
                                }
                            });


                    refSet = db.collection("Groups").document(groupName).collection("Group Content")
                            .document(documentId.get(viewHolderTwo.getAdapterPosition() - 1)).collection("Like Dislike").document(getCurrentUser());

                    refUpdate = db.collection("Groups").document(groupName).collection("Group Content")
                            .document(documentId.get(viewHolderTwo.getAdapterPosition() - 1));


                    if ((viewHolderTwo.dislikeButton.getDrawable().getConstantState() == ct.getResources().
                            getDrawable(R.drawable.dislikeicon).getConstantState())) {
                        viewHolderTwo.dislikeButton.setImageResource(R.drawable.dislike);
                        viewHolderTwo.likeButton.setImageResource(R.drawable.likeiconunlike);

                        likeDislikeStatus.put("Status", "Like");
                        refSet.set(likeDislikeStatus);
                        refUpdate.update("Like", FieldValue.increment(1));
                        refUpdate.update("Dislike", FieldValue.increment(-1));
                    } else if ((viewHolderTwo.likeButton.getDrawable().getConstantState() == ct.getResources().
                            getDrawable(R.drawable.likeicon).getConstantState())) {
                        viewHolderTwo.likeButton.setImageResource(R.drawable.likeiconunlike);
                        likeDislikeStatus.put("Status", "Like");

                        refSet.set(likeDislikeStatus);
                        refUpdate.update("Like", FieldValue.increment(1));


                    } else if ((viewHolderTwo.likeButton.getDrawable().getConstantState() == ct.getResources().
                            getDrawable(R.drawable.likeiconunlike).getConstantState())) {
                        viewHolderTwo.likeButton.setImageResource(R.drawable.likeicon);
                        refUpdate.update("Like", FieldValue.increment(-1));
                        likeDislikeStatus.put("Status", "None");
                        refSet.set(likeDislikeStatus);

                    }
                }
            });



            /*
             * Dislike Button controls
             * Three cases
             * First case if we check if its already liked
             * Then if liked then we unlike it decrement like number
             * Increment dislike number
             * Other cases are No like and already liked
             */

            viewHolderTwo.dislikeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refUpdate = db.collection("Groups").document(groupName).collection("Group Content")
                            .document(documentId.get(viewHolderTwo.getAdapterPosition() - 1));

                    listen = db.collection("Groups").document(groupName).collection("Group Content")
                            .document(documentId.get(position - 1))
                            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                    if (e != null) {

                                        Toast.makeText(ct, "Something went Wrong", Toast.LENGTH_SHORT).show();
                                        Log.i("Error", e.getMessage());
                                        return;
                                    }
                                    if (documentSnapshot != null) {
                                        like.add(viewHolderTwo.getAdapterPosition() - 1, documentSnapshot.getLong("Like"));
                                        dislike.add(viewHolderTwo.getAdapterPosition() - 1, documentSnapshot.getLong("Dislike"));
                                        comments.add(viewHolderTwo.getAdapterPosition() - 1, documentSnapshot.getLong("Comment"));


                                        viewHolderTwo.likeUnlikeStatus.setText("Likes " + like.get(position - 1).toString() +
                                                "\t\t\t\t\t\tDislikes " + dislike.get(position - 1).toString() +
                                                "\t\t\t\t\t\tComments " + comments.get(position - 1));
                                    }
                                }
                            });


                    if ((viewHolderTwo.likeButton.getDrawable().getConstantState() == ct.getResources().
                            getDrawable(R.drawable.likeiconunlike).getConstantState())) {
                        viewHolderTwo.dislikeButton.setImageResource(R.drawable.dislikeicon);
                        viewHolderTwo.likeButton.setImageResource(R.drawable.likeicon);


                        likeDislikeStatus.put("Status", "Dislike");
                        //this helps us identify is user reacted to the post before
                        refSet.set(likeDislikeStatus);

                        //updating like and dislike number in the dataBase
                        refUpdate.update("Like", FieldValue.increment(-1));
                        refUpdate.update("Dislike", FieldValue.increment(1));
                    } else if ((viewHolderTwo.dislikeButton.getDrawable().getConstantState() == ct.getResources().
                            getDrawable(R.drawable.dislike).getConstantState())) {
                        viewHolderTwo.dislikeButton.setImageResource(R.drawable.dislikeicon);

                        likeDislikeStatus.put("Status", "Dislike");
                        refSet.set(likeDislikeStatus);

                        refUpdate.update("Dislike", FieldValue.increment(1));


                    } else if ((viewHolderTwo.dislikeButton.getDrawable().getConstantState() == ct.getResources().
                            getDrawable(R.drawable.dislikeicon).getConstantState())) {
                        viewHolderTwo.dislikeButton.setImageResource(R.drawable.dislike);


                        likeDislikeStatus.put("Status", "None");
                        refSet.set(likeDislikeStatus);
                        refUpdate.update("Dislike", FieldValue.increment(-1));


                    }
                }
            });


            viewHolderTwo.commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ct, comment_acitivity.class);
                    intent.putExtra("Group Name", groupName);
                    intent.putExtra("DocumentId", documentId.get(position - 1));
                    intent.putExtra("Current User", getCurrentUser());

                    ct.startActivity(intent);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return (post.size() + 1);

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

