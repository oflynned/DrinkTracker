package com.glassbyte.drinktracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.ListAdapter;
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

    Preset[] categoryPresets = {
            new Preset(R.drawable.ic_beer, "Beer"),
            new Preset(R.drawable.ic_beer, "Wine"),
            new Preset(R.drawable.ic_beer, "Spirits"),
            new Preset(R.drawable.ic_beer, "Cocktails"),
    };
    Preset[] spiritPresets = {
            new Preset(R.drawable.ic_beer, "30%\nShot"),
            new Preset(R.drawable.ic_beer, "35%\nShot"),
            new Preset(R.drawable.ic_beer, "40%\nShot"),
            new Preset(R.drawable.ic_beer, "45%\nShot")
    };
    Preset[] beerStageOnePresets = {
            new Preset(R.drawable.ic_beer, "2-3%"),
            new Preset(R.drawable.ic_beer, "4-5%"),
            new Preset(R.drawable.ic_beer, "6-7%"),
            new Preset(R.drawable.ic_beer, "7-8%")
    };
    Preset[] beerStageTwoPresets = {
            new Preset(R.drawable.ic_beer, "250ml"),
            new Preset(R.drawable.ic_beer, "330ml"),
            new Preset(R.drawable.ic_beer, "500ml")
    };
    Preset[] cocktailsPresets = {
            new Preset(R.drawable.ic_beer, "Black Russian"),
            new Preset(R.drawable.ic_beer, "White Russian"),
            new Preset(R.drawable.ic_beer, "Sex On The Beach"),
            new Preset(R.drawable.ic_beer, "Motherfucker")
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocalBroadcastManager.getInstance(this.getContext()).registerReceiver(onBackPressedBroadcastReceiver,
                new IntentFilter(MainActivity.ON_BACK_PRESSED_EVENT));

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

                if (stage == 0) {
                    //at chose preset category
                    switch (position) {
                        case 0:
                            //beer
                            adapter = new PresetsAdapter(PresetDrink.this.getContext());
                            gridView.setAdapter(adapter);
                            stage++;
                            currentCategory = PresetCategory.BEER;
                            break;
                        case 1:
                            //wine
                            break;
                        case 2:
                            //spirits
                            adapter = new PresetsAdapter(PresetDrink.this.getContext());
                            gridView.setAdapter(adapter);
                            stage++;
                            currentCategory = PresetCategory.SPIRITS;
                            break;
                        case 3:
                            //cocktails
                            adapter = new PresetsAdapter(PresetDrink.this.getContext());
                            gridView.setAdapter(adapter);
                            stage++;
                            currentCategory = PresetCategory.COCKTAILS;
                            break;
                    }
                } else if (stage == 1) {
                    if (currentCategory == PresetCategory.BEER) {
                      adapter.notifyDataSetChanged();
                    } else if (currentCategory == PresetCategory.SPIRITS) {
                        Toast.makeText(PresetDrink.this.getContext(), "Drink added! Pos:" + position,
                                Toast.LENGTH_SHORT).show();
                    } else if (currentCategory == PresetCategory.COCKTAILS) {
                        Toast.makeText(PresetDrink.this.getContext(), "Drink added! Pos:" + position,
                                Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        return v;
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
            return categoryPresets.length;
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
                switch (stage) {
                    case 0:
                        title = categoryPresets[i].getTitle();
                        imageId = categoryPresets[i].getImageResId();
                        break;
                    case 1:
                        if (currentCategory == PresetCategory.BEER) {
                            title = beerStageOnePresets[i].getTitle();
                            imageId = beerStageOnePresets[i].getImageResId();
                        } else if (currentCategory == PresetCategory.SPIRITS) {
                            title = spiritPresets[i].getTitle();
                            imageId = spiritPresets[i].getImageResId();
                        } else if (currentCategory == PresetCategory.COCKTAILS) {
                            title = cocktailsPresets[i].getTitle();
                            imageId = cocktailsPresets[i].getImageResId();
                        }
                        break;
                    case 2:
                        title = beerStageTwoPresets[i].getTitle();
                        imageId = beerStageTwoPresets[i].getImageResId();
                        break;
                }

                TextView titleTV = (TextView)view.findViewById(R.id.presetTileTitle);
                titleTV.setText(title);

                ImageView presetIV = (ImageView)view.findViewById(R.id.presetTileImage);
                presetIV.setImageResource(imageId);

            }

            return view;
        }
    }

    public class Preset {
        private String title;
        private int imageResId;

        public Preset(int imageResId, String title){
            this.title = title;
            this.imageResId = imageResId;
        }

        public String getTitle(){return title;}
        public int getImageResId(){return imageResId;}
    }
}