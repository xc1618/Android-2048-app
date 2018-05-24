package com.xc161.Android_2048_app;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class my2048 extends RelativeLayout {
    private int column = 5; //列数为5*5
    private int margin = 10;
    private int pading;
    private GestureDetector gestureDetector; //用户滑动手势
    private int score;
    private Item_for_2048[] items;
    private boolean isMoveHappen = true;
    private boolean isMergeHappen = true;
    private boolean once;
    private play_music music_handle;

    public interface OnGameListener {
        void OnScoreChange(int score);

        void OnGameOver();
    }

    private OnGameListener onGameListener;

    public void setOnGameListener(OnGameListener gameListener) {
        this.onGameListener = gameListener;
    }

    //枚举类型表示触摸操作的分类
    private enum ACTION {
        LEFT, RIGHT, UP, DOWN
    }

    public my2048(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        music_handle = new play_music();
        initmusic();
        margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, margin, getResources().getDisplayMetrics());
        pading = min(getPaddingLeft(), getPaddingRight(), getPaddingTop(), getPaddingBottom());
        gestureDetector = new GestureDetector(context, new MyGestureDetector());
    }

    public my2048(Context context) {
        this(context, null);
    }

    public my2048(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    //根据用户触摸操作进行移动、合并
    private void deal_item(ACTION action) {
        for (int i = 0; i < column; i++) {  //按照当前的方向分行处理
            List<Item_for_2048> row = new ArrayList<Item_for_2048>();
            for (int j = 0; j < column; j++) {    //按照当前的方向重新排序后的一行写入一个数组
                int index = getIndexbyAction(action, i, j);
                Item_for_2048 item = items[index];
                if (item.getNum() != 0) {
                    row.add(item);
                }
            }
            for (int j = 0; j < column && j < row.size(); j++) {   //判断有没有发生移动
                int index = getIndexbyAction(action, i, j);
                Item_for_2048 item = items[index];
                if (item.getNum() != row.get(j).getNum()) {
                    isMoveHappen = true;
                }
            }
            mergeItem(row);
            for (int j = 0; j < column; j++) {   //将新的结果写会去
                int index = getIndexbyAction(action, i, j);
                if (row.size() > j) {
                    items[index].setNum(row.get(j).getNum());
                } else {
                    items[index].setNum(0);
                }
            }
        }
        generateNum();   //生成新的数
    }

    //合并同向相邻的相同值
    private void mergeItem(List<Item_for_2048> row) {
        if (row.size() < 2) {
            return;
        }
        for (int j = 0; j < row.size() - 1; j++) {
            Item_for_2048 item1 = row.get(j);
            Item_for_2048 item2 = row.get(j + 1);
            if (item1.getNum() == item2.getNum()) {
                isMergeHappen = true;
                int val = item1.getNum() + item2.getNum();
                item1.setNum(val);
                score += val;
                if (onGameListener != null) {
                    onGameListener.OnScoreChange(score);
                }
                for (int k = j + 1; k < row.size() - 1; k++) {  //合并后后面值的向前移
                    row.get(k).setNum(row.get(k + 1).getNum());
                }
                row.get(row.size() - 1).setNum(0);  //最后空出的值设置为0
            }
        }
    }

    //生成新的数字
    public void generateNum() {
        if (checkOver()) {
            if (onGameListener != null) {
                onGameListener.OnGameOver();
            }
            return;
        }
        if (!isFull()) {
            if (isMergeHappen || isMoveHappen) {
                if (!isMergeHappen) {
                    playmusic(true);
                } else {
                    playmusic(false);
                }
                Random random = new Random();
                int next = random.nextInt(25);
                Item_for_2048 item = items[next];
                while (item.getNum() != 0) {
                    next = random.nextInt(25);
                    item = items[next];
                }
                item.setNum(Math.random() > 0.75 ? 4 : 2);
                isMergeHappen = isMoveHappen = false;
            }
        }
    }

    //初始化音效

    public void initmusic() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = music_handle.obtainMessage(0x001, getContext());
                message.sendToTarget();
            }
        }).start();
    }

    //播放音效
    public void playmusic(final boolean which) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message;
                if (which) {
                    message = music_handle.obtainMessage(0x002, getContext());
                } else {
                    message = music_handle.obtainMessage(0x003, getContext());
                }
                message.sendToTarget();
            }
        }).start();
    }

    public void restart() {
        for (Item_for_2048 item : items) {
            item.setNum(0);
        }
        score = 0;
        if (onGameListener != null) {
            onGameListener.OnScoreChange(score);
        }
        isMoveHappen = isMergeHappen = true;
        generateNum();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    //判断还有没有剩余位置
    private boolean isFull() {
        for (int i = 0; i < items.length; i++) {
            if (items[i].getNum() == 0) {
                return false;
            }
        }
        return true;
    }

    //测量并绘制游戏区域
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int length = Math.min(getMeasuredHeight(), getMeasuredWidth());   //取短边作为正方形游戏区域的边长
        int childWidth = (length - pading * 2 - margin * (column - 1)) / column;
        if (!once) {
            if (items == null) {
                items = new Item_for_2048[column * column];   //初始化方块数组
            }
            for (int i = 0; i < items.length; i++) {
                Item_for_2048 item = new Item_for_2048(getContext());
                items[i] = item;
                item.setId(i + 1);
                RelativeLayout.LayoutParams lp = new LayoutParams(childWidth, childWidth);
                if ((i + 1) % column != 0) {    //不是最后一列设置右外边距
                    lp.rightMargin = margin;
                }
                if (i % column != 0) {   //不是第一列设置Rightof
                    lp.addRule(RelativeLayout.RIGHT_OF, items[i - 1].getId());
                }
                if ((i + 1) > column) {   //不是第一行设置Belowof
                    lp.topMargin = margin;
                    lp.addRule(RelativeLayout.BELOW, items[i - column].getId());
                }
                addView(item, lp);
            }
            generateNum();    //生成第一个数
        }
        once = true;
        setMeasuredDimension(length, length);
    }

    //检测游戏是否结束
    private boolean checkOver() {
        if (!isFull()) {
            return false;
        }
        for (int i = 0; i < column; i++) {  //遍历所有的位置是否存在相邻的相同值
            for (int j = 0; j < column; j++) {
                int index = i * column + j;
                Item_for_2048 item = items[index];
                if ((index + 1) % column != 0) {      //判断是否是最后一列
                    //Log.e("TAG", "RIGHT");
                    Item_for_2048 itemRight = items[index + 1];
                    if (item.getNum() == itemRight.getNum()) {
                        return false;
                    }
                }
                if ((index + column) < column * column) {      //判断是否是最后一行
                    //Log.e("TAG", "DOWN");
                    Item_for_2048 itemBottom = items[index + column];
                    if (item.getNum() == itemBottom.getNum()) {
                        return false;
                    }
                }
                if (index % column != 0) {      //判断是否是第一列
                    //Log.e("TAG", "LEFT");
                    Item_for_2048 itemLeft = items[index - 1];
                    if (item.getNum() == itemLeft.getNum()) {
                        return false;
                    }
                }
                if (index + 1 > column) {      //判断是否是第一行
                    //Log.e("TAG", "UP");
                    Item_for_2048 itemTop = items[index - column];
                    if (item.getNum() == itemTop.getNum()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //按照滑动方向返回对应方向下重新布局后的坐标
    private int getIndexbyAction(ACTION action, int i, int j) {
        int index = -1;
        switch (action) {
            case LEFT:
                index = i * column + j;
                break;
            case RIGHT:
                index = i * column + column - j - 1;
                break;
            case UP:
                index = i + j * column;
                break;
            case DOWN:
                index = i + (column - 1 - j) * column;
                break;
        }
        return index;
    }

    private int min(int... params) {
        int min = params[0];
        for (int param : params) {
            if (min > param) {
                min = param;
            }
        }
        return min;
    }

    //判断用户触摸操作的方向
    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {

        final int FLING_MIN_DISTANCE = 50;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            float x = e2.getX() - e1.getX();
            float y = e2.getY() - e1.getY();

            if (x > FLING_MIN_DISTANCE     //向右的位移打到阈值并且x轴的速度大于y轴
                    && Math.abs(velocityX) > Math.abs(velocityY)) {
                deal_item(ACTION.RIGHT);

            } else if (x < -FLING_MIN_DISTANCE
                    && Math.abs(velocityX) > Math.abs(velocityY)) {
                deal_item(ACTION.LEFT);

            } else if (y > FLING_MIN_DISTANCE
                    && Math.abs(velocityX) < Math.abs(velocityY)) {
                deal_item(ACTION.DOWN);

            } else if (y < -FLING_MIN_DISTANCE
                    && Math.abs(velocityX) < Math.abs(velocityY)) {
                deal_item(ACTION.UP);
            }
            return true;

        }

    }
}
