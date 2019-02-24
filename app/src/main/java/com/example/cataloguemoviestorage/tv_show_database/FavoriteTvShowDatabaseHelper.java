package com.example.cataloguemoviestorage.tv_show_database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class FavoriteTvShowDatabaseHelper extends SQLiteOpenHelper{
	
	// Name of database file
	private static String DATABASE_NAME = "favouritetvshows";
	
	// Version of database, guna untuk handle change in schema database
	private static final int DATABASE_VERSION = 1;
	
	FavoriteTvShowDatabaseHelper(@Nullable Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	// Create table statement
	private static final String SQL_CREATE_FAVORITE_TV_SHOW_STATEMENT =
			String.format("CREATE TABLE %s"
								  + " (%s INTEGER PRIMARY KEY,"
								  + " %s TEXT NOT NULL,"
								  + " %s TEXT NOT NULL,"
								  + " %s TEXT NOT NULL,"
								  + " %s TEXT NOT NULL,"
								  + " %s TEXT NOT NULL,"
								  + " %s TEXT NOT NULL,"
								  + " %s INTEGER NOT NULL DEFAULT 0)",
			FavoriteTvShowDatabaseContract.FavoriteTvShowItemColumns.TABLE_NAME,
			FavoriteTvShowDatabaseContract.FavoriteTvShowItemColumns._ID,
			FavoriteTvShowDatabaseContract.FavoriteTvShowItemColumns.TITLE_COLUMN,
			FavoriteTvShowDatabaseContract.FavoriteTvShowItemColumns.RATINGS_COLUMN,
			FavoriteTvShowDatabaseContract.FavoriteTvShowItemColumns.RELEASE_DATE_COLUMN,
			FavoriteTvShowDatabaseContract.FavoriteTvShowItemColumns.ORIGINAL_LANGUAGE_COLUMN,
			FavoriteTvShowDatabaseContract.FavoriteTvShowItemColumns.FILE_PATH_COLUMN,
			FavoriteTvShowDatabaseContract.FavoriteTvShowItemColumns.DATE_ADDED_COLUMN,
			FavoriteTvShowDatabaseContract.FavoriteTvShowItemColumns.FAVORITE_COLUMN
			);
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Execute create table statement
		db.execSQL(SQL_CREATE_FAVORITE_TV_SHOW_STATEMENT);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop table untuk delete table jika ada perubahan dari schema
		db.execSQL("DROP TABLE IF EXISTS " + FavoriteTvShowDatabaseContract.FavoriteTvShowItemColumns.TABLE_NAME);
		// Create table schema baru jika ada perubahan dari schema
		onCreate(db);
	}
}
