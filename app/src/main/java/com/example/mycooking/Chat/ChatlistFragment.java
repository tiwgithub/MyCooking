package com.example.mycooking.Chat;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mycooking.Adapter.ChatlistAdapter;
import com.example.mycooking.Model.ModelChat;
import com.example.mycooking.Model.ModelChatlist;
import com.example.mycooking.Model.Users;
import com.example.mycooking.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatlistFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerView;
    private List<Users> users ;
    private List<ModelChatlist> Chatlists;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private ChatlistAdapter chatlistAdapter;
    public ChatlistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chatlist, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.recyclerView_chatlist);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Chatlists = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Chatlist").child(currentUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Chatlists.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    ModelChatlist modelChatlist = ds.getValue(ModelChatlist.class);

                    Chatlists.add(modelChatlist);
                }
                loadChat();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        return view;
    }

    private void loadChat() {
        users = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            users.clear();
            for (DataSnapshot ds : dataSnapshot.getChildren()){
                Users users1 = ds.getValue(Users.class);
                for (ModelChatlist modelChatlist : Chatlists){
                    if (users1.getUser_id() != null && users1.getUser_id().equals(modelChatlist.getId())){
                        users.add(users1);
                        break;
                    }
                }
                chatlistAdapter = new ChatlistAdapter(getContext(),users);
                recyclerView.setAdapter(chatlistAdapter);


                for (int i= 0;i<users.size();i++){
                    lastMessage(users.get(i).getUser_id());
                }
            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void lastMessage(final String user_id) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              String thelastMessage = "";
              for (DataSnapshot ds : dataSnapshot.getChildren()){
                  ModelChat modelChat = ds.getValue(ModelChat.class);
                  if (modelChat == null){
                      continue;
                  }
                  String sender = modelChat.getSender();
                  String receiver = modelChat.getReceiver();
                  if (sender == null || receiver == null){
                      continue;
                  }
                  if (modelChat.getReceiver().equals(currentUser.getUid()) && modelChat.getSender().equals(user_id)
                  || modelChat.getReceiver().equals(user_id) && modelChat.getSender().equals(currentUser.getUid())){
                        thelastMessage = modelChat.getMessage();
                  }
              }
              chatlistAdapter.setLastmessageMap(user_id,thelastMessage);
              chatlistAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
