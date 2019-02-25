package com.example.cataloguemoviestorage;

import com.example.cataloguemoviestorage.entity.TvShowItem;

import java.util.ArrayList;

public interface LoadFavoriteTvShowCallback{
	void preExecute();
	void postExecute(ArrayList<TvShowItem> tvShowItems);
}
