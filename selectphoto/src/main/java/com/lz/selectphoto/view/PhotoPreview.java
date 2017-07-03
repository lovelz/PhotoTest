package com.lz.selectphoto.view;


import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * 自定义ImageView
 * 实现图片的放大、放小、双击缩放
 * Created by liuzhu
 * on 2017/6/27.
 */

public class PhotoPreview extends AppCompatImageView{

    private static final String TAG = PhotoPreview.class.getSimpleName();

    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mFlatDetector;

    //缩放级别
    private float scale = 1.0f;
    //缩放的最小级别
    private static final float mMinScale = 0.4f;
    //缩放的最大级别
    private static final float mMaxScale = 4.0f;
    private int mBoundWidth = 0;
    private int mBoundHeight = 0;
    private float translateLeft = 0.f;
    private float translateTop = 0.f;

    private boolean isAutoScale = false;

    public PhotoPreview(Context context) {
        this(context, null);
    }

    public PhotoPreview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhotoPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        mFlatDetector = new GestureDetector(getContext(), new FlatGestureListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();

        mScaleDetector.onTouchEvent(event);
        mFlatDetector.onTouchEvent(event);
        return true;
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        super.setFrame(l, t, r, b);

        Drawable drawable = getDrawable();
        if (drawable == null) return false;
        if (mBoundWidth != 0 && mBoundHeight != 0 && scale != 1) return false;

        adjustBounds(getWidth(), getHeight());

        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        adjustBounds(w, h);
    }

    /**
     * 设置图片的显示区域
     * @param width
     * @param height
     */
    private void adjustBounds(int width, int height) {
        Drawable drawable = getDrawable();
        if (drawable == null) return;
        mBoundWidth = drawable.getBounds().width();
        mBoundHeight = drawable.getBounds().height();
        //得到此时的缩放比例（以宽为标准，让宽度充满屏幕，因为手机屏幕都是宽度比高度要小）
        float scale = (float) mBoundWidth / width;
        //重置宽高边界
        mBoundHeight /= scale;
        mBoundWidth = width;

        drawable.setBounds(0, 0, mBoundWidth, mBoundHeight);

        translateLeft = 0;
        translateTop = getDefaultTranslateTop(mBoundHeight, height);
    }

    /**
     * 得到Y轴上的偏移量
     * @param mBoundHeight 新高度
     * @param height 原高度
     * @return
     */
    private float getDefaultTranslateTop(int mBoundHeight, int height) {
        float top = (height - mBoundHeight) / 2.f;
        return top > 0 ? top : 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable mDrawable = getDrawable();
        if (mDrawable == null) return;

        //一大堆不懂的/(ㄒoㄒ)/~~,只能慢慢学习了

        //得到图片资源的拉伸后的宽度，并非真实宽度，因为不同手机屏幕密度不同，图片会被拉伸，我们得到的就是这个啦
        final int mDrawableWidth = mDrawable.getIntrinsicWidth();
        final int mDrawableHeight = mDrawable.getIntrinsicHeight();
        if (mDrawableWidth == 0 || mDrawableHeight == 0) return;

        //当前栈中save空间的数量
        int saveCount = canvas.getSaveCount();
        //在当前的bitmap中进行操作
        canvas.save();

        //伸缩平移
        canvas.translate(translateLeft, translateTop);
        canvas.scale(scale, scale);

        mDrawable.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    /**
     * 缩放手势检测
     */
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            final float mOldScaleWidth = mBoundWidth * scale;
            final float mOldScaleHeight = mBoundHeight * scale;

            if (mOldScaleWidth > getWidth() && getDiffX() != 0
                    || (mOldScaleHeight > getHeight() && getDiffY() != 0)) return false;

            float factor = detector.getScaleFactor();
            Log.d(TAG, "factor-->  " + factor);
            float value = scale;
            value += (factor - 1) * 2;
            if (value == scale) return true;
            if (value > mMaxScale) return false;
            if (value < mMinScale) return false;
            scale = value;

            final float mScaleWidth = mBoundWidth * scale;
            final float mScaleHeight = mBoundHeight * scale;

            //得到偏移量
//            translateLeft = getWidth() / 2.0f - (getWidth() / 2.0f - translateLeft) * mScaleWidth / mOldScaleWidth;
            translateLeft = getWidth() / 2.0f - (mScaleWidth / 2.0f);
//            translateTop = getHeight() / 2.0f -(getHeight() / 2.0f - translateTop) * mScaleHeight / mOldScaleHeight;
            translateTop = getHeight() / 2.0f - (mScaleHeight / 2.0f);

            final float diffX = getDiffX();
            final float diffY = getDiffY();

            //左边界限制
            if (diffX > 0 && mScaleWidth > getWidth()){
                translateLeft = 0;
            }

            //右边界限制
            if (diffX < 0 && mScaleWidth > getWidth()){
                translateLeft = getWidth() - mScaleWidth;
            }

            //上边界限制
            if (diffY > 0 && mScaleHeight > getHeight()){
                translateTop = 0;
            }

            //下边界限制
            if (diffY < 0 && mScaleHeight > getHeight()){
                translateTop = getHeight() - mScaleHeight;
            }

            invalidate();

            return true;
        }
    }

    /**
     * 手势监听，包含多种手势操作
     */
    private class FlatGestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //得到伸缩后的宽高
            final float mScaledWidth = mBoundWidth * scale;
            final float mScaledHeight = mBoundHeight * scale;

            boolean isReachBorder = false;
            if (mScaledWidth > getWidth()){
                translateLeft -= distanceX * 1.2;
                final float left = getExplicitTranslateLeft(translateLeft);
                if (left != translateLeft) isReachBorder = true;
                translateLeft = left;
            }else {
                isReachBorder = true;
            }

            if (mScaledHeight > getHeight()){
                translateTop -= distanceY * 1.2;
                translateTop = getExplicitTranslateTop(translateTop);
            }

            invalidate();
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return performClick();
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return super.onDoubleTap(e);
        }
    }

    /**
     * 得到真实的translateTop(限制最大最小)
     * @param translateTop
     * @return
     */
    private float getExplicitTranslateTop(float translateTop) {
        final float mScaledHeight = mBoundHeight * scale;
        if (translateTop > 0){
            translateTop = 0;
        }
        if (-translateTop + getHeight() > mScaledHeight){
            translateTop = getHeight() - mScaledHeight;
        }
        return translateTop;
    }

    /**
     * 得到真实的translateLeft(限制最大最小)
     * @param translateLeft
     * @return
     */
    private float getExplicitTranslateLeft(float translateLeft) {
        final float mScaledWidth = mBoundWidth * scale;
        if (translateLeft > 0){
            translateLeft = 0;
        }
        if (-translateLeft + getWidth() > mScaledWidth){
            translateLeft = getWidth() - mScaledWidth;
        }
        return translateLeft;
    }

    /**
     * 得到X轴上的空隙
     * @return 正数 左边有空隙； 负数 右边有空隙； 0 左右都没空隙
     */
    private float getDiffX(){
        //得到缩放后的宽度
        final float mScaledWidth = mBoundWidth * scale;

        return translateLeft >= 0
                ? translateLeft
                : getWidth() - translateLeft - mScaledWidth > 0
                ? -(getWidth() - translateLeft - mScaledWidth)
                : 0;
    }

    /**
     * //得到Y轴上的空隙
     * @return 正数 上边有空隙； 负数 下边有空隙； 0 上下都没空隙
     */
    private float getDiffY(){
        //得到缩放后的高度
        final float mScaledHeight = mBoundHeight * scale;

        return translateTop >- 0
                ? translateTop
                : getHeight() - translateTop - mScaledHeight > 0
                ? -(getHeight() - translateTop - mScaledHeight)
                : 0;
    }

    private ValueAnimator.AnimatorUpdateListener onScaleAnimationUpdate;
    private ValueAnimator.AnimatorUpdateListener onTranslateXAnimationUpdate;
    private ValueAnimator.AnimatorUpdateListener onTranslateYAnimationUpdate;

    private ValueAnimator resetScaleAnimator;
    private ValueAnimator resetTranslateXAnimator;
    private ValueAnimator resetTranslateYAnimator;

    private FloatEvaluator mFloatEvaluator = new FloatEvaluator();
    private AccelerateInterpolator mAccInterpolator = new AccelerateInterpolator();
    private DecelerateInterpolator mDecInterpolator = new DecelerateInterpolator();

    /**
     * 重置伸缩动画的监听器
     * @return
     */
    private ValueAnimator.AnimatorUpdateListener getOnScaleAnimationUpdate(){
        if (onScaleAnimationUpdate != null) return onScaleAnimationUpdate;
        onScaleAnimationUpdate = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scale = (float) animation.getAnimatedValue();
                invalidate();
            }
        };
        return onScaleAnimationUpdate;
    }

    /**
     * 重置水平动画的监听器
     * @return
     */
    private ValueAnimator.AnimatorUpdateListener getOnTranslateXAnimationUpdate(){
        if (onTranslateXAnimationUpdate != null) return onTranslateXAnimationUpdate;
        onTranslateXAnimationUpdate = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                translateLeft = (float) animation.getAnimatedValue();
                invalidate();
            }
        };
        return onTranslateXAnimationUpdate;
    }

    /**
     * 重置垂直动画的监听器
     * @return
     */
    private ValueAnimator.AnimatorUpdateListener getOnTranslateYAnimationUpdate(){
        if (onTranslateYAnimationUpdate != null) return onTranslateYAnimationUpdate;
        onTranslateYAnimationUpdate = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                translateTop = (float) animation.getAnimatedValue();
                invalidate();
            }
        };
        return onTranslateYAnimationUpdate;
    }

    /**
     * 重置伸缩
     * @return
     */
    private ValueAnimator getResetScaleAnimator(){
        if (resetScaleAnimator != null){
            resetScaleAnimator.removeAllUpdateListeners();
        }else {
            resetScaleAnimator = ValueAnimator.ofFloat();
        }
        resetScaleAnimator.setDuration(200);
        resetScaleAnimator.setInterpolator(mAccInterpolator);
        resetScaleAnimator.setEvaluator(mFloatEvaluator);
        return resetScaleAnimator;
    }

    /**
     * 重置水平方向的动画
     * @return
     */
    private ValueAnimator getResetXAnimator(){
        if (resetTranslateXAnimator != null){
            resetTranslateXAnimator.removeAllUpdateListeners();
        }else {
            resetTranslateXAnimator = ValueAnimator.ofFloat();
        }
        resetTranslateXAnimator.setDuration(200);
        resetTranslateXAnimator.setInterpolator(mAccInterpolator);
        resetTranslateXAnimator.setEvaluator(mFloatEvaluator);
        return resetTranslateXAnimator;
    }

    /**
     * 重置垂直方向的动画
     * @return
     */
    private ValueAnimator getResetYAnimator(){
        if (resetTranslateYAnimator != null){
            resetTranslateYAnimator.removeAllUpdateListeners();
        }else {
            resetTranslateYAnimator = ValueAnimator.ofFloat();
        }
        resetTranslateYAnimator.setDuration(200);
        resetTranslateYAnimator.setInterpolator(mAccInterpolator);
        resetTranslateYAnimator.setEvaluator(mFloatEvaluator);
        return resetTranslateYAnimator;
    }

    /**
     * 清理动画
     */
    private void cancleAnimation(){
        if (resetScaleAnimator != null && resetScaleAnimator.isRunning()){
            resetScaleAnimator.cancel();
        }
        if (resetTranslateXAnimator != null && resetTranslateXAnimator.isRunning()){
            resetTranslateXAnimator.cancel();
        }
        if (resetTranslateYAnimator != null && resetTranslateYAnimator.isRunning()){
            resetTranslateYAnimator.cancel();
        }
    }
}
