package com.example.cataloguemoviestorage;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cataloguemoviestorage.factory.DetailedMovieViewModelFactory;
import com.example.cataloguemoviestorage.fragment.NowPlayingMovieFragment;
import com.example.cataloguemoviestorage.item.MovieItems;
import com.example.cataloguemoviestorage.model.DetailedMovieViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {
    // Setup views bedasarkan id yang ada di layout xml
    @BindView(R.id.detailed_poster_image) ImageView imageViewDetailedPosterImage;
    @BindView(R.id.detailed_movie_title_text) TextView textViewDetailedMovieTitle;
    @BindView(R.id.detailed_movie_tagline_text) TextView textViewDetailedMovieTagline;
    @BindView(R.id.detailed_movie_status_text) TextView textViewDetailedMovieStatus;
    @BindView(R.id.detailed_movie_rating_text) TextView textViewDetailedMovieRating;
    @BindView(R.id.detailed_movie_languages_text) TextView textViewDetailedMovieLanguage;
    @BindView(R.id.detailed_movie_genres_text) TextView textViewDetailedMovieGenres;
    @BindView(R.id.detailed_movie_release_date_text) TextView textViewDetailedMovieReleaseDate;
    @BindView(R.id.detailed_movie_overview_text) TextView textViewDetailedMovieOverview;
    private int detailedMovieId;
    private String detailedMovieTitle;
    // Set layout value untuk dapat menjalankan process loading data
    @BindView(R.id.detailed_progress_bar) ProgressBar detailedProgressBar;

    @BindView(R.id.detailed_content_movie)
    LinearLayout detailedContentMovie;

    @BindView(R.id.detailed_app_bar) AppBarLayout detailedAppBarLayout;
    @BindView(R.id.detailed_toolbar) Toolbar detailedToolbar;

    // Gunakan BuildConfig untuk menjaga credential
    private String baseImageUrl = BuildConfig.IMAGE_MOVIE_URL;

    private DetailedMovieViewModel detailedMovieViewModel;

    private Observer<ArrayList<MovieItems>> detailedMovieObserver;
    
    // Global variable to handle menu
    private Menu menu;
    
    // Drawable Global variable to handle orientation changes
	private int drawableMenuMarkedAsFavouriteResourceId = R.drawable.ic_favourite_off;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        setSupportActionBar(detailedToolbar);

        // Get intent untuk mendapatkan id dan title dari {@link MainActivity}
        detailedMovieId = getIntent().getIntExtra(NowPlayingMovieFragment.MOVIE_ID_DATA, 0);
        detailedMovieTitle = getIntent().getStringExtra(NowPlayingMovieFragment.MOVIE_TITLE_DATA);

        // Cek kalo ada action bar
        if(getSupportActionBar() != null){
            // Set action bar title untuk DetailActivity
            getSupportActionBar().setTitle(detailedMovieTitle);
            // Set up button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Set visiblity of views ketika sedang dalam meretrieve data
        detailedContentMovie.setVisibility(View.INVISIBLE);
        detailedProgressBar.setVisibility(View.VISIBLE);

        // Panggil MovieViewModel dengan menggunakan ViewModelFactory sebagai parameter tambahan (dan satu-satunya pilihan) selain activity
        detailedMovieViewModel = ViewModelProviders.of(this, new DetailedMovieViewModelFactory(this.getApplication(), detailedMovieId)).get(DetailedMovieViewModel.class);

        // Buat observer object untuk mendisplay data ke UI
        detailedMovieObserver = createObserver();

        // Tempelkan Observer ke LiveData object
        detailedMovieViewModel.getDetailedMovie().observe(this, detailedMovieObserver);

        // Add on offset changed listener ke AppBarLayout untuk mengatur
        // ketika app barnya itu gede/collapse
        detailedAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isAppBarLayoutShow = false;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                // Jika scrollRange berada di posisi default atau -1, maka set value untuk scrollRange
                if(scrollRange == -1){
                    scrollRange = appBarLayout.getTotalScrollRange();
                }

                // Jika scroll range dengan vertical offset (parameter) berjumlah 0, maka gedein
                // app bar layout
                if(scrollRange + verticalOffset == 0){
                    isAppBarLayoutShow = true;
                } else if(isAppBarLayoutShow){
                    // Collapse app bar layout jika booleannya true
                    isAppBarLayoutShow = false;
                }

            }
        });
    }

    private Observer<ArrayList<MovieItems>> createObserver(){
        Observer<ArrayList<MovieItems>> observer = new Observer<ArrayList<MovieItems>>() {
            @Override
            public void onChanged(@Nullable ArrayList<MovieItems> detailedMovieItems) {
                // Ketika data selesai di load, maka kita akan mendapatkan data dan menghilangkan progress bar
                // yang menandakan bahwa loadingnya sudah selesai
                detailedContentMovie.setVisibility(View.VISIBLE);
                detailedProgressBar.setVisibility(View.GONE);

                // Set semua data ke dalam detail activity
                // Load image jika ada poster path
                Picasso.get().load(baseImageUrl + detailedMovieItems.get(0).getMoviePosterPath()).into(imageViewDetailedPosterImage);

                textViewDetailedMovieTitle.setText(detailedMovieItems.get(0).getMovieTitle());

                textViewDetailedMovieTagline.setText("\"" + detailedMovieItems.get(0).getMovieTagline() + "\"");

                // Set textview content in detailed movie runtime to contain a variety of different colors
                Spannable statusWord = new SpannableString(getString(R.string.span_movie_detail_status) + " ");
                statusWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, statusWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textViewDetailedMovieStatus.setText(statusWord);
                Spannable statusDetailedMovie = new SpannableString(detailedMovieItems.get(0).getMovieStatus());
                statusDetailedMovie.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, statusDetailedMovie.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textViewDetailedMovieStatus.append(statusDetailedMovie);

                // Set textview content in detailed movie rating to contain a variety of different colors
                Spannable ratingWord = new SpannableString(getString(R.string.span_movie_detail_rating) + " ");
                ratingWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ratingWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textViewDetailedMovieRating.setText(ratingWord);
                Spannable ratingDetailedMovie = new SpannableString(detailedMovieItems.get(0).getMovieRatings());
                ratingDetailedMovie.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, ratingDetailedMovie.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textViewDetailedMovieRating.append(ratingDetailedMovie);

                Spannable ratingFromWord = new SpannableString(" " + getString(R.string.span_movie_detail_from) + " ");
                ratingFromWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ratingFromWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textViewDetailedMovieRating.append(ratingFromWord);

                Spannable ratingDetailedMovieVotes = new SpannableString(detailedMovieItems.get(0).getMovieRatingsVote());
                ratingDetailedMovieVotes.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, ratingDetailedMovieVotes.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textViewDetailedMovieRating.append(ratingDetailedMovieVotes);

                Spannable ratingVotesWord = new SpannableString(" " + getString(R.string.span_movie_detail_votes));
                ratingVotesWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ratingVotesWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textViewDetailedMovieRating.append(ratingVotesWord);

                textViewDetailedMovieLanguage.setText(detailedMovieItems.get(0).getMovieLanguages());

                textViewDetailedMovieGenres.setText(detailedMovieItems.get(0).getMovieGenres());

                textViewDetailedMovieReleaseDate.setText(detailedMovieItems.get(0).getMovieReleaseDate());

                textViewDetailedMovieOverview.setText(detailedMovieItems.get(0).getMovieOverview());
            }
        };
        return observer;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	// Inflate menu
    	getMenuInflater().inflate(R.menu.menu_favourite, menu);
    	// Set inflated menu icon
    	menu.findItem(R.id.action_marked_as_favorite).setIcon(drawableMenuMarkedAsFavouriteResourceId);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
			case R.id.action_marked_as_favorite:
				// Check for current state of drawable menu icon
				if(drawableMenuMarkedAsFavouriteResourceId == R.drawable.ic_favourite_off){
					// Change icon into marked as favourite
					drawableMenuMarkedAsFavouriteResourceId = R.drawable.ic_favourite_on;
					invalidateOptionsMenu();
				} else {
					// Change icon into unmarked as favourite
					drawableMenuMarkedAsFavouriteResourceId = R.drawable.ic_favourite_off;
					invalidateOptionsMenu();
				}
				break;
        	default:
        		break;
		}
    	return super.onOptionsItemSelected(item);
    }
    
}

