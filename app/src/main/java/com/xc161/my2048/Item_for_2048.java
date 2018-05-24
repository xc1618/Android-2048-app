package com.xc161.my2048;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class Item_for_2048 extends View {

    private int center_num;
    private String num_str;
    private Paint paint;
    private Rect bound;

    public Item_for_2048(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint = new Paint();
    }

    public Item_for_2048(Context context) {
        this(context, null);
    }

    public Item_for_2048(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void setNum(int num) {
        center_num = num;
        num_str = num + "";
        paint.setTextSize(60.0f);      //设置字体大小
        bound = new Rect();
        paint.getTextBounds(num_str, 0, num_str.length(), bound);       //设置当前数字所需的绘制区域
        invalidate();      //刷新界面
    }

    public int getNum() {
        return center_num;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String color;
        switch (center_num) {
            case 0:
                color = "#CCC0B3";
                break;
            case 2:
                color = "#EEE4DA";
                break;
            case 4:
                color = "#EDE0C8";
                break;
            case 8:
                color = "#F2B179";
                break;
            case 16:
                color = "#F49563";
                break;
            case 32:
                color = "#F5794D";
                break;
            case 64:
                color = "#F55D37";
                break;
            case 128:
                color = "#EEE863";
                break;
            case 256:
                color = "#EDB04D";
                break;
            case 512:
                color = "#EC504D";
                break;
            case 1024:
                color = "#EB9437";
                break;
            case 2048:
                color = "#EA7821";
                break;
            default:
                color = "EA7821";
        }
        paint.setColor(Color.parseColor(color));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        if (center_num != 0) {
            drawText(canvas);
        }
    }

    private void drawText(Canvas canvas) {
        paint.setColor(Color.BLACK);
        float x = (getWidth() - bound.width()) / 2;
        float y = getHeight() / 2 + bound.height() / 2;
        canvas.drawText(num_str, x, y, paint);
    }
}
