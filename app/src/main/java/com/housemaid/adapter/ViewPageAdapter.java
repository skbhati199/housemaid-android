package com.housemaid.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

// AQuery removed - use Glide for image loading
import com.housemaid.R;
import com.housemaid.activities.FullScreenImageSlider;
import com.housemaid.activities.ZoomPhotoActivity;

import java.util.ArrayList;

/**
 * Created by fluper on 24/8/18.
 */


public class ViewPageAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<String> imagesList;

    public ViewPageAdapter(Context context, ArrayList<String> imagesList) {
        this.context = context;
        this.imagesList = imagesList;
    }

    @Override
    public int getCount() {
        return imagesList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_layout, null);


        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);

       /* imageView.setOnClickListener(v -> {

        });*/

        AQuery aQuery = new AQuery(imageView);
        aQuery.id(imageView).image(imagesList.get(position));
        Log.d("MyViewPagerAdapter", imagesList.get(position));


        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, FullScreenImageSlider.class);
                intent.putExtra("images", imagesList);
                intent.putExtra("imagePosition",position);
                context.startActivity(intent);

                Log.e("data","pos");
            }
        });
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }

}