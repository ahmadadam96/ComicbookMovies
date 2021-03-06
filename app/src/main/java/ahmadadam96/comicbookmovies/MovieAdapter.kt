package ahmadadam96.comicbookmovies

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * Created by ahmad on 2017-03-14.
 */

class MovieAdapter(private val context: Context, private var itemResource: Int) : RecyclerView.Adapter<MovieHolder>() {

    private var movies: ArrayList<Movie> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(this.itemResource, parent, false)
        return MovieHolder(this.context, view)
    }

    override fun onBindViewHolder(holder: MovieHolder, position: Int) {
        var movie: Movie
        try {
            movie = movies[position]
        } catch (e: IllegalStateException) {
            movie = Movie()
        }
        holder.bindMovie(movie)
    }


    override fun getItemCount(): Int {
        return movies.size
    }

    fun clear() {
        if (movies.size > 0) {
            movies.clear()
            notifyDataSetChanged()
        }
    }

    fun update(movieList: ArrayList<Movie>) {
        movies = movieList
        notifyDataSetChanged()
    }
}