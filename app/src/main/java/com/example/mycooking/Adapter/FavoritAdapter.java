package com.example.mycooking.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycooking.Food.FoodDetailActivity;
import com.example.mycooking.Model.Foodmenu;
import com.example.mycooking.R;
import com.facebook.FacebookActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FavoritAdapter extends RecyclerView.Adapter<FavoritAdapter.ViewHolder> {

    private OnItemClickListener onItemClickListener;

    private ArrayList<Foodmenu> foodmenus;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }
    public FavoritAdapter(ArrayList<Foodmenu> foodmenu5) {
        foodmenus = foodmenu5;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_item, parent, false);
        return new FavoritAdapter .ViewHolder(view,onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Foodmenu foodmenu = foodmenus.get(position);
        holder.textViewTitle.setText(foodmenu.getFoodname());
        holder.textViewDescription.setText(foodmenu.getUsername());

        try {
            Picasso.get().load(foodmenu.getImage()).placeholder(R.drawable.ic_home).into(holder.image);
        }catch (Exception e){

        }



    }

    @Override
    public int getItemCount() {
        return foodmenus.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewTitle;
        TextView textViewDescription;
        TextView textViewPriority;
        ImageView image;
    public ViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
        super(itemView);
        textViewTitle = itemView.findViewById(R.id.titlenamefood);
        textViewDescription = itemView.findViewById(R.id.titleuser);
        textViewPriority = itemView.findViewById(R.id.titlelike);
        image = itemView.findViewById(R.id.food_photo);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(position);

                    }
                }
            }
        });

    }
}
}
