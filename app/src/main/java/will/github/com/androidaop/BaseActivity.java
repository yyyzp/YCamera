package will.github.com.androidaop;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.opengl.opengltest.widget.SHSlideLayout;
import com.opengl.opengltest.widget.SwipeBackLayout;

/**
 * Created by Answer on 2019/1/2.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        SHSlideLayout shSlideLayout=new SHSlideLayout(this);
//        shSlideLayout.bindActivity(this);
        SwipeBackLayout shSlideLayout=new SwipeBackLayout(this);
        shSlideLayout.attachToActivity(this);
        super.onCreate(savedInstanceState);
    }
}
