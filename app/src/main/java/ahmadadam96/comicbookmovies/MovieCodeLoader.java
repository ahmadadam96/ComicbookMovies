package ahmadadam96.comicbookmovies;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahmad on 2017-03-19.
 */

public class MovieCodeLoader extends AsyncTaskLoader<List<MovieCode>> {
    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = MovieCodeLoader.class.getName();

    private String mUrl;

    public MovieCodeLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public ArrayList<MovieCode> loadInBackground() {
        ArrayList<MovieCode> codes = new ArrayList<>();
        if (mUrl == null) {
            return null;
        }
        //Perform the network request, parse the response, and extract a list of movies.
        codes = QueryUtils.fetchCodes(mUrl);
        return codes;
    }
}
