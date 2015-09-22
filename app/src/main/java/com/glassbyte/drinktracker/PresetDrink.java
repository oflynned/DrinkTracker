package com.glassbyte.drinktracker;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_presetdrink, container, false);

        GridView gridview = (GridView) v.findViewById(R.id.gridview);
        gridview.setAdapter(new PresetsAdapter(this.getContext()));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                    Toast.makeText(PresetDrink.this.getContext(), "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    public class PresetsAdapter extends BaseAdapter {
        Context mContext;
        LayoutInflater inflater;
        int[] imageIds = {
                R.drawable.ic_beer,
                R.drawable.ic_beer,
                R.drawable.ic_beer,
                R.drawable.ic_beer
        };
        String[] titles = {
                "Beer",
                "Wine",
                "Spirits",
                "Cocktails"
        };
        Preset[] presets;

        public PresetsAdapter(Context context){
            mContext = context;
            presets = new Preset[titles.length];

            for (int i =0; i<presets.length; i++) {
                    presets[i] = new Preset(mContext, imageIds[i], titles[i]);
            }

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


    public class Preset {
        private ImageView presetImage;
        private TextView titleTV;
        private String title;
        private Context mContext;
        private int imageResId;

        public Preset(Context c, int imageResId, String title){
            this.title = title;
            this.mContext = c;
            this.imageResId = imageResId;
            presetImage = new ImageView(c);
            presetImage.setImageResource(imageResId);
            titleTV = new TextView(mContext);
            titleTV.setText(this.title);
        }

        public ImageView getPresetImage(){return presetImage;}
        public TextView getTitleTextView(){return titleTV;}
        public String getTitle(){return title;}
        public int getImageResId(){return imageResId;}
    }
}