package ahmadadam96.comicbookmovies;


import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ahmad on 2017-03-14.
 */

public class Movie implements Parcelable {
    //The release date of the movie
    private Date mReleaseDate;

    //The title of the movie
    private String mTitle;

    //A short overview talking about what the movie is about
    private String mOverview;

    //The official poster for the movie
    private String mPosterUrl;

    //The URL to the official movie page (not always available
    private String mUrl;

    //The IMDB id to link to the IMDB page
    private String mIMDBId;

    //Universe the movie is set in
    private String mUniverse;

    //The constructor for the Movie class with a universe
    public Movie(String releaseDate, String title, String overview,
                 String posterUrl, String url, String IMDBId, String universe) {
        mReleaseDate = convertDate(releaseDate);
        mTitle = title;
        mOverview = overview;
        mPosterUrl = posterUrl;
        mUrl = url;
        mIMDBId = IMDBId;
        mUniverse = universe;
    }

    //The constructor for the Movie class without a universe
    public Movie(String releaseDate, String title, String overview,
                 String posterUrl, String url, String IMDBId) {
        mReleaseDate = convertDate(releaseDate);
        mTitle = title;
        mOverview = overview;
        mPosterUrl = posterUrl;
        mUrl = url;
        mIMDBId = IMDBId;
        mUniverse = "Unknown";
    }

    //A function to convert the date from a string to a dateObject
    // with the correct format
    private Date convertDate(String releaseDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.parse(releaseDate, new ParsePosition(0));
    }

    //Member get methods for each object
    public Date getReleaseDate() {
        return mReleaseDate;
    }

    public String getTitle() {
        if (mTitle != null) {
            return mTitle;
        } else return "Unknown";
    }

    public String getOverview() {
        if (mOverview != null) {
            return mOverview;
        } else return "Unknown";
    }

    public String getPosterUrl() {
        if (mPosterUrl != null) {
            return mPosterUrl;
        } else return "Unknown";
    }

    public String getUrl() {
        if (mUrl != null) {
            return mUrl;
        } else return "Unknown";
    }

    public String getIMDBId() {
        if (mIMDBId != null) {
            return mIMDBId;
        } else return "Unknown";
    }

    public String getUniverse() {
        if (mUniverse != null) {
            return mUniverse;
        } else return "Unknown";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mReleaseDate != null ? this.mReleaseDate.getTime() : -1);
        dest.writeString(this.mTitle);
        dest.writeString(this.mOverview);
        dest.writeString(this.mPosterUrl);
        dest.writeString(this.mUrl);
        dest.writeString(this.mIMDBId);
        dest.writeString(this.mUniverse);
    }

    protected Movie(Parcel in) {
        long tmpMReleaseDate = in.readLong();
        this.mReleaseDate = tmpMReleaseDate == -1 ? null : new Date(tmpMReleaseDate);
        this.mTitle = in.readString();
        this.mOverview = in.readString();
        this.mPosterUrl = in.readString();
        this.mUrl = in.readString();
        this.mIMDBId = in.readString();
        this.mUniverse = in.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
