package com.epriest.game.CanvasGL;

import android.os.Bundle;

import com.epriest.game.CanvasGL.R;
import com.epriest.game.CanvasGL.graphics.GLActivity;

public class MainActivity extends GLActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void baseCreate() {				
		setContentView(R.layout.surface_main);	
	}

	@Override
	public void baseResume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void basePause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void basOnBackPressed() {

	}

}
