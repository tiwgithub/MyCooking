package com.example.mycooking.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
private List<Users> userAccountSettings ;
private OnItemClickListener onItemClickListener;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mfollowing = mRootRef.child("following");
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();
    boolean follow = false;
    Dialog dialog;
    private int pos=0;
    private Context context;
    private FirebaseMethods firebaseMethods ;

    public UsersAdapter(Context context,List<Users> userAccountSettings) {
        this.context=context;
        this.userAccountSettings = userAccountSettings;

    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(UsersAdapter.OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_item ,parent, false);
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_contact);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        firebaseMethods = new FirebaseMethods(mUser.getUid().toString());
        return new ViewHolder(view,onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Users accountSettings = userAccountSettings.get(position);
        pos = position;

       DatabaseReference query = mfollowing.child(mUser.getUid());
       query.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if (!dataSnapshot.exists()) {

                holder.textViewPriority.setText("");
               } else {
                   for (DataSnapshot ds : dataSnapshot.getChildren()) {

                       if (ds.child("user_id").getValue().equals(accountSettings.getUser_id())) {

                           holder.textViewPriority.setText("ติดตามแล้ว");

                       }
                   }
               }

           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });


        holder.display_name.setText(accountSettings.getUsername());
        try {
            Picasso.get().load(accountSettings.getProfile_photo()).placeholder(R.drawable.ic_home).into(holder.image);
        }catch (Exception e){

        }


    }

    @Override
    public int getItemCount() {
        return userAccountSettings.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView display_name;
        TextView textViewDescription;
        TextView textViewPriority;
        ImageView image;
        public ViewHolder(@NonNull View itemView,final UsersAdapter.OnItemClickListener listener) {
            super(itemView);

            display_name = itemView.findViewById(R.id.titlename_users);
            image = itemView.findViewById(R.id.users_photo);
            textViewPriority = itemView.findViewById(R.id.tvId);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView name = (TextView)dialog.findViewById(R.id.name);
                    ImageView photo = (ImageView) dialog.findViewById(R.id.user_photo);
                    Button bt1 = (Button)dialog.findViewById(R.id.bt_fl);
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

                                     if (ds.child("user_id").getValue().equals(userAccountSettings.get(getAdapterPosition()).getUser_id())) {

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
                            Toast.makeText(context,bt1.getText().toString(),Toast.LENGTH_SHORT).show();
                            if (!follow){
                                follow = true;
                                setFollow("ติดตามแล้ว");
                                firebaseMethods.addFollowingAndFollowers(userAccountSettings.get(getAdapterPosition()).getUser_id());

                            }else {
                                follow = false;
                                setFollow("");
                                firebaseMethods.removeFollowingAndFollowers(userAccountSettings.get(getAdapterPosition()).getUser_id());
                            }

                            dialog.dismiss();
                        }
                    });
                    bt2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (follow) {
                                Intent intent = new Intent(context, ChatActivity.class);
                                intent.putExtra("hisid", userAccountSettings.get(getAdapterPosition()).getUser_id());
                                intent.putExtra("name", userAccountSettings.get(getAdapterPosition()).getUsername());
                                intent.putExtra("img", userAccountSettings.get(getAdapterPosition()).getProfile_photo());
                                context.startActivity(intent);
                            }else {
                                Toast.makeText(context,"คุณยังไม่ได้ติดตามผู้ใช้งาน",Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
                    });
                    name.setText(userAccountSettings.get(getAdapterPosition()).getUsername());

                    try {
                        Picasso.get().load(userAccountSettings.get(getAdapterPosition()).getProfile_photo()).placeholder(R.drawable.ic_home).into(photo);
                    } catch (Exception e) {

                    }
                    dialog.show();
                }
            });
        }

        public void setFollow(String ss){
            textViewPriority.setText(ss);
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}
