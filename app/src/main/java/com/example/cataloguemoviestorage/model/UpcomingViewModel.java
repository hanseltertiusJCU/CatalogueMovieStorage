package com.example.cataloguemoviestorage.model;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.cataloguemoviestorage.BuildConfig;
import com.example.cataloguemoviestorage.entity.MovieItems;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class UpcomingViewModel extends AndroidViewModel {

    // Create object yang mengextend LiveData<ArrayList<MovieItems>>
    private UpcomingMovieLiveData upcomingMovieLiveData;

    // Beberapa informasi diakses dari BuildConfig untuk menjaga credential
    private String apiKey = BuildConfig.API_KEY;
    private String upcomingUrlBase = BuildConfig.BASE_MOVIE_UPCOMING_URL;
    private String languageUs = BuildConfig.LANGUAGE_US;

    public UpcomingViewModel(@NonNull Application application) {
        super(application);
        upcomingMovieLiveData = new UpcomingMovieLiveData(application);
    }

    // Getter method untuk mereturn LiveData yang berisi ArrayList<MovieItems>
    public LiveData<ArrayList<MovieItems>> getUpcomingMovies() {
        return upcomingMovieLiveData;
    }

    // Create class LiveData untuk menampung ViewModel
    public class UpcomingMovieLiveData extends LiveData<ArrayList<MovieItems>> {
        private final Context context;

        // Set constructor dari LiveData
        public UpcomingMovieLiveData(Context context) {
            this.context = context;
            loadUpcomingLiveData();
        }

        // Method tsb berguna untuk menjalankan tugas scr async sbg pengganti dari loadInBackground()
        // di AsyncTaskLoader
        @SuppressLint("StaticFieldLeak")
        private void loadUpcomingLiveData() {

            new AsyncTask<Void, Void, ArrayList<MovieItems>>() {
                @Override
                protected ArrayList<MovieItems> doInBackground(Void... voids) {

                    // Menginisiasikan SyncHttpClientObject krn Loader itu sudah berjalan pada background thread
                    SyncHttpClient syncHttpClient = new SyncHttpClient();

                    final ArrayList<MovieItems> movieItemses = new ArrayList<>();

                    String upcomingUrl = upcomingUrlBase + apiKey + languageUs;
                    syncHttpClient.get(upcomingUrl, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {
                                String result = new String(responseBody);
                                JSONObject responseObject = new JSONObject(result);
                                JSONArray results = responseObject.getJSONArray("results");
                                // Iterate semua data yg ada dan tambahkan ke ArrayList
                                for (int i = 0; i < results.length(); i++) {
                                    JSONObject movie = results.getJSONObject(i);
                                    boolean detailedItem = false;
                                    MovieItems movieItems = new MovieItems(movie, detailedItem);
                                    // Cek jika posterPath itu tidak "null" karena null dr JSON itu berupa
                                    // String, sehingga perlu menggunakan "" di dalam null
                                    if (!movieItems.getMoviePosterPath().equals("null")) {
                                        movieItemses.add(movieItems);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            // Do nothing jika responsenya itu tidak berhasil
                        }
                    });

                    return movieItemses;
                }

                @Override
                protected void onPostExecute(ArrayList<MovieItems> movieItems) {
                    // Set value dari Observer yang berisi ArrayList yang merupakan
                    // hasil dari doInBackground method
                    setValue(movieItems);
                }
            }.execute(); // Execute AsyncTask
        }
    }
}
