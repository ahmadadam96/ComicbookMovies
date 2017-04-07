package ahmadadam96.comicbookmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static java.lang.Math.abs;

/**
 * Created by ahmad on 2017-03-27.
 */

public class MovieHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.titleOfMovie)
    TextView titleView;
    @BindView(R.id.releaseDate)
    TextView dateView;
    @BindView(R.id.daysLeft)
    TextView daysView;
    @BindView(R.id.overview)
    TextView overview;
    @BindView(R.id.universe)
    TextView universeView;
    @BindView(R.id.poster)
    ImageView posterView;
    @BindView(R.id.list_item)
    LinearLayout listItem;
    @BindView(R.id.progressBarPoster)
    ProgressBar progressBarPoster;

    private Context context;
    private Movie movie;

    public MovieHolder(Context context, View itemView) {
        super(itemView);

        this.context = context;

        ButterKnife.bind(this, itemView);
    }

    public void bindMovie(final Movie movie) {
        this.movie = movie;
        try {
            //Set the text of the TextView to be of the title
            this.titleView.setText(this.movie.getTitle());

            //Format the date from object type Date to a String
            SimpleDateFormat myFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());

            //Turn the date object into a string formatted properly
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

            //Getting the poster image by getting the image using the provided URL
            Glide
                    .with(context)
                    //Entering the URL
                    .load("https://image.tmdb.org/t/p/w500/" + this.movie.getPosterUrl())
                    //Setting the display mode
                    .centerCrop()
                    //Adding fading to improve visuals
                    .crossFade()
                    //Once the image loads, set the progress bar for each image to GONE
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            progressBarPoster.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBarPoster.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    //Place the image into the posterView
                    .into(this.posterView);

            //Displaying the overview of the movie in the overview view
            this.overview.setText(this.movie.getOverview());

            //An onClickListener so that tapping an item opens the IMDB page
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
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}