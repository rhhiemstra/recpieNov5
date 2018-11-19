package com.example.robert.recpienov5;

import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.IOException;

public class food_details extends AppCompatActivity {
    DBHelper myDBHelper;
    SQLiteDatabase db;

    TextView foodName;
    TextView foodIngTv;
    ImageView foodImg;
    TextView foodPrepTv;
    RatingBar foodRatingBar;
    Button rateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        Intent myIntent = getIntent();
        final Intent sentToMain = new Intent(this, MainActivity.class);

        final int foodId = myIntent.getIntExtra("foodId", 0);
        String foodNamestr=myIntent.getStringExtra("foodName");
        String foodIngStr=myIntent.getStringExtra("foodIng");
        String foodPrepStr=myIntent.getStringExtra("foodPrep");
        String foodImgStr=myIntent.getStringExtra("foodImg");
        final Float foodRating = myIntent.getFloatExtra("foodRating", 0);

        foodName = findViewById(R.id.foodNameTV);
        foodImg = findViewById(R.id.foodImg);
        foodIngTv = findViewById(R.id.foodIngdTV);
        foodPrepTv = findViewById(R.id.foodPrepTV);
        foodRatingBar = findViewById(R.id.ratingBar);
        rateBtn = findViewById(R.id.rateBtn);

        String extension = "";
        int i = foodImgStr.lastIndexOf(".");
        if (i > 0){
            extension = foodImgStr.substring(i);
        }
        foodImgStr = foodImgStr.replace(extension, "");
        Log.i("foodImg=", foodImgStr);
        int id = getResources().getIdentifier(getPackageName()+":drawable/"+foodImgStr, null, null);
        foodImg.setImageResource(id);



        foodName.setText(foodNamestr);
        foodIngTv.setText(foodIngStr);
        foodPrepTv.setText(foodPrepStr);
        foodRatingBar.setRating(foodRating);

        createDB();
        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                float r = foodRatingBar.getRating();
                String updateQuarey="";
                if (foodId != 0) {
                    updateQuarey = "update recipe set rating= " + r + " where id =" + foodId + ";";
                }
                    db.execSQL(updateQuarey);

                    sentToMain.putExtra("newRating", r);
                    setResult(RESULT_OK, sentToMain);
                    food_details.this.startActivity(sentToMain);

            }
        });




    }
    private void createDB(){
        myDBHelper = new DBHelper(this);

        try {
            myDBHelper.createDataBase();
        }   catch (IOException e){
            throw new Error("Unable to Connect");
        }
        try {
            myDBHelper.openDataBase();
        }   catch (SQLException sql){

        }
        db = myDBHelper.getWritableDatabase();
    }
}
