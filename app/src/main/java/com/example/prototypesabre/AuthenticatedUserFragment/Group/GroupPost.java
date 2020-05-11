package com.example.prototypesabre.AuthenticatedUserFragment.Group;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prototypesabre.R;
import com.example.prototypesabre.firebaseFunction;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class GroupPost extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageURi = null;
    firebaseFunction Firebasefunction = new firebaseFunction();
    StorageReference objectStorageRefrences;
    FirebaseFirestore objectFirebaseFirestore, db;
    ImageView postImageView, profileImageView;
    ImageButton chooseImage, publicPostButton, privatePostButton;
    EditText postSomethingEditText;
    TextView personsName;
    Boolean showPost = false;
    Menu m;
    Long imageNumber = 0L;


    /*
     *post_type= public or private
     */
    private String postType = "Public";
    private String imageLink = "None";
    private String groupName;
    private String userName, userImageLink;

    Map<String, Object> contents = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentiated_user_group_post_activity);

        Bundle extras = getIntent().getExtras();
        groupName = extras.getString("Group Name");

        objectFirebaseFirestore = FirebaseFirestore.getInstance();
        objectStorageRefrences = FirebaseStorage.getInstance().getReference("Group " + groupName);
        postImageView = findViewById(R.id.imagePostImageView);
        chooseImage = findViewById(R.id.uploadImageForPost);
        publicPostButton = findViewById(R.id.publicPostButton);
        privatePostButton = findViewById(R.id.privatePostbutton);
        postSomethingEditText = findViewById(R.id.postSomethingEditText);
        profileImageView = findViewById(R.id.groupPosterPersonImage);
        personsName = findViewById(R.id.groupPostPersonsName);

        getUserNameAndImage();
        getImageNumber();


        publicPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (privatePostButton.getDrawable().getConstantState() == getResources().
                        getDrawable(R.drawable.privatepostfinalicon).getConstantState()) {
                    privatePostButton.setImageResource(R.drawable.privatepost);
                    publicPostButton.setImageResource(R.drawable.publicposticonfinal);
                    postType = "Public";
                }
            }
        });

        privatePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (publicPostButton.getDrawable().getConstantState() == getResources().
                        getDrawable(R.drawable.publicposticonfinal).getConstantState()) {
                    publicPostButton.setImageResource(R.drawable.publicposticon);
                    privatePostButton.setImageResource(R.drawable.privatepostfinalicon);
                    postType = "Private";
                }
            }
        });


        /*
         *add a back button on the action bar
         */
        getSupportActionBar().setTitle("Create Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChoser();
            }
        });


        /*
         *this is to keep track if there is any change on editText
         *If so then update using prepearmenuoption
         */

        postSomethingEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    showPost = true;

                    onPrepareOptionsMenu(m);
                } else {

                    showPost = false;
                    onPrepareOptionsMenu(m);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    /*
     *opens intent for choosing file
     * Allows user to get to their gallery
     */
    private void openFileChoser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

    }


    /*
     *We check if image pick request was successful
     * If successful then set it to our imageview
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageURi = data.getData();
            postImageView.setImageURI(imageURi);


        }
    }







    /*
     *Menu Item post and its control
     * After anything is written or image has been added post enables
     * Its disabled in xml
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.post_button, menu);
        m = menu;
        return super.onCreateOptionsMenu(menu);
    }


    /*
     *onClickListener for the menu option post
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.groupPostMenuItem:
                contents.put("Time", FieldValue.serverTimestamp());
                contents.put("Name", userName);
                contents.put("User Image Links", userImageLink);
                contents.put("Post", postSomethingEditText.getText().toString());
                contents.put("Comment", 0);
                contents.put("Like", 0);
                contents.put("Dislike", 0);
                contents.put("Post Type", postType);

                if (postImageView.getDrawable() != null) {
                    uploadWithImage();
                    contents.put("Image Link", imageLink);
                } else {

                    contents.put("Image Link", imageLink);
                    uploadWithoutImage();
                }


                finish();


                return true;


            case android.R.id.home:
                onBackPressed();

                return true;

        }
        return super.onOptionsItemSelected(item);

    }


    /*
     *This is for enabling if there is text
     * Disabling if there is not text
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (showPost) {
            menu.getItem(0).setEnabled(true);
        } else {
            menu.getItem(0).setEnabled(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }



    /*
     *uploading image fies to firebase storage
     *get the link for the storage location
     */

    public void uploadWithImage() {
        String nameofImg = imageNumber + "." + "jpg";// getFileExtension(mImageUri);
        updateImageNumber();
        final StorageReference imageRef = objectStorageRefrences.child(nameofImg);

        UploadTask objectUploadTask = imageRef.putFile(imageURi);
        objectUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "FAILED", Toast.LENGTH_SHORT).show();
                }
                return imageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    contents.put("Image Link", task.getResult().toString());
                    objectFirebaseFirestore.collection("Groups").document(groupName).collection("Group Content")
                            .document()
                            .set(contents).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Successfully posted", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(GroupPost.this, "Failed to create post", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });
    }



    /*
     * Upload to database without image link
     */

    public void uploadWithoutImage() {
        objectFirebaseFirestore.collection("Groups").document(groupName).collection("Group Content")
                .document()
                .set(contents)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Successfully posted", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GroupPost.this, "Failed to create post", Toast.LENGTH_SHORT).show();
            }
        });
    }







    /*
     *We need to display current user name
     * This function gets current user name and image
     */

    public void getUserNameAndImage() {
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Users").document(getCurrentUser());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userName = document.get("Name").toString();
                        userImageLink = document.get("Links").toString();
                        personsName.setText(userName);
                        Picasso.with(getApplicationContext()).load(userImageLink).into(profileImageView);
                    } else {
                        Toast.makeText(getApplicationContext(), "No Such user exists", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(GroupPost.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    //Returns the current user for us
    public String getCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return (user.getEmail().toUpperCase().trim());
        } else {
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    public void getImageNumber() {
        db = FirebaseFirestore.getInstance();

        DocumentReference ref = db.collection("Groups").document(groupName);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        imageNumber = document.getLong("Image Number");


                    }
                } else {
                    Toast.makeText(GroupPost.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void updateImageNumber() {
        DocumentReference ref = db.collection("Groups").document(groupName);
        imageNumber++;
        ref.update("Image Number", imageNumber);
    }


}
