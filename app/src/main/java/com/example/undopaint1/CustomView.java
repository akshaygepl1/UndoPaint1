package com.example.undopaint1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.ArrayList;

public class CustomView extends android.support.v7.widget.AppCompatImageView {

    private static final String LOG_CAT = CustomView.class.getSimpleName();

    //drawing path
    private Path drawPath;

    //defines what to draw
    private Paint canvasPaint;

    //defines how to draw
    private Paint drawPaint;

    //initial color
    private int paintColor = 0xFF660000;

    private Paint _paintBlur;


    //canvas - holding pen, holds your drawings
    //and transfers them to the view
    private Canvas drawCanvas;

    //canvas bitmap
    private Bitmap canvasBitmap;

    //brush size
    private float currentBrushSize, lastBrushSize;

    private ArrayList<Path> paths = new ArrayList<Path>();
    private ArrayList<Path> undonePaths = new ArrayList<Path>();
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void init(){
        currentBrushSize = 20;
        lastBrushSize = currentBrushSize;

        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(currentBrushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);

        this._paintBlur = new Paint();
        this._paintBlur.set(drawPaint);
        this._paintBlur.setAntiAlias(true);
        this._paintBlur.setDither(true);
        this._paintBlur.setStyle(Paint.Style.STROKE);
        this._paintBlur.setStrokeJoin(Paint.Join.ROUND);
        this._paintBlur.setStrokeCap(Paint.Cap.ROUND);
        this._paintBlur.setColor(Color.RED);
        this._paintBlur.setStrokeWidth(6);
        this._paintBlur.setMaskFilter(new BlurMaskFilter(10.0F, BlurMaskFilter.Blur.OUTER));


    }

    public void setBackgroundResource(Uri drawable){

        setImageURI(drawable);
       // setBackgroundResource(drawable);

    }

  /*  public void setBackground(View view, Drawable background) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(background);
        } else {
            view.setBackgroundDrawable(background);
        }
    }*/

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
       // setBackgroundResource(R.drawable.one);
    }

    @Override
    protected void onDraw(Canvas canvas) {


        for (Path p : paths) {
            canvas.drawPath(p, drawPaint);

        }


        canvas.drawPath(drawPath, drawPaint);
       // canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.one), 0, 0, canvasPaint);
       // canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
       //create canvas of certain device size.
        super.onSizeChanged(w, h, oldw, oldh);


        //create Bitmap of certain w,h
       canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        //apply bitmap to graphic to start drawing.
        drawCanvas = new Canvas(canvasBitmap);



    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                touch_start(touchX, touchY);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(touchX, touchY);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
            default:
                return false;
        }
        return true;
    }


    private void touch_start(float x, float y) {
        undonePaths.clear();
        drawPath.reset();
        drawPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_up() {
        drawPath.lineTo(mX, mY);
        drawCanvas.drawPath(drawPath, drawPaint);
        paths.add(drawPath);
        drawPath = new Path();

    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            drawPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
    }


    public void onClickUndo () {
       if (paths.size()>0)
        {
            undonePaths.add(paths.remove(paths.size()-1));
            invalidate();
        }

    }


}





