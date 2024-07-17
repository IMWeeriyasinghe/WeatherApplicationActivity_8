package com.example.myapplicationactivity_8;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    String[] date_list = new String[20];
    String[] temp_list = new String[20];
    Integer[] icon_list = new Integer[20];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inflate the header layout and add it to the ListView
        LayoutInflater inflater = getLayoutInflater();
        View headerView = inflater.inflate(R.layout.header_layout, null);
        ListView listView = findViewById(R.id.list_view);
        listView.addHeaderView(headerView);

        FetchOnlineData fetchOnlineData = new FetchOnlineData();
        try {
            fetchOnlineData.execute().get(); // Wait for AsyncTask to complete
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Adjust position to account for the header view
                int adjustedPosition = position - listView.getHeaderViewsCount();
                if (adjustedPosition >= 0 && adjustedPosition < date_list.length) {
                    Toast.makeText(MainActivity.this, "Clicked: " + date_list[adjustedPosition], Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class FetchOnlineData extends AsyncTask<Void, Void, String> {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        StringBuilder forecastJsonStr = new StringBuilder();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Optional: Perform any setup before doInBackground executes
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject weather_object = new JSONObject(s);
                JSONArray data_list = weather_object.getJSONArray("list");
                for (int i = 0; i < data_list.length(); i++) {
                    JSONObject value_object = data_list.getJSONObject(i);
                    date_list[i] = value_object.getString("dt_txt");
                    JSONObject main_object = value_object.getJSONObject("main");
                    temp_list[i] = main_object.getString("temp");
                    JSONArray weather_array = value_object.getJSONArray("weather");
                    JSONObject weather_array_object = weather_array.getJSONObject(0);
                    icon_list[i] = getApplicationContext().getResources().getIdentifier("pic_" +
                                    weather_array_object.getString("icon"), "drawable",
                            getApplicationContext().getPackageName());
                }

                CustomListAdapter adapter = new CustomListAdapter(MainActivity.this, date_list, temp_list, icon_list);
                ListView list = findViewById(R.id.list_view);
                list.setAdapter(adapter);
            } catch (JSONException e) {
                Log.e("JSON Error", "Error parsing JSON", e);
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                final String BASE_URL = "https://api.openweathermap.org/data/2.5/forecast?q=colombo,lk&cnt=20&units=metric&appid=751699e1d8b015830e4de960809775cd";
                URL url = new URL(BASE_URL);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while ((line = reader.readLine()) != null) {
                    forecastJsonStr.append(line).append("\n");
                }

            } catch (IOException e) {
                Log.e("IO Error", "Error fetching data", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    Log.e("IO Error", "Error closing stream", e);
                }
            }
            return forecastJsonStr.toString();
        }
    }
}
