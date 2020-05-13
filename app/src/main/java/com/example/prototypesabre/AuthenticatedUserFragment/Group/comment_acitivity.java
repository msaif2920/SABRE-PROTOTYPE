package com.example.prototypesabre.AuthenticatedUserFragment.Group;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.prototypesabre.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class comment_acitivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference ref;

    ArrayList<String> commentPersonImage = new ArrayList<>();
    ArrayList<String> comments = new ArrayList<>();
    ArrayList<String> commentPersonName = new ArrayList<>();

    String groupName, documentId, currentUser, image, name;

    ConstraintLayout zeroCommentLayout;

    commentAdapter myAdapter;
    RecyclerView showComments;
    Button post;
    EditText firstComment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authenticated_user_group_comment);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Bundle extras = getIntent().getExtras();
        // gerUserInfo();

        groupName = extras.getString("Group Name");
        documentId = extras.getString("DocumentId");
        currentUser = extras.getString("Current User");

        zeroCommentLayout = findViewById(R.id.zeroComments);
        showComments = findViewById(R.id.commentRecylerview);
        post = findViewById(R.id.postCommentButton);
        firstComment = findViewById(R.id.enterFirstComment);

        UserInfo();

        ref = db.collection("Groups").document(groupName).collection("Group Content")
                .document(documentId).collection("Comments");


        getUserInfo(new FirebaseCallback() {

            @Override
            public void onCallback(ArrayList list, ArrayList list2, ArrayList list3) {
                if (comments.size() == 0) {
                    showComments.setVisibility(View.GONE);


                } else {
                    showComments.setVisibility(View.VISIBLE);
                    zeroCommentLayout.setVisibility(View.GONE);

                    myAdapter = new commentAdapter(commentPersonImage, comments, commentPersonName, getApplicationContext());
                    showComments.setAdapter(myAdapter);
                    showComments.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                }

                /**/
            }
        });

                    /*
                }

            }
        });*/


    }


    public void getUserInfo(final FirebaseCallback firebaseCallback) {

        ref.orderBy("Time", (Query.Direction.DESCENDING))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {
                                    comments.add(document.get("Comments").toString());
                                    commentPersonImage.add(document.get("Image").toString());
                                    commentPersonName.add(document.get("Name").toString());
                                } catch (Exception e) {

                                }


                                firebaseCallback.onCallback(comments, commentPersonImage, commentPersonName);
                            }
                        } else {

                            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    public void post(View view) {

        if (firstComment.getText().toString().isEmpty()) {
            Toast.makeText(comment_acitivity.this, "Enter your message", Toast.LENGTH_SHORT).show();
        } else {
            Map<String, Object> addComments = new HashMap<>();
            addComments.put("Comments", firstComment.getText().toString());
            addComments.put("Image", image);
            addComments.put("Name", name);
            addComments.put("Time", FieldValue.serverTimestamp());

            db.collection("Groups").document(groupName).collection("Group Content")
                    .document(documentId).update("Comment", FieldValue.increment(1));

            db.collection("Groups").document(groupName).collection("Group Content")
                    .document(documentId).collection("Comments")
                    .document()
                    .set(addComments);


            finish();
            startActivity(getIntent());


        }

    }


    public void UserInfo() {
        Log.i("INFOOO", currentUser);
        db.collection("Users").document(currentUser)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                try {
                    image = documentSnapshot.get("Links").toString();
                    name = documentSnapshot.get("Name").toString();
                } catch (Exception e) {
                    image = "";
                    name = "Super User";
                }

            }
        });

    }


    interface FirebaseCallback {

        void onCallback(ArrayList list, ArrayList list2, ArrayList list3);
    }


}
