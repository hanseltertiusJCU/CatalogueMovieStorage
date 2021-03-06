package com.example.cataloguemoviestorage.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.example.cataloguemoviestorage.async.LoadFavoriteMoviesAsync;
import com.example.cataloguemoviestorage.database.FavoriteItemsHelper;
import com.example.cataloguemoviestorage.entity.MovieItem;
import com.example.cataloguemoviestorage.model.MovieViewModel;
import com.example.cataloguemoviestorage.support.ItemClickSupport;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFragment extends Fragment implements LoadFavoriteMoviesCallback {
	
	// Key untuk membawa data ke intent (data tidak d private untuk dapat diapplikasikan di berbagai Fragments dan diakses ke {@link DetailActivity})
	public static final String MOVIE_ID_DATA = "MOVIE_ID_DATA";
	public static final String MOVIE_TITLE_DATA = "MOVIE_TITLE_DATA";
	public static final String MOVIE_BOOLEAN_STATE_DATA = "MOVIE_BOOLEAN_STATE_DATA";
	// Constant untuk represent mode agar membuka data tertentu
	public static final String MODE_INTENT = "mode_intent";
	// Bikin constant (key) yang merepresent Parcelable object
	private static final String MOVIE_LIST_STATE = "movieListState";
	@BindView(R.id.rv_movie_item_list)
	RecyclerView recyclerView;
	@BindView(R.id.progress_bar)
	ProgressBar progressBar;
	private MovieAdapter movieAdapter;
	// Bikin parcelable yang berguna untuk menyimpan lalu merestore position
	private Parcelable mMovieListState = null;
	// Helper untuk membuka koneksi ke DB
	private FavoriteItemsHelper favoriteItemsHelper;
	// Bikin linearlayout manager untuk dapat call onsaveinstancestate method
	private LinearLayoutManager movieLinearLayoutManager;
	
	public MovieFragment() {
		// Required empty public constructor
	}
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Cek jika ada ApplicationContext, jika ada maka buka koneksi ke ItemHelper
		if(Objects.requireNonNull(getActivity()).getApplicationContext() != null) {
			favoriteItemsHelper = FavoriteItemsHelper.getInstance(getActivity().getApplicationContext());
			favoriteItemsHelper.open();
		}
		
	}
	
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_movie, container, false);
		ButterKnife.bind(this, view);
		return view;
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		// Set LinearLayoutManager object value dengan memanggil LinearLayoutManager constructor
		movieLinearLayoutManager = new LinearLayoutManager(getContext());
		// Ukuran data recycler view sama
		recyclerView.setHasFixedSize(true);
		// Kita menggunakan LinearLayoutManager berorientasi vertical untuk RecyclerView
		recyclerView.setLayoutManager(movieLinearLayoutManager);
		
		// Initiate movie adapter
		movieAdapter = new MovieAdapter(getContext());
		// Notify when data changed into adapter
		movieAdapter.notifyDataSetChanged();
		
		// Set empty adapter agar dapat di rotate
		recyclerView.setAdapter(movieAdapter);
		
		// Set background color untuk RecyclerView
		recyclerView.setBackgroundColor(getResources().getColor(R.color.colorWhite));
		
		if(getContext() != null) {
			// Buat object DividerItemDecoration dan set drawable untuk DividerItemDecoration
			DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
			itemDecorator.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getContext(), R.drawable.item_divider)));
			// Set divider untuk RecyclerView items
			recyclerView.addItemDecoration(itemDecorator);
		}
		
		// Set visiblity of views ketika sedang dalam meretrieve data
		recyclerView.setVisibility(View.INVISIBLE);
		progressBar.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// Cek jika Bundle exist, jika iya maka kita metretrieve list state as well as
		// list/item positions (scroll position)
		if(savedInstanceState != null) {
			mMovieListState = savedInstanceState.getParcelable(MOVIE_LIST_STATE);
		} else {
			// Lakukan AsyncTask utk meretrieve ArrayList yg isinya data dari database (table movie item)
			new LoadFavoriteMoviesAsync(favoriteItemsHelper, this).execute();
		}
		
		// Dapatkan ViewModel yang tepat dari ViewModelProviders
		MovieViewModel movieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
		
		// Panggil method createObserver untuk return Observer object
		Observer<ArrayList<MovieItem>> movieObserver = createObserver();
		
		// Tempelkan Observer ke LiveData object
		movieViewModel.getMovies().observe(this, movieObserver);
		
	}
	
	private void showSelectedMovieItems(MovieItem movieItem) {
		// Dapatkan id dan title bedasarkan ListView item
		int movieIdItem = movieItem.getId();
		String movieTitleItem = movieItem.getMovieTitle();
		// Item position untuk mengakses arraylist specific position
		int itemPosition = 0;
		// if statement untuk tahu bahwa idnya itu termasuk d dalam tabel ato tidak, looping pake arraylist
		// Cek jika size dari ArrayList itu lebih dari 0
		if(FavoriteMovieFragment.favMovieListData.size() > 0) {
			for(int i = 0 ; i < FavoriteMovieFragment.favMovieListData.size() ; i++) {
				if(movieIdItem == FavoriteMovieFragment.favMovieListData.get(i).getId()) {
					FavoriteMovieFragment.favMovieListData.get(i).setFavoriteBooleanState(1);
					// Dapatin position dari arraylist jika idnya itu sama kyk id yg tersedia
					itemPosition = i;
					break;
				}
			}
		}
		// Tentukan bahwa kita ingin membuka data Movie
		String modeItem = "open_movie_detail";
		
		Intent intentWithMovieIdData = new Intent(getActivity(), DetailActivity.class);
		// Bawa data untuk disampaikan ke {@link DetailActivity}
		intentWithMovieIdData.putExtra(MOVIE_ID_DATA, movieIdItem);
		intentWithMovieIdData.putExtra(MOVIE_TITLE_DATA, movieTitleItem);
		// Cek jika ArrayList ada data
		if(FavoriteMovieFragment.favMovieListData.size() > 0) {
			intentWithMovieIdData.putExtra(MOVIE_BOOLEAN_STATE_DATA, FavoriteMovieFragment.favMovieListData.get(itemPosition).getFavoriteBooleanState());
		}
		intentWithMovieIdData.putExtra(MODE_INTENT, modeItem);
		// Start activity tujuan bedasarkan intent object dan bawa request code
		// REQUEST_CHANGE untuk onActivityResult
		startActivityForResult(intentWithMovieIdData, DetailActivity.REQUEST_CHANGE);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		// Cek jika Parcelable itu exist, jika iya, maka update layout manager dengan memasukkan
		// Parcelable sebagai input parameter
		if(mMovieListState != null) {
			movieLinearLayoutManager.onRestoreInstanceState(mMovieListState);
		}
	}
	
	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		// Cek jika movieLinearLayoutManager itu ada, jika tidak maka kita tidak akan ngapa2in
		// di onSaveInstanceState
		if(movieLinearLayoutManager != null) {
			// Save list state/ scroll position dari list
			mMovieListState = movieLinearLayoutManager.onSaveInstanceState();
			outState.putParcelable(MOVIE_LIST_STATE, mMovieListState);
		}
		
	}
	
	// Callback method dari Interface LoadFavoriteMoviesCallback
	@Override
	public void preExecute() {
		// Method tsb tidak melakukan apa2
	}
	
	@Override
	public void postExecute(ArrayList<MovieItem> movieItems) {
		// Bikin ArrayList global variable sama dengan hasil dari AsyncTask class
		FavoriteMovieFragment.favMovieListData = movieItems;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(data != null) {
			// Check for correct request code
			if(requestCode == DetailActivity.REQUEST_CHANGE) {
				// Check for result code
				if(resultCode == DetailActivity.RESULT_CHANGE) {
					// Tambahkan item ke adapter dan reset scroll position ke paling atas
					boolean changedDataState = data.getBooleanExtra(DetailActivity.EXTRA_CHANGED_STATE, false);
					// Cek jika value dari changedDataState itu true
					if(changedDataState) {
						if(Objects.requireNonNull(getActivity()).getSupportFragmentManager() != null) {
							// Dapatin position fragment dari FavoriteMovieFragment di ViewPager since ViewPager menampung list dari Fragments
							FavoriteMovieFragment favoriteMovieFragment = (FavoriteMovieFragment) getActivity().getSupportFragmentManager().getFragments().get(2);
							// Cek jika favoriteMovieFragment itu ada
							if(favoriteMovieFragment != null) {
								// Komunikasi dengan FavoriteMovieFragment dengan memanggil onActivityResult method di FavoriteMovieFragment
								favoriteMovieFragment.onActivityResult(requestCode, resultCode, data);
							}
						}
					}
				}
			}
		}
	}
	
	
	// Method tsb berguna untuk membuat observer
	public Observer<ArrayList<MovieItem>> createObserver() {
		// Buat Observer yang gunanya untuk update UI
		return new Observer<ArrayList<MovieItem>>() {
			@Override
			public void onChanged(@Nullable final ArrayList<MovieItem> movieItems) {
				// Ketika data selesai di load, maka kita akan mendapatkan data dan menghilangkan progress bar
				// yang menandakan bahwa loadingnya sudah selesai
				recyclerView.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
				// Set data ke adapter
				movieAdapter.setData(movieItems);
				// Set item click listener di dalam recycler view
				ItemClickSupport.addSupportToView(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
					// Implement interface method
					@Override
					public void onItemClicked(RecyclerView recyclerView, int position, View view) {
						// Panggil method showSelectedMovieItems untuk mengakses DetailActivity bedasarkan data yang ada
						if(movieItems != null) {
							showSelectedMovieItems(movieItems.get(position));
						}
					}
				});
			}
		};
	}
	
}