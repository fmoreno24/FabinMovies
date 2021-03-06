package com.fmoreno.fabinmovies.ui;

import static android.content.ContentValues.TAG;
import static com.fmoreno.fabinmovies.internet.WebServicesConstant.API_KEY;
import static com.fmoreno.fabinmovies.internet.WebServicesConstant.BASE_URL_APPLICATION;
import static com.fmoreno.fabinmovies.internet.WebServicesConstant.BASE_URL_DETAIL_MOVIE;
import static com.fmoreno.fabinmovies.internet.WebServicesConstant.MOVIE;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.fmoreno.fabinmovies.R;
import com.fmoreno.fabinmovies.adapter.TrailersAdapter;
import com.fmoreno.fabinmovies.db.Entity.Movie;
import com.fmoreno.fabinmovies.db.Entity.MovieVideos;
import com.fmoreno.fabinmovies.db.ViewModel.MovieVideosViewModel;
import com.fmoreno.fabinmovies.db.ViewModel.MovieViewModel;
import com.fmoreno.fabinmovies.internet.WebApiRequest;
import com.fmoreno.fabinmovies.model.DetailMovie;
import com.fmoreno.fabinmovies.model.MovieList;
import com.fmoreno.fabinmovies.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class DetailMovieActivity extends AppCompatActivity {
    Movie movie;
    DetailMovie movieDetail;

    ConstraintLayout clMovieDetail;

    private ProgressBar progressBar;

    TextView textViewTitle,textViewVotes,textViewStars,textViewDate,textViewDescription,textViewTagline, label_trailers;

    ImageView imageViewPoster, imageViewBanner;

    RecyclerView rvTrailers;
    TrailersAdapter adapter;

    public static List<MovieVideos> sVideoList;

    ViewModelProvider.AndroidViewModelFactory factory;
    public MovieViewModel movieViewModel;
    public MovieVideosViewModel movieVideosViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);
        movie = (Movie) getIntent().getSerializableExtra("movie");
        initView();
        initViewModelRoom();
        setImage();
        callGetTopRatedMoviesApi();
        //setText();
        //setAnimation();
    }

    private void initView(){
        clMovieDetail = findViewById(R.id.clMovieDetail);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewVotes = findViewById(R.id.textViewVotes);
        textViewStars = findViewById(R.id.textViewStars);
        textViewDate = findViewById(R.id.textViewDate);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewTagline = findViewById(R.id.textViewTagline);
        label_trailers = findViewById(R.id.label_trailers);

        imageViewPoster = findViewById(R.id.imageViewPoster);
        imageViewBanner = findViewById(R.id.imageViewBanner);

        rvTrailers = findViewById(R.id.rvTrailers);

        //if(sVideoList == null){
            sVideoList = new ArrayList<MovieVideos>();
        //}
        adapter = new TrailersAdapter(sVideoList);
        // Attach the adapter to the recyclerview to populate items
        rvTrailers.setAdapter(adapter);
        rvTrailers.setLayoutManager(new LinearLayoutManager(this,  LinearLayoutManager.HORIZONTAL, false));

        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        progressBar.setVisibility(View.GONE);
        clMovieDetail.addView(progressBar, params);

    }

    private void initViewModelRoom() {
        try{
            factory = ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication());

            movieViewModel = new ViewModelProvider(this, factory).get(MovieViewModel.class);
            movieVideosViewModel = new ViewModelProvider(this, factory).get(MovieVideosViewModel.class);
        }catch (Exception ex){
            Log.e(TAG, ex.toString());
        }
    }

    private void setImage(){
        Glide.with(this)
                .load("http://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                //.load(moviesList.get(position).getPosterPath())
                .into(imageViewPoster);

        Glide.with(this)
                .load("http://image.tmdb.org/t/p/w500" + movie.getBackdropPath())
                //.load(moviesList.get(position).getPosterPath())
                .into(imageViewBanner);
    }

    private void setText(boolean isOnline){
        textViewTitle.setText(movie.getTitle());
        textViewVotes.setText(movie.getLikes());
        textViewStars.setText(movie.getStars());
        textViewDate.setText(Utils.getYear(movie.getReleaseDate()));
        textViewDescription.setText(movie.getOverview());
        if(isOnline){
            if(movieDetail != null){
                textViewTagline.setText(movieDetail.tagline);
                if(sVideoList != null && sVideoList.size() > 0){
                    //adapter.notifyDataSetChanged();
                    label_trailers.setVisibility(View.VISIBLE);
                    adapter.addMovies(sVideoList);
                } else {
                    label_trailers.setVisibility(View.GONE);
                }
            } else {
                label_trailers.setVisibility(View.GONE);
            }
        } else {
            if(sVideoList != null && sVideoList.size() > 0){
                label_trailers.setVisibility(View.VISIBLE);
            } else {
                label_trailers.setVisibility(View.GONE);
            }
            textViewTagline.setText(movie.getTagline());
        }


    }

    private void setAnimation(){
        final Animation atg = AnimationUtils.loadAnimation(this, R.anim.atg);
        final Animation packageimg = AnimationUtils.loadAnimation(this, R.anim.packageimg);
        final Animation right_in = AnimationUtils.loadAnimation(this, R.anim.right_in);
        final Animation right_out = AnimationUtils.loadAnimation(this, R.anim.right_out);
        final Animation slide_up = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        final Animation slide_bottom = AnimationUtils.loadAnimation(this, R.anim.slide_bottom);
        final Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);

        //imageViewPoster.startAnimation(atg);
        imageViewBanner.startAnimation(packageimg);

        textViewTitle.startAnimation(right_in);
        textViewVotes.startAnimation(slide_bottom);
        textViewStars.startAnimation(slide_bottom);
        textViewDate.startAnimation(slide_bottom);
        textViewTagline.startAnimation(slide_up);
        textViewDescription.startAnimation(slide_up);

    }

    /**
     * Display Progress bar
     */

    private void showProgress() {
        try{
            progressBar.setVisibility(View.VISIBLE);
        }catch (Exception ex){
            Log.d(TAG, "showProgress: ex:"+ ex.toString());
        }

    }

    /**
     * Hide Progress bar
     */

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Call the api to fetch the TopRatedMovies list
     */

    private void callGetTopRatedMoviesApi() {

        /**
         * Checking internet connection before api call.
         * Very important always take care.
         */

        if (!Utils.isNetworkAvailable(DetailMovieActivity.this)) {
            Toast.makeText(DetailMovieActivity.this,
                    getResources().getString(R.string.str_no_internet),
                    Toast.LENGTH_SHORT).show();
            movieVideosViewModel.getMovieVideosList(movie.id).observe(this, new Observer<List<MovieVideos>>() {
                @Override
                public void onChanged(List<MovieVideos> movieVideosList) {
                    if (movieVideosList.isEmpty()) {
                        // TODO: 11/7/2018 optimize this
                        // display empty state since there is no favorites in database
                        Log.d("dataMovie1", movieVideosList.toString());
                    } else {
                        Log.d("dataMovie2", movieVideosList.toString());
                        adapter.submitList(movieVideosList);
                    }
                }
            });
            setText(false);
            return;
        }

        showProgress();


        //constructing api url
        String ws_url = BASE_URL_APPLICATION + MOVIE + movie.getId() +
                "?api_key=" + API_KEY +  BASE_URL_DETAIL_MOVIE;


        //Using Volley to call api

        WebApiRequest webApiRequest = new WebApiRequest(Request.Method.GET,
                ws_url, ReqSuccessListener(), ReqErrorListener());
        Volley.newRequestQueue(DetailMovieActivity.this).add(webApiRequest);
    }

    /**
     * Success listener to handle the movie listing
     * process after api returns the movie list
     *
     * @return
     */

    private Response.Listener<String> ReqSuccessListener() {
        return new Response.Listener<String>() {
            public void onResponse(String response) {
                Log.e("movie list_response", response);
                try {
                    hideProgress();


                    movieDetail = (DetailMovie) Utils.jsonToPojo(response, DetailMovie.class);

                    //Log.d("Detail", movieDetail.title);
                    for(DetailMovie.Video video : movieDetail.videos.results){
                        MovieVideos movieVideos = new MovieVideos(video.id,
                                                                    movieDetail.id,
                                                                    video.name,
                                                                    video.key,
                                                                    video.size);
                        if(!sVideoList.contains(movieVideos)){
                            sVideoList.add(movieVideos);
                            movieVideosViewModel.insert(movieVideos);
                        }

                    }
                    setText(true);
                    setAnimation();

                    movieViewModel.updateTagline(movie.getId(), movieDetail.tagline);

                } catch (Exception e) {
                    Log.e(TAG,"Exception=="+e.getLocalizedMessage());
                    hideProgress();
                }
            }
        };
    }

    /**
     * To Handle the error
     *
     * @return
     */

    private Response.ErrorListener ReqErrorListener() {
        return new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                Log.e("volley error", "volley error");
                Toast.makeText(DetailMovieActivity.this, getResources().getString(R.string.str_error_server), Toast.LENGTH_SHORT).show();
            }
        };
    }
}
