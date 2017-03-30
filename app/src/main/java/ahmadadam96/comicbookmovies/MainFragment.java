package ahmadadam96.comicbookmovies;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;


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


    //String to show which universe the movie belongs to which allows for filtering
    private String mUniverse;


    /**
     * Adapter for the list of movies
     */
    private MovieAdapter mAdapter;

    //A swipe to refresh widget
    private SwipeRefreshLayout mSwipeRefreshLayout;

    //ArrayList to save all the movie codes
    ArrayList<MovieCode> codes = new ArrayList<>();
    ArrayList<Movie> movies = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    private RecyclerView movieListView;

    View view;

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

        movies = args.getParcelableArrayList("Movies");

        // Find a reference to the {@link ListView} in the layout
        movieListView = (RecyclerView) view.findViewById(R.id.list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(movieListView.getContext(),
                layoutManager.getOrientation());

        movieListView.addItemDecoration(dividerItemDecoration);

        movieListView.setLayoutManager(layoutManager);

        try {
            Iterator<Movie> movieIterator = movies.iterator();
            while (movieIterator.hasNext()) {
                Movie next = movieIterator.next();
                if (!(next.getUniverse().equals(mUniverse) || mUniverse.equals("All"))) {
                    movieIterator.remove();
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error code 429", Toast.LENGTH_SHORT).show();
        }

        // Create a new adapter that takes a list of movies as input
        mAdapter = new MovieAdapter(getContext(), R.layout.movie_adapter, movies);

        movieListView.setAdapter(mAdapter);

        return view;
    }

    public static MainFragment newInstance(String universe, ArrayList<Movie> movies) {
        MainFragment f = new MainFragment();
        Bundle b = new Bundle();
        b.putParcelableArrayList("Movies", movies);
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
