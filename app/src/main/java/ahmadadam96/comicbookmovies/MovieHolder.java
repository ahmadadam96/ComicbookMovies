package ahmadadam96.comicbookmovies;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

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
            Long diffInDays = (TimeUnit.MILLISECONDS.toDays(duration));
            if (diffInDays < 0) {
                daysView.setVisibility(GONE);
            } else {
                daysView.setVisibility(VISIBLE);
                //Set the text of the TextView to be of the daysleft
                this.daysView.setText(diffInDays.toString());
                setDaysBackground(diffInDays);
            }

            //Check if there is a universe
            if (this.movie.getUniverse() != null) {
                //Set the text of the universe view
                this.universeView.setText(this.movie.getUniverse());
            }
            //Make the universe view disappear
            else this.universeView.setVisibility(GONE);

            //Getting the poster image by getting the image using the provided URL
            GlideApp
                    .with(context)
                    //Entering the URL
                    .load("https://image.tmdb.org/t/p/w500/" + this.movie.getPosterUrl())
                    //.load("https://d32qys9a6wm9no.cloudfront.net/images/movies/poster/500x735.png")
                    //Setting the display mode
                    .centerCrop()
                    //Adding fading to improve visuals
                    .transition(DrawableTransitionOptions.withCrossFade())
                    //Once the image loads, set the progress bar for each image to GONE
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progressBarPoster.setVisibility(GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBarPoster.setVisibility(GONE);
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

    void setDaysBackground(Long diffInDays) {
        GradientDrawable daysCircle = (GradientDrawable) daysView.getBackground();
        if (diffInDays >= 0 && diffInDays < 30) {
            daysCircle.setColor(ContextCompat.getColor(context, R.color.days10));
        }
        if (diffInDays >= 30 && diffInDays < 60) {
            daysCircle.setColor(ContextCompat.getColor(context, R.color.days8));
        }
        if (diffInDays >= 60 && diffInDays < 90) {
            daysCircle.setColor(ContextCompat.getColor(context, R.color.days6));
        }
        if (diffInDays >= 90 && diffInDays < 180) {
            daysCircle.setColor(ContextCompat.getColor(context, R.color.days4));
        }
        if (diffInDays >= 180 && diffInDays < 360) {
            daysCircle.setColor(ContextCompat.getColor(context, R.color.days2));
        }
        if (diffInDays >= 360) {
            daysCircle.setColor(ContextCompat.getColor(context, R.color.days1));
        }
    }
}