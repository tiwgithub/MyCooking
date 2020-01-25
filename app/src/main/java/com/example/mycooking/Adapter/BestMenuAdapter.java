package com.example.mycooking.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.asksira.loopingviewpager.LoopingPagerAdapter;
import com.bumptech.glide.Glide;
import com.example.mycooking.Food.FoodDetailActivity;
import com.example.mycooking.Model.Foodmenu;
import com.example.mycooking.R;

import java.util.ArrayList;
import java.util.List;



public class BestMenuAdapter extends LoopingPagerAdapter<Foodmenu> {

    private List<String> ingredients, directions;

    public BestMenuAdapter(Context context, List<Foodmenu> itemList, boolean isInfinite) {
        super(context, itemList, isInfinite);
    }

    @Override
    protected View inflateView(int viewType, ViewGroup container, int listPosition) {
        return LayoutInflater.from(context).inflate(R.layout.layout_bestdeal_item,container,false);
    }

    @Override
    protected void bindView(View convertView, int listPosition, int viewType) {

        ImageView imageView = (ImageView)convertView.findViewById(R.id.image_bestdeal);
        TextView textViewv = (TextView)convertView.findViewById(R.id.txt_bestdeal);
        TextView textViewName=(TextView)convertView.findViewById(R.id.txt_bestdeal_name);
        Glide.with(convertView).load(itemList.get(listPosition).getImage()).into(imageView);
        textViewv.setText(itemList.get(listPosition).getFoodname());
        textViewName.setText(itemList.get(listPosition).getUsername());




    }
}
