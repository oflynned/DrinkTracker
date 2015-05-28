package com.glassbyte.drinktracker;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

/**
 * Created by ed on 25/05/15.
 */
public class AddDrinkActivity extends FragmentActivity{
    private static final int NUM_PAGES = 3;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adddrink);

        //Instantiate a ViewPager and a PAgerAdapter
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(1);
    }

    @Override
    public void onBackPressed(){
        if(mPager.getCurrentItem() == 1){
            super.onBackPressed();
        } else {
            mPager.setCurrentItem(1);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter{
        public ScreenSlidePagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position){
            if (position == 0) {
                return new PresetDrink();
            } else if (position == 1) {
                return new ChooseDrink();
            } else {
                return new CustomDrink();
            }
        }

        @Override
        public int getCount(){
            return NUM_PAGES;
        }
    }
}
