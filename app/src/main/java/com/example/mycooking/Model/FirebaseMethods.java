package com.example.mycooking.Model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mycooking.Login.Verify_loginActivity;
import com.example.mycooking.MainActivity;
import com.example.mycooking.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


public class FirebaseMethods {
    private List<String> ingredient5;
    private static final String TAG = "FirebaseMethods";
    private Activity mActivity;
    private String userID;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private long mediaCount = 0;
    public boolean verify = false;
    private Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public FirebaseMethods() {

    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public FirebaseMethods(String userID) {
        this.userID = userID;
    }

    public FirebaseMethods(Activity activity) {

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        myRef = database.getReference();
        mStorageRef = mStorage.getReference();
        mActivity = activity;
        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }





    public void uploadNewPhoto(final String caption, String imageUrl, final ProgressBar progressBar){

        final String FIREBASE_IMAGE_STORAGE = "post/users/";
        FileCompressor compressor = new FileCompressor(mActivity);
        final StorageReference storageReference;

        //If it is not a profile photo
        progressBar.setVisibility(View.VISIBLE);

            storageReference =  mStorageRef.child(FIREBASE_IMAGE_STORAGE+userID+"/"+ UUID.randomUUID().toString());
            final UploadTask uploadTask = storageReference.putFile(Uri.fromFile(compressor.compressImage(imageUrl)));

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    // Continue with the task to get the download URL
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()) {


                        Uri downloadUri = task.getResult();
                        addPhotoToDatabase(caption,downloadUri.toString());
                        progressBar.setVisibility(View.GONE);

                        mActivity.finish();
                        mActivity.startActivity(new Intent(mActivity, MainActivity.class));
                        Toast.makeText(mActivity, "โพสสำเร็จ", Toast.LENGTH_LONG).show();
                    } else {
                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(mActivity, "โพสไม่สำเร็จ", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            //Tracking progress
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    long uploadPercentage = (taskSnapshot.getBytesTransferred()*100)/taskSnapshot.getTotalByteCount();

                }
            });
        //If it is a profile photo
    }




    public void setPostCount(final String uid, final TextView postCount){

        Query query = myRef.child(mActivity.getString(R.string.user_photos_node)).child(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mediaCount = dataSnapshot.getChildrenCount();
                Query query = myRef.child(mActivity.getString(R.string.user_videos_node)).child(uid);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        mediaCount+=dataSnapshot.getChildrenCount();
                        postCount.setText(String.valueOf(mediaCount));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public long getImageCount(DataSnapshot dataSnapshot) {
        Log.d(TAG,"image_count: "+dataSnapshot.getChildrenCount());
        return dataSnapshot.getChildrenCount();
    }





    private void addPhotoToDatabase(String caption, String imageUrl){

        String photoId = myRef.push().getKey();
        String dateAdded = new SimpleDateFormat("dd-MM-yyyy HH:mm:SS", Locale.ENGLISH).format(Calendar.getInstance().getTime());

        BlogPost blogPost = new BlogPost(userID,imageUrl,caption,dateAdded);


        myRef.child(mActivity.getString(R.string.user_photos_node)).child(userID).child(photoId).setValue(blogPost);
        myRef.child(mActivity.getString(R.string.photos_node)).child(photoId).setValue(blogPost);
    }

    public void addFollowingAndFollowers(String uid){

        FirebaseDatabase.getInstance().getReference()
                .child("following")
                .child(userID)
                .child(uid)
                .child("user_id")
                .setValue(uid);

        FirebaseDatabase.getInstance().getReference()
                .child("followers")
                .child(uid)
                .child(userID)
                .child("user_id")
                .setValue(userID);
    }


    public void removeFollowingAndFollowers(String uid){

        FirebaseDatabase.getInstance().getReference()
                .child("following")
                .child(userID)
                .child(uid)
                .removeValue();

        FirebaseDatabase.getInstance().getReference()
                .child("followers")
                .child(uid)
                .child(userID)
                .removeValue();
    }


    public void verifyCheck(final Context context){

         DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
         DatabaseReference mUser = mRootRef.child("verify");

         mUser.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if (dataSnapshot.exists()){
                     verify = true;
                     Toast.makeText(context,"ยืนยันตัวตนแล้ว",Toast.LENGTH_SHORT).show();

                 }else {
                     verify = false;
                     Toast.makeText(context,"กรุณายืนยันตัวตน",Toast.LENGTH_SHORT).show();
                     Intent intent = new Intent(context,Verify_loginActivity.class);
                     context.startActivity(intent);
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });
    }

    private void getVerify(DataSnapshot ds) {

    }

    public void getIngredients(){
        List<Ingredient> ingredients = new ArrayList<>();
        ingredient5 = new ArrayList<>();
        db.collection("Ingredients").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ingredients.clear();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Ingredient ingredient = document.toObject(Ingredient.class);
                        ingredients.add(ingredient);
                    }
                for (int i=0;i<ingredients.size();i++){
                    ingredient5.add(ingredients.get(i).getName());
                }

                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }


}
