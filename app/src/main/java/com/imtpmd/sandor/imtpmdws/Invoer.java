package com.imtpmd.sandor.imtpmdws;


import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;


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


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sandor on 21-3-2016.
 */
public class Invoer extends AppCompatActivity {

    private ProgressDialog pDialog;
    RequestQueue requestQueue;


    // json array response urlhttp://www.nieuwemaker.nl/madvise/index.php?view=JSON&action=advise
    //link en de data is:index.php?action=comparemethod&data=JAD&results=8&weight=11102
    private String urldata = "http://www.fuujokan.nl/subject_lijst.json";
    private static String TAG = Invoer.class.getSimpleName();




    //list
    private ListView mListView;
    private com.imtpmd.sandor.imtpmdws.VakListAdapter mAdapter;
    private List<Course> courseModels = new ArrayList<>();   //data komt hierin

    //array voor de list en de items
    private static final String TAG_METHOD = "method";
    private static final String TAG_SCORE = "score";
    private static final String TAG_WEIGHT = "weight";
    private static final String TAG_ENDSCORE = "endscore";
    //JSON Array verstuur


    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //dialog tijdens het ophalen van de data
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);


        makeJsonArrayRequest();
        requestQueue = Volley.newRequestQueue(getApplicationContext());

    }




    /**
     * Method to make json array request where response starts with [
     */
    private void makeJsonArrayRequest()
    {
        showpDialog();


        JsonArrayRequest req = new JsonArrayRequest(urldata,
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

                                JSONObject methode = (JSONObject) response
                                        .get(i);

                                String vak = methode.getString("name");
                                String ects = methode.getString("ects");
                                String grade = methode.getString("grade");
                                String period = methode.getString("period");

                                //zet ze in de de lijst

                                courseModels.add(new Course(vak,ects,grade,period));
                                Log.d("method", vak);
                                Log.d("Score", ects);
                                Log.d("weight", grade);
                                Log.d("endscore", period);

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
        Log.d("opsturen json","opsturen");
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