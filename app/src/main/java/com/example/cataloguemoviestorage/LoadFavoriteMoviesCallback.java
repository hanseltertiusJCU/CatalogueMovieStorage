package com.example.cataloguemoviestorage;

import com.example.cataloguemoviestorage.entity.MovieItem;

import java.util.ArrayList;

public interface LoadFavoriteMoviesCallback{
	void preExecute();
	
	void postExecute(ArrayList <MovieItem> movieItems);
}
