package com.example.mycooking.Food;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.mycooking.Adapter.GridAdaper;
import com.example.mycooking.Model.Foodmenu;
import com.example.mycooking.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class FoodMenuActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Foods");
    private RecyclerView recyclerView;
    private GridAdaper adapter;
    TextView txt_title_menu;
    private List<String> ingredients, directions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_menu);

        txt_title_menu = findViewById(R.id.txt_food_menu);

        Intent intent = getIntent();
        String key = intent.getStringExtra("menu");


        txt_title_menu.setText(key);

        recyclerView = findViewById(R.id.recyclerView5);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));


        Query query = notebookRef.whereEqualTo("menu",key);

        final FirestoreRecyclerOptions<Foodmenu> options5 = new FirestoreRecyclerOptions.Builder<Foodmenu>()
                .setQuery(query, Foodmenu.class)
                .build();

        adapter = new GridAdaper(options5);
        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new GridAdaper.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Foodmenu foodmenu = adapter.getItem(position);
                String id = options5.getSnapshots().getSnapshot(position).getId();
                foodmenu.setDocumentId(id);

                ingredients = new ArrayList<>();
                directions = new ArrayList<>();
                for (String s : foodmenu.getIngredients()) {
                    ingredients.add(s);
                }
                for (String s : foodmenu.getDirections()) {
                    directions.add(s);
                }

                Intent intent = new Intent(FoodMenuActivity.this, FoodDetailActivity.class);
                intent.putStringArrayListExtra("ingredients", (ArrayList<String>) ingredients);
                intent.putStringArrayListExtra("directions", (ArrayList<String>) directions);
                intent.putExtra("foods", options5.getSnapshots().get(position));
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
