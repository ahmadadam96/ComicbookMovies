package ahmadadam96.comicbookmovies;


import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ahmad on 2017-03-14.
 */

public class Movie {
    private Date mReleaseDate;
    private String mTitle;
    private String mOverview;
    private String mPosterUrl;
    private String mUrl;
    private String mIMDBId;

    public Movie(String releaseDate, String title, String overview,
                 String posterUrl, String url, String IMDBId){
        mReleaseDate = convertDate(releaseDate);
        mTitle = title;
        mOverview = overview;
        mPosterUrl = posterUrl;
        mUrl = url;
        mIMDBId = IMDBId;
    }
    private Date convertDate(String releaseDate){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateObject = dateFormat.parse(releaseDate, new ParsePosition(0));
        return dateObject;
    }
    public Date getReleaseDate(){
        return mReleaseDate;
    }
    public String getTitle(){
        return mTitle;
    }
    public String getOverview(){
        return mOverview;
    }
    public String getPosterUrl(){
        return mPosterUrl;
    }
    public String getUrl(){
        return mUrl;
    }
    public String getIMDBId(){
        return mIMDBId;
    }
}
