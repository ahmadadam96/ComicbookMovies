package ahmadadam96.comicbookmovies;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahmad on 2017-03-14.
 */

public class MovieLoader extends android.support.v4.content.AsyncTaskLoader<List<Movie>> {
    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = MovieLoader.class.getName();

    /**
     * Query URL
     */
    private ArrayList<String> mUrls;
    private ArrayList<MovieCode> mCodes;

    /**
     * Constructs a new {@link MovieLoader}.
     *
     * @param context of the activity
     * @param codes   to load data from
     */
    public MovieLoader(Context context, ArrayList<MovieCode> codes) {
        super(context);
        mUrls = new ArrayList<>();
        for (int i = 0; i < codes.size(); i++) {
            mUrls.add(codes.get(i).getCode());
        }
        mCodes = codes;
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
        // Perform the network request, parse the response, and extract a list of movies.
        for (int i = 0; i < mUrls.size(); i++) {
            movies.add(QueryUtils.fetchMovieData("https://api.themoviedb.org/3/movie/"
                    + mUrls.get(i) + "?api_key=46ca07ce571803077698160e0a3efde5", mCodes.get(i).getUniverse()));
        }
        return movies;
    }
}
