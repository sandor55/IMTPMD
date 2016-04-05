package com.imtpmd.sandor.imtpmdws;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.imtpmd.sandor.imtpmdws.Models.Course;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wendy on 22-3-2016.
 */
public class Invoer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ProgressDialog pDialog;

    RequestQueue requestQueue;
    private ListView mListView;
    private com.imtpmd.sandor.imtpmdws.VakListAdapter mAdapter;
    private List<Course> courseModels = new ArrayList<>();    // NEED A METHOD TO FILL THIS. RETRIEVE THE DATA FROM JSON


    //taggs voor easy gebruik
    private String url = "http://www.fuujokan.nl/subject_lijst.json";
    private static String TAG = Invoer.class.getSimpleName();
    private static final String TAG_VAKNAAM = "name";
    private static final String TAG_ECTS = "ects";
    private static final String TAG_GRADE = "grade";
    private static final String TAG_PERIOD = "period";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //dialog tijdens het ophalen van de data
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                             @Override
//                                             public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                                                 startActivity(new Intent(Invoer.this, VakInfo.class));
//                                             }
//                                         }
//        );
      //  courseModels.add(new Course("vak", "3", "3", "1"));

        makeJsonArrayRequest();
        requestQueue = Volley.newRequestQueue(getApplicationContext());



    }


    private void makeJsonArrayRequest()
    {

        showpDialog();
        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>()
                {

                    @Override
                    public void onResponse(JSONArray response)
                    {
                        Log.d(TAG, response.toString());

                        try {
                            // Parsing json array response
                            // loop through each json object

                            for (int i = 0; i < response.length(); i++)
                            {

                                JSONObject vakken = (JSONObject) response
                                        .get(i);

                                String vak = vakken.getString(TAG_VAKNAAM);
                                String ects = vakken.getString(TAG_ECTS);
                                String grade = vakken.getString(TAG_GRADE);
                                String period = vakken.getString(TAG_PERIOD);


                                //zet ze in de de lijst
                                courseModels.add(new Course(vak, ects, grade, period));
                                Log.d("vaknaam ", vak);
                                Log.d("aantal ects ", ects);
                                Log.d("cijfer ", grade);
                                Log.d("periode ", period);

                            }

                            mListView = (ListView) findViewById(R.id.showVakken);
                            mAdapter = new com.imtpmd.sandor.imtpmdws.VakListAdapter(Invoer.this, 0, courseModels);
                            mListView.setAdapter(mAdapter);

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        hidepDialog();

                    }
                }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
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
            return true;
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
