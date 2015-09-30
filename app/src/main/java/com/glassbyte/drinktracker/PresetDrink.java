package com.glassbyte.drinktracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Maciej on 27/05/15.
 */
public class PresetDrink extends Fragment {
    private static int stage = 0;
    private enum PresetCategory {BEER, WINE, COCKTAILS, SPIRITS};
    private PresetCategory currentCategory;
    private BaseAdapter adapter;
    private GridView gridView;
    private float currentChosenPecentage = 0;
    private int currentChosenVolume = 0;
    Preset[] categoryPresets;
    Preset[] spiritStageOnePresets;
    Preset[] spiritStageTwoPresets;
    Preset[] beerStageOnePresets;
    Preset[] beerStageTwoPresets;
    Preset[] wineStageOnePresets;
    Preset[] cocktailsPresets;
    DrinkTrackerDbHelper dtDb;
    Drawable beerImageDrawable;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dtDb = new DrinkTrackerDbHelper(this.getContext());

        LocalBroadcastManager.getInstance(this.getContext()).registerReceiver(onBackPressedBroadcastReceiver,
                new IntentFilter(MainActivity.ON_BACK_PRESSED_EVENT));

        beerImageDrawable = this.getContext().getDrawable(R.drawable.ic_beer);
        
        categoryPresets = new Preset[4];
        categoryPresets[0] =
                new Preset(beerImageDrawable, getString(R.string.beerCategory));
        categoryPresets[1] =
                new Preset(beerImageDrawable, getString(R.string.wineCategory), 17f);
        categoryPresets[2] =
                new Preset(beerImageDrawable, getString(R.string.spiritsCategory));
        categoryPresets[3] =
                new Preset(beerImageDrawable, getString(R.string.cocktailsCategory));

        spiritStageOnePresets = new Preset[9];
        spiritStageOnePresets[0] =
            new Preset(beerImageDrawable, getString(R.string.absinthe_spirit), 70f);
        spiritStageOnePresets[1] =
            new Preset(beerImageDrawable, getString(R.string.brandy_spirit), 40f);
        spiritStageOnePresets[2] =
            new Preset(beerImageDrawable, getString(R.string.gin_spirit), 40f);
        spiritStageOnePresets[3] =
            new Preset(beerImageDrawable, getString(R.string.jager_spirit), 35f);
        spiritStageOnePresets[4] =
            new Preset(beerImageDrawable, getString(R.string.rum_spirit), 40f);
        spiritStageOnePresets[5] =
            new Preset(beerImageDrawable, getString(R.string.sambuca_spirit), 42f);
        spiritStageOnePresets[6] =
            new Preset(beerImageDrawable, getString(R.string.tequila_spirit), 40f);
        spiritStageOnePresets[7] =
            new Preset(beerImageDrawable, getString(R.string.vodka_spirit), 40f);
        spiritStageOnePresets[8] =
            new Preset(beerImageDrawable, getString(R.string.whiskey_spirit), 40f);

        spiritStageTwoPresets = new Preset[3];
        spiritStageTwoPresets[0] =
                new Preset(beerImageDrawable, getString(R.string.q_single), (int)35);
        spiritStageTwoPresets[1] =
                new Preset(beerImageDrawable, getString(R.string.q_double), (int)70);
        spiritStageTwoPresets[2] =
                new Preset(beerImageDrawable, getString(R.string.q_triple), (int)105);

        beerStageOnePresets = new Preset[12];
        beerStageOnePresets[0] =
                new Preset(beerImageDrawable, "1-2%", 1.5f);
        beerStageOnePresets[1] =
                new Preset(beerImageDrawable, "2-3%", 2.5f);
        beerStageOnePresets[2] =
                new Preset(beerImageDrawable, "3-4%", 3.5f);
        beerStageOnePresets[3] =
                new Preset(beerImageDrawable, "4-5%", 4.5f);
        beerStageOnePresets[4] =
                new Preset(beerImageDrawable, "5-6%", 5.5f);
        beerStageOnePresets[5] =
                new Preset(beerImageDrawable, "6-7%", 6.5f);
        beerStageOnePresets[6] =
                new Preset(beerImageDrawable, "7-8%", 7.5f);
        beerStageOnePresets[7] =
                new Preset(beerImageDrawable, "8-9%", 8.5f);
        beerStageOnePresets[8] =
                new Preset(beerImageDrawable, "9-10%", 9.5f);
        beerStageOnePresets[9] =
                new Preset(beerImageDrawable, "10-11%", 10.5f);
        beerStageOnePresets[10] =
                new Preset(beerImageDrawable, "11-12%", 11.5f);
        beerStageOnePresets[11] =
                new Preset(beerImageDrawable, "12-13%", 12.5f);

        beerStageTwoPresets = new Preset[4];
        beerStageTwoPresets[0] =
                new Preset(beerImageDrawable, "250ml", (int)250);
        beerStageTwoPresets[1] =
                new Preset(beerImageDrawable, "330ml", (int)330);
        beerStageTwoPresets[2] =
                new Preset(beerImageDrawable, "350ml", (int)330);
        beerStageTwoPresets[3] =
                new Preset(beerImageDrawable, "500ml", (int)500);

        wineStageOnePresets = new Preset[3];
        wineStageOnePresets[0] =
                new Preset(beerImageDrawable, getString(R.string.quarter_wine_glass),(int)(215/4));
        wineStageOnePresets[1] =
                new Preset(beerImageDrawable, getString(R.string.half_wine_glass),(int)(215/2));
        wineStageOnePresets[2] =
                new Preset(beerImageDrawable, getString(R.string.three_quarter_wine_glass),(int)(215/4*3));

        cocktailsPresets = new Preset[30];
        cocktailsPresets[0] = new Preset(beerImageDrawable,
                getString(R.string.bellini_cocktail),
                0.4f, 300);
        cocktailsPresets[1] = new Preset(beerImageDrawable,
                getString(R.string.black_russian_cocktail),
                8f, 300);
        cocktailsPresets[2] = new Preset(beerImageDrawable,
                getString(R.string.bloody_mary_cocktail),
                6f, 300);
        cocktailsPresets[3] = new Preset(beerImageDrawable,
                getString(R.string.caipirinha_cocktail),
                6.7f, 300);
        cocktailsPresets[4] = new Preset(beerImageDrawable,
                getString(R.string.champagne_cocktail),
                7.9f, 300);
        cocktailsPresets[5] = new Preset(beerImageDrawable,
                getString(R.string.cosmopolitan_cocktail),
                17.3f, 130);
        cocktailsPresets[6] = new Preset(beerImageDrawable,
                getString(R.string.cuba_libre_cocktail),
                15.4f, 130);
        cocktailsPresets[7] = new Preset(beerImageDrawable,
                getString(R.string.french_75_cocktail),
                6.4f, 300);
        cocktailsPresets[8] = new Preset(beerImageDrawable,
                getString(R.string.french_connection_cocktail),
                8f, 300);
        cocktailsPresets[9] = new Preset(beerImageDrawable,
                getString(R.string.god_father_cocktail),
                8f, 300);
        cocktailsPresets[10] = new Preset(beerImageDrawable,
                getString(R.string.god_mother_cocktail),
                8f, 300);
        cocktailsPresets[11] = new Preset(beerImageDrawable,
                getString(R.string.golden_dream_cocktail),
                9.6f, 130);
        cocktailsPresets[12] = new Preset(beerImageDrawable,
                getString(R.string.grasshopper_cocktail),
                11.5f, 130);
        cocktailsPresets[13] = new Preset(beerImageDrawable,
                getString(R.string.harvey_wallbanger_cocktail),
                8.1f, 300);
        cocktailsPresets[14] = new Preset(beerImageDrawable,
                getString(R.string.horses_neck_cocktail),
                6.8f, 300);
        cocktailsPresets[15] = new Preset(beerImageDrawable,
                getString(R.string.irish_coffee_cocktail),
                6.8f, 350);
        cocktailsPresets[16] = new Preset(beerImageDrawable,
                getString(R.string.kir_cocktail),
                6.6f, 215);
        cocktailsPresets[17] = new Preset(beerImageDrawable,
                getString(R.string.long_island_iced_tea_cocktail),
                9f, 300);
        cocktailsPresets[18] = new Preset(beerImageDrawable,
                getString(R.string.mai_tai_cocktail),
                9.5f, 300);
        cocktailsPresets[19] = new Preset(beerImageDrawable,
                getString(R.string.margarita_cocktail),
                6.3f, 350);
        cocktailsPresets[20] = new Preset(beerImageDrawable,
                getString(R.string.mimosa_cocktail),
                3.3f, 300);
        cocktailsPresets[21] = new Preset(beerImageDrawable,
                getString(R.string.mint_julep_cocktail),
                8f, 300);
        cocktailsPresets[22] = new Preset(beerImageDrawable,
                getString(R.string.mojito_cocktail),
                4f, 400);
        cocktailsPresets[23] = new Preset(beerImageDrawable,
                getString(R.string.moscow_mule_cocktail),
                5.1f, 350);
        cocktailsPresets[24] = new Preset(beerImageDrawable,
                getString(R.string.pina_colada_cocktail),
                4f, 300);
        cocktailsPresets[25] = new Preset(beerImageDrawable,
                getString(R.string.rose_cocktail),
                13.1f, 130);
        cocktailsPresets[26] = new Preset(beerImageDrawable,
                getString(R.string.sea_breeze_cocktail),
                5.3f, 300);
        cocktailsPresets[27] = new Preset(beerImageDrawable,
                getString(R.string.sex_on_the_beach_cocktail),
                6.3f, 300);
        cocktailsPresets[28] = new Preset(beerImageDrawable,
                getString(R.string.singapore_sling_cocktail),
                8.3f, 300);
        cocktailsPresets[29] = new Preset(beerImageDrawable,
                getString(R.string.tequila_sunrise_cocktail),
                4.6f, 400);
    }

    private BroadcastReceiver onBackPressedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (stage > 0){
                stage--;
                switch (stage) {
                    case 0:
                        gridView.setAdapter(new PresetsAdapter(context));
                        break;
                    default:
                        if (currentCategory == PresetCategory.BEER)
                            gridView.setAdapter(new PresetsAdapter(context));
                        else if (currentCategory == PresetCategory.SPIRITS)
                            gridView.setAdapter(new PresetsAdapter(context));
                        else if (currentCategory == PresetCategory.COCKTAILS)
                            gridView.setAdapter(new PresetsAdapter(context));
                }
                gridView.invalidate();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_presetdrink, container, false);

        gridView = (GridView) v.findViewById(R.id.gridview);
        adapter = new PresetsAdapter(this.getContext());
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Preset[] currentPresets = null;

                if (stage == 0) {
                    //at chose preset category
                    currentPresets = categoryPresets;
                    stage++;
                    switch (position) {
                        case 0:
                            //beer
                            currentCategory = PresetCategory.BEER;
                            break;
                        case 1:
                            //wine
                            currentCategory = PresetCategory.WINE;
                            break;
                        case 2:
                            //spirits
                            currentCategory = PresetCategory.SPIRITS;
                            break;
                        case 3:
                            //cocktails
                            currentCategory = PresetCategory.COCKTAILS;
                            break;
                    }
                    gridView.setAdapter(adapter);
                } else if (stage == 1) {
                    if (currentCategory == PresetCategory.BEER) {
                        currentPresets = beerStageOnePresets;
                        stage++;
                        gridView.setAdapter(adapter);
                    } else if (currentCategory == PresetCategory.SPIRITS) {
                        currentPresets = spiritStageOnePresets;
                        stage++;
                        gridView.setAdapter(adapter);
                    } else if (currentCategory == PresetCategory.COCKTAILS) {
                        currentPresets = cocktailsPresets;
                        if (currentPresets[position].isPercentageAssigned()){
                            currentChosenPecentage = currentPresets[position].getPercentage();
                        }
                        if (currentPresets[position].isVolumeAssigned()){
                            currentChosenVolume = currentPresets[position].getVolume();
                        }

                        dtDb.insertNewDrink("Cocktail", currentChosenVolume, currentChosenPecentage);
                        Toast.makeText(PresetDrink.this.getContext(), "Drink added!\nPercent: " +
                                        currentChosenPecentage + "\nVolume: " + currentChosenVolume,
                                Toast.LENGTH_SHORT).show();
                        return;
                    } else if (currentCategory == PresetCategory.WINE) {
                        currentPresets = wineStageOnePresets;
                        if (currentPresets[position].isPercentageAssigned()){
                            currentChosenPecentage = currentPresets[position].getPercentage();
                        }
                        if (currentPresets[position].isVolumeAssigned()){
                            currentChosenVolume = currentPresets[position].getVolume();
                        }

                        dtDb.insertNewDrink("Wine", currentChosenVolume, currentChosenPecentage);
                        Toast.makeText(PresetDrink.this.getContext(), "Drink added!\nPercent: " +
                                        currentChosenPecentage + "\nVolume: " + currentChosenVolume,
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else if (stage == 2) {
                    if (currentCategory == PresetCategory.BEER) {
                        currentPresets = beerStageTwoPresets;
                        if (currentPresets[position].isPercentageAssigned()){
                            currentChosenPecentage = currentPresets[position].getPercentage();
                        }
                        if (currentPresets[position].isVolumeAssigned()){
                            currentChosenVolume = currentPresets[position].getVolume();
                        }

                        dtDb.insertNewDrink("Beer", currentChosenVolume, currentChosenPecentage);
                        Toast.makeText(PresetDrink.this.getContext(), "Drink added!\nPercent: " +
                                        currentChosenPecentage + "\nVolume: " + currentChosenVolume,
                                Toast.LENGTH_SHORT).show();
                        return;
                    } else if (currentCategory == PresetCategory.SPIRITS) {
                        currentPresets = spiritStageTwoPresets;
                        if (currentPresets[position].isPercentageAssigned()){
                            currentChosenPecentage = currentPresets[position].getPercentage();
                        }
                        if (currentPresets[position].isVolumeAssigned()){
                            currentChosenVolume = currentPresets[position].getVolume();
                        }

                        dtDb.insertNewDrink("Spirit", currentChosenVolume, currentChosenPecentage);
                        Toast.makeText(PresetDrink.this.getContext(), "Drink added!\nPercent: " +
                                        currentChosenPecentage + "\nVolume: " + currentChosenVolume,
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (currentPresets != null) {
                    if (currentPresets[position].isPercentageAssigned()){
                        currentChosenPecentage = currentPresets[position].getPercentage();
                    }

                    if (currentPresets[position].isVolumeAssigned()){
                        currentChosenVolume = currentPresets[position].getVolume();
                    }
                }
            }
        });

        return v;
    }

    public void onPause(){
        dtDb.close();
        super.onPause();
    }

    public void onResume(){
        dtDb = new DrinkTrackerDbHelper(this.getContext());
        super.onResume();
    }

    public void onDestroy(){
        dtDb.close();
        super.onDestroy();
    }

    public static int getCurrentStage(){return stage;}

    public class PresetsAdapter extends BaseAdapter {
        Context mContext;
        LayoutInflater inflater;
        public PresetsAdapter(Context context){
            mContext = context;

            inflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            int count = 0;

            if (stage == 0) {
                count = categoryPresets.length;
            } else if (stage == 1) {
                if (currentCategory == PresetCategory.BEER)
                    count = beerStageOnePresets.length;
                else if (currentCategory == PresetCategory.SPIRITS)
                    count = spiritStageOnePresets.length;
                else if (currentCategory == PresetCategory.WINE)
                    count = wineStageOnePresets.length;
                else if (currentCategory == PresetCategory.COCKTAILS)
                    count = cocktailsPresets.length;
            } else if (stage == 2) {
                if (currentCategory == PresetCategory.BEER)
                    count = beerStageTwoPresets.length;
                else if (currentCategory == PresetCategory.SPIRITS)
                    count = spiritStageTwoPresets.length;
            }

            return count;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View view = convertView;

            if (view == null){
                view = inflater.inflate(R.layout.preset_tile_layout, null);

                String title = "";
                int imageId = 0;
                ImageView presetIV = (ImageView)view.findViewById(R.id.presetTileImage);
                switch (stage) {
                    case 0:
                        title = categoryPresets[i].getTitle();
                        presetIV.setImageDrawable(beerStageOnePresets[i].getImageDrawable());
                        break;
                    case 1:
                        if (currentCategory == PresetCategory.BEER) {
                            title = beerStageOnePresets[i].getTitle();
                            presetIV.setImageDrawable(beerStageOnePresets[i].getImageDrawable());
                        } else if (currentCategory == PresetCategory.SPIRITS) {
                            title = spiritStageOnePresets[i].getTitle();
                            presetIV.setImageDrawable(beerStageOnePresets[i].getImageDrawable());
                        } else if (currentCategory == PresetCategory.COCKTAILS) {
                            title = cocktailsPresets[i].getTitle();
                            presetIV.setImageDrawable(beerStageOnePresets[i].getImageDrawable());
                        } else if (currentCategory == PresetCategory.WINE) {
                            title = wineStageOnePresets[i].getTitle();
                            presetIV.setImageDrawable(beerStageOnePresets[i].getImageDrawable());
                        }
                        break;
                    case 2:
                        if (currentCategory == PresetCategory.BEER) {
                            title = beerStageTwoPresets[i].getTitle();
                            presetIV.setImageDrawable(beerStageOnePresets[i].getImageDrawable());
                        } else if (currentCategory == PresetCategory.SPIRITS) {
                            title = spiritStageTwoPresets[i].getTitle();
                            presetIV.setImageDrawable(beerStageOnePresets[i].getImageDrawable());
                        }break;
                }

                TextView titleTV = (TextView)view.findViewById(R.id.presetTileTitle);
                titleTV.setText(title);
            }

            return view;
        }
    }

    public class Preset {
        private String title;
        private Drawable img;
        private boolean percentageAssigned = false;
        private boolean volumeAssigned = false;
        private float percentage = 0;
        private int volume = 0;

        public Preset(Drawable img, String title){this(img, title, -1, -1);}
        public Preset(Drawable img, String title, float percentage){this(img, title, percentage, -1);}
        public Preset(Drawable img, String title, int volume){this(img, title, -1, volume);}
        public Preset(Drawable img, String title, float percentage, int volume){
            if (percentage > 0f) {
                percentageAssigned = true;
                this.percentage = percentage;
            }

            if (volume > 0) {
                volumeAssigned = true;
                this.volume = volume;
            }

            this.title = title;
            this.img = img;
        }

        public String getTitle(){return title;}
        public Drawable getImageDrawable(){return img;}

        public boolean isVolumeAssigned(){return volumeAssigned;}
        public boolean isPercentageAssigned(){return percentageAssigned;}

        public int getVolume(){return volume;}
        public float getPercentage(){return percentage;}
    }
}