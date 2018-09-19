package com.opengl.opengltest.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.opengl.opengltest.R;

/**
 * Created by zhipengyang on 2018/9/19.
 * 旋转动画的view
 */

public class RotateAnimateView extends View {

    //Y轴方向旋转角度
    private float degreeY;
    //不变的那一半，Y轴方向旋转角度
    private float fixDegreeY;
    //Z轴方向（平面内）旋转的角度
    private float degreeZ;

    private Paint paint;
    private Bitmap bitmap;
    private Camera camera;

    public RotateAnimateView(Context context) {
        this(context, null);
    }

    public RotateAnimateView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotateAnimateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RotateAnimateView);
        BitmapDrawable drawable = (BitmapDrawable) a.getDrawable(R.styleable.RotateAnimateView_mv_background);
        a.recycle();

        if (drawable != null) {
            bitmap = drawable.getBitmap();
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.flip_board);
        }
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        camera = new Camera();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float newZ = -displayMetrics.density * 6;
        camera.setLocation(0, 0, newZ);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int x = centerX - bitmapWidth / 2;
        int y = centerY - bitmapHeight / 2;

        //画变换的一半
        canvas.save();//保存最初的画布状态
        camera.save();
        canvas.translate(centerX, centerY);//将坐标原点移动到中心 ，此后的变换将依照这个点进行
        camera.rotateY(degreeY);  //绕Y轴3维旋转变换
        canvas.rotate(-degreeZ);    //画布绕Z轴进行旋转
        camera.applyToCanvas(canvas);//3维旋转应用到canvas
        camera.restore();
        canvas.clipRect(0, -centerY, centerX, centerY);//将变换过的画布进行裁剪 只保留屏幕的右半部分 因为坐标原点已经变化 此时显示在屏幕上的画面坐标已与将屏幕左上角
        //作为坐标原点的坐标不同 需以中心作为坐标原点进行计算
        canvas.rotate(degreeZ);//将画布旋转回来 达到中间动画效果
        canvas.translate(-centerX, -centerY);//讲坐标原点移动回屏幕左上角 因为下面画bitmap的x y坐标是依照左上角为原点的坐标
        canvas.drawBitmap(bitmap, x, y, paint);//将bitmap滑倒画布上
        canvas.restore();//还原画布到最初的状态

        //画不变换的另一半  和前面基本相同  注意裁剪时只保留左半部分
        canvas.save();
        camera.save();
        canvas.translate(centerX, centerY);
        canvas.rotate(-degreeZ);
        camera.rotateY(fixDegreeY);
        camera.applyToCanvas(canvas);
        camera.restore();
        canvas.clipRect(-centerX, -centerY, 0, centerY);
        canvas.rotate(degreeZ);
        canvas.translate(-centerX, -centerY);
        canvas.drawBitmap(bitmap, x, y, paint);
        canvas.restore();

    }

    /**
     * 启动动画之前调用，把参数reset到初始状态
     */
    public void reset() {
        degreeY = 0;
        fixDegreeY = 0;
        degreeZ = 0;
    }

    @Keep
    public void setFixDegreeY(float fixDegreeY) {
        this.fixDegreeY = fixDegreeY;
        invalidate();
    }

    @Keep
    public void setDegreeY(float degreeY) {
        this.degreeY = degreeY;
        invalidate();
    }

    @Keep
    public void setDegreeZ(float degreeZ) {
        this.degreeZ = degreeZ;
        invalidate();
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        invalidate();
    }

}

