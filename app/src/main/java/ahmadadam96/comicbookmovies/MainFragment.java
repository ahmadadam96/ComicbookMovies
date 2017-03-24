package ahmadadam96.comicbookmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment
        implements android.support.v4.app.LoaderManager.LoaderCallbacks<List<Movie>> {

    private static final String TAG = "MainFragment";

    private TextView mEmptyStateTextView;

    private static final String CODE_URL =
            "https://raw.githubusercontent.com/ahmadadam96/ComicbookMovies/master/app/src/main/res/host_codes";

    /**
     * Constant value for the movie loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int MOVIE_LOADER_ID = 1;

    /**
     * Adapter for the list of movies
     */
    private MovieAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    ArrayList<MovieCode> codes = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    View v;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        startLoading();
    }

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.first_fragment, container, false);
        // Find a reference to the {@link ListView} in the layout
        ListView movieListView = (ListView) v.findViewById(R.id.list);

        mEmptyStateTextView = (TextView) v.findViewById(R.id.emptyView);

        movieListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of movies as input
        mAdapter = new MovieAdapter(getContext(), new ArrayList<Movie>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        movieListView.setAdapter(mAdapter);

        startLoading();

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.refreshMain);

        /*
 * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
 * performs a swipe-to-refresh gesture.
 */
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(TAG, "startLoading called from swipeRefreshLayout");
                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        startLoading();
                    }
                }
        );

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open the IMDB page with more information about the selected movie.
        movieListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current movie that was clicked on
                Movie currentMovie = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                assert currentMovie != null;
                Uri movieUri = Uri.parse("http://www.imdb.com/title/" + currentMovie.getIMDBId());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, movieUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });
        return v;
    }

    @Override
    public android.support.v4.content.Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        return new MovieLoader(getContext(), codes);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<List<Movie>> loader, List<Movie> movies) {
        //Clear the adapter of previous movie data
        mAdapter.clear();

        ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        mSwipeRefreshLayout.setRefreshing(false);

        progressBar.setVisibility(GONE);

        ConnectivityManager connMGR = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

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

    private void startLoading() {
        ConnectivityManager connMGR = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connMGR.getActiveNetworkInfo();
        if (activeNetwork == null || !activeNetwork.isConnectedOrConnecting()) {
            mEmptyStateTextView.setText(R.string.no_internet_connection);
            ProgressBar progress = (ProgressBar) v.findViewById(R.id.progressBar);
            progress.setVisibility(GONE);
        } else {
            new getCodesTask().execute();
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<List<Movie>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    private class getCodesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            codes = QueryUtils.fetchCodes(CODE_URL);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            LoaderManager loaderManager = getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(MOVIE_LOADER_ID, null, MainFragment.this);
        }
    }
    public static MainFragment newInstance(String text){
        MainFragment f = new MainFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);
        return f;
    }
/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 * <p>
 * See the Android Training lesson <a href=
 * "http://developer.android.com/training/basics/fragments/communicating.html"
 * >Communicating with Other Fragments</a> for more information.
 */
public interface OnFragmentInteractionListener {
    // TODO: Update argument type and name
    void onFragmentInteraction(Uri uri);
}
}
