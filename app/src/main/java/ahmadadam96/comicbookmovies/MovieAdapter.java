package ahmadadam96.comicbookmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.view.View.GONE;
import static java.lang.Math.abs;

/**
 * Created by ahmad on 2017-03-14.
 */

public class MovieAdapter extends ArrayAdapter<Movie> {
    public MovieAdapter(Context context, List<Movie> movies) {
        super(context, 0, movies);
    }

    /**
     * Returns a list item view that displays information about the movie at the given position
     * in the list of movies.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.movie_adapter, parent, false);
            viewHolder = new ViewHolderItem();
            viewHolder.position = position;
            //Find the TextView with view ID title
            viewHolder.titleView = (TextView) convertView.findViewById(R.id.titleOfMovie);
            //Find the TextView with view ID releaseDate
            viewHolder.dateView = (TextView) convertView.findViewById(R.id.releaseDate);
            //Find the TextView with view ID daysLeft
            viewHolder.daysView = (TextView) convertView.findViewById(R.id.daysLeft);
            //Find the TextView with the view ID overview
            viewHolder.overview = (TextView) convertView.findViewById(R.id.overview);
            //Find the TextView with the view ID universe
            viewHolder.universeView = (TextView) convertView.findViewById(R.id.universe);
            //Find the ImageView with the view ID poster
            viewHolder.posterView = (ImageView) convertView.findViewById(R.id.poster);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }
        // Find the movie at the given position in the list of movies
        Movie currentMovie = getItem(position);

        //Set the text of the TextView to be of the title
        viewHolder.titleView.setText(currentMovie.getTitle());

        //Format the date from object type Date to a String
        SimpleDateFormat myformat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());

        String formattedDate = myformat.format(currentMovie.getReleaseDate());

        //Set the text of the TextView to be of the date
        viewHolder.dateView.setText(formattedDate);

        //Calculate the difference between release date and now
        Date currentDate = new Date(System.currentTimeMillis());
        Long duration = currentMovie.getReleaseDate().getTime() - currentDate.getTime();
        Long diffInDays = abs(TimeUnit.MILLISECONDS.toDays(duration));

        //Set the text of the TextView to be of the daysleft
        viewHolder.daysView.setText(diffInDays.toString());

        //Check if there is a universe
        if (currentMovie.getUniverse() != null) {
            //Set the text of the universe view
            viewHolder.universeView.setText(currentMovie.getUniverse());
        }
        //Make the universe view disappear
        else viewHolder.universeView.setVisibility(GONE);
        Glide
                .with(getContext())
                .load("https://image.tmdb.org/t/p/w500/" + currentMovie.getPosterUrl())
                .centerCrop()
                .crossFade()
                .into(viewHolder.posterView);

        // new DownloadImageTask(viewHolder.posterView)
        //       .execute("https://image.tmdb.org/t/p/w500/" + currentMovie.getPosterUrl());

        viewHolder.overview.setText(currentMovie.getOverview());

        return convertView;
    }


    // our ViewHolder.
    static class ViewHolderItem {
        private TextView titleView;
        private TextView dateView;
        private TextView daysView;
        private TextView overview;
        private TextView universeView;
        private ImageView posterView;
        int position;
    }
}
