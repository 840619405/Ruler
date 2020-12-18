package com.xw.rulerdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class MyRulerView extends View {
    //刻度长度
    private int markMaxHeight, markMidHeight, markMinHeight;
    //刻度间隔
    private int markIntervalWidth;
    //刻度宽
    private int markWidth;
    //左边距
    private int paddingLeft;
    //View可使用宽度(扣除左右边距)
    private int width;
    //刻度尺实际占用宽度
    private int actualWidth;
    //刻度数量
    private int markCount;
    //指示器直径
    private int indicatorDiameter = 20;
    //刻度上边距
    private int markTopMargin;

    private Paint roundPaint;
    /**
     * 滑动计算参数
     */
    //按下时X坐标
    private float downX;
    //滑动的距离
    private float movedX;
    //指示器小球当前的偏移量,计算式千万不要用offsetStart+=movedX
    private float offsetStart = 0;
    //按下时指示器小球的偏移量,移动过程中offsetStart=offsetOriginal+movedX
    private float offsetOriginal = 0;
    public MyRulerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        markTopMargin = indicatorDiameter + 40;
        markWidth = 20;
        markIntervalWidth = 20;
        roundPaint = new Paint();
        roundPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        markCount = (width + markIntervalWidth) / (markWidth + markIntervalWidth);
        markMinHeight = 100;
        markMidHeight = (int) ((markMinHeight) * 1.6);
        markMaxHeight = (markMinHeight) * 2;
        actualWidth = (markCount * markWidth) + ((markCount - 1) * markIntervalWidth);
        paddingLeft = (width - actualWidth) / 2;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < markCount; i++) {
            int rectFLeft = paddingLeft + (markWidth * i) + (markIntervalWidth * i);
            int rectFRight = rectFLeft + markWidth;
            int rectFTop = markTopMargin;
            int rectFBottom = markMaxHeight;
            if (i % 10 == 0) {
                rectFBottom = markMaxHeight + rectFTop;
            } else if (i % 5 == 0) {
                rectFBottom = markMidHeight + rectFTop;
            } else {
                rectFBottom = markMinHeight + rectFTop;
            }
            RectF rectF = new RectF(rectFLeft, rectFTop, rectFRight, rectFBottom);
            canvas.drawRoundRect(rectF, indicatorDiameter * 2, indicatorDiameter * 2, roundPaint);
        }
        //int centerNum = markCount / 2;
        canvas.drawCircle(offsetStart + paddingLeft + (indicatorDiameter / 2), markTopMargin / 2, indicatorDiameter / 2, roundPaint);
    }


    @Override
    public void computeScroll() {
        super.computeScroll();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                offsetOriginal = offsetStart;
                downX = event.getX();
                movedX = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                movedX = event.getX() - downX;
                //左右边界计算
                if (movedX + offsetOriginal < 0) {
                    offsetStart = 0;
                } else if ((movedX + offsetOriginal) > (actualWidth - (indicatorDiameter / 2))) {
                    offsetStart = actualWidth - (indicatorDiameter);
                } else {
                    offsetStart = offsetOriginal + movedX;
                }
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                //计算指示器滑动在第几个刻度上
                int offsetEndNum = floatToInt(((offsetStart) / (markWidth + markIntervalWidth))) + 1;
                offsetStart = ((offsetEndNum - 1) * markWidth) + ((offsetEndNum - 1) * markIntervalWidth);
                postInvalidate();
                break;
        }
        return true;
    }


    /**
     * float转int并且小数点后一位四舍五入
     * @param f
     * @return
     */
    public int floatToInt(float f) {
        int i = 0;
        if (f > 0) //正数
        {
            i = (int) (f * 10 + 5) / 10;
        } else if (f < 0) //负数
        {
            i = (int) (f * 10 - 5) / 10;
        } else {
            i = 0;
        }
        return i;
    }
}
