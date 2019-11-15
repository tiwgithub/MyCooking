package com.example.mycooking.Search;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mycooking.Adapter.FoodmenuAdapter;
import com.example.mycooking.Common.Common;
import com.example.mycooking.Food.FoodDetailActivity;
import com.example.mycooking.Food.FoodMenuActivity;
import com.example.mycooking.Model.Foodmenu;
import com.example.mycooking.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {
    private static final String TAG = "Something";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Foods");
    private RecyclerView recyclerView;
    private FoodmenuAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<String > foodlist = new ArrayList<>();
    private List<Foodmenu> foodmenus;
    RecyclerView filter_search;
    private List<String> ingredients, directions;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_fillter_search);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        BottomNavigationView bottomNav = view.findViewById(R.id.nav_fillter);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_filter:
                        ShowFilterDialog();
                        Toast.makeText(getContext(),"ค้นหา", Toast.LENGTH_SHORT).show();

                        break;
                }
                return true;
            }
        });



        EditText editText = view.findViewById(R.id.edittext5);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());

            }
        });

        return view;
    }

    private void filter(String key) {

        Query query = notebookRef.orderBy("foodname").startAt(key).endAt(key + "\uf8ff");

        final FirestoreRecyclerOptions<Foodmenu> options5 = new FirestoreRecyclerOptions.Builder<Foodmenu>()
                .setQuery(query, Foodmenu.class)
                .build();

        adapter = new FoodmenuAdapter(options5);
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        adapter.setOnItemClickListener(new FoodmenuAdapter.OnItemClickListener() {
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
                Intent intent = new Intent(getContext(), FoodDetailActivity.class);
                intent.putStringArrayListExtra("ingredients", (ArrayList<String>) ingredients);
                intent.putStringArrayListExtra("directions", (ArrayList<String>) directions);
                intent.putExtra("foods", options5.getSnapshots().get(position));
                startActivity(intent);
            }
        });


    }


    private void ShowFilterDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("เลือกวัตถุดิบ");

        final LayoutInflater inflater = this.getLayoutInflater();
        View filter_layout = inflater.inflate(R.layout.dialog_options,null);

        final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView)filter_layout.findViewById(R.id.txt_category);
        final ChipGroup chipGroup = (ChipGroup)filter_layout.findViewById(R.id.chipGroup);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),android.R.layout.select_dialog_item, Common.categories);

        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                autoCompleteTextView.setText("");

                Chip chip = (Chip)inflater.inflate(R.layout.chip_item,null,false);
                chip.setText(((TextView)view).getText());
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(), "ค้นหา", Toast.LENGTH_SHORT).show();
                        chipGroup.removeView(view);
                    }

                });

                //Toast.makeText(getContext(), String.valueOf(chip.getText()), Toast.LENGTH_SHORT).show();
                chipGroup.addView(chip);



            }
        });

        alertDialog.setView(filter_layout);
        alertDialog.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.setPositiveButton("ค้นหา", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                List<String> filter_key = new ArrayList<>();
                StringBuilder filter_query = new StringBuilder("");

                for (int j=0 ; j<chipGroup.getChildCount();j++){
                    Chip chip = (Chip)chipGroup.getChildAt(j);
                    filter_key.add(chip.getText().toString());
                }

                //Toast.makeText(getContext(), filter_key.get(1), Toast.LENGTH_SHORT).show();
                // fetchFilterCategory(filter_query.toString());
            }
        });
        alertDialog.show();



    }


}

