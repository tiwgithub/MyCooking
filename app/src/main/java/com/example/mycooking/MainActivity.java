package com.example.mycooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.multidex.MultiDex;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.mycooking.Chat.ChatlistFragment;
import com.example.mycooking.Home.HomeFragment;
import com.example.mycooking.Like.LikeFragment;
import com.example.mycooking.Post.PostFragment;
import com.example.mycooking.Profile.ProfileFragment;
import com.example.mycooking.Profile.ProfileUIFragment;
import com.example.mycooking.Search.SearchFragment;
import com.example.mycooking.Share.ShareFragment;
import com.example.mycooking.Users.UserChatFragment;
import com.example.mycooking.Users.UsersFragment;
import com.example.mycooking.notifications.Token;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int ACTIVITY_NO = 0;
    private Context mContext = MainActivity.this;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    String mUID = user.getUid();
    private GoogleSignInClient mGoogleSignInClient;
    private long backPressedTime;
    private Toast backToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        UpdateToken(FirebaseInstanceId.getInstance().getToken());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_search:
                            selectedFragment = new SearchFragment();
                            break;

                        case R.id.nav_share:
                            selectedFragment = new PostFragment();
                            break;
                        case R.id.nav_like:
                            selectedFragment = new UserChatFragment();
                            break;
                        case R.id.nav_profile:
                            selectedFragment = new ProfileUIFragment();


                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;
                }


            };

    private void showdialog() {
        final AlertDialog.Builder dDialog = new AlertDialog.Builder(this);
        dDialog.setTitle("Oh!! Android ");
        dDialog.setIcon(android.R.drawable.btn_star_big_on);
        dDialog.setMessage("Sawatdee Android!");
        dDialog.setPositiveButton("Close", null);
        dDialog.show();
    }

    public void UpdateToken(String token){
        DatabaseReference df = FirebaseDatabase.getInstance().getReference("Tokens");
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("users");
        Token mtoken = new Token(token);
        df.child(mUID).setValue(mtoken);
        dr.child(mUID).child("device_token").setValue(token);

    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("ออกจากโปรแกรม")
                .setCancelable(false)
                .setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //finish();
                        mGoogleSignInClient.signOut();
                        mAuth.signOut();
                        MainActivity.this.onSuperBackPressed();
                        //super.onBackPressed();
                    }
                })
                .setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    public void onSuperBackPressed(){
        super.onBackPressed();
    }
}

