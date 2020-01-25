package com.example.mycooking.Home;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import com.asksira.loopingviewpager.LoopingViewPager;
import com.example.mycooking.Adapter.BestMenuAdapter;
import com.example.mycooking.Adapter.RecyclerViewAdapter;
import com.example.mycooking.Common.IBestDealCallbackListener;
import com.example.mycooking.Model.Foodmenu;
import com.example.mycooking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.MutableData;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private TextView textView;
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    LayoutAnimationController animationController;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Foods");
    private List<Foodmenu> foodmenus;
    private BestMenuAdapter bestMenuAdapter;


    private LoopingViewPager loopView;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        textView = view.findViewById(R.id.txt_bestdeal_item);
        mImageUrls.add("https://c1.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg");
        mNames.add("เมนูผัด");

        mImageUrls.add("https://i.redd.it/tpsnoz5bzo501.jpg");
        mNames.add("เมนูแกง");

        mImageUrls.add("https://i.redd.it/qn7f9oqu7o501.jpg");
        mNames.add("เมนูทอด");

        mImageUrls.add("https://i.redd.it/j6myfqglup501.jpg");
        mNames.add("เมนูยำ");


        mImageUrls.add("https://i.redd.it/0h2gm1ix6p501.jpg");
        mNames.add("เมนูปิ้งย่าง");

        mImageUrls.add("https://i.redd.it/k98uzl68eh501.jpg");
        mNames.add("เมนูนึ่ง");

        loopView = view.findViewById(R.id.loopView);
        foodmenus = new ArrayList<>();

        animationController = AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_item_from_left);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(getContext(), mNames, mImageUrls);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutAnimation(animationController);

        db.collection("Foods").orderBy("like").limit(5).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                foodmenus.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Foodmenu foodmenu = document.toObject(Foodmenu.class);
                    foodmenus.add(foodmenu);
                    //textView.setText(String.valueOf(foodmenus.size()));
                }
                bestMenuAdapter = new BestMenuAdapter(getContext(),foodmenus,true);

                loopView.setAdapter(bestMenuAdapter);
            }
        });






        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loopView.resumeAutoScroll();
    }

    @Override
    public void onPause() {
        super.onPause();
       loopView.pauseAutoScroll();
    }
}
