package com.example.cataloguemoviestorage.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.cataloguemoviestorage.entity.MovieItems;
import com.example.cataloguemoviestorage.entity.TvShowItem;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static com.example.cataloguemoviestorage.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_TABLE_NAME;
import static com.example.cataloguemoviestorage.database.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_TABLE_NAME;

// Class ini berguna untuk memanipulasi value dari database (Data Manipulation Language)
public class FavoriteItemsHelper{
    private static final String DATABASE_MOVIE_TABLE = MOVIE_TABLE_NAME;
    private static final String DATABASE_TV_SHOW_TABLE = TV_SHOW_TABLE_NAME;
    private static FavoriteDatabaseHelper favoriteDatabaseHelper;
    private static FavoriteItemsHelper INSTANCE;

    private static SQLiteDatabase favoriteDatabase;

    // Constructor untuk FavoriteItemsHelper
    public FavoriteItemsHelper(Context context) {
        favoriteDatabaseHelper = new FavoriteDatabaseHelper(context);
    }

    // Method tsb berguna untuk menginisiasi database
    public static FavoriteItemsHelper getInstance(Context context){
        if(INSTANCE == null){
            synchronized (SQLiteOpenHelper.class){
                if(INSTANCE == null){
                    INSTANCE = new FavoriteItemsHelper(context);
                }
            }
        }
        return INSTANCE;
    }

    // Open connection to database
    public void open() throws SQLException {
        favoriteDatabase = favoriteDatabaseHelper.getWritableDatabase();
    }

    // Close connection from database
    public void close(){
        favoriteDatabaseHelper.close();

        // Cek jika database sedang connected, jika iya maka disconnect
        if(favoriteDatabase.isOpen())
            favoriteDatabase.close();
    }
    
    // Method untuk read data dari DB dengan menggunakan SQLiteDatabase query method (table movie item)
    public ArrayList<MovieItems> getAllFavoriteMovieItems(){
        ArrayList<MovieItems> favoriteMovieItemsArrayList = new ArrayList<>();
        
        // Call SQLiteDatabase query method dgn sort Date Added Column in descending order (most recent to least recent)
        Cursor cursor = favoriteDatabase.query(DATABASE_MOVIE_TABLE ,
                null,
                null,
                null,
                null,
                null,
                FavoriteDatabaseContract.FavoriteMovieItemColumns.DATE_ADDED_COLUMN +  " DESC", // Data yg paling recent menjadi yang pertama
                null);
        // Memindahkan Cursor ke baris pertama
        cursor.moveToFirst();
        // Initialize variable yg return MovieItems
        MovieItems movieItems;
        if(cursor.getCount() > 0){
            do{
                // Set MovieItems object value sbg item dari ArrayList
                movieItems = new MovieItems();
                movieItems.setId(cursor.getInt(cursor.getColumnIndexOrThrow(FavoriteDatabaseContract.FavoriteMovieItemColumns._ID)));
                movieItems.setMovieTitle(cursor.getString(cursor.getColumnIndexOrThrow(FavoriteDatabaseContract.FavoriteMovieItemColumns.TITLE_COLUMN)));
                movieItems.setMovieRatings(cursor.getString(cursor.getColumnIndexOrThrow(FavoriteDatabaseContract.FavoriteMovieItemColumns.RATINGS_COLUMN)));
                movieItems.setMovieOriginalLanguage(cursor.getString(cursor.getColumnIndexOrThrow(FavoriteDatabaseContract.FavoriteMovieItemColumns.ORIGINAL_LANGUAGE_COLUMN)));
                movieItems.setMovieReleaseDate(cursor.getString(cursor.getColumnIndexOrThrow(FavoriteDatabaseContract.FavoriteMovieItemColumns.RELEASE_DATE_COLUMN)));
                movieItems.setMoviePosterPath(cursor.getString(cursor.getColumnIndexOrThrow(FavoriteDatabaseContract.FavoriteMovieItemColumns.FILE_PATH_COLUMN)));
                movieItems.setDateAddedFavorite(cursor.getString(cursor.getColumnIndexOrThrow(FavoriteDatabaseContract.FavoriteMovieItemColumns.DATE_ADDED_COLUMN)));
                movieItems.setMovieFavorite(cursor.getInt(cursor.getColumnIndexOrThrow(FavoriteDatabaseContract.FavoriteMovieItemColumns.FAVORITE_COLUMN)) == movieItems.getFavoriteBooleanState()); // movieitems getfavoritestate
                // Add movie item data ke ArrayList
                favoriteMovieItemsArrayList.add(movieItems);
                // Memindahkan Cursor ke baris selanjutnya
                cursor.moveToNext();
                
            } while(!cursor.isAfterLast()); // Loop kondisi ini adalah ketika cursornya itu belum berada di baris terakhir
        }
        
        // Close the Cursor
        cursor.close();
        return favoriteMovieItemsArrayList;
    }

    // Method untuk insert data ke DB dengan menggunakan SQLiteDatabase insert method (table movie item)
    public long insertFavoriteMovieItem(MovieItems movieItems){
        // Create ContentValues object
        ContentValues movieItemValues = new ContentValues();
        // Insert value ke ContentValues object
        movieItemValues.put(FavoriteDatabaseContract.FavoriteMovieItemColumns._ID, movieItems.getId());
        movieItemValues.put(FavoriteDatabaseContract.FavoriteMovieItemColumns.TITLE_COLUMN, movieItems.getMovieTitle());
        movieItemValues.put(FavoriteDatabaseContract.FavoriteMovieItemColumns.RATINGS_COLUMN, movieItems.getMovieRatings());
        movieItemValues.put(FavoriteDatabaseContract.FavoriteMovieItemColumns.ORIGINAL_LANGUAGE_COLUMN, movieItems.getMovieOriginalLanguage());
        movieItemValues.put(FavoriteDatabaseContract.FavoriteMovieItemColumns.RELEASE_DATE_COLUMN, movieItems.getMovieReleaseDate());
        movieItemValues.put(FavoriteDatabaseContract.FavoriteMovieItemColumns.FILE_PATH_COLUMN, movieItems.getMoviePosterPath());
        movieItemValues.put(FavoriteDatabaseContract.FavoriteMovieItemColumns.DATE_ADDED_COLUMN, movieItems.getDateAddedFavorite());
        movieItemValues.put(FavoriteDatabaseContract.FavoriteMovieItemColumns.FAVORITE_COLUMN, movieItems.getFavoriteBooleanState());
        // Execute SQLiteDatabase insert method
        return favoriteDatabase.insert(DATABASE_MOVIE_TABLE , null, movieItemValues);
    }
    
    // Method untuk delete data dari DB dengan menggunakan SQLiteDatabase delete method (table movie item)
    public int deleteFavoriteMovieItem(int id){
        return favoriteDatabase.delete(DATABASE_MOVIE_TABLE, _ID + " = '" + id + "'", null);
    }
    
    // Method untuk read data dari DB dengan menggunakan SQLiteDatabase query method (table tv show item)
    public ArrayList<TvShowItem> getAllFavoriteTvShowItems(){
        ArrayList<TvShowItem> favoriteTvShowItemsArrayList = new ArrayList <>();
        
        Cursor cursor = favoriteDatabase.query(DATABASE_TV_SHOW_TABLE,
                null,
                null,
                null,
                null,
                null,
                FavoriteDatabaseContract.FavoriteTvShowItemColumns.DATE_ADDED_COLUMN + " DESC",
                null);
        
        cursor.moveToFirst();
        
        TvShowItem tvShowItem;
        if(cursor.getCount() > 0){
            do{
                tvShowItem = new TvShowItem();
                tvShowItem.setId(cursor.getInt(cursor.getColumnIndexOrThrow(FavoriteDatabaseContract.FavoriteTvShowItemColumns._ID)));
                tvShowItem.setTvShowName(cursor.getString(cursor.getColumnIndexOrThrow(FavoriteDatabaseContract.FavoriteTvShowItemColumns.NAME_COLUMN)));
                tvShowItem.setTvShowRatings(cursor.getString(cursor.getColumnIndexOrThrow(FavoriteDatabaseContract.FavoriteTvShowItemColumns.RATINGS_COLUMN)));
                tvShowItem.setTvShowOriginalLanguage(cursor.getString(cursor.getColumnIndexOrThrow(FavoriteDatabaseContract.FavoriteTvShowItemColumns.ORIGINAL_LANGUAGE_COLUMN)));
                tvShowItem.setTvShowFirstAirDate(cursor.getString(cursor.getColumnIndexOrThrow(FavoriteDatabaseContract.FavoriteTvShowItemColumns.FIRST_AIR_DATE_COLUMN)));
                tvShowItem.setTvShowPosterPath(cursor.getString(cursor.getColumnIndexOrThrow(FavoriteDatabaseContract.FavoriteTvShowItemColumns.FILE_PATH_COLUMN)));
                tvShowItem.setDateAddedFavorite(cursor.getString(cursor.getColumnIndexOrThrow(FavoriteDatabaseContract.FavoriteTvShowItemColumns.DATE_ADDED_COLUMN)));
                tvShowItem.setTvShowFavorite(cursor.getInt(cursor.getColumnIndexOrThrow(FavoriteDatabaseContract.FavoriteTvShowItemColumns.FAVORITE_COLUMN)) == tvShowItem.getFavoriteBooleanState());
                // Add tv show item data ke ArrayList
                favoriteTvShowItemsArrayList.add(tvShowItem);
                // Memindahkan Cursor ke baris selanjutnya
                cursor.moveToNext();
            } while(!cursor.isAfterLast());
        }
        
        // Close the Cursor
        cursor.close();
        return favoriteTvShowItemsArrayList;
    }
    
    // Method untuk insert data ke DB dengan menggunakan SQLiteDatabase insert method (table tv show item)
    public long insertFavoriteTvShowItem(TvShowItem tvShowItem){
        // Create ContentValues object
        ContentValues tvShowItemValues = new ContentValues();
        // Insert value ke ContentValues object
        tvShowItemValues.put(FavoriteDatabaseContract.FavoriteTvShowItemColumns._ID, tvShowItem.getId());
        tvShowItemValues.put(FavoriteDatabaseContract.FavoriteTvShowItemColumns.NAME_COLUMN, tvShowItem.getTvShowName());
        tvShowItemValues.put(FavoriteDatabaseContract.FavoriteTvShowItemColumns.RATINGS_COLUMN, tvShowItem.getTvShowRatings());
        tvShowItemValues.put(FavoriteDatabaseContract.FavoriteTvShowItemColumns.ORIGINAL_LANGUAGE_COLUMN, tvShowItem.getTvShowOriginalLanguage());
        tvShowItemValues.put(FavoriteDatabaseContract.FavoriteTvShowItemColumns.FIRST_AIR_DATE_COLUMN, tvShowItem.getTvShowFirstAirDate());
        tvShowItemValues.put(FavoriteDatabaseContract.FavoriteTvShowItemColumns.FILE_PATH_COLUMN, tvShowItem.getTvShowPosterPath());
        tvShowItemValues.put(FavoriteDatabaseContract.FavoriteTvShowItemColumns.DATE_ADDED_COLUMN, tvShowItem.getDateAddedFavorite());
        tvShowItemValues.put(FavoriteDatabaseContract.FavoriteTvShowItemColumns.FAVORITE_COLUMN, tvShowItem.getFavoriteBooleanState());
        // Execute SQLiteDatabase insert method
        return favoriteDatabase.insert(DATABASE_TV_SHOW_TABLE, null, tvShowItemValues);
    }
    
    // Method untuk delete data dari DB dengan menggunakan SQLiteDatabase delete method (table tv show item)
    public int deleteFavoriteTvShowItem(int id){
        return favoriteDatabase.delete(DATABASE_TV_SHOW_TABLE, _ID + " = '" + id + "'", null);
    }
    
    
}
