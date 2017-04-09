package com.epriest.game.CanvasGL.graphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;

public class CanvasUtil {

    public CanvasUtil() {
    }

    static public boolean recycleBitmap(Bitmap bitmap) {
        try {
            bitmap.recycle();
            bitmap = null;
        } catch (Exception e) {
            return false;
        }
        return true;

    }

	/*static public void drawString(Canvas mCanvas, String text, int size, Paint paint, int color, Align align,
			int picX, int picY){
		paint.setColor(color);
		paint.setTextSize(size);
		paint.setTextAlign(align);
		mCanvas.drawText(text, picX, picY, paint);
	}*/

    static public void drawString(Canvas mCanvas, String text, int size, Paint paint, int color, Align align,
                                  int picX, int picY) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        paint.setTextSize(size);
        paint.setTextAlign(align);
        mCanvas.drawText(text, picX, picY + size, paint);
    }

    static public void drawString(Canvas mCanvas, String text, Paint paint, int picX, int picY) {
        String[] strArr = text.split("\n");
        float textSize = paint.getTextSize();
        for (int i = 0; i < strArr.length; i++) {
            mCanvas.drawText(strArr[i], picX, (i + 1) * textSize + (textSize / 4 * i) + picY, paint);
        }
    }

    static public void drawBox(Canvas mCanvas, Paint paint, int color, boolean isFill, int left, int top, int boxW, int boxH) {
        paint.setColor(color);
        if (isFill)
            paint.setStyle(Paint.Style.FILL);
        else
            paint.setStyle(Paint.Style.STROKE);
        mCanvas.drawRect(left, top, boxW + left, boxH + top, paint);

    }

    static public void drawBitmap(Bitmap bitmap, Canvas mCanvas, Paint mPaint,
                                  int picX, int picY) {
        mCanvas.drawBitmap(bitmap, picX, picY, mPaint);
    }

    static public void drawClip(Bitmap bitmap, Canvas mCanvas, Paint mPaint,
                                int clipX, int clipY, int clipW, int clipH, int drawX, int drawY) {
        Rect src = new Rect(clipX, clipY, clipX + clipW, clipY + clipH);
        Rect dst = new Rect(drawX, drawY, drawX + clipW, drawY + clipH);
        mCanvas.drawBitmap(bitmap, src, dst, mPaint);
    }

    static public void drawClipResize(Bitmap bitmap, Canvas mCanvas, Paint mPaint,
                                      int clipX, int clipY, int clipW, int clipH, int picX, int picY, int picW, int picH) {
        Rect src = new Rect(clipX, clipY, clipX + clipW, clipY + clipH);
        Rect dst = new Rect(picX, picY, picX + picW, picY + picH);
        mCanvas.drawBitmap(bitmap, src, dst, mPaint);
    }

    static public Bitmap rotateBitmap(Bitmap bitmap, int angle) {
        Matrix matrix = new Matrix();
        matrix.setRotate(angle,
                (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);

        return Bitmap.createBitmap(bitmap,
                0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
