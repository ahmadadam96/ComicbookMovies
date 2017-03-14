package ahmadadam96.comicbookmovies;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Movie>> {

    private static final String TAG = "MainActivity";

    private static final String SAMPLE_REQUEST =
            "https://api.themoviedb.org/3/movie/550?api_key=46ca07ce571803077698160e0a3efde5";

    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int MOVIE_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(MOVIE_LOADER_ID, null, this);
    }
    @Override
    public Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        return new MovieLoader(this, SAMPLE_REQUEST);
    }
    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {

        ConnectivityManager connMGR =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connMGR.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }
    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        // Loader reset, so we can clear out our existing data.
    }
}
