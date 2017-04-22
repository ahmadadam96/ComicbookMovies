/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ahmadadam96.comicbookmovies;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

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
import java.util.Locale;

/**
 * Helper methods related to requesting and receiving movie data
 */
public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query and return a list of {@link Movie} objects.
     */
    public static Movie fetchMovieData(String requestUrl, String universe, Context context) {
        // Create URL object"
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request to get the movie data.", e);
        }

        // Extract relevant fields from the JSON response and return a list of {@link Movie}s
        return extractFeatureFromJson(jsonResponse, universe, context);
    }

    //Query to fetch the codes for the movies
    public static ArrayList<MovieCode> fetchCodes(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request to get the code data.", e);
        }
        ArrayList<MovieCode> movieCodes = new ArrayList<>();
        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);

            // Extract the JSONArray associated with the key called "Object",
            // which represents a list of features (or earthquakes).
            JSONArray codesArray = baseJsonResponse.getJSONArray("Object");

            // For each code in the codeArray, create an {@link Code} object
            for (int i = 0; i < codesArray.length(); i++) {

                // Get a single code at position i within the list of codes
                JSONObject currentCode = codesArray.getJSONObject(i);

                // Extract the value for the key called "Code"
                String code = currentCode.getString("Code");

                // Extract the value for the key called "Universe"
                String universe = currentCode.getString("Universe");

                // Create a new {@link MovieCode} object with the Code and Universe
                // from the JSON response.
                MovieCode movieCode = new MovieCode(code, universe);
                // Add the new {@link Earthquake} to the list of earthquakes.
                movieCodes.add(movieCode);
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the movieCodes JSON results", e);
            return null;
        }
        //Return the list of movie codes
        return movieCodes;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the movie JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
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
     * Return a list of {@link Movie} objects that has been built up from
     * parsing the given JSON response.
     */
    private static Movie extractFeatureFromJson(String movieJSON, String universe, Context context) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(movieJSON)) {
            return null;
        }
        Movie movie;
        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(movieJSON);

            // Extract the value for the key called "homepage"
            String url = baseJsonResponse.getString("homepage");

            // Extract the value for the key called "imdb_id"
            String IMDBId = baseJsonResponse.getString("imdb_id");

            // Extract the value for the key called "title"
            String title = baseJsonResponse.getString("title");

            //Get the date in the country the user is from
            JSONObject dates = baseJsonResponse.getJSONObject("release_dates");
            // Extract the value for the key called "url"
            JSONArray datesArray = dates.getJSONArray("results");

            Locale locale = Locale.getDefault();

            String country = locale.getCountry();

            int indexCountry = 0;

            String countryCode = datesArray.getJSONObject(0).getString("iso_3166_1");
            for (int i = 0; !countryCode.equals(country) && i < datesArray.length(); i++) {
                countryCode = datesArray.getJSONObject(i).getString("iso_3166_1");
                indexCountry = i;
            }
            String date = datesArray.getJSONObject(indexCountry).getJSONArray("release_dates").getJSONObject(0).getString("release_date");

            // Extract the value for the key called "overview"
            String overview = baseJsonResponse.getString("overview");

            // Extract the value for the key called "poster_path"
            String posterURL = baseJsonResponse.getString("poster_path");

            // Create a new {@link Movie} object with the magnitude, location, time,
            // and url from the JSON response.
            movie = new Movie(date, title, overview, posterURL, url, IMDBId, universe);
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the movie JSON results", e);
            return null;
        }
        return movie;
    }
}