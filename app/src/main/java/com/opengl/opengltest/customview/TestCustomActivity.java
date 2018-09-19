package com.opengl.opengltest.customview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.opengl.opengltest.R;

/**
 * Created by zhipengyang on 2018/9/17.
 */

public class TestCustomActivity extends Activity {
    RotateAnimateView rotate_animate_view;
    Handler handler;
    ThumbView thumbView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_view);
        thumbView=findViewById(R.id.thumbView);
        thumbView.setOnClickListener(v->{
            thumbView.startAnim();
            if(thumbView.isThumbUp()){
                thumbView.setIsThumbUp(false);
            }else {
                thumbView.setIsThumbUp(true);
            }
        });
    }

    /**
     * 整个动画被拆分成为三个部分
     * 1、绕Y轴3D旋转45度
     * 2、绕Z轴3D旋转270度
     * 3、不变的那一半（上半部分）绕Y轴旋转30度（注意，这里canvas已经旋转了270度，计算第三个动效参数时要注意）
     */
    private void initViewAnimate() {
        handler = new Handler();
        rotate_animate_view = (RotateAnimateView) findViewById(R.id.rotate_animate_view);

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(rotate_animate_view, "degreeY", 0, -45);
        animator1.setDuration(1000);
        animator1.setStartDelay(500);

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(rotate_animate_view, "degreeZ", 0, 270);
        animator2.setDuration(800);
        animator2.setStartDelay(500);

        ObjectAnimator animator3 = ObjectAnimator.ofFloat(rotate_animate_view, "fixDegreeY", 0, 30);
        animator3.setDuration(500);
        animator3.setStartDelay(500);

        final AnimatorSet set = new AnimatorSet();
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rotate_animate_view.reset();
                        set.start();
                    }
                }, 500);
            }
        });
        set.playSequentially(animator1, animator2, animator3);
        set.start();
    }

}
