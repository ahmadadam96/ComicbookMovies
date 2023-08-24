package ahmadadam96.comicbookmovies

import ahmadadam96.comicbookmovies.databinding.ActivityMainBinding
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
import com.google.android.material.tabs.TabLayout
import java.util.*

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    private var actionBar: ActionBar? = null

    private var pagePosition: Int = 0

    //ArrayList to save all the movie codes
    private var codes: ArrayList<MovieCode> = ArrayList()

    private var movieList = ArrayList<Movie>()

    private var sharedPref: SharedPreferences? = null

    private var releasePreference: String? = null

    private var prefListener: SharedPreferences.OnSharedPreferenceChangeListener? = null

    private var loaderManager: LoaderManager? = null

    private var viewPagerAdapter: MyPagerAdapter? = null

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        loaderManager = LoaderManager.getInstance(this)
        actionBar = supportActionBar
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false) //gets default settings and preferences
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        ButterKnife.bind(this)

        startLoading()


        prefListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == Settings.RELEASE_KEY)
                binding.progressBar.visibility = VISIBLE
                binding.viewPager.visibility = GONE
                startLoading()
        }

        sharedPref!!.registerOnSharedPreferenceChangeListener(prefListener)

        /* Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        binding.refreshMain.setOnRefreshListener {
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
            binding.emptyViewActivity.setText(R.string.no_internet_connection)
            binding.emptyViewActivity.visibility = VISIBLE
            binding.progressBar.visibility = GONE
        } else {
            getCodesTask().execute()
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): androidx.loader.content.Loader<ArrayList<Movie>> {
        // Create a new loader for the given URL
        return MovieLoader(this, codes)
    }

    override fun onLoadFinished(loader: androidx.loader.content.Loader<ArrayList<Movie>>, data: ArrayList<Movie>) {
        binding.refreshMain.isRefreshing = false

        binding.progressBar.visibility = GONE

        val connMGR = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = connMGR.activeNetworkInfo

        movieList = data

        viewPagerAdapter = MyPagerAdapter(supportFragmentManager)

        //Set the adapter for the view pager
        binding.viewPager.adapter = viewPagerAdapter

        binding.viewPager.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, v: Float, i1: Int) {}

            override fun onPageSelected(position: Int) {
                pagePosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {
                enableDisableSwipeRefresh(state == androidx.viewpager.widget.ViewPager.SCROLL_STATE_IDLE)
            }
        })
        binding.viewPager.visibility = VISIBLE
        //Set the tabLayout to belong to the view pager
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        //Maximise the tabs to fill the tabLayout
        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        //Set the colors of the text in the tabLayout
        binding.tabLayout.setTabTextColors(Color.LTGRAY, Color.WHITE)
        //Set the color of the tabLayout
        binding.tabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
        //Removes the border under the action bar
        if (actionBar != null) {
            actionBar!!.elevation = 0f
        }

        if (activeNetwork == null) {
            binding.emptyViewActivity.setText(R.string.no_internet_connection)
            binding.emptyViewActivity.visibility = VISIBLE
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
        if (binding.refreshMain != null) {
            binding.refreshMain.isEnabled = enable
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

        override fun getPageTitle(position: Int): CharSequence {
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