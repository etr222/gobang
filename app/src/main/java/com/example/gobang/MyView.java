package com.example.gobang;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MyView extends View {

    private int mPanelWidth;
    private float mLineHeight;
    private int MAX_LINE = 10;
    private int MAX_COUNT_IN_LINE = 5;

    private Paint paint = new Paint();

    private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;

    private float ratioPieceOfLineHeight = 3 * 1.0f / 4;

    private boolean mIsWhite = true;

    private ArrayList<Point> mWhiteArray = new ArrayList<>();
    private ArrayList<Point> mBlackArray = new ArrayList<>();


    private boolean mIsGameOver;
    private boolean mIsWhiteWinner;

    
    private Point whitePoint;
    private Point blackPoint;
    private boolean mIsBackPoint=false;


    public MyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
//        setBackgroundColor(0x44FF0000);
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


        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_w2);
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_b1);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = Math.min(widthSize, heightSize);
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }
        setMeasuredDimension(width, width);

    }

    //    onSizeChange方法在大小改变的时候调用。
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth = w;
        mLineHeight = mPanelWidth * 1.0f / MAX_LINE;

        int pieceWidth = (int) (mLineHeight * ratioPieceOfLineHeight);

        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceWidth, pieceWidth, false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceWidth, pieceWidth, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);

        drawPieces(canvas);

        checkGameOver();
    }

    private void checkGameOver() {
        boolean whiteWin = checkFiveInLine(mWhiteArray);
        boolean blackWin = checkFiveInLine(mBlackArray);

        if (whiteWin || blackWin) {
            mIsGameOver = true;
            mIsWhiteWinner = whiteWin;

            String text = mIsWhiteWinner ? "白棋胜利" : "黑棋胜利";

//            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
            AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
            dialog.setTitle("恭喜"+text);
            dialog.setMessage("是否再来一局");
            dialog.setPositiveButton("不", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            dialog.setNegativeButton("再来一局", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    reStart();
                }
            });
            dialog.show();
        }
    }

    private boolean checkFiveInLine(List<Point> points) {

        for (Point p : points) {
            int x = p.x;
            int y = p.y;

            boolean win = checkHorizontal(x, y, points);
            if (win) return true;
            win = checkVertical(x, y, points);
            if (win) return true;
            win = checkLeftDiagonal(x, y, points);
            if (win) return true;
            win = checkRightDiagonal(x, y, points);
            if (win) return true;
        }
        return false;
    }

    /**
     * 判断先x，y位置的棋子，是否横向有相邻的五个一致。
     *
     * @param x
     * @param y
     * @param points
     * @return
     */
    private boolean checkHorizontal(int x, int y, List<Point> points) {

        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x - i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;
        return false;
    }

    private boolean checkVertical(int x, int y, List<Point> points) {

        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;
        return false;
    }

    private boolean checkLeftDiagonal(int x, int y, List<Point> points) {

        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x - i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;
        return false;
    }

    private boolean checkRightDiagonal(int x, int y, List<Point> points) {

        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x - i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;
        return false;
    }

    private void drawPieces(Canvas canvas) {
        for (int i = 0, n = mWhiteArray.size(); i < n; i++) {
            whitePoint = mWhiteArray.get(i);
            float x = (whitePoint.x + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight;
            float y = (whitePoint.y + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight;
            canvas.drawBitmap(mWhitePiece, x, y, null);

        }
        for (int i = 0, n = mBlackArray.size(); i < n; i++) {
            blackPoint = mBlackArray.get(i);
            float x = (blackPoint.x + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight;
            float y = (blackPoint.y + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight;
            canvas.drawBitmap(mBlackPiece, x, y, null);
        }

    }

    private void drawBoard(Canvas canvas) {
        int w = mPanelWidth;
        float lineHeight = mLineHeight;
        for (int i = 0; i < MAX_LINE; i++) {
            int startX = (int) (mLineHeight / 2);
            int endX = (int) (w - lineHeight / 2);

            int y = (int) ((0.5 + i) * lineHeight);

            canvas.drawLine(startX, y, endX, y, paint);

            canvas.drawLine(y, startX, y, endX, paint);

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mIsGameOver) return false;
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            Point p = getValidPoint(x, y);
            Log.e("Point", p.toString());

            if (mWhiteArray.contains(p) || mBlackArray.contains(p)) {
                return false;
            }

            if (mIsWhite) {
                mWhiteArray.add(p);
            } else {
                mBlackArray.add(p);
            }

            invalidate();

            mIsWhite = !mIsWhite;

        }


        return true;
    }

    private Point getValidPoint(int x, int y) {

        return new Point((int) (x / mLineHeight), (int) (y / mLineHeight));
    }

    public void reStart(){
        mWhiteArray.clear();
        mBlackArray.clear();
        mIsGameOver=false;
        mIsWhiteWinner=false;
        mIsWhite=false;
        invalidate();
    }
    public void backStep(){
        if (mIsWhite&&mBlackArray.size()>0){
            mBlackArray.remove(blackPoint);
            mIsWhite=!mIsWhite;//单机模式悔棋。
            mIsBackPoint=!mIsBackPoint;
        }else {
            if (mWhiteArray.size()>0){
                mWhiteArray.remove(whitePoint);
                mIsWhite=!mIsWhite;//单机模式悔棋。
                mIsBackPoint=!mIsBackPoint;
            }
        }
        invalidate();
    }

//    自定义View的存储数据的两个方法onSaveInstanceState()，onRestoreInstanceState(Parcelable state)。
//    利用Bundle对象。在onSaveInstanceState()方法存储数据。键值对存储。
//    在onRestoreInstanceState(Parcelable state)方法中取出数据。
    private static final String INSTANCE="instance";
    private static final String INSTANCE_GAME_OVER="instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY="instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY="instance_black_array";
    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle=new Bundle();
        bundle.putParcelable(INSTANCE,super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER,mIsGameOver);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY,mWhiteArray);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY,mBlackArray);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        if (state instanceof  Bundle){
            Bundle bundle= (Bundle) state;
            mIsGameOver=bundle.getBoolean(INSTANCE_GAME_OVER);
            mWhiteArray=bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            mBlackArray=bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return ;
        }

        super.onRestoreInstanceState(state);
    }
}
