package com.example.mycooking.Food;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mycooking.Model.Foodmenu;
import com.example.mycooking.Model.Ingredients;
import com.example.mycooking.Model.MyTTS;
import com.example.mycooking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodDetailActivity extends AppCompatActivity implements
        RecognitionListener {
    private Button btnRating;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference Userfavorite = mRootRef.child("favorite");
    private DatabaseReference Usersfav = mRootRef.child("favorite_user");
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    private ArrayList<String> directions, ingredients;
    private TextView food_name, food_user, txt_direction;
    private ImageView food_img;
    private FloatingActionButton btnlike,btnSpeech;
    private static final int REQUEST_RECORD_PERMISSION = 100;
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    boolean like = false;
    boolean speechCheck = false;
    private int i=0;
    private RatingBar ratingBar;

    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Ingredients");
    RecyclerView listView;
    private ArrayList<Ingredients> foodmenu5 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        resetSpeechRecognizer();

        btnRating = findViewById(R.id.btn_rating);
        btnlike = findViewById(R.id.btnlike);
        food_name = findViewById(R.id.food_name);
        food_user = findViewById(R.id.food_user);
        food_img = findViewById(R.id.food_detail_img);
        txt_direction = findViewById(R.id.txt_direction_detail);
        btnSpeech=findViewById(R.id.btnSpeech);
        ratingBar=findViewById(R.id.ratingbar5);

        final Intent intentfood = getIntent();
        final Foodmenu foodmenu = intentfood.getParcelableExtra("foods");
        ingredients = intentfood.getStringArrayListExtra("ingredients");
        directions = intentfood.getStringArrayListExtra("directions");
        listView = (RecyclerView) findViewById(R.id.listView1);

        listView.setLayoutManager(new LinearLayoutManager(this));
        CustomAdapter customAdapter = new CustomAdapter(ingredients);
        listView.setAdapter(customAdapter);
        customAdapter.setOnItemClickListener(new CustomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(FoodDetailActivity.this,ingredients.get(position),Toast.LENGTH_SHORT).show();

                Searchingredients(ingredients.get(position));
                //SerachIn(ingredients.get(position));
                //Searchh();

            }
        });

        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ShowDialog();
            }
        });

        ratingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rating = "Rating is :" + ratingBar.getRating();
                Toast.makeText(FoodDetailActivity.this, rating, Toast.LENGTH_SHORT).show();
            }
        });





        Query query = Userfavorite.child(foodmenu.getDocumentId());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    like = false;

                } else {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        if (ds.child("user_id").getValue().equals(user.getUid())) {

                            like = true;

                            btnlike.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));


                        }
                    }
                }

                if (!like) {
                    like = false;
                    btnlike.setBackgroundTintList(getResources().getColorStateList(R.color.white));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        btnSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!speechCheck){
                    speechCheck = true;
                    btnSpeech.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));

                    int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(FoodDetailActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
                        return;
                    }
                    setRecogniserIntent();
                    speech.startListening(recognizerIntent);

                }else {
                    speechCheck = false;
                    btnSpeech.setBackgroundTintList(getResources().getColorStateList(R.color.white));
                    speech.stopListening();
                     speech.destroy();
                }


            }
        });

        btnlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!like) {
                    like = true;
                    btnlike.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
                    Toast.makeText(FoodDetailActivity.this, "Like", Toast.LENGTH_SHORT).show();
                    mRootRef.child("favorit_list").child(foodmenu.getDocumentId()).child(user.getUid()).child("user_id").setValue(user.getUid());
                    Userfavorite.child(foodmenu.getDocumentId()).child(user.getUid()).child("user_id").setValue(user.getUid());
                    Usersfav.child(user.getUid()).child(foodmenu.getDocumentId()).child("user_id").setValue(user.getUid());
                } else {
                    btnlike.setBackgroundTintList(getResources().getColorStateList(R.color.white));
                    like = false;
                    Toast.makeText(FoodDetailActivity.this, "Unlike", Toast.LENGTH_SHORT).show();
                    Userfavorite.child(foodmenu.getDocumentId()).setValue(null);
                    Usersfav.child(user.getUid()).child(foodmenu.getDocumentId()).setValue(null);
                    mRootRef.child("favorit_list").child(foodmenu.getDocumentId()).child(user.getUid()).setValue(null);
                }
            }
        });

        food_name.setText(foodmenu.getFoodname());
        food_user.setText(foodmenu.getUsername());

        try {
            Picasso.get().load(foodmenu.getImage()).placeholder(R.drawable.ic_launcher_background).into(food_img);
        } catch (Exception e) {

        }

        String step = "";
        StringBuilder direction = new StringBuilder("");


        for (int i = 0; i < directions.size(); i++) {
            direction.append((i + 1) + ". " + directions.get(i));
            direction.append("\n\n");
        }

        txt_direction.setText(direction);

    }

    private void setRecogniserIntent() {
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
    }
    private void resetSpeechRecognizer() {

        if(speech != null)
            speech.destroy();
        speech = SpeechRecognizer.createSpeechRecognizer(this);

        if(SpeechRecognizer.isRecognitionAvailable(this))
            speech.setRecognitionListener(this);
        else
            finish();
    }
    private void restart(){
        ActivityCompat.requestPermissions
                (FoodDetailActivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_RECORD_PERMISSION);
    }
    private void Searchingredients(String s) {



        db.collection("Ingredients").whereEqualTo("name",s).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                foodmenu5.clear();
                String data = "";
                for (DocumentSnapshot document : task.getResult()) {
                    Ingredients ingredient5 =new Ingredients(document.getString("name"),(List<String>) document.get("ingredients"));

                    foodmenu5.add(ingredient5);

                    for (String s : ingredient5.getIngredients()){
                        data += "\n- " + s;
                    }
                }

                if (!foodmenu5.isEmpty()){
                AlertDialog.Builder builder = new AlertDialog.Builder(FoodDetailActivity.this);
                builder.setTitle("สถานที่ซื้อ")
                        .setMessage(data)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                builder.show();

            }else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(FoodDetailActivity.this);
                    builder.setTitle("สถานที่ซื้อ")
                            .setMessage("ไม่พบ")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });

                    builder.show();

                }

            }
        });




    }
    public void ShowDialog()
    {
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final RatingBar rating = findViewById(R.id.rating_Bar);
        rating.setMax(5);
        rating.setNumStars(5);

        popDialog.setIcon(android.R.drawable.btn_star);
        popDialog.setTitle("Vote");
        popDialog.setView(rating);

        // Button OK
        popDialog.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }

                })

                // Button Cancel
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        popDialog.create();
        popDialog.show();

    }

    public void SerachIn(String s){



        db.collection("Ingredients").whereEqualTo("name","ต้นหอม").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String data = "";
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Ingredients ingredient5 = documentSnapshot.toObject(Ingredients.class);
                    foodmenu5.add(ingredient5);

                    for (String tag : foodmenu5.get(0).getIngredients()) {
                        data += "\n-" + tag;
                    }

                }


                if (!foodmenu5.isEmpty()){
                AlertDialog.Builder builder = new AlertDialog.Builder(FoodDetailActivity.this);
                builder.setTitle("55")
                        .setMessage(data)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                builder.show();
            }}
        });
    }

    public void Searchh(){


    }

    public void Alert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(FoodDetailActivity.this);
        builder.setTitle("Information")
                .setMessage("555")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    speech.startListening(recognizerIntent);
                } else {
                    Toast.makeText(FoodDetailActivity.this, "Permission Denied!", Toast
                            .LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int i) {
        resetSpeechRecognizer();
        restart();
    }

    @Override
    public void onResults(Bundle bundle) {

        ArrayList<String> matches = bundle
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = matches.get(0);



        if (text.toUpperCase().equals("START") || text.toUpperCase().startsWith("ST")||text.toUpperCase().endsWith("RT")) {
            MyTTS.getInstance(FoodDetailActivity.this).speak(directions.get(0));
            Toast.makeText(getBaseContext(),"Executing Start Command",Toast.LENGTH_SHORT).show();
            //description.setText(text.toString());
        }else if (text.toUpperCase().equals("NEXT") || text.toUpperCase().startsWith("NE")||text.toUpperCase().endsWith("XT")){
            i+=1;
            if (i>directions.size()){
                MyTTS.getInstance(FoodDetailActivity.this).speak("สิ้นสุดขั้นตอน");
            }else {

                MyTTS.getInstance(FoodDetailActivity.this).speak(directions.get(i));

            }
            //Toast.makeText(getBaseContext(),"Error",Toast.LENGTH_SHORT).show();
        }else if (text.toUpperCase().equals("AGAIN") || text.toUpperCase().startsWith("AG")||text.toUpperCase().startsWith("IN")){
            MyTTS.getInstance(FoodDetailActivity.this).speak(directions.get(i));

        }

        resetSpeechRecognizer();
        restart();
    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (speech != null) {
            speech.destroy();
        }
    }

    public static class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

        private LayoutInflater inflater;
        private Context context;
        private ArrayList<String> menu;
        private OnItemClickListener onItemClickListener;

        public void setOnItemClickListener(OnItemClickListener listener) {
            onItemClickListener = listener;
        }

        public CustomAdapter(ArrayList<String> menu) {
            this.menu = menu;
        }


        @NonNull
        @Override
        public CustomAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_menu_view, parent, false);
            MyViewHolder holder = new MyViewHolder(view,onItemClickListener);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull CustomAdapter.MyViewHolder holder, final int position) {
        holder.food.setText((position+1)+". " + menu.get(position));


        }

        @Override
        public int getItemCount() {
            return menu.size();
        }

        public interface OnItemClickListener {
            void onItemClick(int position);
        }
        public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView food ;
            public MyViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
                super(itemView);
                food = itemView.findViewById(R.id.textView1);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                listener.onItemClick(position);
                            }
                        }
                    }
                });
            }
        }
    }
}
