package ahmadadam96.comicbookmovies;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";

    public static final String LIST_STATE_KEY = "FragmentListStateKey";

    //String to show which universe the movie belongs to which allows for filtering
    private String mUniverse;

    /**
     * Adapter for the list of movies
     */
    private MovieAdapter mAdapter;

    ArrayList<Movie> movies;

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.list)
    RecyclerView movieListView;

    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;

    private SharedPreferences sharedPref;

    private String orderPreference;

    private Parcelable listState;

    View view;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.first_fragment, container, false);

        movies = new ArrayList<>();

        //Gets the arguments from the MainActivity
        Bundle args = getArguments();
        //Sets the universe to the universe defined in the MainActivity to allow filtering
        mUniverse = args.getString("Universe");

        movies = args.getParcelableArrayList("Movies");

        ButterKnife.bind(this, view);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(movieListView.getContext(),
                layoutManager.getOrientation());

        movieListView.addItemDecoration(dividerItemDecoration);

        movieListView.setLayoutManager(layoutManager);

        PreferenceManager.setDefaultValues(getContext(), R.xml.pref_general, false); //gets default settings and preferences

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());

        orderPreference = sharedPref.getString(Settings.ORDER_KEY, "");

        // Create a new adapter that takes a list of movies as input
        mAdapter = new MovieAdapter(getContext(), R.layout.movie_adapter, organizeMovies());

        movieListView.setAdapter(mAdapter);

        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabLayout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                layoutManager.scrollToPositionWithOffset(0, 0);
            }
        });

        if (savedInstanceState != null) {
            listState = savedInstanceState.getParcelable(LIST_STATE_KEY);
        }

        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(Settings.ORDER_KEY)) {
                    orderPreference = sharedPref.getString(Settings.ORDER_KEY, "");
                    updateAdapter();
                }
            }
        };

        sharedPref.registerOnSharedPreferenceChangeListener(prefListener);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(LIST_STATE_KEY, movieListView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listState != null) {
            movieListView.getLayoutManager().onRestoreInstanceState(listState);
        }
    }

    private void updateAdapter() {
        mAdapter.update(organizeMovies());
        movieListView.setAdapter(mAdapter);
    }


    private ArrayList<Movie> organizeMovies() {
        ArrayList<Movie> tempMovieList = (ArrayList<Movie>) movies.clone();
        try {
            Iterator<Movie> movieIterator = tempMovieList.iterator();
            while (movieIterator.hasNext()) {
                Movie next = movieIterator.next();
                if (!(next.getUniverse().equals(mUniverse) || mUniverse.equals("All"))) {
                    movieIterator.remove();
                }
            }

            if (orderPreference.equals("-1")) {
                Long seed = System.nanoTime();
                Collections.shuffle(tempMovieList, new Random(seed));
            } else Collections.sort(tempMovieList, new Comparator<Movie>() {
                @Override
                public int compare(Movie movie1, Movie movie2) {
                    switch (orderPreference) {
                        case "1":
                        default:
                            return movie1.getReleaseDate().compareTo(movie2.getReleaseDate());
                        case "0":
                            return movie1.getTitle().compareTo(movie2.getTitle());
                    }
                }
            });
        } catch (
                NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error code 429, please refresh in a few seconds", Toast.LENGTH_SHORT).show();
        }
        return tempMovieList;
    }

    public static MainFragment newInstance(String universe, ArrayList<Movie> movies) {
        MainFragment f = new MainFragment();
        Bundle b = new Bundle();
        b.putString("Universe", universe);
        b.putParcelableArrayList("Movies", movies);
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
