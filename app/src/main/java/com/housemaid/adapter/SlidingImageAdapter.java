package com.housemaid.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

// AQuery removed - use Glide for image loading
import com.bumptech.glide.Glide;
import com.housemaid.R;
import com.housemaid.activities.ZoomPhotoActivity;

import java.util.ArrayList;

public class SlidingImageAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<String> imagesList;

    public SlidingImageAdapter(Context context, ArrayList<String> imagesList) {
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
        View view = layoutInflater.inflate(R.layout.fullscreen_sliding_image, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.fullScreenImage);

       /* imageView.setOnClickListener(v -> {

        });*/

        Glide.with(context).load(imagesList.get(position)).into(imageView);
        Log.d("SlidingImageAdapter", imagesList.get(position));


        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);

        /*imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                context.startActivity(new Intent(context, ZoomPhotoActivity.class)
                        .putExtra("images", imagesList.get(position)));

                Log.e("data","pos");
            }
        });*/
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }
}

