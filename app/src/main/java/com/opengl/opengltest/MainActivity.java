//package com.opengl.opengltest;
//
//import android.graphics.Point;
//import android.opengl.GLSurfaceView;
//import android.support.design.widget.TabLayout;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentPagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.support.v7.widget.Toolbar;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.Switch;
//import android.widget.TableLayout;
//
//public class MainActivity extends AppCompatActivity implements View.OnClickListener {
//
//    private GLSurfaceView myGlSurfaceView;
//    private SurfaceView surfaceview;
//    private LowCamera mCamera2;
//    private int cameraId = 0;
//
//
//    private Toolbar toolbar;
//    private TabLayout tabLayout;
//    private ViewPager viewPager;
//    private Button btn_start;
//    private Button btn_stop;
//    private YMediaRecorder yMediaRecorder;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        btn_start = findViewById(R.id.btn_start);
//        btn_stop = findViewById(R.id.btn_stop);
//        btn_stop.setOnClickListener(this);
//        btn_start.setOnClickListener(this);
////        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
////        toolbar.setTitle("my toolbar");
////        setSupportActionBar(toolbar);
////        CameraView mCameraView = new CameraView(this);
//        surfaceview = findViewById(R.id.surfaceview);
//
//        mCamera2 = new LowCamera();
//
//        SurfaceHolder surfaceHolder = surfaceview.getHolder();
//
//        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceCreated(SurfaceHolder holder) {
//                mCamera2.open(cameraId);
//                Point point = mCamera2.getPreviewSize();
//                mCamera2.setPreviewSurface(holder);
//                mCamera2.preview();
//                yMediaRecorder = new YMediaRecorder(mCamera2.getmCamera(),holder.getSurface());
//
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//            }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder holder) {
//
//            }
//        });
//        // setType必须设置，要不出错.
//        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
////        mCamera2.setPreviewSurface(surfaceHolder);
////        CameraView2 mCameraView2 = new CameraView2(this);
////        CameraView3 mCameraView3 = new CameraView3(this);
////
////        setContentView(mCameraView3);
////        myGlSurfaceView=new MyGlSurfaceView(this);
////        setContentView(mCameraView);
////        tabLayout=findViewById(R.id.tabs);
////        tabLayout.addTab(tabLayout.newTab().setText("AAAAA"));
////        tabLayout.addTab(tabLayout.newTab().setText("BBBBB"));
////        tabLayout.addTab(tabLayout.newTab().setText("CCCCC"));
////        tabLayout.addTab(tabLayout.newTab().setText("DDDDD"));
////        final String []mTitle={"AAAAA","BBBBB","CCCCCC"};
////        viewPager=findViewById(R.id.viewpager);
////        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
////            //此方法用来显示tab上的名字
////            @Override
////            public CharSequence getPageTitle(int position) {
////                return mTitle[position % mTitle.length];
////            }
////
////            @Override
////            public Fragment getItem(int position) {
////                //创建Fragment并返回
////                TabFragment fragment = new TabFragment();
////                fragment.setTitle(mTitle[position % mTitle.length]);
////                return fragment;
////            }
////
////            @Override
////            public int getCount() {
////                return mTitle.length;
////            }
////        });
////        tabLayout.setupWithViewPager(viewPager);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_start:
//                yMediaRecorder.startRecorder();
//                break;
//            case R.id.btn_stop:
//                yMediaRecorder.stopRecorder();
//                break;
//            default:
//                break;
//        }
//    }
//}
