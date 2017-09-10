package com.epriest.game.CanvasGL.util;

import android.view.MotionEvent;

/**
 * Created by darka on 2016-10-31.
 */

public interface TouchData {
    public static final int TOUCH_MASK = MotionEvent.ACTION_MASK;//255;
    public static final int TOUCH_DOWN = MotionEvent.ACTION_DOWN;//0;
    public static final int TOUCH_UP = MotionEvent.ACTION_UP;//1;
    public static final int TOUCH_MOVE = MotionEvent.ACTION_MOVE;//2;
    public static final int TOUCH_CANCEL = MotionEvent.ACTION_CANCEL;//3;

//	public static final int TOUCH_CANCEL = 21;
//	public static final int TOUCH_CANCEL = 22;

    public static class Touch{
        public MotionEvent event;
        public float mDownX;
        public float mDownY;
        public float mPosX;
        public float mPosY;
//        public float mTouchX;
//        public float mTouchY;
        public float mLastTouchX;
        public float mLastTouchY;
        public int action;
        public int mActivePointerId = MotionEvent.INVALID_POINTER_ID;
    }

    public static class Axis{
        public float lcdW;
        public float lcdH;
        public float lcdValW;
        public float lcdValH;
    }
}
