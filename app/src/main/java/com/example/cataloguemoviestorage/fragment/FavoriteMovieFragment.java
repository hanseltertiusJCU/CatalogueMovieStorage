package com.example.cataloguemoviestorage.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
	public static final String MOVIE_BOOLEAN_STATE_DATA = "MOVIE_BOOLEAN_STATE_DATA";
	// Bikin constant (key) yang merepresent Parcelable object
	private static final String MOVIE_LIST_STATE = "movieListState";
	@BindView(R.id.rv_list)
	RecyclerView recyclerView;
	MovieAdapter movieAdapter;
	@BindView(R.id.progress_bar)
	ProgressBar progressBar;
	// Helper untuk membuka koneksi ke DB (mesti public biar bs akses ke fragment lainnya)
	FavouriteMovieItemsHelper favouriteMovieItemsHelper;
	// Bikin linearlayout manager untuk dapat call onsaveinstancestate method
	private LinearLayoutManager favoriteLinearLayoutManager;
	// Array list untuk menyimpan data bedasarkan Database
	private static ArrayList <MovieItems> favoriteMovieItemList;
	
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater , @Nullable ViewGroup container , @Nullable Bundle savedInstanceState){
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_movie , container , false);
		ButterKnife.bind(this , view);
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
			DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext() , DividerItemDecoration.VERTICAL);
			itemDecorator.setDrawable(ContextCompat.getDrawable(getContext() , R.drawable.item_divider));
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
		
		if(savedInstanceState != null){
			// Retrieve array list parcelable
			ArrayList <MovieItems> movieItemsList = savedInstanceState.getParcelableArrayList(MOVIE_LIST_STATE);
			if(movieItemsList != null){
				if(movieItemsList.size() > 0){
					movieAdapter.setData(movieItemsList);
				}
			}
		} else{
			// Lakukan AsyncTask utk meretrieve ArrayList yg isinya data dari database
			new LoadFavoriteMoviesAsync(favouriteMovieItemsHelper , this).execute();
		}
		
	}
	
	private void showSelectedMovieItems(MovieItems movieItems){
		// Dapatkan id dan title bedasarkan item di ArrayList
		int movieIdItem = movieItems.getId();
		String movieTitleItem = movieItems.getMovieTitle();
		// Item position untuk mengakses arraylist specific position
		int itemPosition = 0;
		
		// if statement untuk tahu bahwa idnya itu termasuk d dalam tabel ato tidak, looping pake arraylist
		// Cek jika size dari ArrayList itu lebih dari 0
		if(favoriteMovieItemList.size() > 0){
			for(int i = 0 ; i < favoriteMovieItemList.size() ; i++){
				if(movieIdItem == favoriteMovieItemList.get(i).getId()){
					favoriteMovieItemList.get(i).setFavoriteBooleanState(1);
					// Dapatin position dari arraylist jika idnya itu sama kyk id yg tersedia
					itemPosition = i;
					break;
				}
			}
		}
		Intent intentWithMovieIdData = new Intent(getActivity() , DetailActivity.class);
		// Bawa data untuk disampaikan ke {@link DetailActivity}
		intentWithMovieIdData.putExtra(MOVIE_ID_DATA , movieIdItem);
		intentWithMovieIdData.putExtra(MOVIE_TITLE_DATA , movieTitleItem);
		// Cek jika ArrayList ada data
		if(favoriteMovieItemList.size() > 0){
			intentWithMovieIdData.putExtra(MOVIE_BOOLEAN_STATE_DATA , favoriteMovieItemList.get(itemPosition).getFavoriteBooleanState());
			intentWithMovieIdData.putExtra(DetailActivity.EXTRA_MOVIE_ITEM_POSITION, itemPosition);
		}
		// Start activity tujuan bedasarkan intent object
		startActivityForResult(intentWithMovieIdData, DetailActivity.REQUEST_CHANGE);
	}
	
	
	@Override
	public void preExecute(){
		// Set progress bar visibility into visible and recyclerview visibility into visible to prepare loading data
		progressBar.setVisibility(View.VISIBLE);
		recyclerView.setVisibility(View.INVISIBLE);
	}
	
	@Override
	public void postExecute(final ArrayList <MovieItems> movieItems){
		if(movieItems.size() > 0){
			// Set LinearLayoutManager object value dengan memanggil LinearLayoutManager constructor
			favoriteLinearLayoutManager = new LinearLayoutManager(getContext());
			// Kita menggunakan LinearLayoutManager berorientasi vertical untuk RecyclerView
			recyclerView.setLayoutManager(favoriteLinearLayoutManager);
			// Ketika data selesai di load, maka kita akan mendapatkan data dan menghilangkan progress bar
			// yang menandakan bahwa loadingnya sudah selesai
			progressBar.setVisibility(View.GONE);
			recyclerView.setVisibility(View.VISIBLE);
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
		} else {
			// Ketika tidak ada data untuk display, set RecyclerView ke
			// invisible dan progress bar menjadi tidak ada
			recyclerView.setVisibility(View.INVISIBLE);
			progressBar.setVisibility(View.GONE);
		}
		
		
	}
	
	@Override
	public void onResume(){
		super.onResume();
//		// todo: mungkin pake if statement klo misalnya data berganti di helper, klo ga ya restore scroll position (basic logic)
//		// Lakukan AsyncTask kembali setelah berpindah dari {@link DetailActivity},
//		// karena ketika balik dr DetailActivity ke MainActivity,
//		// state Activity ke onResume = Fragment ke onResume juga
//		// Hal tsb berguna agar bs load kembali ke DB
//		new LoadFavoriteMoviesAsync(favouriteMovieItemsHelper , this).execute();
	}
	
	
	@Override
	public void onSaveInstanceState(@NonNull Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList(MOVIE_LIST_STATE, favoriteMovieItemList);
	}
	
	// Class tsb berguna untuk membaca data dari Database lalu mendisplay data yg ada di sana
	private static class LoadFavoriteMoviesAsync extends AsyncTask <Void, Void, ArrayList <MovieItems>>{
		// WeakReference digunakan karena AsyncTask akan dibuat dan dieksekusi scr bersamaan di method onCreate().
		// Selain itu, ketika Activity destroyed, Activity tsb dapat dikumpulkan oleh GarbageCollector, sehingga
		// dapat mencegah memory leak
		private final WeakReference <FavouriteMovieItemsHelper> weakFavoriteMovieItemsHelper;
		private final WeakReference <LoadFavoriteMoviesCallback> weakCallback;
		
		private LoadFavoriteMoviesAsync(FavouriteMovieItemsHelper favouriteMovieItemsHelper , LoadFavoriteMoviesCallback callback){
			weakFavoriteMovieItemsHelper = new WeakReference <>(favouriteMovieItemsHelper);
			weakCallback = new WeakReference <>(callback);
		}
		
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			weakCallback.get().preExecute(); // memanggil method preExecute di interface {@link LoadFavoriteMoviesCallback}
		}
		
		@Override
		protected ArrayList <MovieItems> doInBackground(Void... voids){
			favoriteMovieItemList = weakFavoriteMovieItemsHelper.get().getAllFavouriteMovieItems();
			return favoriteMovieItemList; // Memanggil query method dari {@link FavouriteMovieItemsHelper}
		}
		
		@Override
		protected void onPostExecute(ArrayList <MovieItems> movieItems){
			super.onPostExecute(movieItems);
			weakCallback.get().postExecute(movieItems); // memanggil method postExecute di interface {@link LoadFavoriteMoviesCallback}
		}
	}
	
	@Override
	public void onActivityResult(int requestCode , int resultCode , Intent data){
		super.onActivityResult(requestCode , resultCode , data);
		if(data != null){
			// Check for correct request code
			if(requestCode == DetailActivity.REQUEST_CHANGE){
				// Check for result code
				if(resultCode == DetailActivity.RESULT_ADD){
					// Tambahkan item ke adapter dan reset scroll position ke paling atas
					MovieItems newMovieItem = data.getParcelableExtra(DetailActivity.EXTRA_MOVIE_ITEM);
					movieAdapter.addItem(newMovieItem);
					recyclerView.smoothScrollToPosition(0);
				} else if(resultCode == DetailActivity.RESULT_DELETE){
					// Delete item dari adapter dan reset scroll position ke paling atas
					int deletedMovieItemPosition = data.getIntExtra(DetailActivity.EXTRA_MOVIE_ITEM_POSITION, 0);
					movieAdapter.removeItem(deletedMovieItemPosition);
					recyclerView.smoothScrollToPosition(0);
				}
			}
		}
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		// Menutup koneksi terhadap SQL
		favouriteMovieItemsHelper.close();
	}
}
