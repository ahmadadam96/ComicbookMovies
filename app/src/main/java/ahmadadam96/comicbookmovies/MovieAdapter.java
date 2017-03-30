package ahmadadam96.comicbookmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by ahmad on 2017-03-14.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieHolder> {
    private Context context;
    private ArrayList<Movie> movies;
    private int itemResource;

    public MovieAdapter(Context context, int itemResource, ArrayList<Movie> movies) {
        this.context = context;
        this.itemResource = itemResource;
        this.movies = movies;
    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(this.itemResource, parent, false);
        return new MovieHolder(this.context, view);
    }

    @Override
    public void onBindViewHolder(MovieHolder holder, int position) {
        Movie movie = this.movies.get(position);
        holder.bindMovie(movie);

    }

    @Override
    public int getItemCount() {
        return this.movies.size();
    }

    public void clear() {
        if (movies.size() > 0) {
            movies.clear();
            notifyDataSetChanged();
        }
    }

    public void update(ArrayList<Movie> movieList) {
        movies = movieList;
        notifyDataSetChanged();
    }
}
