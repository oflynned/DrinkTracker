package com.glassbyte.drinktracker;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;

/**
 * Created by ed on 25/05/15.
 */
public class AddDrinkActivity extends ActionBarActivity{
    private static final int NUM_PAGES = 4;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private int actionBarHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        /**Bad practice, the below code is ususally executed on create but I required the height of
         * the action bar, for the custom drink fragment which would have been created before this stage,
         * which is only calculated at this stage**/
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        actionBarHeight = this.getSupportActionBar().getHeight();

        setContentView(R.layout.activity_adddrink);

        //Instantiate a ViewPager and a PagerAdapter
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);


        mPager.setCurrentItem(1);
        return true;
    }

    public int getSActionBarHeight(){return actionBarHeight;}

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
            } else if(position == 2){
                CustomDrink cd = new CustomDrink();
                Bundle bundle = new Bundle();
                bundle.putInt(CustomDrink.ARG_ACTION_BAR_HEIGHT, actionBarHeight);
                cd.setArguments(bundle);
                return cd;
            } else {
                return new RealTimeActivity();
            }
        }

        @Override
        public int getCount(){
            return NUM_PAGES;
        }
    }
}
