package com.example.cataloguemoviestorage;

import com.example.cataloguemoviestorage.item.MovieItems;

import java.util.ArrayList;

public interface LoadFavoriteMoviesCallback{
	void preExecute();
	void postExecute(ArrayList<MovieItems> movieItems);
}
