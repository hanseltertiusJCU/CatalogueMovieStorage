package com.example.cataloguemoviestorage;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
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

import com.example.cataloguemoviestorage.database.FavoriteItemsHelper;
import com.example.cataloguemoviestorage.factory.DetailedMovieViewModelFactory;
import com.example.cataloguemoviestorage.fragment.MovieFragment;
import com.example.cataloguemoviestorage.entity.MovieItems;
import com.example.cataloguemoviestorage.model.DetailedMovieViewModel;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity{
	// Setup views bedasarkan id yang ada di layout xml
	@BindView(R.id.detailed_poster_image)
	ImageView imageViewDetailedPosterImage;
	@BindView(R.id.detailed_first_info_text)
	TextView textViewDetailedFirstInfoText;
	@BindView(R.id.detailed_second_info_text)
	TextView textViewDetailedSecondInfoText;
	@BindView(R.id.detailed_third_info_text)
	TextView textViewDetailedThirdInfoText;
	@BindView(R.id.detailed_fourth_info_text)
	TextView textViewDetailedFourthInfoText;
	@BindView(R.id.detailed_fifth_info_title)
	TextView textViewDetailedFifthInfoTitle;
	@BindView(R.id.detailed_fifth_info_text)
	TextView textViewDetailedFifthInfoText;
	@BindView(R.id.detailed_sixth_info_title)
	TextView textViewDetailedSixthInfoTitle;
	@BindView(R.id.detailed_sixth_info_text)
	TextView textViewDetailedSixthInfoText;
	@BindView(R.id.detailed_seventh_info_title)
	TextView textViewDetailedSeventhInfoTitle;
	@BindView(R.id.detailed_seventh_info_text)
	TextView textViewDetailedSeventhInfoText;
	@BindView(R.id.detailed_eighth_info_title)
	TextView textViewDetailedEighthInfoTitle;
	@BindView(R.id.detailed_eighth_info_text)
	TextView textViewDetailedEighthInfoText;
	// Setup intent value
	private int detailedMovieId;
	private String detailedMovieTitle;
	private int detailedMovieFavoriteState;
	// Setup boolean menu clickable state
	private boolean menuClickable = false;
	// Set layout value untuk dapat menjalankan process loading data
	@BindView(R.id.detailed_progress_bar)
	ProgressBar detailedProgressBar;
	
	@BindView(R.id.detailed_content)
	LinearLayout detailedContent;
	
	@BindView(R.id.detailed_app_bar)
	AppBarLayout detailedAppBarLayout;
	@BindView(R.id.detailed_toolbar)
	Toolbar detailedToolbar;
	
	// Gunakan BuildConfig untuk menjaga credential
	private String baseImageUrl = BuildConfig.POSTER_IMAGE_ITEM_URL;
	
	private DetailedMovieViewModel detailedMovieViewModel;
	
	private Observer <ArrayList <MovieItems>> detailedMovieObserver;
	
	// Constant untuk key dri drawable dan boolean state
	private static final String KEY_DRAWABLE_MARKED_AS_FAVORITE_STATE = "drawable_favorite_state";
	
	// Drawable Global variable to handle orientation changes
	private int drawableMenuMarkedAsFavouriteResourceId;
	
	// Initiate
	private MovieItems detailedMovieItem;
	// Initiate Item Helper (DML class)
	private FavoriteItemsHelper favoriteItemsHelper;
	
	// Request code
	public static final int REQUEST_CHANGE = 100;
	// Result code
	public static final int RESULT_CHANGE = 200;
	
	// Constant untuk dibawa ke FavoriteMovieFragment
	public static final String EXTRA_MOVIE_CHANGED_STATE = "extra_movie_changed_state";
	
	private Intent resultIntent;
	
	// Boolean untuk mengetahui apakah state dari movie item itu berganti atau tidak
	private boolean changedState;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		
		ButterKnife.bind(this);
		
		// Create instance dari FavoriteMovieItemsHelper
		favoriteItemsHelper = FavoriteItemsHelper.getInstance(getApplicationContext());
		
		setSupportActionBar(detailedToolbar);
		
		// todo: if statement for handling get intent value mode
		
		// Get intent untuk mendapatkan id dan title dari {@link MainActivity}
		detailedMovieId = getIntent().getIntExtra(MovieFragment.MOVIE_ID_DATA , 0);
		detailedMovieTitle = getIntent().getStringExtra(MovieFragment.MOVIE_TITLE_DATA);
		detailedMovieFavoriteState = getIntent().getIntExtra(MovieFragment.MOVIE_BOOLEAN_STATE_DATA , 0);
		
		// Cek jika savedInstanceState itu ada, jika iya, restore drawable marked as favorite icon state
		if(savedInstanceState != null){
			// todo: if statement for handling get value from bundle mode
			detailedMovieFavoriteState = savedInstanceState.getInt(KEY_DRAWABLE_MARKED_AS_FAVORITE_STATE);
			
		}
		
		// Cek kalo ada action bar
		if(getSupportActionBar() != null){
			// Set action bar title untuk DetailActivity
			getSupportActionBar().setTitle(detailedMovieTitle);
			// Set up button
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
		// Set visiblity of views ketika sedang dalam meretrieve data
		detailedContent.setVisibility(View.INVISIBLE);
		detailedProgressBar.setVisibility(View.VISIBLE);
		
		// todo: pake if statement untuk mengakses view model yg berbeda untuk menangani tv show sm movie
		
		// Panggil MovieViewModel dengan menggunakan ViewModelFactory sebagai parameter tambahan (dan satu-satunya pilihan) selain activity
		detailedMovieViewModel = ViewModelProviders.of(this , new DetailedMovieViewModelFactory(this.getApplication() , detailedMovieId)).get(DetailedMovieViewModel.class);
		
		// Buat observer object untuk mendisplay data ke UI
		detailedMovieObserver = createDetailedMovieObserver();
		
		// Tempelkan Observer ke LiveData object
		detailedMovieViewModel.getDetailedMovie().observe(this , detailedMovieObserver);
		
		
		// Add on offset changed listener ke AppBarLayout untuk mengatur
		// ketika app barnya itu gede/collapse
		detailedAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener(){
			boolean isAppBarLayoutShow = false;
			int scrollRange = - 1;
			
			@Override
			public void onOffsetChanged(AppBarLayout appBarLayout , int verticalOffset){
				// Jika scrollRange berada di posisi default atau -1, maka set value untuk scrollRange
				if(scrollRange == - 1){
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
	
	private Observer <ArrayList <MovieItems>> createDetailedMovieObserver(){
		Observer <ArrayList <MovieItems>> observer = new Observer <ArrayList <MovieItems>>(){
			@Override
			public void onChanged(@Nullable ArrayList <MovieItems> detailedMovieItems){
				// Ketika data selesai di load, maka kita akan mendapatkan data dan menghilangkan progress bar
				// yang menandakan bahwa loadingnya sudah selesai
				detailedContent.setVisibility(View.VISIBLE);
				detailedProgressBar.setVisibility(View.GONE);
				
				// Set semua data ke dalam detail activity
				// Load image jika ada poster path
				Picasso.get().load(baseImageUrl + detailedMovieItems.get(0).getMoviePosterPath()).into(imageViewDetailedPosterImage);
				
				textViewDetailedFirstInfoText.setText(detailedMovieItems.get(0).getMovieTitle());
				
				textViewDetailedSecondInfoText.setText("\"" + detailedMovieItems.get(0).getMovieTagline() + "\"");
				
				// Set textview content in detailed movie runtime to contain a variety of different colors
				Spannable statusWord = new SpannableString(getString(R.string.span_movie_detail_status) + " ");
				statusWord.setSpan(new ForegroundColorSpan(Color.BLACK) , 0 , statusWord.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				textViewDetailedThirdInfoText.setText(statusWord);
				Spannable statusDetailedMovie = new SpannableString(detailedMovieItems.get(0).getMovieStatus());
				statusDetailedMovie.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)) , 0 , statusDetailedMovie.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				textViewDetailedThirdInfoText.append(statusDetailedMovie);
				
				// Set textview content in detailed movie rating to contain a variety of different colors
				Spannable ratingWord = new SpannableString(getString(R.string.span_movie_detail_rating) + " ");
				ratingWord.setSpan(new ForegroundColorSpan(Color.BLACK) , 0 , ratingWord.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				textViewDetailedFourthInfoText.setText(ratingWord);
				Spannable ratingDetailedMovie = new SpannableString(detailedMovieItems.get(0).getMovieRatings());
				ratingDetailedMovie.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)) , 0 , ratingDetailedMovie.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				textViewDetailedFourthInfoText.append(ratingDetailedMovie);
				
				Spannable ratingFromWord = new SpannableString(" " + getString(R.string.span_movie_detail_from) + " ");
				ratingFromWord.setSpan(new ForegroundColorSpan(Color.BLACK) , 0 , ratingFromWord.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				textViewDetailedFourthInfoText.append(ratingFromWord);
				
				Spannable ratingDetailedMovieVotes = new SpannableString(detailedMovieItems.get(0).getMovieRatingsVote());
				ratingDetailedMovieVotes.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)) , 0 , ratingDetailedMovieVotes.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				textViewDetailedFourthInfoText.append(ratingDetailedMovieVotes);
				
				Spannable ratingVotesWord = new SpannableString(" " + getString(R.string.span_movie_detail_votes));
				ratingVotesWord.setSpan(new ForegroundColorSpan(Color.BLACK) , 0 , ratingVotesWord.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				textViewDetailedFourthInfoText.append(ratingVotesWord);
				
				textViewDetailedFifthInfoTitle.setText(getString(R.string.detailed_movie_languages_title));
				
				textViewDetailedFifthInfoText.setText(detailedMovieItems.get(0).getMovieLanguages());
				
				textViewDetailedSixthInfoTitle.setText(getString(R.string.detailed_movie_genres_title));
				
				textViewDetailedSixthInfoText.setText(detailedMovieItems.get(0).getMovieGenres());
				
				textViewDetailedSeventhInfoTitle.setText(getString(R.string.detailed_movie_release_date_title));
				
				textViewDetailedSeventhInfoText.setText(detailedMovieItems.get(0).getMovieReleaseDate());
				
				textViewDetailedEighthInfoTitle.setText(getString(R.string.detailed_movie_overview_title));
				
				textViewDetailedEighthInfoText.setText(detailedMovieItems.get(0).getMovieOverview());
				
				// Set value dari Item bedasarkan parameter lalu akses object pertama
				detailedMovieItem = detailedMovieItems.get(0);
				// Set menu clickable into true, literally setelah asynctask kelar,
				// maka menu bs d click
				menuClickable = true;
				// Update option menu to recall onPrepareOptionMenu method
				invalidateOptionsMenu();
			}
		};
		return observer;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		if(menuClickable){
			menu.findItem(R.id.action_marked_as_favorite).setEnabled(true);
		} else{
			menu.findItem(R.id.action_marked_as_favorite).setEnabled(false);
		}
		
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		// Inflate menu
		getMenuInflater().inflate(R.menu.menu_favourite , menu);
		// Cek jika value boolean nya itu adalah true, yang berarti menandakan movie favorite
		if(detailedMovieFavoriteState == 1){
			// Set drawable resource
			drawableMenuMarkedAsFavouriteResourceId = R.drawable.ic_favourite_on;
		} else{
			drawableMenuMarkedAsFavouriteResourceId = R.drawable.ic_favourite_off;
		}
		// Set inflated menu icon
		menu.findItem(R.id.action_marked_as_favorite).setIcon(drawableMenuMarkedAsFavouriteResourceId);
		// Get icon from drawable
		Drawable menuDrawable = menu.findItem(R.id.action_marked_as_favorite).getIcon();
		menuDrawable = DrawableCompat.wrap(menuDrawable);
		// Set color of menu icon to white, because the default was black
		DrawableCompat.setTint(menuDrawable , ContextCompat.getColor(this , R.color.colorWhite));
		menu.findItem(R.id.action_marked_as_favorite).setIcon(menuDrawable);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		// Create new Intent object
		resultIntent = new Intent();
		switch(item.getItemId()){
			case R.id.action_marked_as_favorite:
				// Check for current state of drawable menu icon
				if(detailedMovieFavoriteState != 1){
					// Change icon into marked as favourite
					drawableMenuMarkedAsFavouriteResourceId = R.drawable.ic_favourite_on;
					detailedMovieFavoriteState = 1;
					// Set current date value into MovieItem, where MovieItem added into Favorite
					detailedMovieItem.setDateAddedFavorite(getCurrentDate());
					// Set boolean state value into MovieItem
					detailedMovieItem.setFavoriteBooleanState(detailedMovieFavoriteState);
					
					// Cek jika value dari detailedMovieFavoriteState sama dengan value bawaan intent dengan key MOVIE_BOOLEAN_STATE_EXTRA
					if(detailedMovieFavoriteState == getIntent().getIntExtra(MovieFragment.MOVIE_BOOLEAN_STATE_DATA , 0)){
						// Set value changedState into false krn ga ad perubahan dengan state di bandingkan dengan value bawaan intent
						changedState = false;
					} else {
						changedState = true;
					}
					
					// Cek jika ada pergantian state dari sebuah data
					if(changedState){
						// Insert based on data
						// todo: handle for movie and tv show
						long newIdItem = favoriteItemsHelper.insertFavoriteMovieItem(detailedMovieItem);
						if(newIdItem > 0){
							// Bawa nilai ke intent
							resultIntent.putExtra(EXTRA_MOVIE_CHANGED_STATE, changedState);
							setResult(RESULT_CHANGE, resultIntent); // Set result that brings result code and intent
						}
					}
					
					// Update option menu
					invalidateOptionsMenu();
				} else{
					// Change icon into unmarked as favourite
					drawableMenuMarkedAsFavouriteResourceId = R.drawable.ic_favourite_off;
					detailedMovieFavoriteState = 0;
					// Set boolean state value into MovieItem
					detailedMovieItem.setFavoriteBooleanState(detailedMovieFavoriteState);
					// Cek jika value dari detailedMovieFavoriteState sama dengan value bawaan intent dengan key MOVIE_BOOLEAN_STATE_EXTRA
					if(detailedMovieFavoriteState == getIntent().getIntExtra(MovieFragment.MOVIE_BOOLEAN_STATE_DATA , 0)){
						changedState = false;
					} else {
						changedState = true;
					}
					
					// Cek jika ada pergantian state dari sebuah data
					if(changedState){
						// Remove from database
						// todo: handle for movie and tv show
						long deletedIdItem = favoriteItemsHelper.deleteFavoriteMovieItem(detailedMovieItem.getId());
						if(deletedIdItem > 0){
							// Bawa nilai ke intent
							resultIntent.putExtra(EXTRA_MOVIE_CHANGED_STATE, changedState);
							setResult(RESULT_CHANGE, resultIntent); // Set result that brings result code and intent
						}
					}
					
					// Update option menu
					invalidateOptionsMenu();
				}
				break;
			case R.id.home:
				// Finish method untuk membawa Intent ke MainActivity
				finish();
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed(){
		super.onBackPressed();
		// Finish method untuk membawa Intent ke MainActivity
		finish();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState){
		// Save drawable marked as favorite state
		outState.putInt(KEY_DRAWABLE_MARKED_AS_FAVORITE_STATE , detailedMovieFavoriteState);
		super.onSaveInstanceState(outState);
	}
	
	// Method tsb berguna untuk mendapatkan waktu dimana sebuah item di tambahkan
	private String getCurrentDate(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss" , Locale.getDefault());
		Date date = new Date();
		
		return dateFormat.format(date);
	}
}

