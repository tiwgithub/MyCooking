package com.example.mycooking.Post;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mycooking.Adapter.PostRecyclerAdapter;
import com.example.mycooking.Adapter.UsersAdapter;
import com.example.mycooking.Chat.ChatActivity;
import com.example.mycooking.CreateRecipe.CreateRecipeActivity;
import com.example.mycooking.Model.BlogPost;
import com.example.mycooking.Model.FirebaseMethods;
import com.example.mycooking.Model.Users;
import com.example.mycooking.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends Fragment {
    private RecyclerView recyclerView;
    private PostRecyclerAdapter usersAdapter;
    private List<BlogPost> userAccountSettings;
    private FirebaseMethods firebaseMethods ;
    private FirebaseDatabase database;

    private DatabaseReference myRef;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();

    public PostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        userAccountSettings = new ArrayList<>();
        firebaseMethods = new FirebaseMethods(mUser.getUid().toString());
        recyclerView = view.findViewById(R.id.post_list_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        addContent();


        view.findViewById(R.id.fab1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),NewPostActivity.class);
                intent.putExtra("status","newpost");
                startActivity(intent);
            }
        });
        view.findViewById(R.id.fab2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(getActivity(), CreateRecipeActivity.class);
               startActivity(intent);
            }
        });
        view.findViewById(R.id.fab3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseMethods.verifyCheck(getContext());
            }
        });
        return view;
    }

    private void addContent() {


        Query query = myRef.child("following")
                .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userAccountSettings.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    getPhoto(ds);



                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void getPhoto(DataSnapshot ds) {

        Query query = myRef.child("post").orderByChild("user_id").equalTo(ds.getKey());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String blogPostId = ds.getKey();

                    BlogPost photo = ds.getValue(BlogPost.class).withId(blogPostId);
                    userAccountSettings.add(photo);

                    usersAdapter = new PostRecyclerAdapter(getContext(),userAccountSettings);
                    recyclerView.setAdapter(usersAdapter);
                    usersAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }

}
