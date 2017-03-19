package ahmadadam96.comicbookmovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static ahmadadam96.comicbookmovies.R.id.overview;
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
        ViewHolderItem viewHolder;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.movie_adapter, parent, false);
            viewHolder = new ViewHolderItem();
            //Find the TextView with view ID title
            viewHolder.titleView = (TextView) listItemView.findViewById(R.id.title);
            //Find the TextView with view ID releaseDate
            viewHolder.dateView = (TextView) listItemView.findViewById(R.id.releaseDate);
            //Find the TextView with view ID daysLeft
            viewHolder.daysView = (TextView) listItemView.findViewById(R.id.daysLeft);
            //Find the TextView with the view ID overview
            viewHolder.overview = (TextView) listItemView.findViewById(overview);
            //Find the ImageView with the view ID poster
            viewHolder.posterView = (ImageView) listItemView.findViewById(R.id.poster);
            listItemView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolderItem) convertView.getTag();
        }
        // Find the movie at the given position in the list of movies
        Movie currentMovie = getItem(position);

        //Set the text of the TextView to be of the title
        viewHolder.titleView.setText(currentMovie.getTitle());

        //Format the date from object type Date to a String
        SimpleDateFormat myformat = new SimpleDateFormat("dd MMMM yyyy");

        String formattedDate = myformat.format(currentMovie.getReleaseDate());

        //Set the text of the TextView to be of the date
        viewHolder.dateView.setText(formattedDate);

        //Calculate the difference between release date and now
        Date currentDate = new Date(System.currentTimeMillis());
        Long duration = currentMovie.getReleaseDate().getTime() - currentDate.getTime();
        Long diffInDays = abs(TimeUnit.MILLISECONDS.toDays(duration));

        //Set the text of the TextView to be of the daysleft
        viewHolder.daysView.setText(diffInDays.toString());

        new DownloadImageTask(viewHolder.posterView)
                .execute("https://image.tmdb.org/t/p/w500/" + currentMovie.getPosterUrl());

        viewHolder.overview.setText(currentMovie.getOverview());

        return listItemView;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
    // our ViewHolder.
    static class ViewHolderItem {
        TextView titleView;
        TextView dateView;
        TextView daysView;
        TextView overview;
        ImageView posterView;
    }
}
