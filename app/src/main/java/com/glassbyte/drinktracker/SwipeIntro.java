package com.glassbyte.drinktracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by ed on 01/09/15.
 */
public class SwipeIntro extends AppIntro2 {

    String title = "title";
    String description = "description";
    int image;
    int background_colour;

    // Please DO NOT override onCreate. Use init
    @Override
    public void init(Bundle savedInstanceState) {

        image = R.drawable.glassbyte;
        background_colour = getResources().getColor(R.color.cyan500);

        // Add your slides fragments here
        // AppIntro will automatically generate the dots indicator and buttons.
        addSlide(SampleSlide.newInstance(R.layout.intro1));
        addSlide(SampleSlide.newInstance(R.layout.intro2));

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest
        addSlide(AppIntroFragment.newInstance(title, description, image, background_colour));

        // OPTIONAL METHODS
        // Override bar/separator color
        // Hide Skip/Done button
        showDoneButton(false);

        // Turn vibration on and set intensity
        // NOTE: you will probably need to ask VIBRATE permesssion in Manifest
        setVibrate(true);
        setVibrateIntensity(30);
    }



    private void loadMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDonePressed() {
        loadMainActivity();
    }

    public void getStarted(View v){
        loadMainActivity();
    }
}
