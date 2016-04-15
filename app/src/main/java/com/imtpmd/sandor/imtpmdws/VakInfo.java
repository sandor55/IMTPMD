package com.imtpmd.sandor.imtpmdws;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.imtpmd.sandor.imtpmdws.Database.DatabaseHelper;
import com.imtpmd.sandor.imtpmdws.Database.DatabaseInfo;

/**
 * Created by sandor on 22-3-2016.
 */
public class VakInfo extends AppCompatActivity {
    //tags voor de data
    //database data
    String[] projection = {DatabaseInfo.CourseColumn.NAME, DatabaseInfo.CourseColumn.ECTS,DatabaseInfo.CourseColumn.GRADE, DatabaseInfo.CourseColumn.PERIOD};
    String TAG_VAK = "name";
    String TAG_ECTS = "ects";
    String TAG_GRADE = "grade";
    String TAG_PERIOD = "period";
    //String WHERE = name = "name" ;


    //data van item
    private String name;
    private int ect;
    private int period;
    private double grade;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vak_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //krijg de data
        Bundle bundle = getIntent().getExtras();
        name = bundle.getString(TAG_VAK);
        Log.d("name",name);
        ect = bundle.getInt(TAG_ECTS);
        period = bundle.getInt(TAG_PERIOD);
        grade = bundle.getDouble(TAG_GRADE);

        //zet de tekstvakken
        TextView vak = (TextView) findViewById(R.id.vakcode);
        TextView vakect = (TextView) findViewById(R.id.vakect);
        TextView vakperiod = (TextView) findViewById(R.id.vakperiode);
        TextView vakgrade = (TextView) findViewById(R.id.vakgrade);
        TextView vakkern = (TextView) findViewById(R.id.kernvak);
        vak.setText(name);
        vakect.setText(String.valueOf(ect));
        vakperiod.setText(String.valueOf(period));
        vakgrade.setText(String.valueOf(grade));
        //als kernvak zet het op ja.
        if(name.equals("IOPR1") ||name.equals("IOPR2") || name.equals("IRDB") || name.equals("INET") || name.equals("IIPXXXX"))
        {
            vakkern.setText("Ja");
        } else
        {
            vakkern.setText("Nee");
        }


    }

    public void annuleren(View v)
    {
        //ga terug naar invoer.
        Intent i = new Intent(getApplicationContext(), Invoer.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);

        finish();
    }
    public void opslaan(View v)
    {
        //zorg dat de data opgeslagen wordt
        saveData();
        Intent i = new Intent(getApplicationContext(), Invoer.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }
    public void saveData()
    {
        DatabaseHelper dbHelper = DatabaseHelper.getHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //store values en schrijf weg ind e database
        EditText cijfer = (EditText) findViewById(R.id.vakgrade);
        double nieuwecijfer = Double.parseDouble(cijfer.getText().toString());
        Log.d("cijfer",String.valueOf(nieuwecijfer));

        //name hoeft eigenlijk niet meegegeven te worden
        ContentValues values = new ContentValues();
        values.put(DatabaseInfo.CourseColumn.NAME,name);
        values.put(DatabaseInfo.CourseColumn.ECTS, ect);
        values.put(DatabaseInfo.CourseColumn.GRADE, nieuwecijfer);
        values.put(DatabaseInfo.CourseColumn.PERIOD,period );

        Log.d("vaknaam" , name);
         //update de tabel waar name gelijk is aan de name in de tabelnaam
        String[] args = {name};
       db.update(DatabaseInfo.CourseTables.COURSE,values,DatabaseInfo.CourseColumn.NAME + "=?" ,args);
    }
}