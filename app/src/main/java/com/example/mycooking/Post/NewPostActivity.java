package com.example.mycooking.Post;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mycooking.MainActivity;
import com.example.mycooking.Model.BlogPost;
import com.example.mycooking.Model.FirebaseMethods;
import com.example.mycooking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;
import io.grpc.Server;

public class NewPostActivity extends AppCompatActivity {
    private Uri mImageUri;
    private ImageView newPostImage;
    private EditText newPostDesc;
    private Button newPostBtn;
    private Uri postImageUri = null;
    private FirebaseAuth firebaseAuth;
    private String current_user_id;
    private Bitmap compressedImageFile;
    private ProgressBar newPostProgress;
    private StorageReference storageReference;
    private String status = null;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        final DatabaseReference myRef = database.getReference();
        final FirebaseMethods firebaseMethods = new FirebaseMethods(this);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        current_user_id = firebaseAuth.getCurrentUser().getUid();

        newPostImage = findViewById(R.id.new_post_image);
        newPostDesc = findViewById(R.id.new_post_desc);
        newPostBtn = findViewById(R.id.post_btn);
        newPostProgress = findViewById(R.id.new_post_progress);

        final Intent intentPost = getIntent();
        final BlogPost blogPost = intentPost.getParcelableExtra("blogPost");
        String stat = intentPost.getStringExtra("status");
        status = stat;

        if (status.equals("editpost")){
            try {
                newPostDesc.setText(blogPost.desc);
                Picasso.get().load(blogPost.image_url).placeholder(R.drawable.post_placeholder).into(newPostImage);

            } catch (Exception e) {

            }
        }else {

            newPostImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setMinCropResultSize(512, 512)
                            .setAspectRatio(1, 1)
                            .start(NewPostActivity.this);

                }
            });

            newPostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String desc = newPostDesc.getText().toString();

                    if (!TextUtils.isEmpty(desc) && postImageUri != null) {

                        newPostProgress.setVisibility(View.VISIBLE);

                        final String randomName = UUID.randomUUID().toString();

                        // PHOTO UPLOAD
                        File newImageFile = new File(postImageUri.getPath());
                        try {

                            compressedImageFile = new Compressor(NewPostActivity.this)
                                    .setMaxHeight(720)
                                    .setMaxWidth(720)
                                    .setQuality(50)
                                    .compressToBitmap(newImageFile);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageData = baos.toByteArray();


                        // PHOTO UPLOAD

                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                firebaseMethods.uploadNewPhoto(newPostDesc.getText().toString(), postImageUri.getPath(), newPostProgress);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }
                }

            });
        }
}


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                postImageUri = result.getUri();
                newPostImage.setImageURI(postImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
    }
}
