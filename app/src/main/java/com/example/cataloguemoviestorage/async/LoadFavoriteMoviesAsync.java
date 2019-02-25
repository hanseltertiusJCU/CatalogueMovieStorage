package com.example.cataloguemoviestorage.async;

import android.os.AsyncTask;

import com.example.cataloguemoviestorage.LoadFavoriteMoviesCallback;
import com.example.cataloguemoviestorage.database.FavoriteItemsHelper;
import com.example.cataloguemoviestorage.entity.MovieItems;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

// Class tsb berguna untuk membaca data dari Database lalu mendisplay data yg ada di sana
public class LoadFavoriteMoviesAsync extends AsyncTask<Void, Void, ArrayList<MovieItems>>{
	// WeakReference digunakan karena AsyncTask akan dibuat dan dieksekusi scr bersamaan di method onCreate().
	// Selain itu, ketika Activity destroyed, Activity tsb dapat dikumpulkan oleh GarbageCollector, sehingga
	// dapat mencegah memory leak
	final WeakReference <FavoriteItemsHelper> weakFavoriteMovieItemsHelper;
	final WeakReference <LoadFavoriteMoviesCallback> weakCallback;
	ArrayList<MovieItems> favoriteMovieItemList;
	
	public LoadFavoriteMoviesAsync(FavoriteItemsHelper favoriteItemsHelper , LoadFavoriteMoviesCallback callback){
		weakFavoriteMovieItemsHelper = new WeakReference<>(favoriteItemsHelper);
		weakCallback = new WeakReference <>(callback);
	}
	
	@Override
	protected void onPreExecute(){
		super.onPreExecute();
		weakCallback.get().preExecute(); // memanggil method preExecute di interface {@link LoadFavoriteMoviesCallback}
	}
	
	@Override
	protected ArrayList <MovieItems> doInBackground(Void... voids){
		favoriteMovieItemList = weakFavoriteMovieItemsHelper.get().getAllFavoriteMovieItems();
		return favoriteMovieItemList; // Memanggil query method dari {@link FavoriteItemsHelper}
	}
	
	@Override
	protected void onPostExecute(ArrayList <MovieItems> movieItems){
		super.onPostExecute(movieItems);
		weakCallback.get().postExecute(movieItems); // memanggil method postExecute di interface {@link LoadFavoriteMoviesCallback}
	}
}