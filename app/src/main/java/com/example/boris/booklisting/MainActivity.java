package com.example.boris.booklisting;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText keyword = (EditText) findViewById(R.id.keyword);
        final EditText maxResult = (EditText) findViewById(R.id.maxresult);
        Button button = (Button) findViewById(R.id.search);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                if (!isConnected) {
                    Toast.makeText(MainActivity.this, "No network now!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String raw = keyword.getText().toString();
                String num = maxResult.getText().toString();
                String url = fromURL(raw, num);
                //Log.v(LOG_TAG, url);
                if (raw.length() > 0) {
                    Intent intent = new Intent(MainActivity.this, ShowBookList.class);
                    intent.putExtra("url", url);
                    startActivity(intent);
                }
            }
        });
    }

    private String fromURL(String raw, String num) {
        String prefix = "https://www.googleapis.com/books/v1/volumes?q=";
        String[] s = raw.split(" ");
        int count = 0;
        for (int i = 0; i < s.length; i++) {
            if (s[i].length() > 0) {
                if (count == 0) {
                    prefix += s[i];
                    count++;
                } else {
                    prefix += "+";
                    prefix += s[i];
                    count++;
                }
            }
        }

        if (num.length() > 0) {
            prefix += "&";
            prefix += "maxResults=";
            prefix += num;
        }
        return prefix;
    }

}
