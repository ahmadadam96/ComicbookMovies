package ahmadadam96.comicbookmovies

import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.first_fragment.view.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MainFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : androidx.fragment.app.Fragment() {

    //String to show which universe the movie belongs to which allows for filtering
    private var mUniverse: String? = null

    /**
     * Adapter for the list of movies
     */
    private var mAdapter: MovieAdapter? = null

    private var movies: ArrayList<Movie>? = null

    private var movieListView: RecyclerView? = null

    private var prefListener: SharedPreferences.OnSharedPreferenceChangeListener? = null

    private var sharedPref: SharedPreferences? = null

    private var orderPreference: String? = null

    private var listState: Parcelable? = null

    private var args: Bundle? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.first_fragment, container, false)
        movieListView = view!!.list


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //Gets the arguments from the MainActivity
        args = arguments

        //Sets the universe to the universe defined in the MainActivity to allow filtering
        mUniverse = args!!.getString("Universe")

        movies = args!!.getParcelableArrayList("Movies")

        if (savedInstanceState != null) {
            listState = savedInstanceState.getParcelable(LIST_STATE_KEY)
        }


        val layoutManager = LinearLayoutManager(context)

        layoutManager.orientation = RecyclerView.VERTICAL

        val dividerItemDecoration = DividerItemDecoration(movieListView!!.context,
                layoutManager.orientation)

        movieListView!!.addItemDecoration(dividerItemDecoration)

        movieListView!!.layoutManager = layoutManager

        PreferenceManager.setDefaultValues(context, R.xml.pref_general, false) //gets default settings and preferences

        sharedPref = PreferenceManager.getDefaultSharedPreferences(context)

        orderPreference = sharedPref!!.getString(Settings.ORDER_KEY, "")

        mAdapter = MovieAdapter(context!!, R.layout.movie_adapter)

        updateAdapter()

        val tabLayout = activity!!.findViewById<View>(R.id.tabLayout) as TabLayout

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                layoutManager.scrollToPositionWithOffset(0, 0)
            }
        })

        prefListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == Settings.ORDER_KEY) {
                orderPreference = sharedPref!!.getString(Settings.ORDER_KEY, "")
                updateAdapter()
            }
        }

        sharedPref!!.registerOnSharedPreferenceChangeListener(prefListener)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(LIST_STATE_KEY, movieListView!!.layoutManager?.onSaveInstanceState())
    }

    override fun onResume() {
        super.onResume()
        if (listState != null) {
            movieListView!!.layoutManager?.onRestoreInstanceState(listState)
        }
    }

    private fun updateAdapter() {
        mAdapter!!.update(organizeMovies())
        movieListView!!.adapter = this.mAdapter
    }


    private fun organizeMovies(): ArrayList<Movie> {
        val tempMovieList = movies!!.clone() as ArrayList<Movie>
        try {
            val movieIterator = tempMovieList.iterator()
            while (movieIterator.hasNext()) {
                val next = movieIterator.next()
                if (!(next.universe == mUniverse || mUniverse == "All")) {
                    movieIterator.remove()
                }
            }

            if (orderPreference == "-1") {
                val seed = System.nanoTime()
                Collections.shuffle(tempMovieList, Random(seed))
            } else
                Collections.sort(tempMovieList) { movie1, movie2 ->
                    when (orderPreference) {
                        "1" -> movie1.releaseDate.compareTo(movie2.releaseDate)
                        "0" -> movie1.title.compareTo(movie2.title)
                        else -> movie1.releaseDate.compareTo(movie2.releaseDate)
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error code 429, please refresh in a few seconds", Toast.LENGTH_SHORT).show()
        }

        return tempMovieList
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        private val TAG = "MainFragment"

        val LIST_STATE_KEY = "FragmentListStateKey"

        fun newInstance(universe: String, movies: ArrayList<Movie>): MainFragment {
            val b = Bundle()
            b.putString("Universe", universe)
            b.putParcelableArrayList("Movies", movies)
            val f = MainFragment()
            f.arguments = b
            return f
        }
    }
}