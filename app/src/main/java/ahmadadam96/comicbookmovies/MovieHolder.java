package ahmadadam96.comicbookmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.view.View.GONE;
import static java.lang.Math.abs;

/**
 * Created by ahmad on 2017-03-27.
 */

public class MovieHolder extends RecyclerView.ViewHolder {
    private TextView titleView;
    private TextView dateView;
    private TextView daysView;
    private TextView overview;
    private TextView universeView;
    private ImageView posterView;
    private LinearLayout listItem;

    private Context context;
    private Movie movie;

    public MovieHolder(Context context, View itemView) {
        super(itemView);

        this.context = context;

        //Find the TextView with view ID title
        this.titleView = (TextView) itemView.findViewById(R.id.titleOfMovie);
        //Find the TextView with view ID releaseDate
        this.dateView = (TextView) itemView.findViewById(R.id.releaseDate);
        //Find the TextView with view ID daysLeft
        this.daysView = (TextView) itemView.findViewById(R.id.daysLeft);
        //Find the TextView with the view ID overview
        this.overview = (TextView) itemView.findViewById(R.id.overview);
        //Find the TextView with the view ID universe
        this.universeView = (TextView) itemView.findViewById(R.id.universe);
        //Find the ImageView with the view ID poster
        this.posterView = (ImageView) itemView.findViewById(R.id.poster);
        //Find the listItem with the view ID listItem
        this.listItem = (LinearLayout) itemView.findViewById(R.id.list_item);
    }

    public void bindMovie(final Movie movie) {
        this.movie = movie;
        //Set the text of the TextView to be of the title
        this.titleView.setText(this.movie.getTitle());

        //Format the date from object type Date to a String
        SimpleDateFormat myFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());

        String formattedDate = myFormat.format(this.movie.getReleaseDate());

        //Set the text of the TextView to be of the date
        this.dateView.setText(formattedDate);

        //Calculate the difference between release date and now
        Date currentDate = new Date(System.currentTimeMillis());
        Long duration = this.movie.getReleaseDate().getTime() - currentDate.getTime();
        Long diffInDays = abs(TimeUnit.MILLISECONDS.toDays(duration));

        //Set the text of the TextView to be of the daysleft
        this.daysView.setText(diffInDays.toString());

        //Check if there is a universe
        if (this.movie.getUniverse() != null) {
            //Set the text of the universe view
            this.universeView.setText(this.movie.getUniverse());
        }
        //Make the universe view disappear
        else this.universeView.setVisibility(GONE);
        Glide
                .with(context)
                .load("https://image.tmdb.org/t/p/w500/" + this.movie.getPosterUrl())
                .centerCrop()
                .crossFade()
                .into(this.posterView);

        // new DownloadImageTask(viewHolder.posterView)
        //       .execute("https://image.tmdb.org/t/p/w500/" + currentMovie.getPosterUrl());

        this.overview.setText(this.movie.getOverview());

        listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri movieUri = Uri.parse("http://www.imdb.com/title/" + movie.getIMDBId());

                // Create a new intent to view the IMDB page for the movie
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, movieUri);

                // Send the intent to launch a new activity
                context.startActivity(websiteIntent);
            }
        }  );
    }
}