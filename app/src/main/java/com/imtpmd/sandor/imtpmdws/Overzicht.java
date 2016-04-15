package com.imtpmd.sandor.imtpmdws;

        import android.content.Intent;
        import android.graphics.Color;
        import android.os.Bundle;
        import android.support.design.widget.NavigationView;
        import android.support.v4.view.GravityCompat;
        import android.support.v4.widget.DrawerLayout;
        import android.support.v7.app.ActionBarDrawerToggle;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.widget.RelativeLayout;

        import com.github.mikephil.charting.charts.PieChart;
        import com.github.mikephil.charting.components.Legend;
        import com.github.mikephil.charting.data.Entry;
        import com.github.mikephil.charting.data.PieData;
        import com.github.mikephil.charting.data.PieDataSet;
        import com.imtpmd.sandor.imtpmdws.Database.DatabaseHelper;

        import java.util.ArrayList;

/**
 * Created by Wendy on 22-3-2016.
 */
public class Overzicht extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RelativeLayout mainLayout;
    private PieChart mChart;
    int TAG_BEHAALD;
    int TAG_NIET_BEHAALD;
    int TAG_UNKNOWN;

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

        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        mChart = new PieChart(this);

        //add pie chart naar pie layout
        mainLayout.addView(mChart);

        //enable hole
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleRadius(0);

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

        yData.add(new Entry(30, 0));
        yData.add(new Entry(10, 0));
        yData.add(new Entry(20, 0));

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
