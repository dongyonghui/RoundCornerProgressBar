package com.dyh.android.tileimage;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Arrays;

/**
 * 贴图进度条
 * 根据一张小图片，平铺拼接
 * 支持圆角设置
 */
public class TileImageProgressBar extends View {

    private final RectF mBounds = new RectF();
    private final RectF mBitmapRect = new RectF();
    private int mBitmapWidth;
    private int mBitmapHeight;
    /**
     * 圆角大小
     */
    private float mCornerRadius = 0;


    private final Paint mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mBgBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private BitmapShader mBitmapShader;
    private BitmapShader mBgBitmapShader;

    private float[] mRadii = new float[]{0, 0, 0, 0, 0, 0, 0, 0};


    private final Path mPath = new Path();
    private boolean mBoundsConfigured = false;
    private int progress = 20;
    private int maxProgress = 100;

    private int mBgImageResId;

    public TileImageProgressBar(Context context) {
        this(context, null);
    }

    public TileImageProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TileImageProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mBitmapPaint.setStyle(Paint.Style.FILL);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.tipb_TileImageProgressBar, defStyle, 0);
        mCornerRadius = a.getDimensionPixelSize(
                R.styleable.tipb_TileImageProgressBar_tipbCornerRadius, 0);
        int progressImage = a.getResourceId(R.styleable.tipb_TileImageProgressBar_tipbProgressImage,
                R.drawable.tipb_progress_drawable_red);
        int bgImage = a.getResourceId(R.styleable.tipb_TileImageProgressBar_tipbBackgroundImage,
                R.drawable.tipb_bg_progress_drawable_red);
        a.recycle();

        Bitmap bm = drawableToBitmap(resolveResource(bgImage));
        if (bm != null) {
            mBgBitmapShader = new BitmapShader(bm, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            mBgBitmapPaint.setShader(mBgBitmapShader);
        }
        //设置图片
        setImageResource(progressImage);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int _WidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int _WidthSpec = MeasureSpec.getSize(widthMeasureSpec);
        int _HeightMode = MeasureSpec.getMode(heightMeasureSpec);
        int _HeightSpec = MeasureSpec.getSize(heightMeasureSpec);
        int _Width;
        int _Height;
        //宽度
        if (_WidthMode == MeasureSpec.EXACTLY) {
            _Width = _WidthSpec;
        } else {
            _Width = widthMeasureSpec;
        }
        //高度
        if (_HeightMode == MeasureSpec.EXACTLY) {
            _Height = _HeightSpec;
        } else {
            _Height = mBitmapHeight;
        }

        setMeasuredDimension(_Width, _Height);

        setMeasuredDimension(_Width,
                Math.max(_Height, mBitmapHeight));
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.save();
        if (!mBoundsConfigured) {
            Rect clipBounds = canvas.getClipBounds();
            mBounds.set(clipBounds);
            mBoundsConfigured = true;
        }

        //圆角大小最大为正圆
        float height = getHeight();
        if (height > 0) {
            mCornerRadius = Math.min(height / 2, mCornerRadius);
        }

        //绘制背景
        mPath.reset();
        mBounds.right = getWidth();
        mPath.addRoundRect(mBounds, mRadii, Path.Direction.CW);
        canvas.drawPath(mPath, mBgBitmapPaint);

        float width = progress * 1f * getWidth() / maxProgress;

        //如果进度大于直径，则直径绘制
        if (width > mCornerRadius * 2) {
            mBounds.right = width;
        } else {
            //如果直径小于圆角直径，则绘制长度等于直径，并裁剪进度的长度
            mBounds.right = mCornerRadius * 2;
            canvas.clipRect(0, 0, width, getHeight());
        }
        Arrays.fill(mRadii, mCornerRadius);
        mPath.reset();
        mPath.addRoundRect(mBounds, mRadii, Path.Direction.CW);
        canvas.drawPath(mPath, mBitmapPaint);
        canvas.restore();
    }

    /**
     * 设置圆角大小，默认为0
     *
     * @param mCornerRadius
     */
    public void setCornerRadius(float mCornerRadius) {
        this.mCornerRadius = mCornerRadius;
        invalidate();
    }

    /**
     * 设置进度大小
     *
     * @param progress
     */
    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    /**
     * 设置进度条最大值，默认100
     *
     * @param maxProgress
     */
    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
        invalidate();
    }

    /**
     * 设置进度条图片
     *
     * @param drawable
     */
    public void setImageDrawable(Drawable drawable) {
        setImageBitmap(drawableToBitmap(drawable));
    }

    /**
     * 设置进度条图片
     *
     * @param bm
     */
    public void setImageBitmap(Bitmap bm) {
        fromBitmap(bm, getResources());
        invalidate();
    }


    /**
     * 设置进度条图片
     *
     * @param resId
     */
    public void setImageResource(int resId) {
        Bitmap bm = drawableToBitmap(resolveResource(resId));
        setImageBitmap(bm);
    }


    /**
     * 设置背景图片
     *
     * @param resId
     */
    public void setBackgroundImageResource(int resId) {
        Bitmap bm = drawableToBitmap(resolveResource(resId));
        setBackgroundImageBitmap(bm);
    }

    /**
     * 设置背景图片
     *
     * @param bm
     */
    public void setBackgroundImageBitmap(Bitmap bm) {
        if (bm != null) {
            mBgBitmapShader = new BitmapShader(bm, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            mBgBitmapPaint.setShader(mBgBitmapShader);
        }
        invalidate();
    }

    /**
     * 设置背景图片
     *
     * @param drawable
     */
    public void setBackgroundImageDrawable(Drawable drawable) {
        setImageBitmap(drawableToBitmap(drawable));
    }

    private Drawable resolveResource(int resId) {
        Resources rsrc = getResources();
        if (rsrc == null) {
            return null;
        }

        Drawable d = null;

        if (resId != 0) {
            try {
                d = rsrc.getDrawable(resId);
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        }
        return d;
    }

    private void fromBitmap(Bitmap bitmap, Resources r) {
        if (bitmap != null) {
            mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mBitmapPaint.setShader(mBitmapShader);

            mBitmapWidth = bitmap.getScaledWidth(r.getDisplayMetrics());
            mBitmapHeight = bitmap.getScaledHeight(r.getDisplayMetrics());

            mBitmapRect.set(0, 0, mBitmapWidth, mBitmapHeight);

            requestLayout();
        }
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap;
        int width = Math.max(drawable.getIntrinsicWidth(), 2);
        int height = Math.max(drawable.getIntrinsicHeight(), 2);
        try {
            bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            bitmap = null;
        }
        return bitmap;
    }
}