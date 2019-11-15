package com.example.mycooking.Profile;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mycooking.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    FirebaseDatabase database;

    TextView user_email;
    TextView displayName ;
    ImageView profilePhoto ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef=database.getReference();
        FirebaseUser user = mAuth.getCurrentUser();


        displayName = (TextView)findViewById(R.id.display_name);
        user_email = (TextView)findViewById(R.id.user_email);
        profilePhoto = (ImageView)findViewById(R.id.profile_photo);


        ShowUser(user);
    }

    private void ShowUser(FirebaseUser user) {

        user_email.setText(user.getEmail());
        displayName.setText(user.getDisplayName());

        // Loading profile image
        Uri profilePicUrl = user.getPhotoUrl();

        Glide.with(this).load(profilePicUrl)
                .into(profilePhoto);
    }
}
