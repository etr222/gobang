package com.example.gobang;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MyView extends View {

    private int mPanelWidth;
    private float mLineHeight;
    private int MAX_LINE=10;

    private Paint paint=new Paint();

    private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;

    private float ratioPieceOfLineHeight=3*1.0f/4;

    private boolean mIsWhite=true;

    private List<Point> mWhiteArray=new ArrayList<>();
    private List<Point> mBlackArray=new ArrayList<>();

    public MyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(0x44FF0000);
        init();
    }

    private void init() {
        paint.setColor(0x88000000);
//        设置抗锯齿。
        paint.setAntiAlias(true);
//
        paint.setDither(true);
//
        paint.setStyle(Paint.Style.STROKE);


        mWhitePiece= BitmapFactory.decodeResource(getResources(),R.drawable.stone_w2);
        mBlackPiece=BitmapFactory.decodeResource(getResources(),R.drawable.stone_b1);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSize=MeasureSpec.getSize(widthMeasureSpec);
        int widthMode=MeasureSpec.getMode(widthMeasureSpec);
        int heightSize=MeasureSpec.getSize(heightMeasureSpec);
        int heightMode=MeasureSpec.getMode(heightMeasureSpec);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width=Math.min(widthSize,heightSize);
        if (widthMode==MeasureSpec.UNSPECIFIED){
            width=heightSize;
        }else if (heightMode==MeasureSpec.UNSPECIFIED){
            width=widthSize;
        }
        setMeasuredDimension(width,width);

    }

//    onSizeChange方法在大小改变的时候调用。
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth=w;
        mLineHeight=mPanelWidth*1.0f/MAX_LINE;

        int pieceWidth= (int) (mLineHeight*ratioPieceOfLineHeight);

        mWhitePiece=Bitmap.createScaledBitmap(mWhitePiece,pieceWidth,pieceWidth,false);
        mBlackPiece=Bitmap.createScaledBitmap(mBlackPiece,pieceWidth,pieceWidth,false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);
    }

    private void drawBoard(Canvas canvas) {
        int w=mPanelWidth;
        float lineHeight=mLineHeight;
        for (int i=0;i<MAX_LINE;i++){
            int startX= (int) (mLineHeight/2);
            int endX= (int) (w-lineHeight/2);

            int y= (int) ((0.5+i)*lineHeight);

            canvas.drawLine(startX,y,endX,y,paint);

            canvas.drawLine(y,startX,y,endX,paint);

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action=event.getAction();
        if (action==MotionEvent.ACTION_UP)
        {
            int x= (int) event.getX();
            int y= (int) event.getY();

            Point p=getValidPoint(x,y);

            if (mWhiteArray.contains(p)||mBlackArray.contains(p)){
                return false;
            }

            if (mIsWhite)
            {
                mWhiteArray.add(p);
            }else
            {
                mBlackArray.add(p);
            }
            invalidate();
            mIsWhite=!mIsWhite;
        }


        return true;
    }

    private Point getValidPoint(int x, int y) {

        return new Point((int)(x/mLineHeight),(int)(y/mLineHeight));
    }
}
