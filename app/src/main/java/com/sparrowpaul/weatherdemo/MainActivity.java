package com.sparrowpaul.weatherdemo;

import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    // declaring the views
    private EditText txtCity;
    private ImageButton searchIcon;
    private ImageView weatherImage;
    private TextView weatherTemperature, weatherTitle, weatherDescription;
    private TextView day;
    private LinearLayout parentLayout;
    private ProgressBar progressBar;

    // our weather access point
    String url = "http://api.openweathermap.org/data/2.5/weather?appid=6cbee30e3b0163be5df22eda4a538e66&units=metric&q=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // finding the views using function
        initViews();

    } // end of main function our declared function

    private void initViews() {

        // initializing the views using find view by id
        txtCity = findViewById(R.id.txtCity);
        searchIcon = findViewById(R.id.searchIcon);
        weatherImage = findViewById(R.id.weatherImage);
        weatherTemperature = findViewById(R.id.weatherTemperature);
        weatherTitle = findViewById(R.id.weatherTitle);
        weatherDescription = findViewById(R.id.weatherDescription);
        day = findViewById(R.id.day);
        parentLayout = findViewById(R.id.parentLayout);
        progressBar = findViewById(R.id.progressBar);

        // when search icon is clicked
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // retrieve city entered by user
                String city = txtCity.getText().toString();

                // use city to form url
                String newURL = MainActivity.this.url + city;

                // search for weather info from server
                getWeather(newURL);
            }
        });

    }

    private void getWeather(String url) { // function to get weather data

        // show progress bar
        progressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject main_object = response.getJSONObject("main"); // calling it from the open weather json api
                    JSONArray array = response.getJSONArray("weather"); // calling weather elements into an array
                    JSONObject object = array.getJSONObject(0); // getting the first element of the weather array

                    String temperature = String.valueOf(main_object.getDouble("temp")); //converting the double to a string
                    String title = object.getString("main");
                    String description = object.getString("description");//getting the description
                    String city = response.getString("name"); //getting the name of the city

                    // setting them to their respective views
                    weatherTemperature.setText(((int)Double.parseDouble(temperature))+"\u2103");
                    weatherTitle.setText(title);
                    weatherDescription.setText(description);
                    day.setText(getCurrentDay());
                    weatherImage.setImageDrawable(getRightImage(title));
                    getSupportActionBar().setTitle(city);
                } catch (JSONException e) { //catching unexpected error
                    e.printStackTrace();

                } finally {
                    // hide progress bar
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                // handle error: gracefully
                alertWithSnackBar("Could not find weather info for the city you entered, Check your " +
                        " Internet Connection and make sure that the city you entered is valid");
                // hide progress bar
                progressBar.setVisibility(View.GONE);
            }
        }
        );
        //adding json obj to the request queue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);

    }

    // returns the image that best describes the given weather title
    private Drawable getRightImage(String weatherTitle) {

        weatherTitle = weatherTitle.toLowerCase();

        if (weatherTitle.contains("cloud")) {
            return getResources().getDrawable(R.drawable.icon_brokenclouds);
        } else if (weatherTitle.contains("clear")) {
            return getResources().getDrawable(R.drawable.icon_clearsky);
        } else if (weatherTitle.contains("rain")) {
            return getResources().getDrawable(R.drawable.icon_showerrain);
        } else if (weatherTitle.contains("mist")) {
            return getResources().getDrawable(R.drawable.icon_mist);
        } else if (weatherTitle.contains("snow")) {
            return getResources().getDrawable(R.drawable.icon_snow);
        } else if (weatherTitle.contains("thunder")) {
            return getResources().getDrawable(R.drawable.icon_thunderstorm);
        } else {
            return getResources().getDrawable(R.drawable.icon_fewclouds);
        }
    }

    // returns the current day
    private String getCurrentDay() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE-MM-dd");
        String formatted_date = sdf.format(calendar.getTime());

        return formatted_date;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    // alert the given text with a snack bar
    private void alertWithSnackBar(String text) {
        Snackbar.make(parentLayout, text, Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menuExitID) {
            this.finish();
            System.exit(1);
        }

        return super.onOptionsItemSelected(item);
    }
}
