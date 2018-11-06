package com.example.robert.recpienov5;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    Boolean first = true;



    String[] catagoryArr={"All", "Main Dish", "Side Dish", "Salad", "Soup", "Desert", "None"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String allQuary = "Select * from recipe";

        myList = findViewById(R.id.myList);
        mySpinner = findViewById(R.id.mySpinner);
        searchBtn = findViewById(R.id.searchBtn);
        searchText = findViewById(R.id.searchText);
        noResult = findViewById(R.id.noResult);
        noResult.setVisibility(View.GONE);

        mySpinnerAdapter=new ArrayAdapter<String>(this, R.layout.spinner_item, catagoryArr);
        mySpinner.setAdapter(mySpinnerAdapter);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cat = catagoryArr[position];
                String catQuary = allQuary;
                if (position != 0 && position !=6){
                    catQuary = "Select * from recipe where category='" + cat + "'";

                }if (first) first=false;
                if (!first && position !=6) getResult(catQuary);
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

        int foodNumber = 1;
        if (count >= 1){
            //I have results
            do{
                RNameList.add(result.getString(1));
                RIngList.add(result.getString(2));
                RPrepList.add(result.getString(3));
            }while (result.moveToNext());

        } else {
            //no results
        }
        myListAdapter = new ArrayAdapter<String>(this, R.layout.list_item, RNameList);
        myList.setAdapter(myListAdapter);
    }

}
