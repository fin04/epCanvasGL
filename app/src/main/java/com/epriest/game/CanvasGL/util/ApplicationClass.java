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

    private final int BG_TEXTURE_WIDTH = 1024;
    private final int BG_TEXTURE_HEIGHT = 1024;

    public final int TextureClipW = 240;
    public final int TextureClipH = 256;
    public final int totalVertics = 4;

    private final int GAMECANVAS_WIDTH = 720;
    private final int GAMECANVAS_HEIGHT = 1280;

    public float mGameScaleValueWidth;
    public float mGameScaleValueHeight;
    public float mGameScaleValue;

    public int mGameOrientation = 0;
    public static final int GAMECANVAS_ORIENTATION_PORTRAIT = 0;
    public static final int GAMECANVAS_ORIENTATION_LANDSCAPE = 1;

    private int mTextureWidth;
    private int mTextureHeight;

    private int mTextureClipWidth;
    private int mTextureClipHeight;

    private int mDeviceScreenWidth;
    private int mDeviceScreenHeight;

    private int mGameCanvasWidth;
    private int mGameCanvasHeight;

    public int stateMode;
    public int gameState;
    public boolean isSceneInit;
    public boolean isGameInit;
    public Bitmap loadingBg;

    public int mAdBannerHeight;
    public String newName = null;

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

    public void setGameCanvasOrientation(int orientation){
        this.mGameOrientation = orientation;
        if(orientation == GAMECANVAS_ORIENTATION_PORTRAIT){
            this.mGameCanvasWidth = GAMECANVAS_WIDTH;
            this.mGameCanvasHeight = GAMECANVAS_HEIGHT;
            this.mTextureWidth = BG_TEXTURE_WIDTH;
            this.mTextureHeight = BG_TEXTURE_HEIGHT;
            this.mTextureClipWidth = TextureClipW;
            this.mTextureClipHeight = TextureClipH;
        }else
        if(orientation == GAMECANVAS_ORIENTATION_LANDSCAPE){
            this.mGameCanvasWidth = GAMECANVAS_HEIGHT;
            this.mGameCanvasHeight = GAMECANVAS_WIDTH;
            this.mTextureWidth = BG_TEXTURE_HEIGHT;
            this.mTextureHeight = BG_TEXTURE_WIDTH;
            this.mTextureClipWidth = TextureClipH;
            this.mTextureClipHeight = TextureClipW;
        }
    }

    public void setDeviceScreenWidth(int lcdW){
        this.mDeviceScreenWidth = lcdW;
    }

    public void setDeviceScreenHeight(int lcdH){
        this.mDeviceScreenHeight = lcdH;
    }

    public int getDeviceScreenWidth(){
        return mDeviceScreenWidth;
    }

    public int getDeviceScreenHeight(){
        return mDeviceScreenHeight;
    }

    public void setGameCanvasWidth(int width) {
        this.mGameCanvasWidth = width;
    }

    public void setGameCanvasHeight(int height) {
        this.mGameCanvasHeight = height;
    }

    public int getGameCanvasWidth(){
        return mGameCanvasWidth;
    }

    public int getGameCanvasHeight(){
        return mGameCanvasHeight;
    }

    public int getTextureWidth() {
        return mTextureWidth;
    }

    public int getTextureHeight() {
        return mTextureHeight;
    }

    public int getTextureClipWidth(){
        return mTextureClipWidth;
    }

    public int getTextureClipHeight() {
        return mTextureClipHeight;
    }
}
