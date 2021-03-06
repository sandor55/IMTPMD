package com.imtpmd.sandor.imtpmdws;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.imtpmd.sandor.imtpmdws.Database.DatabaseHelper;
import com.imtpmd.sandor.imtpmdws.Database.DatabaseInfo;
import com.imtpmd.sandor.imtpmdws.Models.Course;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    //request info
    private ProgressDialog pDialog;
    RequestQueue requestQueue;
    private String urlstring = "http://www.fuujokan.nl/subject_lijst.json";
    private static String TAG = MainActivity.class.getSimpleName();

    //nodig voor halen data
    int huidigeperiode;
    int studiepunten;
    int nogbehalen;
    int nietbehaald;
    int kerngehaald = 0;
    int kernhalen = 4 - huidigeperiode+1;//huidige periode kan je er namelijk ook 1 halen
    String TAG_VAK = "name";
    String TAG_ECTS = "ects";
    String TAG_GRADE = "grade";
    String TAG_PERIOD = "period";
    String[] projection = {DatabaseInfo.CourseColumn.NAME, DatabaseInfo.CourseColumn.ECTS, DatabaseInfo.CourseColumn.GRADE, DatabaseInfo.CourseColumn.PERIOD};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        //als niet gekoppeld aan internet geef melding
        if (!isInternetAvailable()) {
            Toast.makeText(this,
                    "geen connectie met internet",
                    Toast.LENGTH_LONG).show();
        }

        //haal de huidige periode op
        huidigePeriode();

        //check database
        CheckDatabase();

        //laat het aantal studiepunten zien
        showStudiepunten();
        nogBehalen();
        //geef niet behaald aan van het aantal studiepunten
        this.nietbehaald = 60 - (studiepunten + nogbehalen);

        kernVakken();
        showAdvies();




    }

    @Override
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
            Intent intent = new Intent (MainActivity.this, Settings.class);
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

        } else if (id == R.id.nav_p3) {
            startActivity(new Intent(this, Overzicht.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //check internet connectie
    public boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;


    }


    //maak database / eenmalig zodra er internet is

    /**
     * Method to make json array request where response starts with [
     */

    private void huidigePeriode()
    {
        //bereken huidige periode
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        int maand = today.month;
        int dag = today.monthDay;
        if(maand >=9 && (maand <= 11 && dag <= 9))
        {
            this.huidigeperiode = 1;
        }
        if((maand >=11 && dag >9) && (maand <= 2 && dag <= 8))
        {
            this.huidigeperiode = 2;
        }
        if((maand >=2 && dag > 7) && (maand <= 4 && dag <= 24));
        {
            this.huidigeperiode = 3;
        }
        if((maand >=4 && dag > 24)&& maand <= 9)
        {
            this.huidigeperiode = 4;
        }
        //zet de tekst van de huidige periode
        TextView vak = (TextView) findViewById(R.id.periodeweergave);
        vak.setText(String.valueOf(this.huidigeperiode));
    }

    public void showStudiepunten() {


        //where clausule
        String selection = DatabaseInfo.CourseColumn.GRADE + ">= 5.5";
        DatabaseHelper dbHelper = DatabaseHelper.getHelper(this);
        Cursor rs = dbHelper.query(DatabaseInfo.CourseTables.COURSE, projection, selection, null, null, null, null);
        //skip lege elementen die misschien eerst staan.
        rs.moveToFirst();
        if (rs.getCount() == 0) {
            //doe niks als er geen database is
        } else {
            //gooi  het in een loop en lees ze stuk voor stk uit
            for (int a = 0; a < rs.getCount(); a++) {
                String vak = (String) rs.getString(rs.getColumnIndex(TAG_VAK));
                int ects = (Integer) rs.getInt(rs.getColumnIndex(TAG_ECTS));
                double grade = (Double) rs.getDouble(rs.getColumnIndex(TAG_GRADE));
                int period = (Integer) rs.getInt(rs.getColumnIndex(TAG_PERIOD));
                //ga naar de volgende in de rij.
                rs.moveToNext();
                this.studiepunten += ects;
            }


        }

        TextView test = (TextView) findViewById(R.id.studiepunten);

        test.setText(String.valueOf(this.studiepunten));
    }
    public void nogBehalen() {

        String[] selectionargs = {String.valueOf(this.huidigeperiode),String.valueOf(0.0)};
        String selection = DatabaseInfo.CourseColumn.PERIOD + ">=?" + " AND " + DatabaseInfo.CourseColumn.GRADE + "=?";
        DatabaseHelper dbHelper = DatabaseHelper.getHelper(this);
        Cursor rs = dbHelper.query(DatabaseInfo.CourseTables.COURSE, projection, selection, selectionargs, null, null, null);
        //skip lege elementen die misschien eerst staan.
        rs.moveToFirst();
        if (rs.getCount() == 0) {
            //doe niks
        } else {
            //gooi  het in een loop en lees ze stuk voor stk uit
            for (int a = 0; a < rs.getCount(); a++) {
                int ects = (Integer) rs.getInt(rs.getColumnIndex(TAG_ECTS));

                //ga naar de volgende in de rij.
                rs.moveToNext();

                //tel de ects op
                this.nogbehalen += ects;
            }


        }

    }
    public void kernVakken() {
        //lees database door op vakken en of het gehaald is
        String[] selectionargs = {String.valueOf(this.huidigeperiode)};
        String selection = DatabaseInfo.CourseColumn.PERIOD + "<=?" + " AND " + DatabaseInfo.CourseColumn.GRADE + ">=5.5";

        DatabaseHelper dbHelper = DatabaseHelper.getHelper(this);
        Cursor rs = dbHelper.query(DatabaseInfo.CourseTables.COURSE, projection, selection, selectionargs, null, null, null);
        //skip lege elementen die misschien eerst staan.
        rs.moveToFirst();
        if (rs.getCount() == 0) {
            //doe niks
        } else {
            //gooi  het in een loop en lees ze stuk voor stk uit
            for (int a = 0; a < rs.getCount(); a++) {
                String vak = (String) rs.getString(rs.getColumnIndex(TAG_VAK));
                double grade = (Double) rs.getDouble(rs.getColumnIndex(TAG_GRADE));
                //als een van deze vakken hoger is dan 5,5 zet dan dat een kernvak is gehaald
                if((vak.equals("IOPR1") || vak.equals("IOPR2") || vak.equals("IRDB") || vak.equals("INET") && grade > 5.5))
                {
                    this.kerngehaald += 1;
                }
                        rs.moveToNext();
            }



        }

    }

    public void CheckDatabase() {        //check de datbase of er wat in staat.
        DatabaseHelper dbHelper = DatabaseHelper.getHelper(this);
        String[] projection = {DatabaseInfo.CourseColumn.NAME, DatabaseInfo.CourseColumn.NAME, DatabaseInfo.CourseColumn.ECTS, DatabaseInfo.CourseColumn.GRADE, DatabaseInfo.CourseColumn.PERIOD};
        Cursor rs = dbHelper.query(DatabaseInfo.CourseTables.COURSE, projection, null, null, null, null, null);
        //skip lege elementen die misschien eerst staan.
        rs.moveToFirst();

        //als er niks in database staat, en er wel internet is, haal data op
        if (rs.getCount() == 0 && isInternetAvailable()) {
            //als voor het ophalen van de data
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Data ophalen...");
            pDialog.setCancelable(false);
            //nodig voor het ophalen
            maakDatabase();
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        //geen internet en geen database
        if (rs.getCount() == 0 && !isInternetAvailable()) {
            Toast.makeText(this,
                    "kan geen database aanmaken, geen internet connectie",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void showAdvies()
    {
        //zet het advies aan de hand van de behaalde dingen
        String advies = "";
        TextView adviestext = (TextView) findViewById(R.id.adviesweergave);
        //minder dan 40 studiepunten of geen kernvakken meer mogelijk om te halen
        if((studiepunten + nogbehalen < 40) || (kernhalen + kerngehaald < 2))
        {
            advies = " Negatief, Je kan het jaar niet meer halen. Tekort studiepunten of te weinig kernvakken.";
        }
        if((studiepunten + nogbehalen > 40) && (studiepunten + nogbehalen <50) && (kerngehaald < 2) )
        {
            int halen = 2 - kerngehaald;
        advies = "Waarschuwing, Je kan het jaar halen met maximaal tussen de 40-50 ECTS." + " Maar je moet nog " + String.valueOf(halen) + " kernvak(ken) halen." ;
        }
        if((studiepunten + nogbehalen >40) && (studiepunten + nogbehalen <50) && (kerngehaald >= 2))
        {

            advies = "Waarschuwing, Je kan het jaar halen met maximaal tussen de 40-50 ECTS." + " Maar je hebt wel al " + String.valueOf(kerngehaald) + " kernvakken gehaald." ;
        }
        if((studiepunten + nogbehalen >50)  && (kerngehaald < 2))
        {

            int halen = 2 - kerngehaald;
            advies = "Je kan het jaar halen met 50+ studiepunten." + " Maar je moet nog " + String.valueOf(halen) + " kernvak(ken) halen." ;
        }adviestext.setText(advies);
        if((studiepunten + nogbehalen >50)  && (kerngehaald >= 2))
        {
            advies = "Je kan het jaar halen met 50+ studiepunten." + " En je hebt " + String.valueOf(kerngehaald) + " kernvakken gehaald." ;
        }adviestext.setText(advies);



    }
    public void maakDatabase() {
        showpDialog();


        JsonArrayRequest req = new JsonArrayRequest(urlstring,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        try {
                            // Parsing json array response
                            // loop through each json object

                            for (int i = 0; i < response.length(); i++) {

                                JSONObject methode = (JSONObject) response
                                        .get(i);

                                String vak = methode.getString("name");
                                String holdects = methode.getString("ects");
                                String holdperiod = methode.getString("period");
                                int ects = Integer.parseInt(holdects);
                                //cijfer wordt standaard 0. niet ingevuld.
                                double grade = 0.0;
                                int period = Integer.parseInt(holdperiod);
                                DatabaseHelper dbHelper = DatabaseHelper.getHelper(getApplicationContext());

                                //store values en schrijf weg ind e database
                                ContentValues values = new ContentValues();
                                values.put(DatabaseInfo.CourseColumn.NAME, vak);
                                values.put(DatabaseInfo.CourseColumn.ECTS, ects);
                                values.put(DatabaseInfo.CourseColumn.GRADE, grade);
                                values.put(DatabaseInfo.CourseColumn.PERIOD, period);
                                dbHelper.insert(DatabaseInfo.CourseTables.COURSE, null, values);

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        hidepDialog();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                hidepDialog();
            }
        });
        // Adding request to request queue
        JSONcontroller.getInstance().addToRequestQueue(req);

    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}