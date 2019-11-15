package com.example.mycooking.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Foodmenu implements Parcelable {
    private String documentId;
    private String foodname;
    private String username;
    private String image;
    private int like;
    List<String> directions;
    List<String> ingredients;
    public Foodmenu() {
        //empty constructor needed
    }

    public Foodmenu(String foodname, String username) {
        this.foodname = foodname;
        this.username = username;
    }

    public Foodmenu(String documentId, String foodname, String username, String image, int like, List<String> directions, List<String> ingredients) {
        this.documentId = documentId;
        this.foodname = foodname;
        this.username = username;
        this.image = image;
        this.like = like;
        this.directions = directions;
        this.ingredients = ingredients;
    }

    protected Foodmenu(Parcel in) {
        documentId = in.readString();
        foodname = in.readString();
        username = in.readString();
        image = in.readString();
        like = in.readInt();
        directions = in.createStringArrayList();
        ingredients = in.createStringArrayList();
    }

    public static final Creator<Foodmenu> CREATOR = new Creator<Foodmenu>() {
        @Override
        public Foodmenu createFromParcel(Parcel in) {
            return new Foodmenu(in);
        }

        @Override
        public Foodmenu[] newArray(int size) {
            return new Foodmenu[size];
        }
    };

    public String getDocumentId() {
        return documentId;
    }

    public String getFoodname() {
        return foodname;
    }

    public String getUsername() {
        return username;
    }

    public String getImage() {
        return image;
    }

    public int getLike() {
        return like;
    }

    public List<String> getDirections() {
        return directions;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(documentId);
        parcel.writeString(foodname);
        parcel.writeString(username);
        parcel.writeString(image);
        parcel.writeInt(like);
        parcel.writeStringList(directions);
        parcel.writeStringList(ingredients);
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
