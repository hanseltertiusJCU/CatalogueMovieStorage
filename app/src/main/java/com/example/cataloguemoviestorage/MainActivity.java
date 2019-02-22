package com.example.cataloguemoviestorage;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.cataloguemoviestorage.adapter.MovieSectionsFragmentPagerAdapter;
import com.example.cataloguemoviestorage.fragment.FavoriteMovieFragment;
import com.example.cataloguemoviestorage.fragment.NowPlayingMovieFragment;
import com.example.cataloguemoviestorage.fragment.UpcomingMovieFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    // Create ViewPager untuk swipe Fragments
    @BindView(R.id.movie_viewPager) ViewPager viewPager;
    // Assign TabLayout
    @BindView(R.id.menu_tabs) TabLayout tabLayout;
    private MovieSectionsFragmentPagerAdapter movieSectionsFragmentPagerAdapter;

    private TextView tabNowPlaying;
    private TextView tabUpcoming;
    private TextView tabFavorite;

    private Drawable[] nowPlayingDrawables;
    private Drawable nowPlayingDrawable;
    private Drawable[] upcomingDrawables;
    private Drawable upcomingDrawable;
    private Drawable[] favoriteDrawables;
    private Drawable favoriteDrawable;

    @BindView(R.id.main_toolbar) Toolbar mainToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set content activity to use layout xml file activity_main.xml
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(mainToolbar);

        // Cek kalo ada action bar
        if(getSupportActionBar() != null){
            // Set default action bar title, yaitu "Now Playing"
            getSupportActionBar().setTitle(getString(R.string.now_playing));
        }

        // Panggil method ini untuk saving Fragment state di ViewPager, kesannya kyk simpen
        // fragment ketika sebuah fragment sedang tidak di display.
        // Kita menggunakan value 2 sebagai parameter karena kita punya 3 fragments, dan kita
        // hanya butuh simpan 2 fragments (1 lg untuk display).
        viewPager.setOffscreenPageLimit(2);

        // Panggil method tsb untuk membuat fragment yang akan disimpan ke ViewPager
        createViewPagerContent(viewPager);

        // Beri ViewPager ke TabLayout
        tabLayout.setupWithViewPager(viewPager);

        // Panggil method tsb untuk membuat isi dari setiap tab
        createTabIcons();

        // Set listener untuk tab layout
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            // Set action bar title ketika sebuah tab dipilih
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                // Cast getPageTitle return ke String dari CharSequence (return type yang semula)
                setActionBarTitle((String) movieSectionsFragmentPagerAdapter.getPageTitle(position));
                // Ubah text color dan drawable tint menjadi colorAccent, yang menandakan bahwa itemnya
                // sedang dipilih
                switch (position){
                    case 0:
                        tabNowPlaying.setTextColor(getResources().getColor(R.color.colorAccent));
                        nowPlayingDrawables = tabNowPlaying.getCompoundDrawables();
                        nowPlayingDrawable = nowPlayingDrawables[1];
                        nowPlayingDrawable.setTint(getResources().getColor(R.color.colorAccent));
                        break;
                    case 1:
                        tabUpcoming.setTextColor(getResources().getColor(R.color.colorAccent));
                        upcomingDrawables = tabUpcoming.getCompoundDrawables();
                        upcomingDrawable = upcomingDrawables[1];
                        upcomingDrawable.setTint(getResources().getColor(R.color.colorAccent));
                        break;
                    case 2:
                        tabFavorite.setTextColor(getResources().getColor(R.color.colorAccent));
                        favoriteDrawables = tabFavorite.getCompoundDrawables();
                        favoriteDrawable = favoriteDrawables[1];
                        favoriteDrawable.setTint(getResources().getColor(R.color.colorAccent));
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                // Ubah text color dan drawable tint menjadi hitam, yang menandakan bahwa itemnya
                // sedang tidak dipilih
                switch (position){
                    case 0:
                        tabNowPlaying.setTextColor(getResources().getColor(R.color.colorBlack));
                        nowPlayingDrawables = tabNowPlaying.getCompoundDrawables();
                        nowPlayingDrawable = nowPlayingDrawables[1];
                        nowPlayingDrawable.setTint(getResources().getColor(R.color.colorBlack));
                        break;
                    case 1:
                        tabUpcoming.setTextColor(getResources().getColor(R.color.colorBlack));
                        upcomingDrawables = tabUpcoming.getCompoundDrawables();
                        upcomingDrawable = upcomingDrawables[1];
                        upcomingDrawable.setTint(getResources().getColor(R.color.colorBlack));
                        break;
                    case 2:
                        tabFavorite.setTextColor(getResources().getColor(R.color.colorBlack));
                        favoriteDrawables = tabFavorite.getCompoundDrawables();
                        favoriteDrawable = favoriteDrawables[1];
                        favoriteDrawable.setTint(getResources().getColor(R.color.colorBlack));
                    default:
                        break;
                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    // Method tsb berguna untuk membuat icons beserta isinya di Tab
    private void createTabIcons(){
        tabNowPlaying = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        // Set isi dari text di sebuah tab
        tabNowPlaying.setText(getString(R.string.now_playing));
        // Set default text color yang menandakan bahwa tabnya itu sedang d select
        tabNowPlaying.setTextColor(getResources().getColor(R.color.colorAccent));
        // Set icon di atas text
        tabNowPlaying.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_now_playing, 0, 0);
        // Dapatkan getCompoundDrawable dari setCompoundDrawablesWithIntrinsicBounds
        nowPlayingDrawables = tabNowPlaying.getCompoundDrawables();
        // Akses drawableTop, which is in this case kita mengakses element ke 2 (index value: 1)
        // dari Drawable[]
        nowPlayingDrawable = nowPlayingDrawables[1];
        // Set default tint untuk drawable yang menandakan bahwa tabnya itu sedang d select
        nowPlayingDrawable.setTint(getResources().getColor(R.color.colorAccent));

        // Inflate custom_tab.xml ke dalam TabLayout
        tabLayout.getTabAt(0).setCustomView(tabNowPlaying);

        tabUpcoming = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabUpcoming.setText(getString(R.string.upcoming));
        tabUpcoming.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_upcoming, 0,0);
        tabLayout.getTabAt(1).setCustomView(tabUpcoming);
        
        tabFavorite = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabFavorite.setText(getString(R.string.favorite));
        tabFavorite.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_favourite_off, 0, 0);
        // Make the color black as the default tint color in drawable is white
        favoriteDrawables = tabFavorite.getCompoundDrawables();
        favoriteDrawable = favoriteDrawables[1];
        favoriteDrawable.setTint(getResources().getColor(R.color.colorBlack));
        tabLayout.getTabAt(2).setCustomView(tabFavorite);
    }

    // Method tsb berguna untuk membuat isi dari ViewPager
    private void createViewPagerContent(ViewPager viewPager){

        // Create FragmentPagerAdapter untuk mengetahui fragment mana yg di show
        movieSectionsFragmentPagerAdapter = new MovieSectionsFragmentPagerAdapter(this, getSupportFragmentManager());

        // Tambahkan fragment beserta title ke FragmentPagerAdapter
        movieSectionsFragmentPagerAdapter.addMovieSectionFragment(new NowPlayingMovieFragment(), getString(R.string.now_playing));
        movieSectionsFragmentPagerAdapter.addMovieSectionFragment(new UpcomingMovieFragment(), getString(R.string.upcoming));
        movieSectionsFragmentPagerAdapter.addMovieSectionFragment(new FavoriteMovieFragment(), getString(R.string.favorite));

        // Set FragmentPagerAdapter ke ViewPager
        viewPager.setAdapter(movieSectionsFragmentPagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_language_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_change_language_settings){
            Intent mIntent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
            startActivity(mIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void setActionBarTitle(String title){
        // Gunakan getSupportActionBar untuk backward compatibility
        getSupportActionBar().setTitle(title);
    }

}
