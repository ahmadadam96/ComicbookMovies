package ahmadadam96.comicbookmovies

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.loader.app.LoaderManager
import butterknife.ButterKnife
import com.google.android.gms.ads.AdRequest
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), androidx.loader.app.LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    private var actionBar: ActionBar? = null

    private var pagePosition: Int = 0

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

        loaderManager = LoaderManager.getInstance(this)
        actionBar = supportActionBar
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
                progressBar.visibility = VISIBLE
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
        refreshMain!!.setOnRefreshListener {
            Log.i(TAG, "startLoading called from swipeRefreshLayout")
            // This method performs the actual data-refresh operation.
            // The method calls setRefreshing(false) when it's finished.
            startLoading()
        }
    }



    private fun startLoading() {
        releasePreference = sharedPref!!.getString(Settings.RELEASE_KEY, "")

        val connMGR = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connMGR.activeNetworkInfo
        if (activeNetwork == null || !activeNetwork.isConnected) {
            emptyViewActivity.setText(R.string.no_internet_connection)
            emptyViewActivity.visibility = VISIBLE
            progressBar.visibility = GONE
        } else {
            getCodesTask().execute()
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): androidx.loader.content.Loader<ArrayList<Movie>> {
        // Create a new loader for the given URL
        return MovieLoader(this, codes)
    }

    override fun onLoadFinished(loader: androidx.loader.content.Loader<ArrayList<Movie>>, data: ArrayList<Movie>) {
        refreshMain.isRefreshing = false

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

        if (activeNetwork == null) {
            emptyViewActivity!!.setText(R.string.no_internet_connection)
            emptyViewActivity!!.visibility = VISIBLE
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
        if (refreshMain != null) {
            refreshMain!!.isEnabled = enable
        }
    }

    private inner class getCodesTask : AsyncTask<Void, Void, String>() {
        @Deprecated("Deprecated in Java")
        override fun onPreExecute() {
            super.onPreExecute()
            codes.clear()
        }

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: Void?): String? {
            var tempCodes: ArrayList<MovieCode>
            if (releasePreference == "1") {
                codes = QueryUtils.fetchCodes(UNRELEASED_URL)
            } else if (releasePreference == "0") {
                codes = QueryUtils.fetchCodes(RELEASED_URL)
            } else if (releasePreference == "-1") {
                tempCodes = QueryUtils.fetchCodes(UNRELEASED_URL)
                codes.addAll(tempCodes)
                tempCodes = QueryUtils.fetchCodes(RELEASED_URL)
                codes.addAll(tempCodes)
            } else
                codes = QueryUtils.fetchCodes(UNRELEASED_URL)
            return null
        }

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: String?) {
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            //loaderManager.initLoader(MOVIE_LOADER_ID, null, MainActivity.this);
            loaderManager!!.restartLoader(MOVIE_LOADER_ID, null, this@MainActivity)

        }
    }

    //Adapter for the view pager in use
    private inner class MyPagerAdapter constructor(fm: FragmentManager) : FragmentStatePagerAdapter(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT ) {

        private val pages = ArrayList<String>()

        init {
            pages.add(0, "All")
            pages.add(1, "MCU")
            pages.add(2, "DC")
            pages.add(3, "Sony")
        }

        override fun getPageTitle(position: Int): CharSequence? {
            //Setting the ViewPager tab names
            //For all movies
            return pages[position]
        }

        override fun getItem(position: Int): androidx.fragment.app.Fragment {
            //Passes the input of each fragment in order to filter the movies
            return MainFragment.newInstance(pages[position], movieList)
        }

        override fun getItemPosition(`object`: Any): Int {
            return pagePosition
        }

        override//Method to correspond to the number of tabs used
        fun getCount(): Int {
            return pages.size
        }
    }

    companion object {

        private const val TAG = "MainActivity"

        //The URL for the JSON string for unreleased movie codes
        private const val UNRELEASED_URL = "https://raw.githubusercontent.com/ahmadadam96/ComicbookMovies/master/app/src/main/res/codes_unreleased"

        //The URL for the JSON string for released movie codes
        private const val RELEASED_URL = "https://raw.githubusercontent.com/ahmadadam96/ComicbookMovies/master/app/src/main/res/codes_released"

        /**
         * Constant value for the movie loader ID. We can choose any integer.
         * This really only comes into play if you're using multiple loaders.
         */
        private const val MOVIE_LOADER_ID = 1
    }
}