package ahmadadam96.comicbookmovies

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.View.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView

import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide

/**
 * Created by ahmad on 2017-03-27.
 */

class MovieHolder(private val context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {
    var titleView: TextView? = null
    var dateView: TextView? = null
    var daysView: TextView? = null
    var overview: TextView? = null
    var universeView: TextView? = null
    var posterView: ImageView? = null
    var listItem: LinearLayout? = null
    var progressBarPoster: ProgressBar? = null
    var movie: Movie? = null

    init {
        titleView = itemView.findViewById(R.id.titleOfMovie)
        dateView = itemView.findViewById(R.id.releaseDate)
        daysView = itemView.findViewById(R.id.daysLeft)
        overview = itemView.findViewById(R.id.overview)
        universeView = itemView.findViewById(R.id.universe)
        posterView = itemView.findViewById(R.id.poster)
        listItem = itemView.findViewById(R.id.list_item)
        progressBarPoster = itemView.findViewById(R.id.progressBarPoster)

        ButterKnife.bind(this, itemView)
    }

    fun bindMovie(movie: Movie) {
        this.movie = movie
        try {
            //Set the text of the TextView to be of the title
            this.titleView!!.text = this.movie!!.title

            //Format the date from object type Date to a String
            val myFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

            //Turn the date object into a string formatted properly
            val formattedDate = myFormat.format(this.movie!!.releaseDate)

            //Set the text of the TextView to be of the date
            this.dateView!!.text = formattedDate

            //Calculate the difference between release date and now
            val currentDate = Date(System.currentTimeMillis())
            val duration = this.movie!!.releaseDate.time - currentDate.time
            val diffInDays = TimeUnit.MILLISECONDS.toDays(duration)
            if (diffInDays < 0) {
                daysView!!.visibility = GONE
            } else {
                daysView!!.visibility = VISIBLE
                //Set the text of the TextView to be of the daysleft
                this.daysView!!.text = diffInDays.toString()
                setDaysBackground(diffInDays)
            }

            //Check if there is a universe
            if (this.movie!!.universe != null) {
                //Set the text of the universe view
                this.universeView!!.text = this.movie!!.universe
            } else
                this.universeView!!.visibility = GONE//Make the universe view disappear

            //Getting the poster image by getting the image using the provided URL
            Glide
                    .with(context)
                    //Entering the URL
                    .load("https://image.tmdb.org/t/p/w500/" + this.movie!!.posterUrl)
                    //.load("https://d32qys9a6wm9no.cloudfront.net/images/movies/poster/500x735.png")
                    //Setting the display mode
                    .centerCrop()
                    //Adding fading to improve visuals
                    .transition(DrawableTransitionOptions.withCrossFade())
                    //Once the image loads, set the progress bar for each image to GONE
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                            progressBarPoster!!.visibility = GONE
                            return false
                        }

                        override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                            progressBarPoster!!.visibility = GONE
                            return false
                        }
                    })
                    //Place the image into the posterView
                    .into(this.posterView!!)

            //Displaying the overview of the movie in the overview view
            this.overview!!.text = this.movie!!.overview

            //An onClickListener so that tapping an item opens the IMDB page
            listItem!!.setOnClickListener {
                // Convert the String URL into a URI object (to pass into the Intent constructor)
                val movieUri = Uri.parse("http://www.imdb.com/title/" + movie.imdbId)

                // Create a new intent to view the IMDB page for the movie
                val websiteIntent = Intent(Intent.ACTION_VIEW, movieUri)

                // Send the intent to launch a new activity
                context.startActivity(websiteIntent)
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }

    }

    fun setDaysBackground(diffInDays: Long) {
        val daysCircle = daysView!!.background as GradientDrawable
        if (diffInDays >= 0 && diffInDays < 30) {
            daysCircle.setColor(ContextCompat.getColor(context, R.color.days10))
        }
        if (diffInDays >= 30 && diffInDays < 60) {
            daysCircle.setColor(ContextCompat.getColor(context, R.color.days8))
        }
        if (diffInDays >= 60 && diffInDays < 90) {
            daysCircle.setColor(ContextCompat.getColor(context, R.color.days6))
        }
        if (diffInDays >= 90 && diffInDays < 180) {
            daysCircle.setColor(ContextCompat.getColor(context, R.color.days4))
        }
        if (diffInDays >= 180 && diffInDays < 360) {
            daysCircle.setColor(ContextCompat.getColor(context, R.color.days2))
        }
        if (diffInDays >= 360) {
            daysCircle.setColor(ContextCompat.getColor(context, R.color.days1))
        }
    }
}