package com.example.mycooking.Favorite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mycooking.Adapter.FavoritAdapter;
import com.example.mycooking.Adapter.FoodmenuAdapter;
import com.example.mycooking.Food.FoodDetailActivity;
import com.example.mycooking.Model.Foodmenu;
import com.example.mycooking.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference Userfavorite = mRootRef.child("favorite");
    private DatabaseReference mUsersEditRef = mRootRef.child("favorite_user");
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference FoodRef = db.collection("Foods");
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    private RecyclerView recyclerView;
    private FavoritAdapter adapter;
    private List<String> ingredients, directions;
    private TextView food_menu;;
    private static final String TAG = "Something";
    ArrayList<Foodmenu> foodmenuLike = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        food_menu = (TextView) findViewById(R.id.txt_food_menu);
        recyclerView = findViewById(R.id.recyclerView5);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(FavoriteActivity.this));




        Query query = mUsersEditRef.child(user.getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                final ArrayList<String> food = new ArrayList<>();
                final ArrayList<Foodmenu> food5 = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    food.add(snapshot.getKey());

                }
                //food_menu.setText(String.valueOf(food.size()));
                //search(food);
                for (int i=0;i<food.size();i++) {

                    FoodRef.document(food.get(i)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            if (documentSnapshot.exists()) {
                                final Foodmenu foodmenu = documentSnapshot.toObject(Foodmenu.class);
                                String id = documentSnapshot.getId();
                                foodmenu.setDocumentId(id);



                                foodmenuLike.add(foodmenu);



                                adapter = new FavoritAdapter(foodmenuLike);
                                recyclerView.setAdapter(adapter);

                                adapter.setOnItemClickListener(new FavoritAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position) {
                                        ingredients = new ArrayList<>();
                                        directions = new ArrayList<>();

                                        for (String s : foodmenu.getIngredients()) {
                                            ingredients.add(s);
                                        }
                                        for (String s : foodmenu.getDirections()) {
                                            directions.add(s);
                                        }

                                        Intent intent = new Intent(FavoriteActivity.this, FoodDetailActivity.class);
                                        intent.putExtra("foods",foodmenu);
                                        intent.putStringArrayListExtra("ingredients", (ArrayList<String>) ingredients);
                                        intent.putStringArrayListExtra("directions", (ArrayList<String>) directions);
                                        startActivity(intent);
                                    }
                                });


                            } else {
                                Toast.makeText(FavoriteActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                            }





                        }
                    });

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }

    private void foodAdd(Foodmenu foodmenu) {

        Foodmenu foodmenu1 = foodmenu;

        foodmenuLike.add(foodmenu);



        adapter = new FavoritAdapter(foodmenuLike);
        recyclerView.setAdapter(adapter);





    }

}

