package com.example.mycooking.Profile;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mycooking.Favorite.FavoriteActivity;
import com.example.mycooking.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileUIFragment extends Fragment {
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mUsersEditRef = mRootRef.child("favorite_user");
    private DatabaseReference myRef;
    FirebaseDatabase  database;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    CardView cv;
    ImageView profilePhoto ;
    TextView displayName ;
    public ProfileUIFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_profile_ui, container, false);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef=database.getReference();
        final FirebaseUser user = mAuth.getCurrentUser();
        cv = (CardView) view.findViewById(R.id.layout_favorite);
        profilePhoto = (ImageView)view.findViewById(R.id.profile_photo);
        displayName = (TextView)view.findViewById(R.id.display_name);

        ShowUser(user);
        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FavoriteActivity.class);
                startActivity(intent);
            }
        });
       return view;
    }

    private void ShowUser(FirebaseUser user) {
        displayName.setText(user.getDisplayName());

        // Loading profile image
        Uri profilePicUrl = user.getPhotoUrl();

        Glide.with(this).load(profilePicUrl)
                .into(profilePhoto);
    }

}
