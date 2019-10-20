package ahmadadam96.comicbookmovies

import android.content.Context
import android.widget.Toast

import androidx.loader.content.AsyncTaskLoader

import java.util.ArrayList

/**
 * Created by ahmad on 2017-03-14.
 */

class MovieLoader
/**
 * Constructs a new [MovieLoader].
 *
 * @param context of the activity
 * @param codes   to load data from
 */
(context: Context, codes: ArrayList<MovieCode>) : AsyncTaskLoader<ArrayList<Movie>>(context) {

    /**
     * Query URL
     */
    private val mUrls: ArrayList<String>?
    private val mCodes: ArrayList<MovieCode>

    init {
        mUrls = ArrayList()
        mCodes = ArrayList()
        try {
            for (i in codes.indices) {
                mUrls.add(codes[i].code)
                mCodes.add(i, codes[i])
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
            Toast.makeText(getContext(), "Internet connection not working", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onStartLoading() {
        forceLoad()
    }

    /**
     * This is on a background thread.
     */
    override fun loadInBackground(): ArrayList<Movie>? {
        val movies = ArrayList<Movie>()
        if (mUrls == null) {
            return null
        }
        // Perform the network request, parse the response, and extract a list of movies.
        for (i in mUrls.indices) {
            movies.add(QueryUtils.fetchMovieData("https://api.themoviedb.org/3/movie/"
                    + mUrls[i] + "?api_key=46ca07ce571803077698160e0a3efde5" + "&append_to_response=release_dates",
                    mCodes[i].universe, context))
            if (i == 39) {
                try {
                    Thread.sleep(10000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
        }
        return movies
    }

    companion object {
        /**
         * Tag for log messages
         */
        private val LOG_TAG = MovieLoader::class.java.name
    }
}
