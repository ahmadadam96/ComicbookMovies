package ahmadadam96.comicbookmovies

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Ahmed El Yaacoub on 2017-03-14.
 */

@Parcelize
class Movie() : Parcelable {

    //The release date of the movie
    //Member get methods for each object
    var releaseDate: Date = Calendar.getInstance().time

    //The title of the movie
    var title: String = "Unknown"

    //A short overview talking about what the movie is about
    var overview: String = "Unknown"

    //The official poster for the movie
    var posterUrl: String = "Unknown"

    //The URL to the official movie page (not always available
    var url: String = "Unknown"

    //The IMDB id to link to the IMDB page
    var IMDBId: String = "Unknown"

    //Universe the movie is set in
    var universe: String = "Unknown"

    constructor(releaseDate: String, title: String, overview: String,
                posterUrl: String, url: String, IMDBId: String, universe: String) : this() {
        this.releaseDate = convertDate(releaseDate)
        this.title = title
        this.overview = overview
        this.posterUrl = posterUrl
        this.url = url
        this.IMDBId = IMDBId
        this.universe = universe
    }

    //The constructor for the Movie class without a universe
    constructor(releaseDate: String, title: String, overview: String,
                posterUrl: String, url: String, IMDBId: String) : this() {
        this.releaseDate = convertDate(releaseDate)
        this.title = title
        this.overview = overview
        this.posterUrl = posterUrl
        this.url = url
        this.IMDBId = IMDBId
        this.universe = "Unknown"
    }


    //A function to convert the date from a string to a dateObject
    // with the correct format
    private fun convertDate(releaseDate: String): Date {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        return dateFormat.parse(releaseDate, ParsePosition(0))
    }
}
