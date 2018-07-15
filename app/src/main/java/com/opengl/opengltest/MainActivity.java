package com.opengl.opengltest;

import android.opengl.GLSurfaceView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TableLayout;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView myGlSurfaceView;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.material_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("my toolbar");
        setSupportActionBar(toolbar);
//        CameraView mCameraView=new CameraView(this);
//        myGlSurfaceView=new MyGlSurfaceView(this);
//        setContentView(mCameraView);
        tabLayout=findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("AAAAA"));
        tabLayout.addTab(tabLayout.newTab().setText("BBBBB"));
        tabLayout.addTab(tabLayout.newTab().setText("CCCCC"));
        tabLayout.addTab(tabLayout.newTab().setText("DDDDD"));
        final String []mTitle={"AAAAA","BBBBB","CCCCCC"};
//        viewPager=findViewById(R.id.viewpager);
//        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
//            //此方法用来显示tab上的名字
//            @Override
//            public CharSequence getPageTitle(int position) {
//                return mTitle[position % mTitle.length];
//            }
//
//            @Override
//            public Fragment getItem(int position) {
//                //创建Fragment并返回
//                TabFragment fragment = new TabFragment();
//                fragment.setTitle(mTitle[position % mTitle.length]);
//                return fragment;
//            }
//
//            @Override
//            public int getCount() {
//                return mTitle.length;
//            }
//        });
//        tabLayout.setupWithViewPager(viewPager);
    }
}
