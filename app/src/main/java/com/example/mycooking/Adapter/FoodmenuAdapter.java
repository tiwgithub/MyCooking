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

public class FoodmenuAdapter extends FirestoreRecyclerAdapter<Foodmenu, FoodmenuAdapter.NoteHolder> {
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
    public FoodmenuAdapter(@NonNull FirestoreRecyclerOptions<Foodmenu> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteHolder holder, int i, @NonNull Foodmenu model) {
        holder.textViewTitle.setText(model.getFoodname());
        holder.textViewDescription.setText(model.getUsername());
        //holder.textViewPriority.setText("ถูกใจ "+ String.valueOf(model.getLike()));
        try {
            Picasso.get().load(model.getImage()).placeholder(R.drawable.ic_home).into(holder.image);
        }catch (Exception e){

        }


    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_item,
                parent, false);
        return new NoteHolder(v,mListener);
    }

    class NoteHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewDescription;
        TextView textViewPriority;
        ImageView image;

        public NoteHolder(View itemView,final OnItemClickListener listener) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.titlenamefood);
            textViewDescription = itemView.findViewById(R.id.titleuser);
            textViewPriority = itemView.findViewById(R.id.titlelike);
            image = itemView.findViewById(R.id.food_photo);

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
