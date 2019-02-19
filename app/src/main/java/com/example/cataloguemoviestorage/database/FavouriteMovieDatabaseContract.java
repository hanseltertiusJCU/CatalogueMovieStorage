package com.example.cataloguemoviestorage.database;

import android.provider.BaseColumns;

// Kelas ini berguna untuk membangun struktur tabel dari database
public class FavouriteMovieDatabaseContract {
    // Class tsb berguna untuk membuat nama tabel serta columnnya dan
    // tidak perlu initiate _ID krn ud otomatis dr sananya
    static final class FavouriteMovieItemColumns implements BaseColumns {
        // Nama tabel dari database
        static String TABLE_NAME = "favourite_movies";
        // Nama columns dari database
        static String RATINGS_COLUMN = "ratings";
        static String RELEASE_DATE_COLUMN = "release_date";
        static String ORIGINAL_LANGUAGE_COLUMN = "original_language";
    }
}
