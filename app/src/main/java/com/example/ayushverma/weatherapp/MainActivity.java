package com.example.ayushverma.weatherapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private String url;
    private Download download;
    private String result;
    private JSONObject jsonObject;

    public class Download extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            String result = "";
            URL url;
            HttpURLConnection httpURLConnection;

            try {

                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {

                    char c = (char) data;
                    result += c;
                    data = reader.read();

                }

                return result;

            } catch (MalformedURLException e) {
                Log.i("Exception","MalformedURLException");
                e.printStackTrace();
            } catch (IOException e) {
                Log.i("Exception","IOException");
                e.printStackTrace();
            }

            return null;
        }
    }

    public void tapped(View view) {

        EditText editText = findViewById(R.id.editText);
        String city = editText.getText().toString();

        boolean flag=false;

        if (city.equals("")) {
            Toast.makeText(this, "Enter City Name", Toast.LENGTH_SHORT).show();
        } else {

            download = new Download();
            url = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=787f977c90948beef277805e62e3a315";


            try {
                result = download.execute(url).get();
                if(result!=null) {
                    Log.i("Returned","Not Null");
                    jsonObject = null;
                    jsonObject = new JSONObject(result);
                }else {
                    Log.i("Returned","Null");
                    flag=true;
                }
            } catch (InterruptedException e) {
                Log.i("Exception","InterruptedException");
                e.printStackTrace();
            } catch (ExecutionException e) {
                Log.i("Exception","ExecutionException");
                e.printStackTrace();
            } catch (JSONException e) {
                Log.i("Exception","JSONException");
                e.printStackTrace();
            }

            if (flag) {
                Log.i("Not Found",city);
                Toast.makeText(this, "Sorry! City Not Found", Toast.LENGTH_SHORT).show();
            } else {

                TextView textView = findViewById(R.id.textView);
                textView.setText("Coordinates: " + data("coord"));

                TextView textView1 = findViewById(R.id.textView1);
                textView1.setText("Weather: " + data("weather"));

                TextView textView2 = findViewById(R.id.textView2);
                String s = data("main");
                Double kelvin = Double.parseDouble(s);
                Double celsius = kelvin - 273.15;
                DecimalFormat df = new DecimalFormat("#.##");
                textView2.setText("Temperature: " + df.format(celsius) + "*C");

            }
        }
    }

    public void feedback(View view){

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto: business.ayushverma@gmail.com")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, "Regarding Weather App");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public String data(String part) {

        try {

            String s = "", s1 = "", s2 = "";
            String str = jsonObject.getString(part);
            if (part.equalsIgnoreCase("coord")) {

                Log.i("Entered Coord", "Coord");
                JSONObject jsonObject1 = new JSONObject(str);
                s = jsonObject1.getString("lon");
                s1 = jsonObject1.getString("lat");
                s2 = "lon " + s + " " + " lat " + s1;
                Log.i("Coord", s2);

            } else if (part.equalsIgnoreCase("weather")) {

                Log.i("Entered Weather", "Weather");
                JSONArray jsonArray = new JSONArray(str);
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                    s = jsonObject2.getString("main");
                    s2 = s;
                    Log.i("Weather", s2);
                }
            } else {

                JSONObject jsonObject1 = new JSONObject(str);
                s = jsonObject1.getString("temp");
                s2 = s;

            }
//            }

            return s2;


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}
