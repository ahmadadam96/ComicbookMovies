package ahmadadam96.comicbookmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity
        implements android.support.v4.app.LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    private static final String TAG = "MainActivity";

    //TextView for the empty state
    private TextView mEmptyStateTextView;

    //A swipe to refresh widget
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ActionBar actionBar;

    //Got the reference for the tabLayout
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    //Got the reference to the view pager
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.adViewMain)
    AdView adViewMain;

    //The URL for the JSON string for unreleased movie codes
    private static final String UNRELEASED_URL =
            "https://raw.githubusercontent.com/ahmadadam96/ComicbookMovies/master/app/src/main/res/codes_unreleased";

    //The URL for the JSON string for released movie codes
    private static final String RELEASED_URL =
            "https://raw.githubusercontent.com/ahmadadam96/ComicbookMovies/master/app/src/main/res/codes_released";

    /**
     * Constant value for the movie loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int MOVIE_LOADER_ID = 1;

    //ArrayList to save all the movie codes
    private ArrayList<MovieCode> codes = new ArrayList<>();

    private ArrayList<Movie> movieList = new ArrayList<>();

    private SharedPreferences sharedPref;

    private String releasePreference;

    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;

    private android.support.v4.app.LoaderManager loaderManager = getSupportLoaderManager();

    //If the configuration is changed then the data must be reloaded
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actionBar = getSupportActionBar();
        mEmptyStateTextView = (TextView) findViewById(R.id.emptyView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshMain);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        ButterKnife.bind(this);

        AdRequest adRequest = new AdRequest.Builder().build();
        adViewMain.loadAd(adRequest);

        startLoading();

        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(Settings.RELEASE_KEY)) {
                    ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar);
                    progress.setVisibility(VISIBLE);
                    viewPager.setVisibility(GONE);
                    startLoading();
                }
            }
        };

        sharedPref.registerOnSharedPreferenceChangeListener(prefListener);

 /* Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
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
    }

    private void startLoading() {
        releasePreference = sharedPref.getString(Settings.RELEASE_KEY, "");

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

        mSwipeRefreshLayout.setRefreshing(false);

        progressBar.setVisibility(GONE);

        ConnectivityManager connMGR = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connMGR.getActiveNetworkInfo();

        movieList = data;

        //Set the adapter for the view pager
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                enableDisableSwipeRefresh(state == ViewPager.SCROLL_STATE_IDLE);
            }
        });
        viewPager.getAdapter().notifyDataSetChanged();
        viewPager.setVisibility(VISIBLE);
        //Set the tabLayout to belong to the view pager
        tabLayout.setupWithViewPager(viewPager);
        //Maximise the tabs to fill the tabLayout
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        //Set the colors of the text in the tabLayout
        tabLayout.setTabTextColors(Color.LTGRAY, Color.WHITE);
        //Set the color of the tabLayout
        tabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        //Removes the border under the action bar
        if (actionBar != null) {
            actionBar.setElevation(0);
        }

        //Find the reference to the empty view
        mEmptyStateTextView = (TextView) findViewById(R.id.emptyView);

        //Set the empty view to GONE
        mEmptyStateTextView.setVisibility(GONE);

        // If there is a valid list of {@link Movie}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (!movieList.isEmpty()) {
            mEmptyStateTextView.setVisibility(GONE);
        } else {
            mEmptyStateTextView.setText(R.string.no_movies);
            mEmptyStateTextView.setVisibility(VISIBLE);
        }
        if (activeNetwork == null) {
            mEmptyStateTextView.setText(R.string.no_internet_connection);
            mEmptyStateTextView.setVisibility(VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(MainActivity.this, Settings.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    private void enableDisableSwipeRefresh(boolean enable) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setEnabled(enable);
        }
    }

    private class getCodesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (codes != null) {
                codes.clear();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<MovieCode> tempCodes;
            if (releasePreference.equals("1")) {
                codes = QueryUtils.fetchCodes(UNRELEASED_URL);
            }
            if (releasePreference.equals("0")) {
                codes = QueryUtils.fetchCodes(RELEASED_URL);
            }
            if (releasePreference.equals("-1")) {
                tempCodes = QueryUtils.fetchCodes(UNRELEASED_URL);
                if (tempCodes != null) {
                    codes.addAll(tempCodes);
                }
                tempCodes = QueryUtils.fetchCodes(RELEASED_URL);
                if (tempCodes != null) {
                    codes.addAll(tempCodes);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            //loaderManager.initLoader(MOVIE_LOADER_ID, null, MainActivity.this);
            loaderManager.restartLoader(MOVIE_LOADER_ID, null, MainActivity.this);
        }
    }

    //Adapter for the view pager in use
    private class MyPagerAdapter extends FragmentStatePagerAdapter {
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
        public int getItemPosition(Object object) {
            return POSITION_NONE;
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
