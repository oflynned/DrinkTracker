package com.glassbyte.drinktracker;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public final static String ON_BACK_PRESSED_EVENT = "on_back_pressed_event";
    private UpdateCurrentBACAlarmReceiver currentBACAlarm = new UpdateCurrentBACAlarmReceiver();
    private static final int NUM_PAGES = 3;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private int actionBarHeight;
    private String run = "";

    Locale locale;
    String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean alarmUp = (PendingIntent.getBroadcast(this, 0,
                new Intent(this, UpdateCurrentBACAlarmReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null);

        if (alarmUp)
            System.out.println("Alarm is already active");
        else
            currentBACAlarm.setAlarm(this);

        //retrieve data from sharedreferences for initial run setup
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        //set irish if the checkbox is checked in settings
        boolean irish = false;
        boolean nds = false;
        boolean irishChosen = sp.getBoolean(getResources().getString(R.string.pref_key_irish), irish);
        boolean ndsChosen = sp.getBoolean(getResources().getString(R.string.pref_key_nds), nds);

        if(irishChosen) {
            setLocale("ga");
        }
        else if(ndsChosen) {
            setLocale("nds");
        }

        run = (sp.getString(getResources().getString(R.string.pref_key_run),""));
        if (run == "" || run == null) {
            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void setLocale(String language){
        this.language = language;
        locale = new Locale(language);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {super.onCreateOptionsMenu(menu);
        /**Bad practice, the below code is ususally executed on create but I required the height of
         * the action bar, for the custom drink fragment which would have been created before this stage,
         * which is only calculated at this stage**/
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        if (!(run == "" || run == null)) {
            actionBarHeight = this.getSupportActionBar().getHeight();

            setContentView(R.layout.activity_adddrink);

            //Instantiate a ViewPager and a PagerAdapter
            mPager = (ViewPager) findViewById(R.id.pager);
            mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
            mPager.setAdapter(mPagerAdapter);

            mPager.setCurrentItem(1);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, PreferencesActivity.class);
            startActivity(settingsIntent);
            return true;
        } else if (id == R.id.action_listDrinks) {
            Intent listDrinksIntent = new Intent(this, ListDrinksActivity.class);
            startActivity(listDrinksIntent);
            return true;
        } else if (id == R.id.action_aboutus){
            Intent aboutUsIntent = new Intent(this, AboutUs.class);
            startActivity(aboutUsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        if(mPager.getCurrentItem() == 1){
            super.onBackPressed();
        } else if (mPager.getCurrentItem() == 2){
            mPager.setCurrentItem(1);
        } else if (mPager.getCurrentItem() == 0) {
            if (PresetDrink.getCurrentStage() == 0)
                mPager.setCurrentItem(1);
            else {
                Intent broadcastIntent = new Intent(ON_BACK_PRESSED_EVENT);
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
            }
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public void destroyItem(ViewGroup viewPager, int position, Object object) {
            System.out.println("Destroy item: "+object.toString());
        }

        @Override
        public Fragment getItem(int position){
            if (position == 0) {
                return new PresetDrink();
            } else if (position == 1) {
                return new ChooseDrink();
            } else {
                CustomDrink cd = new CustomDrink();
                Bundle bundle = new Bundle();
                bundle.putInt(CustomDrink.ARG_ACTION_BAR_HEIGHT, actionBarHeight);
                cd.setArguments(bundle);
                return cd;
            }
        }

        @Override
        public int getCount(){
            return NUM_PAGES;
        }
    }
}
