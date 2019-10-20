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
package ahmadadam96.comicbookmovies

import android.content.Context
import android.text.TextUtils
import android.util.Log

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset
import java.util.ArrayList
import java.util.Locale

/**
 * Helper methods related to requesting and receiving movie data
 */
object QueryUtils {

    /**
     * Tag for the log messages
     */
    private val LOG_TAG = QueryUtils::class.java.simpleName

    /**
     * Query and return a list of [Movie] objects.
     */
    fun fetchMovieData(requestUrl: String, universe: String, context: Context): Movie {
        // Create URL object"
        val url = createUrl(requestUrl)

        // Perform HTTP request to the URL and receive a JSON response back
        var jsonResponse: String? = null
        try {
            jsonResponse = makeHttpRequest(url)
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Problem making the HTTP request to get the movie data.", e)
        }

        // Extract relevant fields from the JSON response and return a list of {@link Movie}s
        return extractFeatureFromJson(jsonResponse, universe, context)
    }

    //Query to fetch the codes for the movies
    fun fetchCodes(requestUrl: String): ArrayList<MovieCode> {
        // Create URL object
        val url = createUrl(requestUrl)
        // Perform HTTP request to the URL and receive a JSON response back
        var jsonResponse: String? = null
        try {
            jsonResponse = makeHttpRequest(url)
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Problem making the HTTP request to get the code data.", e)
        }

        val movieCodes = ArrayList<MovieCode>()
        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            val baseJsonResponse = JSONObject(jsonResponse!!)

            // Extract the JSONArray associated with the key called "Object",
            // which represents a list of features (or earthquakes).
            val codesArray = baseJsonResponse.getJSONArray("Object")

            // For each code in the codeArray, create an {@link Code} object
            for (i in 0 until codesArray.length()) {

                // Get a single code at position i within the list of codes
                val currentCode = codesArray.getJSONObject(i)

                // Extract the value for the key called "Code"
                val code = currentCode.getString("Code")

                // Extract the value for the key called "Universe"
                val universe = currentCode.getString("Universe")

                // Create a new {@link MovieCode} object with the Code and Universe
                // from the JSON response.
                val movieCode = MovieCode(code, universe)
                // Add the new {@link Earthquake} to the list of earthquakes.
                movieCodes.add(movieCode)
            }
        } catch (e: JSONException) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the movieCodes JSON results", e)
            return movieCodes
        }

        //Return the list of movie codes
        return movieCodes
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private fun createUrl(stringUrl: String): URL? {
        var url: URL? = null
        try {
            url = URL(stringUrl)
        } catch (e: MalformedURLException) {
            Log.e(LOG_TAG, "Problem building the URL ", e)
        }

        return url
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    @Throws(IOException::class)
    private fun makeHttpRequest(url: URL?): String {
        var jsonResponse = ""

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse
        }

        var urlConnection: HttpURLConnection? = null
        var inputStream: InputStream? = null
        try {
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.readTimeout = 10000
            urlConnection.connectTimeout = 15000
            urlConnection.requestMethod = "GET"
            urlConnection.connect()

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.responseCode == 200) {
                inputStream = urlConnection.inputStream
                jsonResponse = readFromStream(inputStream)
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.responseCode)
            }
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Problem retrieving the movie JSON results.", e)
        } finally {
            urlConnection?.disconnect()
            inputStream?.close()
        }
        return jsonResponse
    }

    /**
     * Convert the [InputStream] into a String which contains the
     * whole JSON response from the server.
     */
    @Throws(IOException::class)
    private fun readFromStream(inputStream: InputStream?): String {
        val output = StringBuilder()
        if (inputStream != null) {
            val inputStreamReader = InputStreamReader(inputStream, Charset.forName("UTF-8"))
            val reader = BufferedReader(inputStreamReader)
            var line: String? = reader.readLine()
            while (line != null) {
                output.append(line)
                line = reader.readLine()
            }
        }
        return output.toString()
    }

    /**
     * Return a list of [Movie] objects that has been built up from
     * parsing the given JSON response.
     */
    private fun extractFeatureFromJson(movieJSON: String?, universe: String, context: Context): Movie {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(movieJSON)) {
            return Movie()
        }
        val movie: Movie
        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            val baseJsonResponse = JSONObject(movieJSON!!)

            // Extract the value for the key called "homepage"
            val url = baseJsonResponse.getString("homepage")

            // Extract the value for the key called "imdb_id"
            val IMDBId = baseJsonResponse.getString("imdb_id")

            // Extract the value for the key called "title"
            val title = baseJsonResponse.getString("title")

            //Get the date in the country the user is from
            val dates = baseJsonResponse.getJSONObject("release_dates")
            // Extract the value for the key called "url"
            val datesArray = dates.getJSONArray("results")

            val locale = Locale.getDefault()

            val country = locale.country

            var indexCountry = 0

            var countryCode = datesArray.getJSONObject(0).getString("iso_3166_1")
            var i = 0
            while (countryCode != country && i < datesArray.length()) {
                countryCode = datesArray.getJSONObject(i).getString("iso_3166_1")
                indexCountry = i
                i++
            }
            val date = datesArray.getJSONObject(indexCountry).getJSONArray("release_dates").getJSONObject(0).getString("release_date")

            // Extract the value for the key called "overview"
            val overview = baseJsonResponse.getString("overview")

            // Extract the value for the key called "poster_path"
            val posterURL = baseJsonResponse.getString("poster_path")

            // Create a new {@link Movie} object with the magnitude, location, time,
            // and url from the JSON response.
            movie = Movie(date, title, overview, posterURL, url, IMDBId, universe)
        } catch (e: JSONException) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the movie JSON results", e)
            return Movie()
        }

        return movie
    }
}
/**
 * Create a private constructor because no one should ever create a [QueryUtils] object.
 * This class is only meant to hold static variables and methods, which can be accessed
 * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
 */