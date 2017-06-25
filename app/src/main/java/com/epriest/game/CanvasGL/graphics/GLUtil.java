package com.epriest.game.CanvasGL.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.opengl.ETC1Util;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

public class GLUtil {
	
	static public Bitmap loadDrawableBitmap(Context context, String bitName){
		// Retrieve our image from resources.
        int id = context.getResources().getIdentifier("drawable/"+bitName, null, context.getPackageName());         
        // Temporary create a bitmap
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id);
        return bitmap;
	}

	static public Bitmap loadAssetsBitmap(Context context, String path, Bitmap.Config format) {
		Bitmap bitmap = null;
		try{
			InputStream is = context.getAssets().open(path);
			BitmapFactory.Options opt = new BitmapFactory.Options();
			if(format == null)
				opt.inPreferredConfig = Config.RGB_565;
			else
				opt.inPreferredConfig = format;
			bitmap = BitmapFactory.decodeStream(is, null, opt);
			if(is != null)
				try{
					is.close();
				}catch(IOException e){
					Log.d("","IOException"+e);
				}
		}catch(IOException e){
			Log.d("", "IOException"+e);
		}
		return bitmap;
	}
	
	static public int loadTexture(Bitmap bitmap, int[] texturenames, int num){
	    if (texturenames[num] != 0){
	        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + num);
	        // Bind to the texture in OpenGL
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[num]);
	        // Set filtering
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR );
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR );
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE );
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE );

	        // Load the bitmap into the bound texture.
	        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
	        
	        // Recycle the bitmap, since its data has been loaded into OpenGL.
//	        bitmap.recycle();
	    }else if (texturenames[num] == 0){
	        throw new RuntimeException("Error loading texture.");
	    }	 
	    return texturenames[num];
	}
	
}
