package com.opengl.opengltest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by Answer on 2018/10/24.
 */

public class TestAnnotation2 extends Activity {
    /** 显示数据的TextView*/
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testannotation2);
        textView = (TextView) findViewById(R.id.text);

        /**
         * 想获取从哪个界面传递过来的数据，就已哪个类打头，init结尾，例如 MainActivity$Init
         */
        TestAnnotation1$Init formIntent =
                (TestAnnotation1$Init)new TestAnnotation1$Init().initFields(this,0);
        textView.setText(formIntent.count
                + "---"
                + formIntent.str
                + "---"
               );

        //打印上个界面传递过来的数据
        Log.i("Tag",formIntent.count + "---" + formIntent.str + "---" + formIntent);

    }

}