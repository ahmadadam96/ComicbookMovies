package ahmadadam96.comicbookmovies;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.Loader;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity
        implements android.support.v4.app.LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    private static final String TAG = "MainActivity";

    //TextView for the empty state
    private TextView mEmptyStateTextView;

    private RecyclerView movieListView;

    //A swipe to refresh widget
    // private SwipeRefreshLayout mSwipeRefreshLayout;

    //The URL for the JSON string
    private static final String CODE_URL =
            "https://raw.githubusercontent.com/ahmadadam96/ComicbookMovies/master/app/src/main/res/host_codes";

    /**
     * Constant value for the movie loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int MOVIE_LOADER_ID = 1;

    //ArrayList to save all the movie codes
    private ArrayList<MovieCode> codes = new ArrayList<>();

    private ArrayList<Movie> movieList = new ArrayList<>();

    //If the configuration is changed then the data must be reloaded
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        startLoading();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEmptyStateTextView = (TextView) findViewById(R.id.emptyView);
        startLoading();

        /*
 * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
 * performs a swipe-to-refresh gesture.
 */
     /*  mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(TAG, "startLoading called from swipeRefreshLayout");
                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        startLoading();
                    }
                }
        );*/
    }

    private void startLoading() {
        ConnectivityManager connMGR = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connMGR.getActiveNetworkInfo();
        if (activeNetwork == null || !activeNetwork.isConnectedOrConnecting()) {
            mEmptyStateTextView.setText(R.string.no_internet_connection);
            mEmptyStateTextView.setVisibility(VISIBLE);
            ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar);
            progress.setVisibility(GONE);
        } else {
            new getCodesTask().execute();
        }
    }

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
        // Create a new loader for the given URL
        return new MovieLoader(this, codes);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //   mSwipeRefreshLayout.setRefreshing(false);

        progressBar.setVisibility(GONE);

        ConnectivityManager connMGR = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connMGR.getActiveNetworkInfo();

        movieList = data;

        //Got the reference to the view pager
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        //Set the adapter for the view pager
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        //Got the reference for the tabLayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        //Set the tabLayout to belong to the view pager
        tabLayout.setupWithViewPager(viewPager);
        //Maximise the tabs to fill the tabLayout
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        //Set the colors of the text in the tabLayout
        tabLayout.setTabTextColors(Color.LTGRAY, Color.WHITE);
        //Set the color of the tabLayout
        tabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        //Removes the border under the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }
        //Set the reference for the swipe to refresh widget
        //   mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshMain);

        //Find the reference to the empty view
        mEmptyStateTextView = (TextView) findViewById(R.id.emptyView);

        //Set the empty view to GONE
        mEmptyStateTextView.setVisibility(GONE);

        // If there is a valid list of {@link Movie}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (!movieList.isEmpty()) {
            mEmptyStateTextView.setVisibility(GONE);
        }
        else{
            mEmptyStateTextView.setText(R.string.no_movies);
            mEmptyStateTextView.setVisibility(VISIBLE);
        }
        if (activeNetwork == null) {
            mEmptyStateTextView.setText(R.string.no_internet_connection);
            mEmptyStateTextView.setVisibility(VISIBLE);
            movieListView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
    }

    private class getCodesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            codes = QueryUtils.fetchCodes(CODE_URL);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            android.support.v4.app.LoaderManager loaderManager = getSupportLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(MOVIE_LOADER_ID, null, MainActivity.this);
        }
    }

    //Adapter for the view pager in use
    private class MyPagerAdapter extends FragmentPagerAdapter {
        SparseArrayCompat<Fragment> registeredFragments = new SparseArrayCompat<>();

        String page1 = "All";
        String page2 = "MCU";
        String page3 = "DC";
        String page4 = "Fox";

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //Setting the ViewPager tab names
            //For all movies
            if (position == 0) {
                return page1;
            }
            //For MCU movies
            if (position == 1) {
                return page2;
            }
            //For DC movies
            if (position == 2) {
                return page3;
            }
            if (position == 3) {
                return page4;
            }
            //if it is an unknown page (should not happen) then set the title to unknown
            else return "Unknown";
        }

        @Override
        public Fragment getItem(int position) {
            //Passes the input of each fragment in order to filter the movies
            switch (position) {
                //All movies
                case 0:
                    return MainFragment.newInstance(page1, movieList);
                //MCU movies
                case 1:
                    return MainFragment.newInstance(page2, movieList);
                //DC movies
                case 2:
                    return MainFragment.newInstance(page3, movieList);
                //Fox movies
                case 3:
                    return MainFragment.newInstance(page4, movieList);
                default:
                    return null;
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }

        @Override
        //Method to correspond to the number of tabs used
        public int getCount() {
            return 4;
        }
    }
}
