package com.epriest.game.CanvasGL.util;

import android.app.Application;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.view.MotionEvent;

/**
 * Created by darka on 2016-10-31.
 */

public class ApplicationClass  extends Application {
    public TouchData.Touch touch;
    public TouchData.Axis axis;
    public MotionEvent mEvent;

    public static final int GAMECANVAS_WIDTH = 512;
    public static final int GAMECANVAS_HEIGHT = 910;

//    public static final int GAMECANVAS_WIDTH = 720;
//    public static final int GAMECANVAS_HEIGHT = 1280;

    public int mGameOrientation = 0;
    public static final int GAMECANVAS_ORIENTATION_PORTRAIT = 0;
    public static final int GAMECANVAS_ORIENTATION_LANDSCAPE = 1;


    private int mScreenWidth;
    private int mScreenHeight;

    public float mGameScreenWidthVal;
    public float mGameScreenHeightVal;

    public float mGameScreenVal;
    public int mScreenOverWidth;
    public int mScreenOverHeight;
    public int mGameCanvasWidth;
    public int mGameCanvasHeight;

    public int gameFlag;
    public boolean isSceneInit;
    public boolean isGameInit;
    public Bitmap loadingBg;

    @Override
    public void onCreate() {
        super.onCreate();
        onSetClass();
    }

    public void onSetClass(){
//		game = new Game(this);
        axis = new TouchData.Axis();
        touch = new TouchData.Touch();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }

    public void setGamecanvasOrientation(int orientation){
        this.mGameOrientation = orientation;
        if(mGameOrientation == GAMECANVAS_ORIENTATION_PORTRAIT){
            setGameCanvasWidth(GAMECANVAS_WIDTH);
            setGameCanvasHeight(GAMECANVAS_HEIGHT);
        }else
        if(mGameOrientation == GAMECANVAS_ORIENTATION_LANDSCAPE){
            setGameCanvasWidth(GAMECANVAS_HEIGHT);
            setGameCanvasHeight(GAMECANVAS_WIDTH);
        }
    }

    public void setScreenWidth(int lcdW){
        this.mScreenWidth = lcdW;
    }

    public int getScreenWidth(){
        return mScreenWidth;
    }

    public void setScreenHeight(int lcdH){
        this.mScreenHeight = lcdH;
    }

    public int getScreenHeight(){
        return mScreenHeight;
    }

    public void setGameCanvasWidth(int canvasW){
        this.mGameCanvasWidth = canvasW;
    }

    public void setGameCanvasHeight(int canvasH){
        this.mGameCanvasHeight = canvasH;
    }

    public int getGameCanvasWidth(){
        return mGameCanvasWidth;
    }

    public int getGameCanvasHeight(){
        return mGameCanvasHeight;
    }
}
