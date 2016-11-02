package com.example.boris.booklisting;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class ShowBookList extends AppCompatActivity {

    public static final String LOG_TAG = ShowBookList.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);
        String url = getIntent().getStringExtra("url");
        //Log.v(LOG_TAG, url);
        BookListAsyncTask bookListAsyncTask = new BookListAsyncTask(url);
        bookListAsyncTask.execute();
    }

    private class BookListAsyncTask extends AsyncTask<URL, Void, ArrayList<Book>> {
        String urlString = "";
        public BookListAsyncTask(String url) {
            super();
            this.urlString = url;
        }
        @Override
        protected ArrayList<Book> doInBackground(URL... urls) {
            // Create URL object
            URL url = createUrl(urlString);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                // TODO Handle the IOException
                Log.e("IOException", e.toString());
            }
            return extractFeatureFromJson(jsonResponse);
        }

        @Override
        protected void onPostExecute(final ArrayList<Book> books) {
            /*for (Book b: books) {
                Log.v(LOG_TAG+" after ", b.toString());
            }*/

            ListView listView = (ListView) findViewById(R.id.list);
            listView.setEmptyView(findViewById(R.id.empty));

            BookListAdapter bookListAdapter = new BookListAdapter(ShowBookList.this, books);
            listView.setAdapter(bookListAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String url = books.get(i).getUrl();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
            });
        }

        /**
         * Returns new URL object from the given string URL.
         */
        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }

        /**
         * Make an HTTP request to the given URL and return a String as the response.
         */
        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();
                int responseCode = urlConnection.getResponseCode();
                //Log.v(LOG_TAG, "response code is "+responseCode);
                if (responseCode == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                }
                else {
                    Log.e(LOG_TAG, "status code is not 200!");
                }
            } catch (IOException e) {
                // TODO: Handle the exception
                Log.e(LOG_TAG, e.toString());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }
            Log.v(LOG_TAG, jsonResponse);
            return jsonResponse;
        }

        /**
         * Convert the {@link InputStream} into a String which contains the
         * whole JSON response from the server.
         */
        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        /**
         * Return an {@link Event} object by parsing out information
         * about the first earthquake from the input earthquakeJSON string.
         */
        private ArrayList<Book> extractFeatureFromJson(String earthquakeJSON) {
            ArrayList<Book> books = new ArrayList<Book>();
            if (earthquakeJSON.isEmpty()) {
                return books;
            }
            try {
                JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);
                JSONArray featureArray = baseJsonResponse.getJSONArray("items");
                Log.v(LOG_TAG+" print array: ", featureArray.toString());
                // If there are results in the features array
                if (featureArray.length() > 0) {
                    // Extract out the first feature (which is an earthquake)
                    for (int i = 0; i < featureArray.length(); i++) {
                        JSONObject eachItem = featureArray.getJSONObject(i);
                        String title = new String();
                        String author = new String();
                        String date = new String();
                        String bookURL = new String();
                        //parsing title, if empty, then empty string
                        if (eachItem.has("volumeInfo") && eachItem.getJSONObject("volumeInfo").has("title")) {
                            title = eachItem.getJSONObject("volumeInfo").getString("title");
                        }

                        //parsing authors
                        if (eachItem.has("volumeInfo") && eachItem.getJSONObject("volumeInfo").has("authors")) {
                            JSONArray authorList = eachItem.getJSONObject("volumeInfo").getJSONArray("authors");
                            for (int j = 0; j < authorList.length(); j++) {
                                author += authorList.getString(j);
                                if (j != authorList.length() - 1) {
                                    author += ", ";
                                }
                            }
                        }

                        //parsing published date
                        if (eachItem.has("volumeInfo") && eachItem.getJSONObject("volumeInfo").has("publishedDate")) {
                            date = eachItem.getJSONObject("volumeInfo").getString("publishedDate");
                        }

                        //parsing URL of the book
                        if (eachItem.has("accessInfo") && eachItem.getJSONObject("accessInfo").has("webReaderLink")) {
                            bookURL = eachItem.getJSONObject("accessInfo").getString("webReaderLink");
                        }

                        Book book = new Book(title, author, date, bookURL);
                        //Log.v(LOG_TAG+" "+i, book.toString());
                        books.add(book);
                    }
                    // Create a new {@link Event} object
                    return books;
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
            }
            return books;
        }
    }
}
