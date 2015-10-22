package com.erikmafo.weatherapp;

import android.location.Location;;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class CurrentWeatherActivity extends AppCompatActivity
        implements LocationFragment.OnLocationUpdateListener {

    private final static String TAG = CurrentWeatherActivity.class.getSimpleName();

    static final String API_KEY = BuildConfig.API_KEY;
    static final String HOST_URL = "https://api.forecast.io/forecast";
    static final String BASE_URL = HOST_URL + "/" + API_KEY;

    private Location mCurrentLocation;
    private final Object mCurrentLocationLock = new Object();

    private TextView mTemperatureView;
    private TextView mApparentTemperatureView;
    private TextView mDailySummary;
    private ImageView mIconView;
    private TextView mPercipProbability;


    private Button mUpdateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_weather);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTemperatureView = (TextView) findViewById(R.id.temperature);
        mApparentTemperatureView = (TextView) findViewById(R.id.apparentTemperature);
        mDailySummary = (TextView) findViewById(R.id.dailySummary);
        mIconView = (ImageView) findViewById(R.id.iconImage);
        mPercipProbability = (TextView) findViewById(R.id.precipProbability);

        mUpdateButton = (Button) findViewById(R.id.updateButton);


        mUpdateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                updateWeatherData();

            }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_current_weather, menu);
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


    private void updateWeatherData() {
        new UpdateWeatherDataTask().execute();
    }


    @Override
    public void onLocationUpdate(final Location location) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                synchronized (mCurrentLocationLock) {
                    mCurrentLocation = location;
                    mCurrentLocationLock.notifyAll();
                }

                updateWeatherData();

            }
        }).start();

    }


    private class UpdateWeatherDataTask extends AsyncTask<Void, Integer, Forecast> {


        @Override
        protected Forecast doInBackground(Void... params) {

            Location location = waitForLocation();

            Forecast forecast = null;
            HttpURLConnection connection = null;

            try {

                URL url = new URL(getUrlString(location));
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                String weatherDataString = readStream(inputStream);
                forecast = parseJson(new JSONObject(weatherDataString));

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

            return forecast;
        }

        Forecast parseJson(JSONObject json) throws JSONException {

            CurrentForecast currentForecast = getCurrentForecast(json);

            Forecast forecast =  new Forecast.Builder()
                    .setCurrentForecast(currentForecast)
                    .build();

            return forecast;

        }


        CurrentForecast getCurrentForecast(JSONObject json) throws JSONException {

            JSONObject currently = json.getJSONObject("currently");

            CurrentForecast currentForecast = new CurrentForecast.Builder()
                    .setTemperature(currently.getDouble("temperature"))
                    .setApparentTemperature(currently.getDouble("apparentTemperature"))
                    .setIcon(currently.getString("icon"))
                    .setSummary(currently.getString("summary"))
                    .setPrecipProbability(currently.getDouble("precipProbability"))
                    .build();

            return currentForecast;
        }


        Location waitForLocation() {

            Location location = null;

            synchronized (mCurrentLocationLock) {
                while (mCurrentLocation == null) {
                    try {
                        mCurrentLocationLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                location = mCurrentLocation;
            }

            return location;
        }

        @Override
        protected void onPostExecute(Forecast forecast) {

            CurrentForecast currentForecast = forecast.getCurrentForecast();
            mDailySummary.setText(currentForecast.getSummary());
            mTemperatureView.setText("" + (int) currentForecast.getTemperature() + "\u00B0");
            mIconView.setImageDrawable(Forecast
                    .getIconDrawable(CurrentWeatherActivity.this, currentForecast.getIcon()));
            mApparentTemperatureView.setText("" + (int) currentForecast.getApparentTemperature() + "\u00B0");
            mPercipProbability.setText("" + (int) currentForecast.getPrecipProbability() + "%");

        }

        String getUrlString(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLatitude();
            return BASE_URL + "/" + latitude + "," + longitude;

        }

        String readStream(InputStream in) {

            StringBuffer data = new StringBuffer("");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                String line = "";
                while ((line = reader.readLine()) != null) {
                    data.append(line);
                }
            } catch (IOException e) {
                Log.e(TAG, "IOException");
            }
            return data.toString();
        }


    }


}
