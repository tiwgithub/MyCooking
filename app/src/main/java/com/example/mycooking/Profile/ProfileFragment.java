package com.example.mycooking.Profile;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mycooking.Favorite.FavoriteActivity;
import com.example.mycooking.Login.LoginActivity;
import com.example.mycooking.Model.Foodmenu;
import com.example.mycooking.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mUsersEditRef = mRootRef.child("favorite_user");
    private DatabaseReference myRef;
    FirebaseDatabase  database;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    private LinearLayout linearLayout,layout_logout;
    private GoogleSignInClient mGoogleSignInClient;
    TextView user_email,LikeCount;
    TextView displayName ;
    ImageView profilePhoto ;
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef=database.getReference();
        final FirebaseUser user = mAuth.getCurrentUser();
        LikeCount = (TextView)view.findViewById(R.id.LikeCount) ;
        linearLayout = (LinearLayout)view.findViewById(R.id.layout_favorite);
        displayName = (TextView)view.findViewById(R.id.display_name);
        user_email = (TextView)view.findViewById(R.id.user_email);
        profilePhoto = (ImageView)view.findViewById(R.id.profile_photo);
        layout_logout = (LinearLayout)view.findViewById(R.id.layout_logout);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        ShowUser(user);

        Query query = mUsersEditRef.child(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ArrayList<String> food = new ArrayList<>();
                final ArrayList<Foodmenu> food5 = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    food.add(snapshot.getKey());

                }
                LikeCount.setText(String.valueOf(food.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //username.setText(user.getDisplayName().toString());
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FavoriteActivity.class);
                startActivity(intent);
            }
        });

        layout_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                    }
                });
            }
        });

        return view;
    }

    private void ShowUser(FirebaseUser user){

        user_email.setText(user.getEmail());
        displayName.setText(user.getDisplayName());

        // Loading profile image
            Uri profilePicUrl = user.getPhotoUrl();

                Glide.with(this).load(profilePicUrl)
                        .into(profilePhoto);


    }
}
