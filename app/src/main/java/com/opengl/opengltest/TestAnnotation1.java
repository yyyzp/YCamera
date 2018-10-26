package com.opengl.opengltest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.yzp.annotation.IntentField;

/**
 * Created by Answer on 2018/10/24.
 */

public class TestAnnotation1 extends Activity {
    @IntentField("TestAnnotation2")
    int count = 10;

    @IntentField("TestAnnotation2")
    String str = "编译器注解";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testannotation1);
        addOnclickListener();
    }

    public void addOnclickListener() {
        findViewById(R.id.btn_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 从哪个界面进行跳转，则以哪个界面打头，enter 结尾，例如 MainActivity$Enter
                 */
                new TestAnnotation1$Enter()
                        .intentTo(TestAnnotation1.this,count,str);
            }
        });
    }
}

