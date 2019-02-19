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
import com.example.cataloguemoviestorage.R;
import com.example.cataloguemoviestorage.adapter.MovieAdapter;
import com.example.cataloguemoviestorage.item.MovieItems;
import com.example.cataloguemoviestorage.model.UpcomingViewModel;
import com.example.cataloguemoviestorage.support.MovieItemClickSupport;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpcomingMovieFragment extends Fragment {

    // Key untuk membawa data ke intent (data tidak d private untuk dapat diakses ke {@link DetailActivity})
    public static final String MOVIE_ID_DATA = "MOVIE_ID_DATA";
    public static final String MOVIE_TITLE_DATA = "MOVIE_TITLE_DATA";
    // Bikin constant (key) yang merepresent Parcelable object
    private static final String MOVIE_LIST_STATE = "movieListState";
    @BindView(R.id.rv_list) RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    @BindView(R.id.progress_bar) ProgressBar progressBar;
    private UpcomingViewModel upcomingViewModel;
    // Bikin parcelable yang berguna untuk menyimpan lalu merestore position
    private Parcelable mUpcomingListState = null;
    // Bikin linearlayout manager untuk dapat call onsaveinstancestate dan onrestoreinstancestate method
    private LinearLayoutManager upcomingLinearLayoutManager;

    private Observer<ArrayList<MovieItems>> upcomingObserver;

    public UpcomingMovieFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        movieAdapter = new MovieAdapter(getContext());
        movieAdapter.notifyDataSetChanged();

        // Set background color untuk RecyclerView
        recyclerView.setBackgroundColor(getResources().getColor(R.color.color_white));

        if(getContext() != null){
            // Buat object DividerItemDecoration dan set drawable untuk DividerItemDecoration
            DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
            itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.item_divider));

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
        if (savedInstanceState != null) {
            mUpcomingListState = savedInstanceState.getParcelable(MOVIE_LIST_STATE);
        }

        // Dapatkan ViewModel yang tepat dari ViewModelProviders
        upcomingViewModel = ViewModelProviders.of(this).get(UpcomingViewModel.class);

        // Panggil method createObserver untuk return Observer object
        upcomingObserver = createObserver();

        // Tempelkan Observer ke LiveData object
        upcomingViewModel.getUpcomingMovies().observe(this, upcomingObserver);
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
    public void onResume() {
        super.onResume();
        // Cek jika Parcelable itu exist, jika iya, maka update layout manager dengan memasukkan
        // Parcelable sebagai input parameter
        if (mUpcomingListState != null) {
            upcomingLinearLayoutManager.onRestoreInstanceState(mUpcomingListState);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Cek jika upcomingLinearLayoutManager itu ada, jika tidak maka kita tidak akan ngapa2in
        // di onSaveInstanceState
        if (upcomingLinearLayoutManager != null) {
            // Save list state/scroll position dari list
            mUpcomingListState = upcomingLinearLayoutManager.onSaveInstanceState();
            outState.putParcelable(MOVIE_LIST_STATE, mUpcomingListState);
        }
    }

    // Method tsb berguna untuk membuat observer
    public Observer<ArrayList<MovieItems>> createObserver() {
        // Buat Observer yang gunanya untuk update UI
        return new Observer<ArrayList<MovieItems>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<MovieItems> movieItems) {
                // Set LinearLayoutManager object value dengan memanggil LinearLayoutManager constructor
                upcomingLinearLayoutManager = new LinearLayoutManager(getContext());
                // Kita menggunakan LinearLayoutManager berorientasi vertical untuk RecyclerView
                recyclerView.setLayoutManager(upcomingLinearLayoutManager);
                // Ketika data selesai di load, maka kita akan mendapatkan data dan menghilangkan progress bar
                // yang menandakan bahwa loadingnya sudah selesai
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                movieAdapter.setData(movieItems);
                recyclerView.setAdapter(movieAdapter);
                // Set item click listener di dalam recycler view
                MovieItemClickSupport.addSupportToView(recyclerView).setOnItemClickListener(new MovieItemClickSupport.OnItemClickListener() {
                    // Implement interface method
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View view) {
                        // Panggil method showSelectedMovieItems untuk mengakses DetailActivity bedasarkan data yang ada
                        showSelectedMovieItems(movieItems.get(position));
                    }
                });
            }
        };
    }
}
