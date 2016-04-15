package com.imtpmd.sandor.imtpmdws;

import android.content.Intent;
import android.database.Cursor;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.imtpmd.sandor.imtpmdws.Database.DatabaseHelper;
import com.imtpmd.sandor.imtpmdws.Database.DatabaseInfo;
import com.imtpmd.sandor.imtpmdws.Models.Course;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sandor on 21-3-2016.
 */
public class Invoer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    //listdata
    private ListView mListView;
    private com.imtpmd.sandor.imtpmdws.VakListAdapter mAdapter;
    private List<Course> courseModel = new ArrayList<>();   //data komt hierin

    //database data
    String[] projection = {DatabaseInfo.CourseColumn.NAME, DatabaseInfo.CourseColumn.ECTS,DatabaseInfo.CourseColumn.GRADE, DatabaseInfo.CourseColumn.PERIOD};
    String TAG_VAK = "name";
    String TAG_ECTS = "ects";
    String TAG_GRADE = "grade";
    String TAG_PERIOD = "period";


    protected void onCreate(Bundle savedInstanceState) {

        //settings voor drawer en startup
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

       ShowData();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //krijg de opgeslagen data van de positie
                    Course data = (Course) parent.getItemAtPosition(position);

                    //haal de data eruit
                    String name = data.name;
                    int ects = data.ects;
                    double grade = data.grade;
                    int period = data.period;
                    Log.d("vaknaam",name);
                    Log.d("ects",String.valueOf(ects));
                    Log.d("cijfer",String.valueOf(grade));
                    Log.d("periode",String.valueOf(period));
                Intent i = new Intent(getApplicationContext(), VakInfo.class);
                    i.putExtra(TAG_VAK,name);
                    i.putExtra(TAG_ECTS,ects);
                    i.putExtra(TAG_GRADE,grade);
                    i.putExtra(TAG_PERIOD,period);
                startActivity(i);
            }
        });



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
    public void ShowData()
    {
        DatabaseHelper dbHelper = DatabaseHelper.getHelper(this);
        //sortorder op periode
        String sortorder = DatabaseInfo.CourseColumn.PERIOD + " ASC";
        Cursor rs = dbHelper.query(DatabaseInfo.CourseTables.COURSE, projection, null, null, null, null, sortorder);
        //skip lege elementen die misschien eerst staan.
        rs.moveToFirst();
        if(rs.getCount() == 0)
        {
            Toast.makeText(this,
                    "geen database beschikbaar",
                    Toast.LENGTH_LONG).show();
        }
        else {
            //gooi  het in een loop en lees ze stuk voor stk uit
            for(int a =0;a <rs.getCount(); a++) {
                String vak = (String) rs.getString(rs.getColumnIndex(TAG_VAK));
                int ects = (Integer) rs.getInt(rs.getColumnIndex(TAG_ECTS));
                double grade = (Double) rs.getDouble(rs.getColumnIndex(TAG_GRADE));
                int period = (Integer) rs.getInt(rs.getColumnIndex(TAG_PERIOD));
                Log.d("vak",  vak);
                Log.d("ects", String.valueOf(ects));
                Log.d("cijfer", String.valueOf(grade));
                Log.d("periode", String.valueOf(period));
                //add opgehaalde data in de model
                courseModel.add(new Course(vak, ects,grade, period));

                //ga naar de volgende in de rij.
                rs.moveToNext();
            }
            //zet listview items etc nadat het geladen is uit de e database
            mListView = (ListView) findViewById(R.id.showVakken);
            mAdapter = new com.imtpmd.sandor.imtpmdws.VakListAdapter(Invoer.this,0, courseModel);
            mListView.setAdapter(mAdapter);

        }
    }
}