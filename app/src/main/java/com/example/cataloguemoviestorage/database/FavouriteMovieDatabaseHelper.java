package com.example.cataloguemoviestorage.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

// Class ini berguna untuk mengimplementasikan definisi dari database (Data Definition Language)
public class FavouriteMovieDatabaseHelper extends SQLiteOpenHelper {

    // Name of database file
    public static String DATABASE_NAME = "favouritemovies";

    // Version of database, guna untuk handle change in schema database
    public static final int DATABASE_VERSION = 1;

    // Constructor from DB Helper
    public FavouriteMovieDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create table statement
    private static final String SQL_CREATE_FAVOURITE_MOVIE_STATEMENT = String.format("CREATE TABLE %s"
            + " (%s INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " %s INTEGER NOT NULL,"
            + " %s TEXT NOT NULL,"
            + " %s TEXT NOT NULL,"
            + " %s TEXT NOT NULL,"
            + " %s TEXT NOT NULL,"
            + " %s TEXT NOT NULL)",
            FavouriteMovieDatabaseContract.FavouriteMovieItemColumns.TABLE_NAME,
            FavouriteMovieDatabaseContract.FavouriteMovieItemColumns._ID,
            FavouriteMovieDatabaseContract.FavouriteMovieItemColumns.ID_COLUMN,
            FavouriteMovieDatabaseContract.FavouriteMovieItemColumns.TITLE_COLUMN,
            FavouriteMovieDatabaseContract.FavouriteMovieItemColumns.RATINGS_COLUMN,
            FavouriteMovieDatabaseContract.FavouriteMovieItemColumns.RELEASE_DATE_COLUMN,
            FavouriteMovieDatabaseContract.FavouriteMovieItemColumns.ORIGINAL_LANGUAGE_COLUMN,
            FavouriteMovieDatabaseContract.FavouriteMovieItemColumns.FILE_PATH_COLUMN
    );

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Execute create table statement
        db.execSQL(SQL_CREATE_FAVOURITE_MOVIE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop table untuk delete table jika ada perubahan dari schema
        db.execSQL("DROP TABLE IF EXISTS " + FavouriteMovieDatabaseContract.FavouriteMovieItemColumns.TABLE_NAME);
        // Create table schema baru jika ada perubahan dari schema
        onCreate(db);
    }
}
