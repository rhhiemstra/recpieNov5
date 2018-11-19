package com.example.robert.recpienov5;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DBHelper myDBHelper;
    SQLiteDatabase db;
    String txt;
    Intent myIntent;

    ListView myList;
    Spinner mySpinner;
    ArrayAdapter<String> myListAdapter;
    ArrayAdapter<String> mySpinnerAdapter;

    EditText searchText;
    Button searchBtn;
    TextView noResult;

    ArrayList<String> RNameList;
    ArrayList<String> RIngList;
    ArrayList<String> RPrepList;
    ArrayList<String> RImgList;
    ArrayList<Integer>RIdList;
    ArrayList<Float>RRatingList;
    int arrayIndex = -1;
    Boolean first = true;



    String[] catagoryArr={"All", "Main Dish", "Side Dish", "Salad", "Soup", "Desert", "None", "Sort by Rating"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myIntent = new Intent(this, food_details.class);
        final String allQuary = "Select * from recipe";


        myList = findViewById(R.id.myList);
        mySpinner = findViewById(R.id.mySpinner);
        searchBtn = findViewById(R.id.searchBtn);
        searchText = findViewById(R.id.searchText);
        noResult = findViewById(R.id.noResult);
        noResult.setVisibility(View.GONE);

        mySpinnerAdapter=new ArrayAdapter<String>(this, R.layout.spinner_item, catagoryArr);
        mySpinner.setAdapter(mySpinnerAdapter);

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myIntent.putExtra("foodId", RIdList.get(position));
                myIntent.putExtra("foodName", RNameList.get(position));
                myIntent.putExtra("foodIng", RIngList.get(position));
                myIntent.putExtra("foodPrep", RPrepList.get(position));
                myIntent.putExtra("foodImg", RImgList.get(position));
                myIntent.putExtra("foodRating", RRatingList.get(position));
                arrayIndex = position;
                MainActivity.this.startActivityForResult(myIntent, 123);
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt = searchText.getText().toString();
                String searchQuery = "select * from recipe where ingredients like '%" + txt + "%';";
                getResult(searchQuery);

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(),  0);
                mySpinner.setSelection(6);
            }
        });

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cat = catagoryArr[position];
                String catQuary = allQuary;
                if (position != 0 && position !=6){
                    catQuary = "Select * from recipe where category='" + cat + "'";

                }if (first) first=false;
                if (!first && position !=6) getResult(catQuary);
                if (position!=6) searchText.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
        db = myDBHelper.getReadableDatabase();


        getResult(allQuary);
    }
    public void getResult(String q){
        Cursor result = db.rawQuery(q, null);
        result.moveToFirst();
        int count = result.getCount();
        Log.i("count=", String.valueOf(count));
        RNameList = new ArrayList<String>();
        RIngList = new ArrayList<String>();
        RPrepList = new ArrayList<String>();
        RImgList = new ArrayList<String>();
        RIdList = new ArrayList<Integer>();
        RRatingList = new ArrayList<Float>();

        int foodNumber = 1;
        if (count >= 1){
            //I have results
            noResult.setVisibility(View.GONE);
            do{
                RIdList.add(result.getInt(0));
                RNameList.add(result.getString(1));
                RIngList.add(result.getString(2));
                RPrepList.add(result.getString(3));
                RImgList.add(result.getString(5));
                RRatingList.add(result.getFloat(6));
            }while (result.moveToNext());

        } else {
            //no results
            noResult.setVisibility(View.VISIBLE);
        }
        myListAdapter = new ArrayAdapter<String>(this, R.layout.list_item, RNameList);
        myList.setAdapter(myListAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Float newRDate;

        if (requestCode == 123){
            if (requestCode==RESULT_OK){
                newRDate = data.getFloatExtra("newRating", 0);

                RRatingList.set(arrayIndex, newRDate);
            }
        }
    }
}
