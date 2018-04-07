package com.example.jaume.activity5b;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private static final String IMAGE_URL = "https://s.inyourpocket.com/gallery/113383.jpg";
    private static final String WEB_URL = "https://www.facebook.com/";
    Button buttonWeb;
    Button buttonImage;
    TextView textView;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonWeb = findViewById(R.id.buttonWeb);
        buttonImage = findViewById(R.id.buttonImage);
        textView = findViewById(R.id.webText);
        imageView = findViewById(R.id.image);

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()){
            buttonWeb.setEnabled(true);
            buttonImage.setEnabled(true);
        }

        Log.d("SHIT","BeforeClickListener");

        buttonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SHIT","OnClick");
                new DownloadImageTask().execute(IMAGE_URL);
            }
        });
        
        buttonWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadWebPageTask().execute(WEB_URL);
            }
        });
    }

    private class DownloadWebPageTask extends AsyncTask<String, Void, String > {

        @Override
        protected String doInBackground(String... urls){
            return downloadPage(urls[0]);
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            textView.setText(result);
        }

        private String downloadPage(String url) {
            InputStream is = null;
            int len = 500;
            String result = "";

            try {
                URL myUrl = new URL(url);
                HttpsURLConnection connection = (HttpsURLConnection) myUrl.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                connection.connect();
                connection.getResponseCode();
                is = connection.getInputStream();
                result = convertStreamToString(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        private String convertStreamToString(InputStream is) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            Log.d("SHIT","Background");
            return downloadImage(urls[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            Log.d("SHIT","onPostExecute");
            imageView.setImageBitmap(bitmap);
        }

        private Bitmap downloadImage(String url) {
            Log.d("SHIT","Download");
            Bitmap bitmap = null;
            InputStream inputStream = null;

            try {
                inputStream = OpenHttpGETConnection(url);
                bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            } catch (Exception e) {
                Log.d("DownloadError",e.getLocalizedMessage());
            }
            return bitmap;
        }

        private InputStream OpenHttpGETConnection(String url) {
            InputStream is = null;
            int len = 500;

            try {
                URL url1 = new URL(url);
                HttpsURLConnection connection = (HttpsURLConnection) url1.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                connection.connect();
                int response = connection.getResponseCode();
                Log.d("Connection Response", String.valueOf(response));
                is = connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return is;
        }
    }
}
