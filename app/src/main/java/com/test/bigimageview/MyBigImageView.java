package com.test.bigimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;
import androidx.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;

public class MyBigImageView extends View implements GestureDetector.OnGestureListener, View.OnTouchListener {

    private Scroller mScroll;
    private Rect mRect;
    private int pictureWidth,pictureHeight;//图片宽高
    private int viewWidth,viewHeight;//控件宽高
    private BitmapFactory.Options mOptions;
    private GestureDetector mGestureDetector;
    private BitmapRegionDecoder mDecoder;//解码器
    private float mScale;//压缩比例
    private Bitmap mBitmap;
    private Matrix matrix;//图片压缩类
    public MyBigImageView(Context context) {
        this(context,null);
    }
    /*
    *   注意：因为在xml文件中使用自定义控件的时候调用的是第二个构造方法，所以该构造方法
    *   必须被重写，否则会直接报错
    * */
    public MyBigImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyBigImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRect = new Rect();
        mOptions = new BitmapFactory.Options();
        mScroll = new Scroller(context);
        matrix = new Matrix();
        mGestureDetector = new GestureDetector(this);
        setOnTouchListener(this);
    }
    public void setPicture(InputStream inputStream){
        //  在不把图片加载进内存的情况下获取图片的宽和高
        mOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream,null,mOptions);
        pictureWidth = mOptions.outWidth;
        pictureHeight = mOptions.outHeight;
        //  开启内存复用
        mOptions.inMutable = true;
        /*
        *注：在计算机中，图片是由像素点组成，而像素点又是由三原色（红绿蓝，即RGB）组成，
        *   565代表的是每个颜色的像素点占多少位，而565格式表示每个像素点占据16位，即两个字节。
        *   ARGB中的A表示透明度。
        *内存优化：从内存优化的角度上来说，采用RGB_565替代ARGB_8888节省了一半的存储空间，
        *   从一定角度上来说本身就是一种内存优化，同时也提高了性能
        * */
        //  设置图片格式
        mOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        //  注意：这句话必须是和mOptions.inJustDecodeBounds=true代码成对出现，可以理解为将其改为true之后在使用完又复原
        mOptions.inJustDecodeBounds = false;
        //  创建解码区域
        try {
            mDecoder = BitmapRegionDecoder.newInstance(inputStream,false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //  1.获取控件宽高
        viewHeight = getMeasuredHeight();
        viewWidth = getMeasuredWidth();
        //  2.确定加载区域
        mRect.top = 0;
        mRect.left = 0;
        mRect.right = viewWidth;
        mScale = viewWidth/(float)pictureWidth;
        mRect.bottom = (int) (viewHeight/mScale);
        //  3.按照图片和屏幕比例，对图片开始压缩
        matrix.setScale(mScale,mScale);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDecoder == null)
            return;
        //  解析加载区域
        mBitmap = mDecoder.decodeRegion(mRect,mOptions);
        mOptions.inBitmap = mBitmap;
        // 绘制
        canvas.drawBitmap(mBitmap,matrix,null);
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        //  如果图片由于惯性仍然在滑动，当手指又放到屏幕上时，上一次的滑动应该被停止
        if (!mScroll.isFinished())
            mScroll.forceFinished(true);
        //  在由于惯性任然在滑动，我们又将手指放了上去，这时又摆动手指滑动，所以应该返回true
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        mRect.offset(0, (int) distanceY);
        if (mRect.bottom > pictureHeight) {
            mRect.bottom = pictureHeight;
            mRect.top = (int) (pictureHeight - viewHeight/mScale);
        }
        if (mRect.top < 0){
            mRect.top = 0;
            mRect.bottom = (int) (viewHeight/mScale);
        }
        invalidate();//通过调用该方法去触发onDraw，对区域进行重新绘制
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
