package com.epriest.game.CanvasGL.util;

import android.view.MotionEvent;

/**
 * Created by darka on 2016-10-31.
 */

public abstract class Game {
    public void Start(){
        gStart();
    }

    public void Stop(){
        gStop();
    }

    public void gameState() {
        Update();
    }

    void Update (){
        gUpdate();
    }

    public abstract void gStart();
    public abstract void gStop();
    public abstract void gUpdate();
    public abstract void gOnTouchEvent(MotionEvent event);
}
