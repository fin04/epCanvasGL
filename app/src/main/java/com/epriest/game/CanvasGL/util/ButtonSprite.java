package com.epriest.game.CanvasGL.util;

/**
 * Created by darka on 2016-10-31.
 */

public class ButtonSprite {
    public static final int TouchAction_NONE = 0;
    public static final int TouchAction_MOVE = 1;
    public static final int TouchAction_CLICK = 2;

    public static class Button{
        public int btnNum;
        public String btnName;
        public int touchAction;
        public boolean isEnable;
        public int btnX;
        public int btnY;
        public int btnW;
        public int btnH;
        public int btnAnimFrame;
        public int btnAnimFrameEnd;
    }
}
