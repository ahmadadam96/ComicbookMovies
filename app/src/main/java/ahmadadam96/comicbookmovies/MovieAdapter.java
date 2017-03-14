package ahmadadam96.comicbookmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.abs;

/**
 * Created by ahmad on 2017-03-14.
 */

public class MovieAdapter extends ArrayAdapter<Movie> {
    public MovieAdapter(Context context, List<Movie> movies) {
        super(context, 0, movies);
    }

    /**
     * Returns a list item view that displays information about the earthquake at the given position
     * in the list of earthquakes.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.movie_adapter, parent, false);
        }
        // Find the movie at the given position in the list of movies
        Movie currentMovie = getItem(position);

        //Find the TextView with view ID title
        TextView titleView = (TextView) listItemView.findViewById(R.id.title);

        //Set the text of the TextView to be of the title
        titleView.setText(currentMovie.getTitle());

        //Find the TextView with view ID releaseDate
        TextView dateView = (TextView) listItemView.findViewById(R.id.releaseDate);

        //Format the date from object type Date to a String
        SimpleDateFormat myformat = new SimpleDateFormat("dd MMMM yyyy");

        String formattedDate = myformat.format(currentMovie.getReleaseDate());

        //Set the text of the TextView to be of the date
        dateView.setText(formattedDate);

        //Calculate the difference between release date and now
        Date currentDate = new Date(System.currentTimeMillis());
        Long duration = currentMovie.getReleaseDate().getTime() - currentDate.getTime();
        Long diffInDays = abs(TimeUnit.MILLISECONDS.toDays(duration));

        //Find the TextView with view ID daysLeft
        TextView daysView = (TextView) listItemView.findViewById(R.id.daysLeft);

        //Set the text of the TextView to be of the daysleft
        daysView.setText(diffInDays.toString());

        return listItemView;
    }
}
