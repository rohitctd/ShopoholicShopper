package com.shopoholic.firebasechat.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.shopoholic.R;

import java.util.ArrayList;

import ru.truba.touchgallery.GalleryWidget.GalleryViewPager;
import ru.truba.touchgallery.GalleryWidget.UrlPagerAdapter;


public class FullScreenImageSliderActivity extends AppCompatActivity {
private Toolbar toolbar;
    private int position =0;
    private int height,width;
    ArrayList<String> imageList;
    ArrayList<String> captionList;
    ImageView iv_left,iv_right;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        iv_left = (ImageView) findViewById(R.id.iv_left);
        iv_right = (ImageView)findViewById(R.id.iv_right);
        final GalleryViewPager mViewPager = (GalleryViewPager)findViewById(R.id.viewer);
        if (toolbar != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//                window.setStatusBarColor(ContextCompat.getColor(FullScreenImageSliderActivity.this, R.color.colorBlack));
            }
            ((ImageView)toolbar.findViewById(R.id.back)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
       if(getIntent()!=null){
//           getIntent().getStringArrayListExtra("imagelist");
           position = getIntent().getIntExtra("pos",0);
           if(getIntent().getStringArrayListExtra("imagelist")!=null || getIntent().getStringArrayListExtra("imagelist").size()>0) {
               imageList = getIntent().getStringArrayListExtra("imagelist");
               captionList = getIntent().getStringArrayListExtra("captionList");
               UrlPagerAdapter pagerAdapter = new UrlPagerAdapter(this, imageList);
               mViewPager.setOffscreenPageLimit(2);
               mViewPager.setAdapter(pagerAdapter);
               mViewPager.setCurrentItem(position);
           }
       }
        if(imageList!=null && imageList.size()>1 && imageList.size()-1==position)
        {
            iv_right.setVisibility(View.GONE);
            iv_left.setVisibility(View.VISIBLE);
        }
        else if(imageList!=null && imageList.size()>1 && position==0)
        {
            iv_right.setVisibility(View.VISIBLE);
            iv_left.setVisibility(View.GONE);
        }
        else if(imageList!=null &&imageList.size()==1)
        {
            iv_right.setVisibility(View.GONE);
            iv_left.setVisibility(View.GONE);
        }
        else
        {
            iv_right.setVisibility(View.VISIBLE);
            iv_left.setVisibility(View.VISIBLE);
        }
        if(imageList!=null && imageList.size()==1)
        {
            iv_left.setVisibility(View.GONE);
            iv_right.setVisibility(View.GONE);
        }
        else
        {
            iv_left.setVisibility(View.VISIBLE);
            iv_right.setVisibility(View.VISIBLE);
        }
        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem()-1);
            }
        });
        iv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
            }
        });
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (imageList != null) {
                    if (imageList.size() > 1 && imageList.size() - 1 == position) {
                        iv_right.setVisibility(View.GONE);
                        iv_left.setVisibility(View.VISIBLE);
                    } else if (position == 0) {
                        iv_right.setVisibility(View.VISIBLE);
                        iv_left.setVisibility(View.GONE);
                    } else {
                        iv_right.setVisibility(View.VISIBLE);
                        iv_left.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
