package com.imtpmd.sandor.imtpmdws;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.imtpmd.sandor.imtpmdws.Database.DatabaseHelper;
import com.imtpmd.sandor.imtpmdws.Database.DatabaseInfo;

import java.util.ArrayList;

/**
 * Created by Wendy on 22-3-2016.
 */
public class Overzicht extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ProgressDialog pDialog;

    int huidigeperiode;
    int studiepunten;
    int nogbehalen;
    int nietbehaald;
    String TAG_VAK = "name";
    String TAG_ECTS = "ects";
    String TAG_GRADE = "grade";
    String TAG_PERIOD = "period";
    String[] projection = {DatabaseInfo.CourseColumn.NAME, DatabaseInfo.CourseColumn.ECTS, DatabaseInfo.CourseColumn.GRADE, DatabaseInfo.CourseColumn.PERIOD};

    private RelativeLayout mainLayout;
    private PieChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overzicht_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //laad de informatie voor alels
        huidigePeriode();
        CheckDatabase();
        showStudiepunten();
        nogBehalen();
        //zet het niet behaalde aantal punten
        this.nietbehaald = 60 - (studiepunten + nogbehalen);


        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        mChart = new PieChart(this);

        //add pie chart naar pie layout
        mainLayout.addView(mChart);

        //enable hole
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleRadius(45f);
        mChart.setTransparentCircleRadius(5f);

        //rotatie van de chart
        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);

        //initialiseren legenda
        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);

        initChart();
    }

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

    }

    public void showStudiepunten() {
        int studiepunten = 0;

        //where clausule
        String selection = DatabaseInfo.CourseColumn.GRADE + ">= 5.5";
        DatabaseHelper dbHelper = DatabaseHelper.getHelper(this);
        Cursor rs = dbHelper.query(DatabaseInfo.CourseTables.COURSE, projection, selection, null, null, null, null);
        //skip lege elementen die misschien eerst staan.
        rs.moveToFirst();
        if (rs.getCount() == 0) {
            Toast.makeText(this,
                    "geen database beschikbaar",
                    Toast.LENGTH_LONG).show();
        } else {
            //gooi  het in een loop en lees ze stuk voor stk uit
            for (int a = 0; a < rs.getCount(); a++) {
                String vak = (String) rs.getString(rs.getColumnIndex(TAG_VAK));
                int ects = (Integer) rs.getInt(rs.getColumnIndex(TAG_ECTS));
                double grade = (Double) rs.getDouble(rs.getColumnIndex(TAG_GRADE));
                int period = (Integer) rs.getInt(rs.getColumnIndex(TAG_PERIOD));
                //add opgehaalde data in de model
                //ga naar de volgende in de rij.
                rs.moveToNext();
                this.studiepunten += ects;
            }
        }
    }

    public void nogBehalen() {
        int studiepunten = 0;
        String[] selectionargs = {String.valueOf(this.huidigeperiode),String.valueOf(0.0)};
        String selection = DatabaseInfo.CourseColumn.PERIOD + ">=?" + " AND " + DatabaseInfo.CourseColumn.GRADE + "=?";
        DatabaseHelper dbHelper = DatabaseHelper.getHelper(this);
        Cursor rs = dbHelper.query(DatabaseInfo.CourseTables.COURSE, projection, selection, selectionargs, null, null, null);
        //skip lege elementen die misschien eerst staan.
        rs.moveToFirst();
        if (rs.getCount() == 0) {
            Toast.makeText(this,
                    "geen database beschikbaar",
                    Toast.LENGTH_LONG).show();
        } else {
            //gooi  het in een loop en lees ze stuk voor stk uit
            for (int a = 0; a < rs.getCount(); a++) {
                String vak = (String) rs.getString(rs.getColumnIndex(TAG_VAK));
                int ects = (Integer) rs.getInt(rs.getColumnIndex(TAG_ECTS));
                double grade = (Double) rs.getDouble(rs.getColumnIndex(TAG_GRADE));
                int period = (Integer) rs.getInt(rs.getColumnIndex(TAG_PERIOD));
                //add opgehaalde data in de model
                //ga naar de volgende in de rij.
                rs.moveToNext();
                this.nogbehalen += ects;
            }
        }
    }

    public void CheckDatabase() {
        //check de datbase of er wat in staat.
        DatabaseHelper dbHelper = DatabaseHelper.getHelper(this);
        String[] projection = {DatabaseInfo.CourseColumn.NAME, DatabaseInfo.CourseColumn.NAME, DatabaseInfo.CourseColumn.ECTS, DatabaseInfo.CourseColumn.GRADE, DatabaseInfo.CourseColumn.PERIOD};
        Cursor rs = dbHelper.query(DatabaseInfo.CourseTables.COURSE, projection, null, null, null, null, null);
        //skip lege elementen die misschien eerst staan.
        rs.moveToFirst();
    }


    public void initChart() {
        mChart = (PieChart) findViewById(R.id.chart);
        mChart.setDescription("Overzicht behaalde EC's");
        DatabaseHelper dbHelper = DatabaseHelper.getHelper(this);
        setData();
    }

    private void setData(){
        ArrayList<Entry> yData = new ArrayList<>();
        ArrayList<String> xData = new ArrayList<>();
        ArrayList<Integer> colours = new ArrayList<>();

        yData.add(new Entry(studiepunten, 0));
        yData.add(new Entry(nietbehaald, 0));
        yData.add(new Entry(nogbehalen, 0));

        xData.add("Behaalde EC");
        xData.add("Niet behaalde EC");
        xData.add("Te behalen EC");

        colours.add(Color.parseColor("#8BC34A"));
        colours.add(Color.parseColor("#F44336"));
        colours.add(Color.parseColor("#9E9E9E"));


        PieDataSet dataSet = new PieDataSet(yData, "ECTS");
        dataSet.setSliceSpace(0);
        dataSet.setSelectionShift(5);
        dataSet.setDrawValues(true);
        dataSet.setColors(colours);
        dataSet.setValueTextSize(15f);
        dataSet.setValueTextColor(Color.DKGRAY);
        PieData data = new PieData(xData, dataSet);

        mChart.setData(data);
        mChart.invalidate();
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
            Intent intent = new Intent (Overzicht.this, Settings.class);
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
