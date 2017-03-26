package com.epriest.game.CanvasGL.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by darka on 2016-10-31.
 */

public abstract class Scene {
    public abstract void initScene(ApplicationClass appClass);

    public abstract void recycleScene();

    public abstract void drawLoading(Canvas mCanvas, Bitmap loading);

    public abstract void draw(Canvas mCanvas);
}
