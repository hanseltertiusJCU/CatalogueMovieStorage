package com.example.cataloguemoviestorage.tv_show_database;

import android.provider.BaseColumns;

public class FavoriteTvShowDatabaseContract{
	// Class tsb berguna untuk membuat nama tabel serta columnnya dan
	// tidak perlu initiate _ID krn ud otomatis dr sananya
	static final class FavoriteTvShowItemColumns implements BaseColumns{
		// Nama tabel dari database
		static String TABLE_NAME = "favourite_tv_shows";
		// Nama columns dari database
		static String NAME_COLUMN = "name";
		static String RATINGS_COLUMN = "ratings";
		static String FIRST_AIR_DATE_COLUMN = "first_air_date";
		static String ORIGINAL_LANGUAGE_COLUMN = "original_language";
		static String FILE_PATH_COLUMN = "file_path";
		static String DATE_ADDED_COLUMN = "date_added";
		static String FAVORITE_COLUMN = "favorite";
	}
}
