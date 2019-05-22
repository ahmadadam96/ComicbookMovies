package ahmadadam96.comicbookmovies

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import java.util.ArrayList

/**
 * Created by ahmad on 2017-03-14.
 */

class MovieAdapter(private val context: Context, private val itemResource: Int, private var movies: ArrayList<Movie>?) : androidx.recyclerview.widget.RecyclerView.Adapter<MovieHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(this.itemResource, parent, false)
        return MovieHolder(this.context, view)
    }

    override fun onBindViewHolder(holder: MovieHolder, position: Int) {
        val movie = this.movies!![position]
        holder.bindMovie(movie)

    }

    override fun getItemCount(): Int {
        return this.movies!!.size
    }

    fun clear() {
        if (movies!!.size > 0) {
            movies!!.clear()
            notifyDataSetChanged()
        }
    }

    fun update(movieList: ArrayList<Movie>) {
        movies = movieList
        notifyDataSetChanged()
    }
}
