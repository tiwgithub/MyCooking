package com.example.mycooking.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycooking.Model.Foodmenu;
import com.example.mycooking.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GridAdaper extends FirestoreRecyclerAdapter<Foodmenu, GridAdaper.NoteHolder> {
    private OnItemClickListener mListener;



    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public GridAdaper(@NonNull FirestoreRecyclerOptions<Foodmenu> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteHolder holder, int position, @NonNull Foodmenu model) {

        holder.food_title.setText(model.getUsername());
        holder.tv_book_title.setText(model.getFoodname());

        try {
            Picasso.get().load(model.getImage()).placeholder(R.drawable.ic_launcher_background).into(holder.img_book_thumbnail);
        }catch (Exception e){

        }
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardveiw_item_grid,
                parent, false);
        return new NoteHolder(v,mListener);
    }

    public class NoteHolder extends RecyclerView.ViewHolder{
        TextView tv_book_title ,food_title;
        ImageView img_book_thumbnail;
        public NoteHolder(@NonNull View itemView,final OnItemClickListener listener) {
            super(itemView);
            tv_book_title = (TextView) itemView.findViewById(R.id.book_title_id) ;
            food_title = (TextView) itemView.findViewById(R.id.food_title) ;
            img_book_thumbnail = (ImageView) itemView.findViewById(R.id.book_img_id);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(position);

                        }
                    }

                }
            });
        }
    }
}
