package com.ljd.book.anim;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 版权：XXX公司 版权所有
 * 作者：lijinduo
 * 版本：2.0
 * 创建日期：2018/8/8
 * 描述：(重构)
 * 修订历史：
 * 参考链接：
 */
public class NumberRollingMoveView extends View {
    private Paint paint;
    private String TAG = "自绘view";
    private boolean run;
    private float showHeight;
    private float showWidth;
    int helfHeight;
    private Context context;
    List<String> list = new ArrayList<>();
    private int count;
    private int strHeight;
    private int strWidth;

    public NumberRollingMoveView(Context context) {
        this(context, null);
    }

    public NumberRollingMoveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(dp2px(getContext(), 50));
        Rect rect = new Rect();
        paint.getTextBounds("8", 0, 1, rect);
        helfHeight = rect.height() / 2;
        strHeight=rect.height();
        showHeight =  strHeight / 2 + helfHeight;
        showWidth = rect.width();

    }


    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(".")) {
                canvas.drawText(list.get(i), showWidth * i,  strHeight/ 2 + helfHeight, paint);
            } else {
                if (Integer.parseInt(list.get(i)) > count) {
                    canvas.drawText(count + "", showWidth * i, showHeight, paint);
                } else {
                    canvas.drawText(list.get(i), showWidth * i,  strHeight / 2 + helfHeight, paint);
                }

            }

        }
        super.onDraw(canvas);
    }

    public void startRolling(String money) {
        Rect rect = new Rect();
        paint.getTextBounds(money, 0, money.length()-1, rect);
        strHeight = rect.height();
        strWidth = rect.width();
        measure(0,0);
        list.clear();
        count = 0;
        for (int i = 0; i < money.length(); i++) {
            list.add(money.substring(i, i + 1));
        }
        run = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (run) {
                    if (count == 9) {
                        run = false;
                        showHeight =  strHeight / 2 + helfHeight;
                        postInvalidate();
                        return;
                    }
                    showHeight = showHeight - 6;
                    if (showHeight < 0) {
                        count++;
                        showHeight =  strHeight / 2 + helfHeight;
                    }
                    postInvalidate();
                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        }).start();


    }


    private int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureSpecHandler(widthMeasureSpec, strWidth), measureSpecHandler(heightMeasureSpec, strHeight));
    }

    private int measureSpecHandler(int measureSpec, int defaultSize) {
        int result = defaultSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            strHeight=getMeasuredHeight();
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(result, specSize);
        } else {
            result = defaultSize;
        }
        return result;
    }
}
