package com.example.cataloguemoviestorage.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cataloguemoviestorage.R;

import butterknife.ButterKnife;

public class FavoriteMovieFragment extends Fragment{
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
		super.onViewCreated(view , savedInstanceState);
	}
}
