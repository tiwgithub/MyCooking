package com.example.mycooking.Search;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mycooking.Adapter.FavoritAdapter;
import com.example.mycooking.Adapter.FoodmenuAdapter;
import com.example.mycooking.Common.Common;
import com.example.mycooking.Favorite.FavoriteActivity;
import com.example.mycooking.Food.FoodDetailActivity;
import com.example.mycooking.Food.FoodMenuActivity;
import com.example.mycooking.Model.Foodmenu;
import com.example.mycooking.Model.Ingredients;
import com.example.mycooking.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
    private List<Foodmenu> foodmenus = new ArrayList<>();
    RecyclerView filter_search;
    private List<String> ingredients, directions;
    private List<Ingredients> ingredient5 = new ArrayList<>();
    List<String>  f2;
    ArrayList<List<String>> f4 = new ArrayList<List<String>>();
    List<String> f5 = new ArrayList<String>();
    private FavoritAdapter adapter5;
    ArrayList<Foodmenu> foodmenuSearch = new ArrayList<>();
    private Toolbar mToolbar;
    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        mToolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        mToolbar.setTitle("ค้นหาเมนู");
        setHasOptionsMenu(true);
        recyclerView = view.findViewById(R.id.recyclerView_fillter_search);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        f2 = new ArrayList<>();

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
        final String arr[]={"ไข่ไก่","น้ำมัน","ซอส","เนื้อหมู","น้ำปลา","กระเทียม","พริกไทย","เนื้อไก่","พริกแกง","ใบมะกรูด","ถั่วฝักยาว","ไส้อ่อนหมู","ตะไคร้","ข่า"};
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("เลือกวัตถุดิบ");

        final LayoutInflater inflater = this.getLayoutInflater();
        View filter_layout = inflater.inflate(R.layout.dialog_options,null);

        final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView)filter_layout.findViewById(R.id.txt_category);
        final ChipGroup chipGroup = (ChipGroup)filter_layout.findViewById(R.id.chipGroup);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),android.R.layout.select_dialog_item, arr);

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
                        Toast.makeText(getContext(), "ลบ", Toast.LENGTH_SHORT).show();
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

                final List<String> filter_key = new ArrayList<>();
                StringBuilder filter_query = new StringBuilder("");

                for (int j=0 ; j<chipGroup.getChildCount();j++){
                    Chip chip = (Chip)chipGroup.getChildAt(j);
                    filter_key.add(chip.getText().toString());
                }

                //Toast.makeText(getContext(), filter_key.get(1), Toast.LENGTH_SHORT).show();
                // fetchFilterCategory(filter_query.toString());

                notebookRef.orderBy("like").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        foodmenus.clear();
                        f2.clear();
                        f4.clear();
                        f5.clear();


                        for (QueryDocumentSnapshot q: task.getResult()){
                            Foodmenu foodmenu = q.toObject(Foodmenu.class);
                            f5.add(q.getId());
                            f4.add((List<String>) q.get("ingredients"));
                            foodmenus.add(foodmenu);


                        }

                        int s = 0;
                        for (int i = 0; i < f4.size(); i++) {

                            for (int j = 0; j < f4.get(i).size(); j++) {

                                for (int k=0 ; k<filter_key.size();k++) {

                                    if (f4.get(i).get(j).equals(filter_key.get(k))) {
                                        s += 1;

                                        //btn1.setText(String.valueOf(f4.get(i).get(j)));
                                        //btn2.setText(String.valueOf(foodmenus.get(i).getFoodname()));
                                    }
                                }

                                if (s >= f4.get(i).size()) {
                                    if (!f2.contains(f5.get(i))){
                                        f2.add(f5.get(i));
                                    }

                                }


                            }
                            s=0;

                        }
                        foodmenuSearch.clear();
                        foodAdd(f2);
                        //filter(f2.get(0));
                        //Toast.makeText(getContext(), f2.get(0), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        alertDialog.show();



    }

    private void foodAdd(List<String> f2) {

        for (int i=0;i<f2.size();i++){
            notebookRef.document(f2.get(i)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(final DocumentSnapshot documentSnapshot) {

                    if (documentSnapshot.exists()) {
                        final Foodmenu foodmenu = documentSnapshot.toObject(Foodmenu.class);
                        String id = documentSnapshot.getId();
                        foodmenu.setDocumentId(id);


                        foodmenuSearch.add(foodmenu);

                        adapter5 = new FavoritAdapter(foodmenuSearch);
                        recyclerView.setAdapter(adapter5);


                        adapter5.setOnItemClickListener(new FavoritAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {


                                Toast.makeText(getContext(),documentSnapshot.getId(),Toast.LENGTH_SHORT).show();

                                ingredients = new ArrayList<>();
                                directions = new ArrayList<>();

                                for (String s : foodmenu.getIngredients()) {
                                    ingredients.add(s);
                                }
                                for (String s : foodmenu.getDirections()) {
                                    directions.add(s);
                                }

                                Intent intent = new Intent(getContext(),FoodDetailActivity.class);
                                intent.putExtra("foods",foodmenu);
                                intent.putStringArrayListExtra("ingredients", (ArrayList<String>) ingredients);
                                intent.putStringArrayListExtra("directions", (ArrayList<String>) directions);
                                startActivity(intent);
                            }
                        });



                    } else {
                        Toast.makeText(getContext(), "ไม่พบเรายการ", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });


        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}

