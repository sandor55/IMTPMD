package com.imtpmd.sandor.imtpmdws;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.imtpmd.sandor.imtpmdws.Database.DatabaseHelper;
import com.imtpmd.sandor.imtpmdws.Database.DatabaseInfo;

/**
 * Created by sandor on 22-3-2016.
 */
public class VakInfo extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //krijg de data
        Bundle bundle = getIntent().getExtras();
        name = bundle.getString(TAG_VAK);
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


        //name hoeft eigenlijk niet meegegeven te worden
        ContentValues values = new ContentValues();
        values.put(DatabaseInfo.CourseColumn.NAME,name);
        values.put(DatabaseInfo.CourseColumn.ECTS, ect);
        values.put(DatabaseInfo.CourseColumn.GRADE, nieuwecijfer);
        values.put(DatabaseInfo.CourseColumn.PERIOD,period );


         //update de tabel waar name gelijk is aan de name in de tabelnaam
        String[] args = {name};
       db.update(DatabaseInfo.CourseTables.COURSE,values,DatabaseInfo.CourseColumn.NAME + "=?" ,args);
    }
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent (VakInfo.this, Settings.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_p1) {
            startActivity(new Intent(this, MainActivity.class));
            finish();

        } else if (id == R.id.nav_p2) {
            startActivity(new Intent(this, Invoer.class));
            finish();

        } else if (id == R.id.nav_p3) {
            startActivity(new Intent(this, Overzicht.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}