package com.aa.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {
ImageView quizTrigger;
Spinner categorySelector;
private MediaPlayer backgroundMusicPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Category Selector");


        //BACKGROUND MUSIC CONFIGURATION
        backgroundMusicPlayer = MediaPlayer.create(this, R.raw.background_music);
        backgroundMusicPlayer.setVolume(0.1f,0.1f);
        backgroundMusicPlayer.start();
        backgroundMusicPlayer.setLooping(true);
        //REFERENCES
        quizTrigger=(ImageView) findViewById(R.id.imageView);
        categorySelector=(Spinner)findViewById(R.id.categorySelector);
        quizTrigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //fetch selected index and translate to url
                int selectedIndex = categorySelector.getSelectedItemPosition();
                String[] urls = getResources().getStringArray(R.array.spinner_urls);
                String selectedUrl = urls[selectedIndex];
                //start intent and put extra String url
                Intent intent = new Intent(getApplicationContext(), quizActivity.class);
                intent.putExtra("category", selectedUrl);
                startActivity(intent);
                //stop background music on activity transition
                backgroundMusicPlayer.stop();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        backgroundMusicPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        backgroundMusicPlayer.start();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        int spinnerItemId= categorySelector.getSelectedItemPosition();
        outState.putInt("spinnerItemId",spinnerItemId);
        backgroundMusicPlayer.pause();
    }
}