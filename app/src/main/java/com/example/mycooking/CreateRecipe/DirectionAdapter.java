package com.example.mycooking.CreateRecipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mycooking.R;


import java.util.List;


public class DirectionAdapter extends RecyclerView.Adapter<DirectionAdapter.DirectionViewHolder> {

    private List<String> directionList;
    private boolean isEditable = true;
    private Context mContext;
    private DirectionListener directionListener;

    public DirectionAdapter(Context context, List<String> directionList) {
        mContext = context;
        this.directionList = directionList;
    }

    public DirectionAdapter(Context context, List<String> directionList, boolean isEditable) {
        mContext = context;
        this.directionList = directionList;
        this.isEditable = isEditable;
    }

    @Override
    public int getItemViewType(int position) {
        return isEditable ? 0 : 1;
    }

    @Override
    public DirectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(viewType == 0 ? R.layout.direction_item_row : R.layout.direction_item_row_non_editable,
                        parent, false);
        return new DirectionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final DirectionViewHolder holder, int position) {

        holder.directionText.setText(directionList.get(position));

    }

    @Override
    public int getItemCount() {
        return directionList.size();
    }

    public class DirectionViewHolder extends RecyclerView.ViewHolder {

        TextView directionText;
        ImageView wasteBin;

        public DirectionViewHolder(View itemView) {
            super(itemView);

            directionText = itemView.findViewById(R.id.directionText);
            if (isEditable) {
                wasteBin = itemView.findViewById(R.id.wasteBin);
                wasteBin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (directionListener != null)
                            directionListener.onDeleteDirection(getAdapterPosition());
                    }
                });
            }
        }

    }

    public void setDirectionListener(DirectionListener directionListener) {
        this.directionListener = directionListener;
    }

    public interface DirectionListener {
        void onDeleteDirection(int position);
    }
}
