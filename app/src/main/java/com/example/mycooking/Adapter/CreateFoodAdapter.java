package com.example.mycooking.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.mycooking.R;

import java.util.ArrayList;

public class CreateFoodAdapter extends RecyclerView.Adapter<CreateFoodAdapter.MyViewHolder>{
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<String> menu;
    private OnItemClickListener mListener;

    public CreateFoodAdapter(ArrayList<String> menu) {
        this.menu = menu;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @NonNull
    @Override
    public CreateFoodAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_menu_view, parent, false);
        MyViewHolder holder = new MyViewHolder(view,mListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.food.setText((position+1)+". " + menu.get(position));
    }

    @Override
    public int getItemCount() {
        return menu.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView food ;
        public MyViewHolder(@NonNull View itemView, OnItemClickListener mListener) {
            super(itemView);
            food = itemView.findViewById(R.id.textView1);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CreateFoodAdapter.this.mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            CreateFoodAdapter.this.mListener.onItemClick(position);
                        }
                    }
                }
            });
        }
        }
    }

