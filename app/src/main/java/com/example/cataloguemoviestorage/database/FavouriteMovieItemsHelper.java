package com.example.cataloguemoviestorage.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.cataloguemoviestorage.item.MovieItems;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static com.example.cataloguemoviestorage.database.FavouriteMovieDatabaseContract.FavouriteMovieItemColumns.DATE_ADDED_COLUMN;
import static com.example.cataloguemoviestorage.database.FavouriteMovieDatabaseContract.FavouriteMovieItemColumns.FAVORITE_COLUMN;
import static com.example.cataloguemoviestorage.database.FavouriteMovieDatabaseContract.FavouriteMovieItemColumns.FILE_PATH_COLUMN;
import static com.example.cataloguemoviestorage.database.FavouriteMovieDatabaseContract.FavouriteMovieItemColumns.ORIGINAL_LANGUAGE_COLUMN;
import static com.example.cataloguemoviestorage.database.FavouriteMovieDatabaseContract.FavouriteMovieItemColumns.RATINGS_COLUMN;
import static com.example.cataloguemoviestorage.database.FavouriteMovieDatabaseContract.FavouriteMovieItemColumns.RELEASE_DATE_COLUMN;
import static com.example.cataloguemoviestorage.database.FavouriteMovieDatabaseContract.FavouriteMovieItemColumns.TABLE_NAME;
import static com.example.cataloguemoviestorage.database.FavouriteMovieDatabaseContract.FavouriteMovieItemColumns.TITLE_COLUMN;

// Class ini berguna untuk memanipulasi value dari database (Data Manipulation Language)
public class FavouriteMovieItemsHelper {
    private static final String DATABASE_TABLE = TABLE_NAME;
    private static FavouriteMovieDatabaseHelper favouriteMovieDatabaseHelper;
    private static FavouriteMovieItemsHelper INSTANCE;

    private static SQLiteDatabase favouriteMovieDatabase;

    // Constructor untuk FavouriteMovieItemsHelper
    public FavouriteMovieItemsHelper(Context context) {
        favouriteMovieDatabaseHelper = new FavouriteMovieDatabaseHelper(context);
    }

    // Method tsb berguna untuk menginisiasi database
    public static FavouriteMovieItemsHelper getInstance(Context context){
        if(INSTANCE == null){
            synchronized (SQLiteOpenHelper.class){
                if(INSTANCE == null){
                    INSTANCE = new FavouriteMovieItemsHelper(context);
                }
            }
        }
        return INSTANCE;
    }

    // Open connection to database
    public void open() throws SQLException {
        favouriteMovieDatabase = favouriteMovieDatabaseHelper.getWritableDatabase();
    }

    // Close connection from database
    public void close(){
        favouriteMovieDatabaseHelper.close();

        // Cek jika database sedang connected, jika iya maka disconnect
        if(favouriteMovieDatabase.isOpen())
            favouriteMovieDatabase.close();
    }
    
    // Method untuk read data dari DB dengan menggunakan SQLiteDatabase query method
    public ArrayList<MovieItems> getAllFavouriteMovieItems(){
        ArrayList<MovieItems> favouriteMovieItemsArrayList = new ArrayList<>();
        
        // Call SQLiteDatabase query method dgn sort ID in ascending order (lowest to highest), where clausenya itu booleannya itu true
        Cursor cursor = favouriteMovieDatabase.query(DATABASE_TABLE,
                null,
                null,
                null,
                null,
                null,
                DATE_ADDED_COLUMN +  " DESC", // Data yg paling recent menjadi yang pertama
                null);
        // Memindahkan Cursor ke baris pertama
        cursor.moveToFirst();
        // Initialize variable yg return MovieItems
        MovieItems movieItems;
        if(cursor.getCount() > 0){
            do{
                // Set MovieItems object value sbg item dari ArrayList
                movieItems = new MovieItems();
                movieItems.setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
                movieItems.setMovieTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE_COLUMN)));
                movieItems.setMovieRatings(cursor.getString(cursor.getColumnIndexOrThrow(RATINGS_COLUMN)));
                movieItems.setMovieOriginalLanguage(cursor.getString(cursor.getColumnIndexOrThrow(ORIGINAL_LANGUAGE_COLUMN)));
                movieItems.setMovieReleaseDate(cursor.getString(cursor.getColumnIndexOrThrow(RELEASE_DATE_COLUMN)));
                movieItems.setMoviePosterPath(cursor.getString(cursor.getColumnIndexOrThrow(FILE_PATH_COLUMN)));
                movieItems.setDateAddedFavorite(cursor.getString(cursor.getColumnIndexOrThrow(DATE_ADDED_COLUMN)));
                movieItems.setMovieFavorite(cursor.getInt(cursor.getColumnIndexOrThrow(FAVORITE_COLUMN)) == movieItems.getFavoriteBooleanState()); // movieitems getfavoritestate
                // Add movie item data ke ArrayList
                favouriteMovieItemsArrayList.add(movieItems);
                // Memindahkan Cursor ke baris selanjutnya
                cursor.moveToNext();
                
            } while(!cursor.isAfterLast()); // Loop kondisi ini adalah ketika cursornya itu belum berada di baris terakhir
        }
        
        // Close the Cursor
        cursor.close();
        return favouriteMovieItemsArrayList;
    }

    // Method untuk insert data ke DB dengan menggunakan SQLiteDatabase insert method
    public long insertFavouriteMovieItem(MovieItems movieItems){
        // Create ContentValues object
        ContentValues movieItemValues = new ContentValues();
        // Insert value ke ContentValues object
        movieItemValues.put(_ID, movieItems.getId());
        movieItemValues.put(TITLE_COLUMN, movieItems.getMovieTitle());
        movieItemValues.put(RATINGS_COLUMN, movieItems.getMovieRatings());
        movieItemValues.put(ORIGINAL_LANGUAGE_COLUMN, movieItems.getMovieOriginalLanguage());
        movieItemValues.put(RELEASE_DATE_COLUMN, movieItems.getMovieReleaseDate());
        movieItemValues.put(FILE_PATH_COLUMN, movieItems.getMoviePosterPath());
        movieItemValues.put(DATE_ADDED_COLUMN, movieItems.getDateAddedFavorite());
        movieItemValues.put(FAVORITE_COLUMN, movieItems.getFavoriteBooleanState());
        // Execute SQLiteDatabase insert method
        return favouriteMovieDatabase.insert(DATABASE_TABLE, null, movieItemValues);
    }
    
    // Method untuk delete data dari DB dengan menggunakan SQLiteDatabase delete method
    public int deleteFavouriteMovieItem(int id){
        return favouriteMovieDatabase.delete(TABLE_NAME, _ID + " = '" + id + "'", null);
    }
}
