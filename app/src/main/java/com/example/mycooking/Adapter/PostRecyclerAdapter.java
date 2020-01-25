package com.example.mycooking.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycooking.Comment.CommentsActivity;
import com.example.mycooking.Model.BlogPost;
import com.example.mycooking.Model.UserAccountSettings;
import com.example.mycooking.Model.Users;
import com.example.mycooking.Post.NewPostActivity;
import com.example.mycooking.R;
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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PostRecyclerAdapter extends RecyclerView.Adapter<PostRecyclerAdapter.MyHolder>{
    public List<BlogPost> blog_list;
    private List<Users> userAccount;
    public Context context;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mUser = mRootRef.child("users");
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser User = mAuth.getCurrentUser();
    final DatabaseReference commentcount = mRootRef;

    public PostRecyclerAdapter(Context context,List<BlogPost> blog_list) {
        this.context = context;
        this.blog_list = blog_list;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_item, parent, false);


        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {
        final BlogPost blogPost = blog_list.get(position);
        final String blogPostId = blog_list.get(position).BlogPostId;

        userAccount = new ArrayList<>();

        DatabaseReference df = mUser;
        df.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userAccount.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Users users = ds.getValue(Users.class);
                    if (users.getUser_id().equals(blogPost.user_id)) {
                        userAccount.add(0,users);
                    }
                }

                holder.user.setText(userAccount.get(0).getUsername());

                try {
                    Picasso.get().load(userAccount.get(0).getProfile_photo()).placeholder(R.drawable.post_placeholder).into(holder.blogImageuser);

                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    String descView1 = blog_list.get(position).getDesc();

    String date = blogPost.timestamp;
    String blogImageView1 = blog_list.get(position).getImage_url();


    holder.descView.setText(descView1);
    holder.blogDate.setText(date);

        try {
            Picasso.get().load(blogImageView1).placeholder(R.drawable.post_placeholder).into(holder.blogImageView);

        } catch (Exception e) {

        }



        holder.blogCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commentIntent = new Intent(context, CommentsActivity.class);
                commentIntent.putExtra("blog_post_id", blogPostId);
                context.startActivity(commentIntent);
            }
        });

        holder.blogLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String dateAdded = new SimpleDateFormat("dd-MM-yyyy HH:mm:SS", Locale.ENGLISH).format(Calendar.getInstance().getTime());
                commentcount.child("post").child(blogPostId).child("likes").child(User.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()){
                                    Map<String, Object> commentsMap = new HashMap<>();
                                    commentsMap.put("user_id", User.getUid());
                                    commentsMap.put("timestamp", dateAdded);
                                    commentcount.child("post").child(blogPostId).child("likes").child(User.getUid()).setValue(commentsMap);
                                    holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.action_like_accent));

                                    Query likes = commentcount.child("post").child(blogPostId).child("likes");
                                    likes.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getChildrenCount()>0){
                                                holder.bloglikecount.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                                            }else {
                                                holder.bloglikecount.setText(String.valueOf(0));
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }else {
                                    commentcount.child("post").child(blogPostId).child("likes").child(User.getUid()).removeValue();
                                    holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.action_like_gray));

                                    Query likes = commentcount.child("post").child(blogPostId).child("likes");
                                    likes.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getChildrenCount()>0){
                                                holder.bloglikecount.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                                            }else {
                                                holder.bloglikecount.setText(String.valueOf(0));
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        });



        Query query = commentcount.child("post").child(blogPostId).child("comments");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()>0){
                    holder.blogcount.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                }else {
                    holder.blogcount.setText(String.valueOf(0));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query likes = commentcount.child("post").child(blogPostId).child("likes");
        likes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()>0){
                    holder.bloglikecount.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                }else {
                    holder.bloglikecount.setText(String.valueOf(0));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query btnlikes = commentcount.child("post").child(blogPostId).child("likes").child(User.getUid());
        btnlikes.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.action_like_accent));
                }else {
                    holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.action_like_gray));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        private TextView descView;
        private ImageView blogImageView,blogImageuser;
        private TextView blogDate,user, blogcount,bloglikecount;
        private ImageView blogLikeBtn,overflow;
        private ImageView blogCommentBtn;

        private TextView blogUserName;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            overflow = itemView.findViewById(R.id.overflow);
            blogCommentBtn = itemView.findViewById(R.id.blog_comment_icon);
            blogLikeBtn = itemView.findViewById(R.id.blog_like_btn);
            descView = itemView.findViewById(R.id.blog_desc);
            blogImageView= itemView.findViewById(R.id.blog_image);
            blogDate= itemView.findViewById(R.id.blog_date);
            user= itemView.findViewById(R.id.blog_user_name);
            blogImageuser= itemView.findViewById(R.id.blog_user_image);
            blogcount= itemView.findViewById(R.id.blog_comment_count);
            bloglikecount= itemView.findViewById(R.id.blog_like_count);

            overflow.setOnClickListener(this::showPopupMenu);
        }

        private void showPopupMenu(View view) {
            PopupMenu popup = new PopupMenu(view.getContext(), view);
            popup.inflate(R.menu.grid_popup_menu);
            popup.setOnMenuItemClickListener(
                    new MyMenuItemClickListener(blog_list.get(getAdapterPosition())));
            popup.show();
        }
        class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

            public BlogPost blog_list;

            public MyMenuItemClickListener(BlogPost recipe) {
                this.blog_list = recipe;
            }

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_edit_recipe:
                        Intent intent = new Intent(context, NewPostActivity.class);
                        intent.putExtra("blogPost", blog_list);
                        intent.putExtra("status","editpost");
                        context.startActivity(intent);
                        Toast.makeText(context,"แก้ไข",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_delete_recipe:
                        Toast.makeText(context,"ลบ",Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                }
                return false;
            }
        }

    }
}
