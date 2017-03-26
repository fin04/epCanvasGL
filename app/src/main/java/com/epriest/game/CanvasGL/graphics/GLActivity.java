package com.epriest.game.CanvasGL.graphics;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public abstract class GLActivity extends Activity {
	private int currentApiVersion;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		hideSystemBar();
		baseCreate();
	}
	
	void hideSystemBar(){
		currentApiVersion = android.os.Build.VERSION.SDK_INT;
		final int flags;

		if(currentApiVersion < Build.VERSION_CODES.KITKAT){
			return;
//			flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE 
//					| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN 
//					| View.SYSTEM_UI_FLAG_FULLSCREEN ;
		}else{
			flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		}
		
		// This work only for android 4.4+
//		if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {
			getWindow().getDecorView().setSystemUiVisibility(flags);
			// Code below is to handle presses of Volume up or Volume down.
			// Without this, after pressing volume buttons, the navigation bar
			// will
			// show up and won't hide
			final View decorView = getWindow().getDecorView();
			decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
				@Override
				public void onSystemUiVisibilityChange(int visibility) {
					if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
						decorView.setSystemUiVisibility(flags);
					}
				}
			});
//		}
		
//		getWindow().getDecorView().getSystemUiVisibility();
//		int newUiOptions = 0;
//		 
//		if (Build.VERSION.SDK_INT >= 14) {
//			newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
//		}
//		
//		if (Build.VERSION.SDK_INT >= 16) {
//			newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
//		}
//		
//		if (Build.VERSION.SDK_INT >= 19) {
//			newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//		}
//		getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
	}
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus) {
			getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE 
					| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN 
					| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		}
	}

	
	protected boolean hasGLES20() {
	    ActivityManager am = (ActivityManager)
	                getSystemService(Context.ACTIVITY_SERVICE);
	    ConfigurationInfo info = am.getDeviceConfigurationInfo();
	    return info.reqGlEsVersion >= 0x20000;
	}

	@Override
	protected void onResume() {
		super.onResume();
		baseResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		basePause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void onBackPressed() {
		basOnBackPressed();
	}
	
	public abstract void baseCreate();
	public abstract void baseResume();
	public abstract void basePause();
	public abstract void basOnBackPressed();
}
