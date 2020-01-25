package com.example.mycooking.Users;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mycooking.Adapter.UsersAdapter;
import com.example.mycooking.Chat.ChatActivity;
import com.example.mycooking.Model.FirebaseMethods;
import com.example.mycooking.Model.UserAccountSettings;
import com.example.mycooking.Model.Users;
import com.example.mycooking.R;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment {
    boolean follow = false;
    private RecyclerView recyclerView;
    private UsersAdapter usersAdapter;
    private List<Users> userAccountSettings;
    Dialog dialog;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mfollowing = mRootRef.child("following");
    private DatabaseReference mfollowers = mRootRef.child("followers");
    private FirebaseMethods firebaseMethods ;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();

    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        userAccountSettings = new ArrayList<>();
        firebaseMethods = new FirebaseMethods(mUser.getUid().toString());

        recyclerView = view.findViewById(R.id.recyclerView_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        DatabaseReference df = FirebaseDatabase.getInstance().getReference("users");
        df.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Users users = ds.getValue(Users.class);

                    if (!mUser.getUid().equals(users.getUser_id())) {
                        userAccountSettings.add(users);
                    }
                    usersAdapter = new UsersAdapter(getContext(),userAccountSettings);
                    recyclerView.setAdapter(usersAdapter);

                    dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.dialog_contact);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    usersAdapter.setOnItemClickListener(new UsersAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(final int position) {


                            TextView name = (TextView)dialog.findViewById(R.id.name);
                            ImageView photo = (ImageView) dialog.findViewById(R.id.user_photo);
                            final Button bt1 = (Button)dialog.findViewById(R.id.bt_fl);
                            Button bt2 = (Button)dialog.findViewById(R.id.bt_msg);

                            Query query = mfollowing.child(mUser.getUid());

                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()) {
                                        follow = false;
                                        bt1.setText("ติดตาม");

                                    } else {
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                            if (ds.child("user_id").getValue().equals(userAccountSettings.get(position).getUser_id())) {

                                                follow = true;
                                                bt1.setText("เลิกติดตาม");
                                            }
                                        }
                                    }

                                    if (!follow) {
                                        follow = false;
                                        bt1.setText("ติดตาม");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });



                            bt1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(getContext(),bt1.getText().toString(),Toast.LENGTH_SHORT).show();
                                    if (!follow){
                                        follow = true;

                                        firebaseMethods.addFollowingAndFollowers(userAccountSettings.get(position).getUser_id());

                                    }else {
                                        follow = false;

                                        firebaseMethods.removeFollowingAndFollowers(userAccountSettings.get(position).getUser_id());
                                    }

                                    dialog.dismiss();
                                }
                            });
                            bt2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getContext(),ChatActivity.class);
                                    intent.putExtra("hisid",userAccountSettings.get(position).getUser_id());
                                    intent.putExtra("name",userAccountSettings.get(position).getUsername());
                                    intent.putExtra("img",userAccountSettings.get(position).getProfile_photo());
                                    startActivity(intent);

                                    dialog.dismiss();
                                }
                            });
                            name.setText(userAccountSettings.get(position).getUsername());

                            try {
                                Picasso.get().load(userAccountSettings.get(position).getProfile_photo()).placeholder(R.drawable.ic_home).into(photo);
                            } catch (Exception e) {

                            }
                            dialog.show();

                            //Intent intent = new Intent(getContext(),ChatActivity.class);
                            //intent.putExtra("hisid",userAccountSettings.get(position).getUser_id());
                            //intent.putExtra("name",userAccountSettings.get(position).getUsername());
                            //intent.putExtra("img",userAccountSettings.get(position).getProfile_photo());
                            //startActivity(intent);
                            usersAdapter.notifyDataSetChanged();
                        }

                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        return view;
    }


}
