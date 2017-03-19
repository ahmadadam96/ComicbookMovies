package ahmadadam96.comicbookmovies;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahmad on 2017-03-14.
 */

public class MovieLoader extends AsyncTaskLoader<List<Movie>> {
    /** Tag for log messages */
    private static final String LOG_TAG = MovieLoader.class.getName();

    /** Query URL */
    private ArrayList<String> mUrls;

    /**
     * Constructs a new {@link MovieLoader}.
     *
     * @param context of the activity
     * @param urls to load data from
     */
    public MovieLoader(Context context, ArrayList<String> urls) {
        super(context);
        mUrls = urls;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public ArrayList<Movie> loadInBackground() {
        ArrayList<Movie> movies = new ArrayList<>();
        if (mUrls == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of earthquakes.
        for(int i = 0; i < mUrls.size(); i++){
            movies.add(QueryUtils.fetchMovieData("https://api.themoviedb.org/3/movie/"
                    + mUrls.get(i) + "?api_key=46ca07ce571803077698160e0a3efde5"));
        }
        return movies;
    }
}
