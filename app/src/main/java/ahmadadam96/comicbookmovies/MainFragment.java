package ahmadadam96.comicbookmovies;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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
public class MainFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<SharedPreferences> {

    private static final String TAG = "MainFragment";

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

    private SharedPreferences sharedPref;

    private String orderPreference;

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

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(movieListView.getContext(),
                layoutManager.getOrientation());

        movieListView.addItemDecoration(dividerItemDecoration);

        movieListView.setLayoutManager(layoutManager);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());

        orderPreference = sharedPref.getString(Settings.ORDER_KEY, "");

        // Create a new adapter that takes a list of movies as input
        mAdapter = new MovieAdapter(getContext(), R.layout.movie_adapter, organizeMovies());

        movieListView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public Loader<SharedPreferences> onCreateLoader(int id, Bundle args) {
        return (new SharedPreferencesLoader(getContext()));
    }

    @Override
    public void onLoadFinished(Loader<SharedPreferences> loader, SharedPreferences data) {
        sharedPref = data;
        orderPreference = sharedPref.getString(Settings.ORDER_KEY, "");
        updateAdapter();
        movieListView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<SharedPreferences> loader) {
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
        } catch (
                NullPointerException e)

        {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error code 429", Toast.LENGTH_SHORT).show();
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
