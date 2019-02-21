package com.example.cataloguemoviestorage.fragment;

import android.content.Intent;
import android.graphics.Movie;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.cataloguemoviestorage.DetailActivity;
import com.example.cataloguemoviestorage.LoadFavoriteMoviesCallback;
import com.example.cataloguemoviestorage.R;
import com.example.cataloguemoviestorage.adapter.MovieAdapter;
import com.example.cataloguemoviestorage.database.FavouriteMovieItemsHelper;
import com.example.cataloguemoviestorage.item.MovieItems;
import com.example.cataloguemoviestorage.support.MovieItemClickSupport;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteMovieFragment extends Fragment implements LoadFavoriteMoviesCallback{
	
	// Key untuk membawa data ke intent (data tidak d private untuk dapat diapplikasikan di berbagai Fragments dan diakses ke {@link DetailActivity})
	public static final String MOVIE_ID_DATA = "MOVIE_ID_DATA";
	public static final String MOVIE_TITLE_DATA = "MOVIE_TITLE_DATA";
	// Bikin constant (key) yang merepresent Parcelable object
	private static final String MOVIE_LIST_STATE = "movieListState";
	@BindView(R.id.rv_list)
	RecyclerView recyclerView;
	private MovieAdapter movieAdapter;
	@BindView(R.id.progress_bar)
	ProgressBar progressBar;
	private FavouriteMovieItemsHelper favouriteMovieItemsHelper;
	// Bikin linearlayout manager untuk dapat call onsaveinstancestate method
	private LinearLayoutManager favoriteLinearLayoutManager;
	
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater , @Nullable ViewGroup container , @Nullable Bundle savedInstanceState){
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_movie, container, false);
		ButterKnife.bind(this, view);
		return view;
	}
	
	@Override
	public void onViewCreated(@NonNull View view , @Nullable Bundle savedInstanceState){
		movieAdapter = new MovieAdapter(getContext());
		movieAdapter.notifyDataSetChanged();
		
		// Set background color untuk RecyclerView
		recyclerView.setBackgroundColor(getResources().getColor(R.color.colorWhite));
		
		if(getContext() != null){
			// Buat object DividerItemDecoration dan set drawable untuk DividerItemDecoration
			DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
			itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.item_divider));
			// Set divider untuk RecyclerView items
			recyclerView.addItemDecoration(itemDecorator);
		}
	}
	
	// Load database
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		if(getActivity().getApplicationContext() != null){
			favouriteMovieItemsHelper = FavouriteMovieItemsHelper.getInstance(getActivity().getApplicationContext());
			favouriteMovieItemsHelper.open();
		}
		
		if(savedInstanceState == null){
			// Panggil AsyncTask class
			new LoadFavoriteMoviesAsync(favouriteMovieItemsHelper, this).execute();
		} else {
			// Retrieve array list parcelable
			ArrayList<MovieItems> movieItemsList = savedInstanceState.getParcelableArrayList(MOVIE_LIST_STATE);
			if(movieItemsList != null){
				if(movieItemsList.size() > 0){
					movieAdapter.setData(movieItemsList);
				}
			}
		}


	}
	
	private void showSelectedMovieItems(MovieItems movieItems) {
		// Dapatkan id dan title bedasarkan ListView item
		int movieIdItem = movieItems.getId();
		String movieTitleItem = movieItems.getMovieTitle();
		Intent intentWithMovieIdData = new Intent(getActivity(), DetailActivity.class);
		// Bawa data untuk disampaikan ke {@link DetailActivity}
		intentWithMovieIdData.putExtra(MOVIE_ID_DATA, movieIdItem);
		intentWithMovieIdData.putExtra(MOVIE_TITLE_DATA, movieTitleItem);
		// Start activity tujuan bedasarkan intent object
		startActivity(intentWithMovieIdData);
	}
	
	
	
	@Override
	public void preExecute(){
		// Set progress bar visibility into visible and recyclerview visibility into visible to prepare loading data
		progressBar.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void postExecute(final ArrayList <MovieItems> movieItems){
		// Set LinearLayoutManager object value dengan memanggil LinearLayoutManager constructor
		favoriteLinearLayoutManager = new LinearLayoutManager(getContext());
		// Kita menggunakan LinearLayoutManager berorientasi vertical untuk RecyclerView
		recyclerView.setLayoutManager(favoriteLinearLayoutManager);
		// Ketika data selesai di load, maka kita akan mendapatkan data dan menghilangkan progress bar
		// yang menandakan bahwa loadingnya sudah selesai
		progressBar.setVisibility(View.GONE);
		// Set data into adapter
		movieAdapter.setData(movieItems);
		recyclerView.setAdapter(movieAdapter);
		// Set item click listener di dalam recycler view
		MovieItemClickSupport.addSupportToView(recyclerView).setOnItemClickListener(new MovieItemClickSupport.OnItemClickListener(){
			@Override
			public void onItemClicked(RecyclerView recyclerView , int position , View view){
				// Panggil method showSelectedMovieItems untuk mengakses DetailActivity bedasarkan data yang ada
				showSelectedMovieItems(movieItems.get(position));
				
			}
		});
		
	}
	
	@Override
	public void onSaveInstanceState(@NonNull Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList(MOVIE_LIST_STATE, movieAdapter.getmMovieData());
	}
	
	// Class tsb berguna untuk membaca data dari Database lalu mendisplay data yg ada di sana
	private static class LoadFavoriteMoviesAsync extends AsyncTask<Void, Void, ArrayList<MovieItems>>{
		// WeakReference digunakan karena AsyncTask akan dibuat dan dieksekusi scr bersamaan di method onCreate().
		// Selain itu, ketika Activity destroyed, Activity tsb dapat dikumpulkan oleh GarbageCollector, sehingga
		// dapat mencegah memory leak
		private final WeakReference<FavouriteMovieItemsHelper> weakFavoriteMovieItemsHelper;
		private final WeakReference<LoadFavoriteMoviesCallback> weakCallback;
		
		private LoadFavoriteMoviesAsync(FavouriteMovieItemsHelper favouriteMovieItemsHelper, LoadFavoriteMoviesCallback callback){
			weakFavoriteMovieItemsHelper = new WeakReference<>(favouriteMovieItemsHelper);
			weakCallback = new WeakReference<>(callback);
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			weakCallback.get().preExecute(); // memanggil method preExecute di interface {@link LoadFavoriteMoviesCallback}
		}
		
		@Override
		protected ArrayList<MovieItems> doInBackground(Void... voids) {
			return weakFavoriteMovieItemsHelper.get().getAllFavouriteMovieItems(); // Memanggil query method dari {@link FavouriteMovieItemsHelper}
		}
		
		@Override
		protected void onPostExecute(ArrayList<MovieItems> movieItems) {
			super.onPostExecute(movieItems);
			weakCallback.get().postExecute(movieItems); // memanggil method postExecute di interface {@link LoadFavoriteMoviesCallback}
		}
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		// Menutup koneksi terhadap SQL
		favouriteMovieItemsHelper.close();
	}
}
