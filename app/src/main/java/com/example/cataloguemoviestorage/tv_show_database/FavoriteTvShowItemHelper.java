package com.example.cataloguemoviestorage.tv_show_database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.cataloguemoviestorage.entity.TvShowItem;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static com.example.cataloguemoviestorage.tv_show_database.FavoriteTvShowDatabaseContract.FavoriteTvShowItemColumns.DATE_ADDED_COLUMN;
import static com.example.cataloguemoviestorage.tv_show_database.FavoriteTvShowDatabaseContract.FavoriteTvShowItemColumns.FAVORITE_COLUMN;
import static com.example.cataloguemoviestorage.tv_show_database.FavoriteTvShowDatabaseContract.FavoriteTvShowItemColumns.FILE_PATH_COLUMN;
import static com.example.cataloguemoviestorage.tv_show_database.FavoriteTvShowDatabaseContract.FavoriteTvShowItemColumns.FIRST_AIR_DATE_COLUMN;
import static com.example.cataloguemoviestorage.tv_show_database.FavoriteTvShowDatabaseContract.FavoriteTvShowItemColumns.ORIGINAL_LANGUAGE_COLUMN;
import static com.example.cataloguemoviestorage.tv_show_database.FavoriteTvShowDatabaseContract.FavoriteTvShowItemColumns.RATINGS_COLUMN;
import static com.example.cataloguemoviestorage.tv_show_database.FavoriteTvShowDatabaseContract.FavoriteTvShowItemColumns.TABLE_NAME;
import static com.example.cataloguemoviestorage.tv_show_database.FavoriteTvShowDatabaseContract.FavoriteTvShowItemColumns.NAME_COLUMN;

// Class ini berguna untuk memanipulasi value dari database (Data Manipulation Language)
public class FavoriteTvShowItemHelper{
	private static final String DATABASE_TABLE = TABLE_NAME;
	private static FavoriteTvShowDatabaseHelper favoriteTvShowDatabaseHelper;
	private static FavoriteTvShowItemHelper INSTANCE;
	
	private static SQLiteDatabase favoriteTvShowDatabase;
	
	// Constructor untuk FavoriteTvShowHelper
	public FavoriteTvShowItemHelper(Context context){
		favoriteTvShowDatabaseHelper = new FavoriteTvShowDatabaseHelper(context);
	}
	
	// Method tsb berguna untuk menginisiasi database
	public static FavoriteTvShowItemHelper getInstance(Context context){
		if(INSTANCE == null){
			synchronized(SQLiteOpenHelper.class){
				if(INSTANCE == null){
					INSTANCE = new FavoriteTvShowItemHelper(context);
				}
			}
		}
		return INSTANCE;
	}
	
	// Open connection to database
	public void open() throws SQLException{
		favoriteTvShowDatabase = favoriteTvShowDatabaseHelper.getWritableDatabase();
	}
	
	// Close connection from database
	public void close(){
		favoriteTvShowDatabaseHelper.close();
		
		// Cek jika database sedang connected, jika iya maka disconnect
		if(favoriteTvShowDatabase.isOpen())
			favoriteTvShowDatabase.close();
	}
	
	// Method untuk read data dari DB dengan menggunakan SQLiteDatabase query method
	public ArrayList<TvShowItem> getAllFavoriteTvShowItems(){
		ArrayList<TvShowItem> favoriteTvShowItemsArrayList = new ArrayList <>();
		
		Cursor cursor = favoriteTvShowDatabase.query(DATABASE_TABLE,
				null,
				null,
				null,
				null,
				null,
				DATE_ADDED_COLUMN + " DESC",
				null);
		
		cursor.moveToFirst();
		
		TvShowItem tvShowItem;
		if(cursor.getCount() > 0){
			do{
				tvShowItem = new TvShowItem();
				tvShowItem.setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
				tvShowItem.setTvShowName(cursor.getString(cursor.getColumnIndexOrThrow(NAME_COLUMN)));
				tvShowItem.setTvShowRatings(cursor.getString(cursor.getColumnIndexOrThrow(RATINGS_COLUMN)));
				tvShowItem.setTvShowOriginalLanguage(cursor.getString(cursor.getColumnIndexOrThrow(ORIGINAL_LANGUAGE_COLUMN)));
				tvShowItem.setTvShowFirstAirDate(cursor.getString(cursor.getColumnIndexOrThrow(FIRST_AIR_DATE_COLUMN)));
				tvShowItem.setTvShowPosterPath(cursor.getString(cursor.getColumnIndexOrThrow(FILE_PATH_COLUMN)));
				tvShowItem.setDateAddedFavorite(cursor.getString(cursor.getColumnIndexOrThrow(DATE_ADDED_COLUMN)));
				tvShowItem.setTvShowFavorite(cursor.getInt(cursor.getColumnIndexOrThrow(FAVORITE_COLUMN)) == tvShowItem.getFavoriteBooleanState());
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
	
	// Method untuk insert data ke DB dengan menggunakan SQLiteDatabase insert method
	public long insertFavoriteTvShowItem(TvShowItem tvShowItem){
		// Create ContentValues object
		ContentValues tvShowItemValues = new ContentValues();
		// Insert value ke ContentValues object
		tvShowItemValues.put(_ID, tvShowItem.getId());
		tvShowItemValues.put(NAME_COLUMN, tvShowItem.getTvShowName());
		tvShowItemValues.put(RATINGS_COLUMN, tvShowItem.getTvShowRatings());
		tvShowItemValues.put(ORIGINAL_LANGUAGE_COLUMN, tvShowItem.getTvShowOriginalLanguage());
		tvShowItemValues.put(FIRST_AIR_DATE_COLUMN, tvShowItem.getTvShowFirstAirDate());
		tvShowItemValues.put(FILE_PATH_COLUMN, tvShowItem.getTvShowPosterPath());
		tvShowItemValues.put(DATE_ADDED_COLUMN, tvShowItem.getDateAddedFavorite());
		tvShowItemValues.put(FAVORITE_COLUMN, tvShowItem.getFavoriteBooleanState());
		// Execute SQLiteDatabase insert method
		return favoriteTvShowDatabase.insert(DATABASE_TABLE, null, tvShowItemValues);
	}
	
	// Method untuk delete data dari DB dengan menggunakan SQLiteDatabase delete method
	public int deleteFavoriteTvShowItem(int id){
		return favoriteTvShowDatabase.delete(TABLE_NAME, _ID + " = '" + id + "'", null);
	}
	
}
