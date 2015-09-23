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
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Maciej on 27/05/15.
 */
public class PresetDrink extends Fragment {
    private static int stage = 0;
    private enum PresetCategory {BEER, WINE, COCKTAILS, SPIRITS};
    private PresetCategory currentCategory;

    private GridView gridView;

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
                            gridView.setAdapter(new BeerAdapter(context));
                        else if (currentCategory == PresetCategory.SPIRITS)
                            gridView.setAdapter(new SpiritsAdapter(context));
                        else if (currentCategory == PresetCategory.COCKTAILS)
                            gridView.setAdapter(new SpiritsAdapter(context));
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
        gridView.setAdapter(new PresetsAdapter(this.getContext()));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(PresetDrink.this.getContext(), "" + position,
                        Toast.LENGTH_SHORT).show();

                if (stage == 0) {
                    //at chose preset category
                    switch (position) {
                        case 0:
                            //beer
                            gridView.setAdapter(new BeerAdapter(PresetDrink.this.getContext()));
                            stage++;
                            break;
                        case 1:
                            //wine
                            break;
                        case 2:
                            //spirits
                            gridView.setAdapter(new SpiritsAdapter(PresetDrink.this.getContext()));
                            stage++;
                            break;
                        case 3:
                            //cocktails
                            gridView.setAdapter(new CocktailsAdapter(PresetDrink.this.getContext()));
                            stage++;
                            break;
                    }
                } else if (stage == 1) {
                    
                }

            }
        });

        return v;
    }

    public static int getCurrentStage(){return stage;}

    public class PresetsAdapter extends BaseAdapter {
        Context mContext;
        LayoutInflater inflater;
        Preset[] presets = {
                new Preset(R.drawable.ic_beer, "Beer"),
                new Preset(R.drawable.ic_beer, "Wine"),
                new Preset(R.drawable.ic_beer, "Spirits"),
                new Preset(R.drawable.ic_beer, "Cocktails"),
        };

        public PresetsAdapter(Context context){
            mContext = context;

            inflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return presets.length;
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

                TextView titleTV = (TextView)view.findViewById(R.id.presetTileTitle);
                titleTV.setText(presets[i].getTitle());

                ImageView presetIV = (ImageView)view.findViewById(R.id.presetTileImage);
                presetIV.setImageResource(presets[i].getImageResId());

            }

            return view;
        }
    }

    public class BeerAdapter extends BaseAdapter {
        LayoutInflater inflater;
        Preset[] presets = {
                new Preset(R.drawable.ic_beer, "2-3%"),
                new Preset(R.drawable.ic_beer, "4-5%"),
                new Preset(R.drawable.ic_beer, "6-7%"),
                new Preset(R.drawable.ic_beer, "7-8%")
        };

        public BeerAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return 0;
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = inflater.inflate(R.layout.preset_tile_layout, null);


            }
            return view;
        }
    }

    public class SpiritsAdapter extends BaseAdapter {
        Context mContext;

        public SpiritsAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return 0;
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            return null;
        }
    }

    public class CocktailsAdapter extends BaseAdapter {
        Context mContext;

        public CocktailsAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return 0;
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            return null;
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