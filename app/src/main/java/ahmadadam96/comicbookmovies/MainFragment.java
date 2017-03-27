package ahmadadam96.comicbookmovies;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    //TextView for the empty state
    private TextView mEmptyStateTextView;

    //String to show which universe the movie belongs to which allows for filtering
    private String mUniverse;

    //The URL for the JSON string
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

    //A swipe to refresh widget
    private SwipeRefreshLayout mSwipeRefreshLayout;

    //ArrayList to save all the movie codes
    ArrayList<MovieCode> codes = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    private RecyclerView movieListView;

    View view;

    //If the configuration is changed then the data must be reloaded
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
        view = inflater.inflate(R.layout.first_fragment, container, false);

        //Gets the arguments from the MainActivity
        Bundle args = getArguments();
        //Sets the universe to the universe defined in the MainActivity to allow filtering
        mUniverse = args.getString("Universe");

        // Find a reference to the {@link ListView} in the layout
        movieListView = (RecyclerView) view.findViewById(R.id.list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        movieListView.setLayoutManager(layoutManager);

        //Find the reference to the empty view
        mEmptyStateTextView = (TextView) view.findViewById(R.id.emptyView);

        mEmptyStateTextView.setVisibility(GONE);

        //Set the empty view for the list view
        //TODO fix the empty view
        // movieListView.setEmptyView(mEmptyStateTextView);

        //Begin loading the data
        startLoading();

        //Set the reference for the swipe to refresh widget
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshMain);

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
        return view;
    }
    private void setAdapter(List<Movie> movies){
        // Create a new adapter that takes an empty list of movies as input
        mAdapter = new MovieAdapter(getContext(),R.layout.movie_adapter, movies);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        movieListView.setAdapter(mAdapter);
    }

    @Override
    public android.support.v4.content.Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        return new MovieLoader(getContext(), codes);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<List<Movie>> loader, List<Movie> movies) {
        //Clear the adapter of previous movie data
        if(mAdapter != null){
            mAdapter.clear();
        }

        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        mSwipeRefreshLayout.setRefreshing(false);

        progressBar.setVisibility(GONE);

        ConnectivityManager connMGR = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connMGR.getActiveNetworkInfo();

        for (int i = 0; i < movies.size(); i++) {
            if (!(movies.get(i).getUniverse().equals(mUniverse) ||
                    mUniverse.equals("All"))) {
                movies.remove(i);
            }
        }

        // If there is a valid list of {@link Movie}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (movies != null && !movies.isEmpty()) {
            setAdapter(movies);
        } else {
            mEmptyStateTextView.setText(R.string.no_movies);
            mEmptyStateTextView.setVisibility(View.VISIBLE);
        }
        if (activeNetwork == null) {
            mEmptyStateTextView.setText(R.string.no_internet_connection);
            mEmptyStateTextView.setVisibility(View.VISIBLE);
        }
    }

    private void startLoading() {
        ConnectivityManager connMGR = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connMGR.getActiveNetworkInfo();
        if (activeNetwork == null || !activeNetwork.isConnectedOrConnecting()) {
            mEmptyStateTextView.setText(R.string.no_internet_connection);
            ProgressBar progress = (ProgressBar) view.findViewById(R.id.progressBar);
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

    public static MainFragment newInstance(String universe) {
        MainFragment f = new MainFragment();
        Bundle b = new Bundle();
        b.putString("Universe", universe);
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
