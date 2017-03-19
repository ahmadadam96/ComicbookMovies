package ahmadadam96.comicbookmovies;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Movie>>{

    private static final String TAG = "MainActivity";

    private TextView mEmptyStateTextView;

    private static final String SAMPLE_REQUEST1 =
            "299536";

    private static final String SAMPLE_REQUEST2 =
            "299537";

    private static final String CODE_URL =
            "https://api.myjson.com/bins/pn6o3";

    /**
     * Constant value for the movie loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int MOVIE_LOADER_ID = 1;
    private static final int CODES_LOADER_ID = 2;

    /**
     * Adapter for the list of movies
     */
    private MovieAdapter mAdapter;

    ArrayList<MovieCode> codes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the {@link ListView} in the layout
        ListView movieListView = (ListView) findViewById(R.id.list);

        mEmptyStateTextView = (TextView) findViewById(R.id.emptyView);

        movieListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of movies as input
        mAdapter = new MovieAdapter(this, new ArrayList<Movie>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        movieListView.setAdapter(mAdapter);

        new getCodesTask().execute();

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open the IMDB page with more information about the selected movie.
        movieListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current movie that was clicked on
                Movie currentMovie = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri movieUri = Uri.parse("http://www.imdb.com/title/" + currentMovie.getIMDBId());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, movieUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {
        ArrayList<String> urls = new ArrayList<>();
       for(int index = 0; index < codes.size(); index++){
           urls.add(codes.get(index).getCode());
       }
        // Create a new loader for the given URL
        return new MovieLoader(this, urls);
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {
        //Clear the adapter of previous movie data
        mAdapter.clear();

        ConnectivityManager connMGR = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connMGR.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        // If there is a valid list of {@link Movie}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (movies != null && !movies.isEmpty()) {
            mAdapter.addAll(movies);
        } else {
            mEmptyStateTextView.setText(R.string.no_movies);
        }
        if (activeNetwork == null) {
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }
    private class getCodesTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {
            codes = QueryUtils.fetchCodes(CODE_URL);
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(MOVIE_LOADER_ID, null, MainActivity.this);
            loaderManager.initLoader(CODES_LOADER_ID, null, MainActivity.this);
        }
    }
}
