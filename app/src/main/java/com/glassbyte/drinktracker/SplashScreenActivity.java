package com.glassbyte.drinktracker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;


public class SplashScreenActivity extends Activity
{
    public void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/good-times.regular.ttf");
        TextView GlassByte = (TextView) findViewById(R.id.logotext);
        GlassByte.setTypeface(tf);

        StartAnimations();

        Thread splash = new Thread() {
            public void run() {
                try {
                    int timer = 0;
                    while (timer < 100) {
                        sleep(100);
                        timer = timer + 100;
                    }
                    startActivity(new Intent("com.glassbyte.drinktracker.ClearScreen"));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    finish();
                }
            }
        };
        splash.start();
    }

    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        ScrollView l=(ScrollView) findViewById(R.id.scrollView);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.image_view);
        iv.clearAnimation();
        iv.startAnimation(anim);
    }
}
