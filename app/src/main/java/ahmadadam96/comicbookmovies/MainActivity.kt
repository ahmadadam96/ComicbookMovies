package ahmadadam96.comicbookmovies

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import android.os.Bundle
import android.os.Parcelable
import android.preference.PreferenceManager
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.core.content.ContextCompat
import androidx.loader.content.Loader
import androidx.viewpager.widget.ViewPager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

import java.lang.reflect.Array
import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife

import android.view.View.GONE
import android.view.View.SCROLLBAR_POSITION_DEFAULT
import android.view.View.VISIBLE
import androidx.loader.app.LoaderManager

class MainActivity : AppCompatActivity(), androidx.loader.app.LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    //TextView for the empty state
    private var mEmptyStateTextView: TextView? = null

    //A swipe to refresh widget
    private var mSwipeRefreshLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout? = null

    private var actionBar: ActionBar? = null

    private var pagePosition: Int = 0

    //Got the reference for the tabLayout
    //@BindView(R.id.tabLayout)
    internal var tabLayout: TabLayout? = null

    //Got the reference to the view pager
    //@BindView(R.id.viewPager)
    internal var viewPager: androidx.viewpager.widget.ViewPager?= null

    //@BindView(R.id.adViewMain)
    internal var adViewMain: AdView?= null

    //ArrayList to save all the movie codes
    private var codes: ArrayList<MovieCode> = ArrayList()

    private var movieList = ArrayList<Movie>()

    private var sharedPref: SharedPreferences? = null

    private var releasePreference: String? = null

    private var prefListener: SharedPreferences.OnSharedPreferenceChangeListener? = null

    private var loaderManager: androidx.loader.app.LoaderManager? = null

    private var viewPagerAdapter: MyPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        adViewMain = findViewById(R.id.adViewMain)

        loaderManager = LoaderManager.getInstance(this)
        actionBar = supportActionBar
        mEmptyStateTextView = findViewById(R.id.emptyView)
        mSwipeRefreshLayout = findViewById(R.id.refreshMain)
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false) //gets default settings and preferences
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        ButterKnife.bind(this)

        startLoading()

        val adRequest = AdRequest.Builder().build()
        adViewMain
        adViewMain!!.loadAd(adRequest)

        if (sharedPref!!.getBoolean(Settings.AD_SWITCH_KEY, true)) {
            adViewMain!!.visibility = VISIBLE
        } else
            adViewMain!!.visibility = GONE

        prefListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == Settings.RELEASE_KEY) {
                val progress = findViewById<ProgressBar>(R.id.progressBar)
                progress.visibility = VISIBLE
                viewPager!!.visibility = GONE
                startLoading()
            }
            if (key == Settings.AD_SWITCH_KEY) {
                if (sharedPreferences.getBoolean(Settings.AD_SWITCH_KEY, true)) {
                    adViewMain!!.visibility = VISIBLE
                } else
                    adViewMain!!.visibility = GONE
            }
        }

        sharedPref!!.registerOnSharedPreferenceChangeListener(prefListener)

        /* Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        mSwipeRefreshLayout!!.setOnRefreshListener {
            Log.i(TAG, "startLoading called from swipeRefreshLayout")
            // This method performs the actual data-refresh operation.
            // The method calls setRefreshing(false) when it's finished.
            startLoading()
        }
    }

    private fun startLoading() {
        releasePreference = sharedPref!!.getString(Settings.RELEASE_KEY, "")

        val connMGR = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connMGR != null) {
            val activeNetwork = connMGR.activeNetworkInfo
            if (activeNetwork == null || !activeNetwork.isConnected) {
                mEmptyStateTextView!!.setText(R.string.no_internet_connection)
                mEmptyStateTextView!!.visibility = VISIBLE
                val progress = findViewById<ProgressBar>(R.id.progressBar)
                progress.visibility = GONE
            } else {
                getCodesTask().execute()
            }
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): androidx.loader.content.Loader<ArrayList<Movie>> {
        // Create a new loader for the given URL
        return MovieLoader(this, codes)
    }

    override fun onLoadFinished(loader: androidx.loader.content.Loader<ArrayList<Movie>>, data: ArrayList<Movie>) {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        mSwipeRefreshLayout!!.isRefreshing = false

        progressBar.visibility = GONE

        val connMGR = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = connMGR.activeNetworkInfo

        movieList = data

        viewPagerAdapter = MyPagerAdapter(supportFragmentManager)

        //Set the adapter for the view pager
        viewPager!!.adapter = viewPagerAdapter

        viewPager!!.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, v: Float, i1: Int) {}

            override fun onPageSelected(position: Int) {
                pagePosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {
                enableDisableSwipeRefresh(state == androidx.viewpager.widget.ViewPager.SCROLL_STATE_IDLE)
            }
        })
        viewPager!!.visibility = VISIBLE
        //Set the tabLayout to belong to the view pager
        tabLayout!!.setupWithViewPager(viewPager)
        //Maximise the tabs to fill the tabLayout
        tabLayout!!.tabGravity = TabLayout.GRAVITY_FILL
        //Set the colors of the text in the tabLayout
        tabLayout!!.setTabTextColors(Color.LTGRAY, Color.WHITE)
        //Set the color of the tabLayout
        tabLayout!!.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
        //Removes the border under the action bar
        if (actionBar != null) {
            actionBar!!.elevation = 0f
        }

        //Find the reference to the empty view
        mEmptyStateTextView = findViewById(R.id.emptyView)

        //Set the empty view to GONE
        mEmptyStateTextView!!.visibility = GONE

        // If there is a valid list of {@link Movie}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (!movieList.isEmpty()) {
            mEmptyStateTextView!!.visibility = GONE
        } else {
            mEmptyStateTextView!!.setText(R.string.no_movies)
            mEmptyStateTextView!!.visibility = VISIBLE
        }
        if (activeNetwork == null) {
            mEmptyStateTextView!!.setText(R.string.no_internet_connection)
            mEmptyStateTextView!!.visibility = VISIBLE
        }
    }

    override fun onLoaderReset(loader: androidx.loader.content.Loader<ArrayList<Movie>>) {}

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
                val intent = Intent(this@MainActivity, Settings::class.java)
                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    private fun enableDisableSwipeRefresh(enable: Boolean) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout!!.isEnabled = enable
        }
    }

    private inner class getCodesTask : AsyncTask<Void, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            if (codes != null) {
                codes.clear()
            }
        }

        override fun doInBackground(vararg params: Void?): String? {
            var tempCodes: ArrayList<MovieCode>
            if (releasePreference == "1") {
                codes = QueryUtils.fetchCodes(UNRELEASED_URL)
            } else if (releasePreference == "0") {
                codes = QueryUtils.fetchCodes(RELEASED_URL)
            } else if (releasePreference == "-1") {
                tempCodes = QueryUtils.fetchCodes(UNRELEASED_URL)
                if (tempCodes != null) {
                    codes.addAll(tempCodes)
                }
                tempCodes = QueryUtils.fetchCodes(RELEASED_URL)
                if (tempCodes != null) {
                    codes.addAll(tempCodes)
                }
            } else
                codes = QueryUtils.fetchCodes(UNRELEASED_URL)
            return null
        }

        override fun onPostExecute(result: String?) {
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            //loaderManager.initLoader(MOVIE_LOADER_ID, null, MainActivity.this);
            loaderManager!!.restartLoader(MOVIE_LOADER_ID, null, this@MainActivity)

        }
    }

    //Adapter for the view pager in use
    private inner class MyPagerAdapter constructor(fm: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentStatePagerAdapter(fm) {

        private val Pages = ArrayList<String>()

        init {
            Pages.add(0, "All")
            Pages.add(1, "MCU")
            Pages.add(2, "DC")
            Pages.add(3, "Fox")
        }

        override fun getPageTitle(position: Int): CharSequence? {
            //Setting the ViewPager tab names
            //For all movies
            return Pages[position]
        }

        override fun getItem(position: Int): androidx.fragment.app.Fragment {
            //Passes the input of each fragment in order to filter the movies
            return MainFragment.newInstance(Pages[position], movieList)
        }

        override fun getItemPosition(`object`: Any): Int {
            return pagePosition
        }

        override//Method to correspond to the number of tabs used
        fun getCount(): Int {
            return Pages.size
        }
    }

    companion object {

        private val TAG = "MainActivity"

        //The URL for the JSON string for unreleased movie codes
        private val UNRELEASED_URL = "https://raw.githubusercontent.com/ahmadadam96/ComicbookMovies/master/app/src/main/res/codes_unreleased"

        //The URL for the JSON string for released movie codes
        private val RELEASED_URL = "https://raw.githubusercontent.com/ahmadadam96/ComicbookMovies/master/app/src/main/res/codes_released"

        /**
         * Constant value for the movie loader ID. We can choose any integer.
         * This really only comes into play if you're using multiple loaders.
         */
        private val MOVIE_LOADER_ID = 1
    }
}