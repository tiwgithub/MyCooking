package com.example.mycooking.CreateRecipe;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycooking.Model.Foodmenu;
import com.example.mycooking.Model.Ingredients;
import com.example.mycooking.R;


import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeIngredientsFragment extends NavigableFragment {

    private IngredientListener mListener;
    private List<String> ingredientList;
    private IngredientAdapter ingredientAdapter;
    //private DatabaseAdapter databaseAdapter;

    private RecyclerView ingredientRecyclerView;
    private TextView emptyView;
    private Button addButton;
    private AutoCompleteTextView ingredientField;
    private static final String[] COUNTRIES = new String[]{
            "กระเพาะหมู",
            "เส้นเล็ก",
            "เส้นใหญ่",
            "ขนมจีน",
            "ขอบกระด้ง",
            "ขาหมู",
            "ขาไก่",
            "ซี่โครงหมู",
            "ตับหมู",
            "ถั่วงอก",
            "น่องวัว",
            "น่องไก่",
            "บะหมี่",
            "ปลากระบอกแดดเดียว",
            "ปลาช่อนแดดเดียว",
            "ปลาดุกแดดเดียว",
            "ปลาทูนึ่ง",
            "ปลาสลิดแดดเดียว",
            "ปลาสวายแดดเดียว",
            "ปีกกลางไก่",
            "ปีกบนไก่",
            "ปีกไก่เต็ม",
            "มันหมู",
            "ม้าม",
            "ริ้ว",
            "ลิ้นหมูสด",
            "ลูกชิ้นปลา",
            "ลูกชิ้นหมู",
            "ลูกชิ้นเนื้อ"
    };
    public RecipeIngredientsFragment() {
        // Required empty public constructor
    }

    public static RecipeIngredientsFragment newInstance(Foodmenu recipe) {
        RecipeIngredientsFragment fragment = new RecipeIngredientsFragment();

        if (recipe.getIngredients() != null) {
            Bundle args = new Bundle();
            args.putStringArrayList("ingredients", (ArrayList<String>) recipe.getIngredients());
            fragment.setArguments(args);
        }

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe_ingredients, container, false);
        //databaseAdapter = DatabaseAdapter.getInstance(getActivity());

        Bundle args = getArguments();
        if (args != null)
            ingredientList = args.getStringArrayList("ingredients");
        if (ingredientList == null)
            ingredientList = new ArrayList<>();

        ingredientRecyclerView = view.findViewById(R.id.recyclerView);
        emptyView = view.findViewById(R.id.empty_view);
        addButton = view.findViewById(R.id.add_button);
        ingredientField = view.findViewById(R.id.ingredientField);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                R.layout.ingredient_list_item, R.id.text_view_list_item, COUNTRIES);
        ingredientField.setAdapter(adapter);

        ingredientAdapter = new IngredientAdapter(getActivity(), ingredientList);
        ingredientAdapter.setIngredientListener(position -> {
            ingredientList.remove(position);
            toggleEmptyView();
            ingredientAdapter.notifyDataSetChanged();
        });

        toggleEmptyView();

        ingredientRecyclerView.setHasFixedSize(true);
        ingredientRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ingredientRecyclerView.setAdapter(ingredientAdapter);

        addButton.setOnClickListener(v -> {
            String newIngredient = ingredientField.getText().toString();
            if (!newIngredient.isEmpty()) {
                ingredientField.setText("");
                ingredientList.add(newIngredient);
                toggleEmptyView();
                ingredientAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    private void toggleEmptyView() {
        if (ingredientList.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            ingredientRecyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            ingredientRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (IngredientListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement IngredientListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onNext() {
        if (mListener != null)
            mListener.navigateToDirectionsFragment(ingredientList);
    }

    public interface IngredientListener {
        void navigateToDirectionsFragment(List<String> ingredients);
    }
}
