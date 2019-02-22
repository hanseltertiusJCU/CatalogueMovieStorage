package com.example.cataloguemoviestorage.item;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

public class MovieItems implements Parcelable{

    // Nilai dari value untuk MovieItems
    private int id;
    private String movieTitle;
    private String movieTagline;
    private String movieStatus;
    private String movieRatings;
    private String movieRatingsVote;
    private String movieOriginalLanguage;
    private String movieLanguages;
    private String movieGenres;
    private String movieReleaseDate;
    private String movieOverview;
    private String moviePosterPath;
    // Nilai untuk mengetahui waktu dimana sebuah data di add menjadi favorite
    private String dateAddedFavorite;
    // Nilai untuk tahu bahwa movie item itu termasuk dalam kategori favorit ato tidak
    private boolean isMovieFavorite;
    private int favoriteBooleanState;
    

    public MovieItems(JSONObject object, boolean isMovieDetailed) {
        // Cek jika app berada di section DetailActivity agar dapat mengakses URL Movie Details
        if(isMovieDetailed){
            try {
                int dataId = object.getInt("id");
                String dataTitle = object.getString("title");
                String dataTagline = object.getString("tagline");
                String dataStatus = object.getString("status");
                String dataVoteAverage = object.getString("vote_average");
                String dataVoteCount = object.getString("vote_count");
                // value tsb berguna untuk mentransfer ke MainActivity
                String dataOriginalLanguage = object.getString("original_language");
                // Ubah language menjadi upper case
                String displayed_language = dataOriginalLanguage.toUpperCase();
                JSONArray dataLanguageArray = object.getJSONArray("spoken_languages");
                String dataLanguages = null;
                // Cek jika languageArray ada datanya atau tidak
                if (dataLanguageArray.length() > 0) {
                    // Iterate language array untuk mendapatkan language yang akan ditambahkan ke languages
                    // fyi: languages itu adalah koleksi dari language field
                    for (int i = 0; i < dataLanguageArray.length(); i++) {
                        JSONObject languageObject = dataLanguageArray.getJSONObject(i);
                        String language = languageObject.getString("name");
                        if (i == 0)
                            dataLanguages = language + " ";
                        else
                            dataLanguages += language + " ";
                    }
                } else {
                    dataLanguages = "Language Unknown";
                }

                JSONArray dataGenreArray = object.getJSONArray("genres");
                String dataGenres = null;

                // Cek jika genreArray ada datanya atau tidak, jika tidak set default value untuk String
                // genres (isinya adalah item yg ada di array)
                if (dataGenreArray.length() > 0) {
                    // Iterate genre array untuk mendapatkan genre yang akan ditambahkan ke genres
                    // fyi: genres itu adalah koleksi dari genre field
                    for (int i = 0; i < dataGenreArray.length(); i++) {
                        JSONObject genreObject = dataGenreArray.getJSONObject(i);
                        String genre = genreObject.getString("name");
                        if (i == 0)
                            dataGenres = genre + " ";
                        else
                            dataGenres += genre + " ";
                    }
                } else {
                    dataGenres = "Genre Unknown";
                }

                String dataReleaseDate = object.getString("release_date");
                String dataOverview = object.getString("overview");
                // Dapatkan detailed movie poster path untuk url {@link DetailActivity}
                String dataPosterPath = object.getString("poster_path");

                // Set values bedasarkan variable-variable yang merepresentasikan field dari sebuah JSON
                // object
                this.id = dataId;
                this.movieTitle = dataTitle;
                this.movieTagline = dataTagline;
                this.movieStatus = dataStatus;
                this.movieRatings = dataVoteAverage;
                this.movieRatingsVote = dataVoteCount;
                this.movieOriginalLanguage = displayed_language;
                this.movieLanguages = dataLanguages;
                this.movieGenres = dataGenres;
                this.movieReleaseDate = dataReleaseDate;
                this.movieOverview = dataOverview;
                this.moviePosterPath = dataPosterPath;

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else { // Jika tidak, maka kita akan mengakses URL movie NowPlaying/Upcoming
            try{
                // Get JSON object fields
                int dataId = object.getInt("id");
                String dataTitle = object.getString("title");
                String dataVoteAverage = object.getString("vote_average");
                String dataReleaseDate = object.getString("release_date");
                String dataOriginalLanguage = object.getString("original_language");
                // Ubah language menjadi upper case
                String displayed_language = dataOriginalLanguage.toUpperCase();
                // Dapatkan poster path untuk di extract ke url {@link MovieAdapter}
                String dataPosterPath = object.getString("poster_path");

                this.id = dataId;
                this.movieTitle = dataTitle;
                this.movieRatings = dataVoteAverage;
                this.movieReleaseDate = dataReleaseDate;
                this.movieOriginalLanguage = displayed_language;
                this.moviePosterPath = dataPosterPath;
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
	
	public MovieItems(){
	
	}
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMovieTitle() {
        // Set default value for DetailedMovieTitle if DetailedMovieTitle is null or ""
        if (movieTitle != null && !movieTitle.isEmpty()) {
            return movieTitle;
        } else {
            return "Title Unknown";
        }
    }
	
	public void setMovieTitle(String movieTitle){
		this.movieTitle = movieTitle;
	}
	
	public String getMovieTagline() {
        // Set default value for DetailedMovieTagline if DetailedMovieTagline is null or ""
        if (movieTagline != null && !movieTagline.isEmpty()) {
            return movieTagline;
        } else {
            return "Tagline Unknown";
        }

    }

    public String getMovieStatus() {
        // Set default value for DetailedMovieStatus if DetailedMovieStatus is null or ""
        if (movieStatus != null && !movieStatus.isEmpty()) {
            return movieStatus;
        } else {
            return "Status Unknown";
        }

    }

    public String getMovieRatings() {
        return movieRatings;
    }
	
	public void setMovieRatings(String movieRatings){
		this.movieRatings = movieRatings;
	}
	
	public String getMovieRatingsVote() {
        return movieRatingsVote;
    }

    public String getMovieOriginalLanguage() {
        if (movieOriginalLanguage != null) {
            return movieOriginalLanguage;
        } else {
            return "Language Unknown";
        }
    }
	
	public void setMovieOriginalLanguage(String movieOriginalLanguage){
		this.movieOriginalLanguage = movieOriginalLanguage;
	}
	
	public String getMovieLanguages() {
        if(movieLanguages != null){
            return movieLanguages;
        } else {
            return "Languages Unknown";
        }
    }

    public String getMovieGenres() {
        if(movieGenres != null && !movieGenres.isEmpty()){
            return movieGenres;
        } else {
            return "Genres Unknown";
        }

    }

    public String getMovieReleaseDate() {
        // Set default value for DetailedMovieReleaseDate if DetailedMovieReleaseDate is null or ""
        if (movieReleaseDate != null && !movieReleaseDate.isEmpty()) {
            return movieReleaseDate;
        } else {
            return "Release Date Unknown";
        }
    }
	
	public void setMovieReleaseDate(String movieReleaseDate){
		this.movieReleaseDate = movieReleaseDate;
	}
	
	public String getMovieOverview() {
        // Set default value for DetailedMovieOverview if DetailedMovieOverview is null or ""
        if (movieOverview != null && !movieOverview.isEmpty()) {
            return movieOverview;
        } else {
            return "Overview Unknown";
        }
    }

    public String getMoviePosterPath() {
        return moviePosterPath;
    }
	
	public void setMoviePosterPath(String moviePosterPath){
		this.moviePosterPath = moviePosterPath;
	}
    
    public String getDateAddedFavorite(){
        return dateAddedFavorite;
    }
    
    public void setDateAddedFavorite(String dateAddedFavorite){
        this.dateAddedFavorite = dateAddedFavorite;
    }
    
    public void setMovieFavorite(boolean movieFavorite){
        isMovieFavorite = movieFavorite;
    }
    
    public int getFavoriteBooleanState(){
        return favoriteBooleanState;
    }
    
    public void setFavoriteBooleanState(int favoriteBooleanState){
        this.favoriteBooleanState = favoriteBooleanState;
    }
    
    // Parcelable implementation
    @Override
    public int describeContents(){
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest , int flags){
    
        dest.writeInt(id);
        dest.writeString(movieTitle);
        dest.writeString(movieTagline);
        dest.writeString(movieStatus);
        dest.writeString(movieRatings);
        dest.writeString(movieRatingsVote);
        dest.writeString(movieOriginalLanguage);
        dest.writeString(movieLanguages);
        dest.writeString(movieGenres);
        dest.writeString(movieReleaseDate);
        dest.writeString(movieOverview);
        dest.writeString(moviePosterPath);
        dest.writeString(dateAddedFavorite);
        dest.writeByte((byte) (isMovieFavorite?1:0));
        dest.writeInt(favoriteBooleanState);
    }
    
    protected MovieItems(Parcel in){
        id = in.readInt();
        movieTitle = in.readString();
        movieTagline = in.readString();
        movieStatus = in.readString();
        movieRatings = in.readString();
        movieRatingsVote = in.readString();
        movieOriginalLanguage = in.readString();
        movieLanguages = in.readString();
        movieGenres = in.readString();
        movieReleaseDate = in.readString();
        movieOverview = in.readString();
        moviePosterPath = in.readString();
        dateAddedFavorite = in.readString();
        isMovieFavorite = in.readByte() != 0;
        favoriteBooleanState = in.readInt();
    }
    
    public static final Creator <MovieItems> CREATOR = new Creator <MovieItems>(){
        @Override
        public MovieItems createFromParcel(Parcel in){
            return new MovieItems(in);
        }
        
        @Override
        public MovieItems[] newArray(int size){
            return new MovieItems[size];
        }
    };
}
