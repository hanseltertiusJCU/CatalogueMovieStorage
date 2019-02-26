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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cataloguemoviestorage.database.FavoriteItemsHelper;
import com.example.cataloguemoviestorage.entity.TvShowItem;
import com.example.cataloguemoviestorage.factory.DetailedMovieViewModelFactory;
import com.example.cataloguemoviestorage.factory.DetailedTvShowViewModelFactory;
import com.example.cataloguemoviestorage.fragment.MovieFragment;
import com.example.cataloguemoviestorage.entity.MovieItems;
import com.example.cataloguemoviestorage.fragment.TvShowFragment;
import com.example.cataloguemoviestorage.model.DetailedMovieViewModel;
import com.example.cataloguemoviestorage.model.DetailedTvShowViewModel;
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
	// Setup intent value untuk movie items
	private int detailedMovieId;
	private String detailedMovieTitle;
	private int detailedMovieFavoriteState;
	// Setup intent value untuk tv show items
	private int detailedTvShowId;
	private String detailedTvShowName;
	private int detailedTvShowFavoriteState;
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
	// Buat ViewModel untuk detailedMovieInfo
	private DetailedMovieViewModel detailedMovieViewModel;
	// Buat Observer untuk detailedMovieInfo
	private Observer <ArrayList <MovieItems>> detailedMovieObserver;
	// Buat ViewModel untuk detailedTvShowInfo
	private DetailedTvShowViewModel detailedTvShowViewModel;
	// Buat Observer untuk detailedTvShowInfo
	private Observer <ArrayList <TvShowItem>> detailedTvShowObserver;
	
	// Constant untuk key dri drawable dan boolean state
	private static final String KEY_DRAWABLE_MARKED_AS_FAVORITE_STATE = "drawable_favorite_state";
	
	// Drawable Global variable to handle orientation changes
	private int drawableMenuMarkedAsFavouriteResourceId;
	
	// Initiate MovieItems class untuk mengotak-atik value dr sebuah item di MovieItems class
	private MovieItems detailedMovieItem;
	// Initiate TvShowItem class untuk mengotak-atik value dr sebuah item di TvShowItem class
	private TvShowItem detailedTvShowItem;
	
	// Initiate Item Helper (DML class)
	private FavoriteItemsHelper favoriteItemsHelper;
	
	// Request code
	public static final int REQUEST_CHANGE = 100;
	// Result code
	public static final int RESULT_CHANGE = 200;
	
	// Constant untuk dibawa ke FavoriteMovieFragment ataupun FavoriteTvShowFragment
	public static final String EXTRA_CHANGED_STATE = "extra_changed_state";
	
	private Intent resultIntent;
	
	// Boolean untuk mengetahui apakah state dari movie item itu berganti atau tidak
	private boolean changedState;
	
	// String value untuk mengetahui mode data yg dibuka
	private String accessItemMode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		
		ButterKnife.bind(this);
		
		// Create instance dari FavoriteMovieItemsHelper
		favoriteItemsHelper = FavoriteItemsHelper.getInstance(getApplicationContext());
		
		setSupportActionBar(detailedToolbar);
		
		accessItemMode = getIntent().getStringExtra(MovieFragment.MODE_INTENT);
		
		// Cek untuk mode yg tepat
		if(accessItemMode.equals("open_movie_detail")){
			// Get intent untuk mendapatkan id, title serta favorite movie state dari {@link MainActivity}
			detailedMovieId = getIntent().getIntExtra(MovieFragment.MOVIE_ID_DATA , 0);
			detailedMovieTitle = getIntent().getStringExtra(MovieFragment.MOVIE_TITLE_DATA);
			detailedMovieFavoriteState = getIntent().getIntExtra(MovieFragment.MOVIE_BOOLEAN_STATE_DATA , 0);
		} else if(accessItemMode.equals("open_tv_show_detail")){
			// Get intent untuk mendapatkan id, title serta favorite tv show state dari {@link MainActivity}
			detailedTvShowId = getIntent().getIntExtra(TvShowFragment.TV_SHOW_ID_DATA , 0);
			detailedTvShowName = getIntent().getStringExtra(TvShowFragment.TV_SHOW_NAME_DATA);
			detailedTvShowFavoriteState = getIntent().getIntExtra(TvShowFragment.TV_SHOW_BOOLEAN_STATE_DATA , 0);
		}
		
		// Cek jika savedInstanceState itu ada, jika iya, restore drawable marked as favorite icon state
		if(savedInstanceState != null){
			if(accessItemMode.equals("open_movie_detail")){
				detailedMovieFavoriteState = savedInstanceState.getInt(KEY_DRAWABLE_MARKED_AS_FAVORITE_STATE);
			} else if(accessItemMode.equals("open_tv_show_detail")){
				detailedTvShowFavoriteState = savedInstanceState.getInt(KEY_DRAWABLE_MARKED_AS_FAVORITE_STATE);
			}
			
		}
		
		// Cek kalo ada action bar
		if(getSupportActionBar() != null){
			// Set action bar title untuk DetailActivity
			if(accessItemMode.equals("open_movie_detail")){
				getSupportActionBar().setTitle(detailedMovieTitle);
			} else if(accessItemMode.equals("open_tv_show_detail")){
				getSupportActionBar().setTitle(detailedTvShowName);
			}
			// Set up button
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
		// Set visiblity of views ketika sedang dalam meretrieve data
		detailedContent.setVisibility(View.INVISIBLE);
		detailedProgressBar.setVisibility(View.VISIBLE);
		
		// Mode untuk menangani ViewModel yg berbeda
		if(accessItemMode.equals("open_movie_detail")){
			// Panggil MovieViewModel dengan menggunakan ViewModelFactory sebagai parameter tambahan (dan satu-satunya pilihan) selain activity
			detailedMovieViewModel = ViewModelProviders.of(this , new DetailedMovieViewModelFactory(this.getApplication() , detailedMovieId)).get(DetailedMovieViewModel.class);
			// Buat observer object untuk mendisplay data ke UI
			detailedMovieObserver = createDetailedMovieObserver();
			// Tempelkan Observer ke LiveData object
			detailedMovieViewModel.getDetailedMovie().observe(this , detailedMovieObserver);
		} else if(accessItemMode.equals("open_tv_show_detail")){
			// Panggil MovieViewModel dengan menggunakan ViewModelFactory sebagai parameter tambahan (dan satu-satunya pilihan) selain activity
			detailedTvShowViewModel = ViewModelProviders.of(this , new DetailedTvShowViewModelFactory(this.getApplication() , detailedTvShowId)).get(DetailedTvShowViewModel.class);
			// Buat observer object untuk mendisplay data ke UI
			detailedTvShowObserver = createDetailedTvShowObserver();
			// Tempelkan Observer ke LiveData object
			detailedTvShowViewModel.getDetailedTvShow().observe(this , detailedTvShowObserver);
			
		}
		
		
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
	
	private Observer <ArrayList <TvShowItem>> createDetailedTvShowObserver(){
		Observer <ArrayList <TvShowItem>> observer = new Observer <ArrayList <TvShowItem>>(){
			@Override
			public void onChanged(@Nullable ArrayList <TvShowItem> detailedTvShowItems){
				// Ketika data selesai di load, maka kita akan mendapatkan data dan menghilangkan progress bar
				// yang menandakan bahwa loadingnya sudah selesai
				detailedContent.setVisibility(View.VISIBLE);
				detailedProgressBar.setVisibility(View.GONE);
				
				// Set semua data ke dalam detail activity
				// Load image jika ada poster path
				Picasso.get().load(baseImageUrl + detailedTvShowItems.get(0).getTvShowPosterPath()).into(imageViewDetailedPosterImage);
				
				textViewDetailedFirstInfoText.setText(detailedTvShowItems.get(0).getTvShowName());
				
				Spannable seasonsWord = new SpannableString(getString(R.string.span_tv_show_detail_number_of_seasons) + " ");
				seasonsWord.setSpan(new ForegroundColorSpan(Color.BLACK) , 0 , seasonsWord.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				textViewDetailedSecondInfoText.setText(seasonsWord);
				Spannable seasonsDetailedTvShow = new SpannableString(detailedTvShowItems.get(0).getTvShowSeasons());
				seasonsDetailedTvShow.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)) , 0 , seasonsDetailedTvShow.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				textViewDetailedSecondInfoText.append(seasonsDetailedTvShow);
				
				// Set textview content in detailed movie runtime to contain a variety of different colors
				Spannable episodesWord = new SpannableString(getString(R.string.span_tv_show_detail_number_of_episodes) + " ");
				episodesWord.setSpan(new ForegroundColorSpan(Color.BLACK) , 0 , episodesWord.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				textViewDetailedThirdInfoText.setText(episodesWord);
				Spannable episodesDetailedMovie = new SpannableString(detailedTvShowItems.get(0).getTvShowEpisodes());
				episodesDetailedMovie.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)) , 0 , episodesDetailedMovie.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				textViewDetailedThirdInfoText.append(episodesDetailedMovie);
				
				// Set textview content in detailed movie rating to contain a variety of different colors
				Spannable ratingWord = new SpannableString(getString(R.string.span_tv_show_detail_rating) + " ");
				ratingWord.setSpan(new ForegroundColorSpan(Color.BLACK) , 0 , ratingWord.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				textViewDetailedFourthInfoText.setText(ratingWord);
				Spannable tvShowDetailedMovie = new SpannableString(detailedTvShowItems.get(0).getTvShowRatings());
				tvShowDetailedMovie.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)) , 0 , tvShowDetailedMovie.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				textViewDetailedFourthInfoText.append(tvShowDetailedMovie);
				Spannable ratingFromWord = new SpannableString(" " + getString(R.string.span_tv_show_detail_from) + " ");
				ratingFromWord.setSpan(new ForegroundColorSpan(Color.BLACK) , 0 , ratingFromWord.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				textViewDetailedFourthInfoText.append(ratingFromWord);
				Spannable ratingDetailedTvShowVotes = new SpannableString(detailedTvShowItems.get(0).getTvShowRatingsVote());
				ratingDetailedTvShowVotes.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)) , 0 , ratingDetailedTvShowVotes.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				textViewDetailedFourthInfoText.append(ratingDetailedTvShowVotes);
				Spannable ratingVotesWord = new SpannableString(" " + getString(R.string.span_tv_show_detail_votes));
				ratingVotesWord.setSpan(new ForegroundColorSpan(Color.BLACK) , 0 , ratingVotesWord.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				textViewDetailedFourthInfoText.append(ratingVotesWord);
				
				textViewDetailedFifthInfoTitle.setText(getString(R.string.detailed_tv_show_networks_title));
				textViewDetailedFifthInfoText.setText(detailedTvShowItems.get(0).getTvShowNetworks());
				
				textViewDetailedSixthInfoTitle.setText(getString(R.string.detailed_tv_show_genres_title));
				textViewDetailedSixthInfoText.setText(detailedTvShowItems.get(0).getTvShowGenres());
				
				textViewDetailedSeventhInfoTitle.setText(getString(R.string.detailed_tv_show_first_air_date_title));
				textViewDetailedSeventhInfoText.setText(detailedTvShowItems.get(0).getTvShowFirstAirDate());
				
				textViewDetailedEighthInfoTitle.setText(getString(R.string.detailed_tv_show_overview_title));
				textViewDetailedEighthInfoText.setText(detailedTvShowItems.get(0).getTvShowOverview());
				
				// Set value dari Item bedasarkan parameter lalu akses object pertama
				detailedTvShowItem = detailedTvShowItems.get(0);
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
		getMenuInflater().inflate(R.menu.menu_favorite , menu);
		if(accessItemMode.equals("open_movie_detail")){
			// Cek jika value boolean nya itu adalah true, yang berarti menandakan movie favorite
			if(detailedMovieFavoriteState == 1){
				// Set drawable resource
				drawableMenuMarkedAsFavouriteResourceId = R.drawable.ic_favourite_on;
			} else{
				drawableMenuMarkedAsFavouriteResourceId = R.drawable.ic_favourite_off;
			}
		} else if(accessItemMode.equals("open_tv_show_detail")){
			// Cek jika value boolean nya itu adalah true, yang berarti menandakan tv show favorite
			if(detailedTvShowFavoriteState == 1){
				// Set drawable resource
				drawableMenuMarkedAsFavouriteResourceId = R.drawable.ic_favourite_on;
			} else{
				drawableMenuMarkedAsFavouriteResourceId = R.drawable.ic_favourite_off;
			}
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
				if(accessItemMode.equals("open_movie_detail")){ // Cek jika mode yg dibuka itu berada di Movie
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
						changedState = detailedMovieFavoriteState != getIntent().getIntExtra(MovieFragment.MOVIE_BOOLEAN_STATE_DATA, 0);
						
						// Cek jika ada pergantian state dari sebuah data
						if(changedState){
							// Insert based on data
							long newIdItem = favoriteItemsHelper.insertFavoriteMovieItem(detailedMovieItem);
							if(newIdItem > 0){
								// Bawa nilai ke intent
								resultIntent.putExtra(EXTRA_CHANGED_STATE , changedState);
								setResult(RESULT_CHANGE , resultIntent); // Set result that brings result code and intent
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
						changedState = detailedMovieFavoriteState != getIntent().getIntExtra(MovieFragment.MOVIE_BOOLEAN_STATE_DATA, 0);
						
						// Cek jika ada pergantian state dari sebuah data
						if(changedState){
							// Remove from database
							long deletedIdItem = favoriteItemsHelper.deleteFavoriteMovieItem(detailedMovieItem.getId());
							if(deletedIdItem > 0){
								// Bawa nilai ke intent
								resultIntent.putExtra(EXTRA_CHANGED_STATE , changedState);
								setResult(RESULT_CHANGE , resultIntent); // Set result that brings result code and intent
							}
						}
						
						// Update option menu
						invalidateOptionsMenu();
					}
				} else if(accessItemMode.equals("open_tv_show_detail")){ // Cek jika mode yg dibuka itu berada di TV Show
					if(detailedTvShowFavoriteState != 1){
						// Change icon into marked as favourite
						drawableMenuMarkedAsFavouriteResourceId = R.drawable.ic_favourite_on;
						detailedTvShowFavoriteState = 1;
						// Set current date value into TV Show item, where MovieItem added into Favorite
						detailedTvShowItem.setDateAddedFavorite(getCurrentDate());
						// Set boolean state value into TV Show item
						detailedTvShowItem.setFavoriteBooleanState(detailedTvShowFavoriteState);
						// Cek jika value dari detailedTvShowFavoriteState sama dengan value
						// bawaan intent dengan key TV_SHOW_BOOLEAN_STATE_EXTRA
						changedState = detailedTvShowFavoriteState != getIntent().getIntExtra(TvShowFragment.TV_SHOW_BOOLEAN_STATE_DATA, 0);
						
						// Cek jika ada pergantian state dari sebuah data
						if(changedState){
							// Insert based on data
							long newIdItem = favoriteItemsHelper.insertFavoriteTvShowItem(detailedTvShowItem);
							if(newIdItem > 0){
								// Bawa nilai ke intent
								resultIntent.putExtra(EXTRA_CHANGED_STATE , changedState);
								setResult(RESULT_CHANGE , resultIntent); // Set result that brings result code and intent
							}
						}
						
						// Update option menu
						invalidateOptionsMenu();
					} else{
						// Change icon into unmarked as favourite
						drawableMenuMarkedAsFavouriteResourceId = R.drawable.ic_favourite_off;
						detailedTvShowFavoriteState = 0;
						// Set boolean state value into MovieItem
						detailedTvShowItem.setFavoriteBooleanState(detailedTvShowFavoriteState);
						// Cek jika value dari detailedTvShowFavoriteState sama dengan value
						// bawaan intent dengan key TV_SHOW_BOOLEAN_STATE_EXTRA
						changedState = detailedTvShowFavoriteState != getIntent().getIntExtra(TvShowFragment.TV_SHOW_BOOLEAN_STATE_DATA, 0);
						
						// Cek jika ada pergantian state dari sebuah data
						if(changedState){
							// Remove from database
							long deletedIdItem = favoriteItemsHelper.deleteFavoriteTvShowItem(detailedTvShowItem.getId());
							if(deletedIdItem > 0){
								// Bawa nilai ke intent
								resultIntent.putExtra(EXTRA_CHANGED_STATE , changedState);
								setResult(RESULT_CHANGE , resultIntent); // Set result that brings result code and intent
							}
						}
						
						// Update option menu
						invalidateOptionsMenu();
					}
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
		if(accessItemMode.equals("open_movie_detail")){
			// Save drawable marked as favorite state for movie
			outState.putInt(KEY_DRAWABLE_MARKED_AS_FAVORITE_STATE , detailedMovieFavoriteState);
		} else if(accessItemMode.equals("open_tv_show_detail")){
			// Save drawable marked as favorite state for tv show
			outState.putInt(KEY_DRAWABLE_MARKED_AS_FAVORITE_STATE , detailedTvShowFavoriteState);
		}
		super.onSaveInstanceState(outState);
	}
	
	// Method tsb berguna untuk mendapatkan waktu dimana sebuah item di tambahkan
	private String getCurrentDate(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss" , Locale.getDefault());
		Date date = new Date();
		
		return dateFormat.format(date);
	}
}

