package com.epriest.game.CanvasGL.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.os.Build;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.epriest.game.CanvasGL.util.ApplicationClass;
import com.epriest.game.CanvasGL.util.gameLog;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public abstract class GLView extends GLSurfaceView implements Renderer {

    private Context mContext;
    public static long framelate = 0;

    // Our matrices
    private final float[] mtrxProjection = new float[16];
    private final float[] mtrxView = new float[16];
    private final float[] mtrxProjectionAndView = new float[16];

    // Geometric variables
    public static float vertices[];
    public static short indices[];
    public static float uvs[];
    public FloatBuffer vertexBuffer;
    public ShortBuffer drawListBuffer;
    public FloatBuffer uvBuffer;

    // public Rect image;
    public Sprite sprite;

    int mProgram;

//    float ssu = 1.0f;
    float ssx = 1.0f;
	float ssy = 1.0f;
    float swp;// = INN.GAMECANVAS_WIDTH;
    float shp;// = INN.mGameCanvasHeight;

    int mGameCanvasWidth;
    int mGameCanvasHeight;
	int BG_TEXTURE_WIDTH = 512;
	int BG_TEXTURE_HEIGHT = 1024;

//    int BG_TEXTURE_WIDTH_1 = 1024;
//    int BG_TEXTURE_HEIGHT_1 = 2048;

    int mGameOrientation = 0;

    public GLView(Context context) {
        super(context);
        // appClass = (ApplicationClass)context.getApplicationContext();
        // appClass.game.Start();
        prevFrameTime = System.currentTimeMillis() + 100;

        this.setEGLContextClientVersion(2);
        // GLSurfaceView는 주기적으로 onDrawFrame를 호출하여 장면을 계속 갱신한다.
        // 디폴트가 연속모드(RENDERMODE_CONTINUOUSLY)임. 연속적으로 다시 그리기를
        // 할 필요가 없으면 랜더 모드를 RENDERMODE_WHEN_DIRTY로 변경.
        // => 최초 한번 그리기를 수행하고 다음 그리기 요청이 있을때까지는 화면을
        // 갱신하지 않는다. 다시 그리기를 요청할 때는 requestRender() 호출.
        this.setRenderer(this);
        this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        sprite = new Sprite();
    }

    boolean isGameInit = true;
    long mFrameStart;
    long frameCount = 0;
    long totalElapsedTime = 0;
    long prevFrameTime;

    @Override
    public void onDrawFrame(GL10 gl) {
        // Get the current time
        long timeNow = System.currentTimeMillis();

        // We should make sure we are valid and sane
        // if (prevFrameTime > timeNow ) return;

        // appClass.game.gameState();
        cUpdateLogic();
        int[] textures = CreateImage();
        Render(mtrxProjectionAndView);
        // UpdateSprite();
        GLES20.glDeleteTextures(1, textures, 0);

        // Get the amount of time the last frame took.
        long mElapsedTime = timeNow - prevFrameTime;
        totalElapsedTime += mElapsedTime;
        if (totalElapsedTime > 1000) {
            GLView.framelate = frameCount;
            frameCount = 0;
            totalElapsedTime = 0;
        }

        long timeFrameIntetval = System.currentTimeMillis() - timeNow;
        // gameLog.d("timeFrameIntetval : "+timeFrameIntetval);
        if (timeFrameIntetval < 33) {
            try {
                Thread.sleep(33 - timeFrameIntetval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        prevFrameTime = timeNow;
        ++frameCount;
    }

    /**
     * draw..
     *
     * @param m
     */
    private void Render(float[] m) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        int mPositionHandle = GLES20.glGetAttribLocation(riGraphicTools.sp_Image, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        int mTexCoordLoc = GLES20.glGetAttribLocation(riGraphicTools.sp_Image, "a_texCoord");
        GLES20.glEnableVertexAttribArray(mTexCoordLoc);
        GLES20.glVertexAttribPointer(mTexCoordLoc, 2, GLES20.GL_FLOAT, false, 0, uvBuffer);
        int mtrxhandle = GLES20.glGetUniformLocation(riGraphicTools.sp_Image, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, m, 0);
        int mSamplerLoc = GLES20.glGetUniformLocation(riGraphicTools.sp_Image, "s_texture");
        GLES20.glUniform1i(mSamplerLoc, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordLoc);
    }

    private void init() {
        SetupScaling();
        SetupTriangle();
        setVertices();
        // SetupImage();
    }

    // 그리기 표면이 생성될 때나 ESL 컨텍스트가 제거될때 호출.
    // 모든 리소스는 자동으로 제거되므로 일부러 제거할 필요 없음.
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mContext = cGLView();
        // SetupScaling();
        // SetupTriangle();
        // SetupImage();
        String viewRange = cOnSurfaceCreate();
        mGameCanvasWidth = Integer.parseInt(viewRange.split(",")[0]);
        mGameCanvasHeight = Integer.parseInt(viewRange.split(",")[1]);
        mGameOrientation = Integer.parseInt(viewRange.split(",")[2]);

        if (mGameOrientation == ApplicationClass.GAMECANVAS_ORIENTATION_LANDSCAPE) {
            int i = BG_TEXTURE_WIDTH;
            BG_TEXTURE_WIDTH = BG_TEXTURE_HEIGHT;
            BG_TEXTURE_HEIGHT = i;
        }
        init();
        gl.glClearColor(0.0f, 1.0f, 0.0f, 0.5f);
        // Create the shaders
        int vertexShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER, riGraphicTools.vs_SolidColor);
        int fragmentShader = riGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER, riGraphicTools.fs_SolidColor);

        riGraphicTools.sp_SolidColor = GLES20.glCreateProgram(); // create empty
        // OpenGL ES
        // Program
        GLES20.glAttachShader(riGraphicTools.sp_SolidColor, vertexShader); // add
        // the
        // vertex
        // shader
        // to
        // program
        GLES20.glAttachShader(riGraphicTools.sp_SolidColor, fragmentShader); // add
        // the
        // fragment
        // shader
        // to
        // program
        GLES20.glLinkProgram(riGraphicTools.sp_SolidColor); // creates OpenGL ES
        // program
        // executables

        // Create the shaders, images
        vertexShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER, riGraphicTools.vs_Image);
        fragmentShader = riGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER, riGraphicTools.fs_Image);

        riGraphicTools.sp_Image = GLES20.glCreateProgram();
        GLES20.glAttachShader(riGraphicTools.sp_Image, vertexShader);
        GLES20.glAttachShader(riGraphicTools.sp_Image, fragmentShader);
        GLES20.glLinkProgram(riGraphicTools.sp_Image);

        // Set our shader programm
        GLES20.glUseProgram(riGraphicTools.sp_Image);
    }

//    int overCanvasHeight;

    public void SetupScaling() {

        // The screen resolutions
        swp = (int) (mContext.getResources().getDisplayMetrics().widthPixels);
        shp = (int) (mContext.getResources().getDisplayMetrics().heightPixels);

        //SoftKey_Navigation Bar 확인
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            boolean useSoftNavigation;
            Resources resources = mContext.getResources();
            int resourceId = resources.getIdentifier("config_showNavigationBar", "bool", "android");

            if (resourceId > 0) {
                useSoftNavigation = resources.getBoolean(resourceId);
            } else {
                boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
                boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
                useSoftNavigation = (!(hasBackKey && hasHomeKey));
            }

            //SoftKey_Navigation Bar가 있으면 그 높이만큼 디스플레이 너비에 합산
            if (useSoftNavigation) {
                int resId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
                    if (mGameOrientation == ApplicationClass.GAMECANVAS_ORIENTATION_LANDSCAPE) {
                        swp += resources.getDimensionPixelSize(resId);
                    } else {
                        shp += resources.getDimensionPixelSize(resId);
                    }
            }
        }

        // Orientation is assumed portrait
        ssx = (float) (swp / mGameCanvasWidth);
        ssy = (float) (shp / mGameCanvasHeight);
//		float deviceVal = shp/swp;
//		float gameVal = (float)mGameCanvasHeight/(float)mGameCanvasWidth;
        gameLog.d("swp:" + swp + ",,shp:" + shp + "\ncanvasWidth:" + mGameCanvasWidth + ",,canvasHeight:" + mGameCanvasHeight + "\nssx:" + ssx + ",,ssy:" + ssy);
        // Get our uniform scaler
//		if (ssx > ssy){
//			ssu = ssx;
//			overCanvasHeight = (int) ((mGameCanvasHeight * ssu) - shp);
//			if(overCanvasHeight < 0)
//				overCanvasHeight = 0;
//			float i1 = (mGameCanvasHeight-BG_TEXTURE_HEIGHT)*ssu;
//			overCanvasHeight = (int) (i1-overCanvasHeight);
//		}else{
//			ssu = ssx;
//		}
//		sprite.ssu = ssu;
//        sprite.ssx = ssx;
//        sprite.ssy = ssy;

        /*if (mGameOrientation == ApplicationClass.GAMECANVAS_ORIENTATION_PORTRAIT) {
            if (ssx < ssy) {
                ssu = ssx;
            } else {
                ssu = ssy;
            }
        } else if (mGameOrientation == ApplicationClass.GAMECANVAS_ORIENTATION_LANDSCAPE) {
            if (ssx < ssy) {
                ssu = ssy;
            } else {
                ssu = ssx;
            }
        }*/

        /*sprite.ssu = ssu;
//		overCanvasHeight = (int) ((mGameCanvasHeight * ssu) - shp);
//		if(overCanvasHeight < 0)
        overCanvasHeight = 0;
        float i1 = shp - mGameCanvasHeight * ssu;
        overCanvasHeight = (int) (i1 - overCanvasHeight);
        gameLog.d("overCanvasHeight:" + overCanvasHeight);*/
    }

//	public float screenValue(){
//		float valW = (float)(mGameCanvasWidth / swp);
//		float valH = (float)(mGameCanvasHeight / shp);
//		if(valW > valH)
//			return valW;
//		else
//			return valH;
//	}

//	public int screenOverWidth(){
//		return Math.abs((int) (swp - (mGameCanvasWidth*ssu)));
//	}
//	
//	public int screenOverHeight(){
//		return Math.abs((int) (shp - (mGameCanvasHeight*ssu)));
//	}
//	
//	public int getGameCanvasWidth(){
//		return mGameCanvasWidth-screenOverWidth();
//	}
//	
//	public int getGameCanvasHeight(){
//		return mGameCanvasHeight-screenOverHeight();
//	}

    private void setVertices() {
        // Create our UV coordinates.
        uvs = new float[]{0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f};

        // The texture buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(uvs.length * 4);
        bb.order(ByteOrder.nativeOrder());
        uvBuffer = bb.asFloatBuffer();
        uvBuffer.put(uvs);
        uvBuffer.position(0);
    }

    private boolean canvasInit = true;
    private Bitmap mCanvasBitmap;
    private Canvas mCanvas;
//	private Scene mScene;

    private int[] CreateImage() {
        if (mCanvasBitmap == null) {
//			gameLog.d("===setInit==");
//			mScene = new Scene_Main();
//			mScene.initScene(appClass);
            canvasInit = false;

            mCanvasBitmap = Bitmap.createBitmap(BG_TEXTURE_WIDTH, BG_TEXTURE_HEIGHT, Bitmap.Config.RGB_565);

                /*if (mCanvasBitmap != null) {
                    mCanvasBitmap.recycle();
                    mCanvasBitmap = null;
                }*/
                mCanvas = new Canvas(mCanvasBitmap);
                gameLog.d("===setInit==" + mCanvasBitmap.getWidth() + "," + mCanvasBitmap.getHeight());
        }

//		// 비트맵 텍스쳐 생성
//		mScene.draw(mCanvas);
        Bitmap mBitmap = cOnDraw();
        mCanvas.drawColor(Color.WHITE);
        mCanvas.drawBitmap(mBitmap,0,BG_TEXTURE_HEIGHT-mBitmap.getHeight(), null);

        // 텍스처 포인터 설정
        int[] texturenames = new int[1];
        GLES20.glGenTextures(1, texturenames, 0);
        texturenames[0] = GLUtil.loadTexture(mCanvasBitmap, texturenames,0);

        // gameLog.d("texturenames = "+texturenames[0]);
        return texturenames;
    }

    public void SetupTriangle() {
        // We will need a randomizer
        // Random rnd = new Random();

        // Our collection of vertices
        vertices = new float[4 * 3];

        // Create the vertex data
        // for(int i=0;i<30;i++)
        // {
//		int overHeight = (mGameCanvasHeight*ssu) -
        int offset_x = 0;// rnd.nextInt((int)swp);
        int offset_y = 0;//overCanvasHeight;// rnd.nextInt((int)shp);

        // Create the 2D parts of our 3D vertices, others are default 0.0f
//		gameLog.d("ssu = " + ssu);

        vertices[0] = offset_x;
        vertices[1] = offset_y + (BG_TEXTURE_HEIGHT * ssy);
        vertices[2] = 0f;

        vertices[3] = offset_x;
        vertices[4] = offset_y;
        vertices[5] = 0f;

        vertices[6] = offset_x + (BG_TEXTURE_WIDTH * ssx);
        vertices[7] = offset_y;
        vertices[8] = 0f;

        vertices[9] = offset_x + (BG_TEXTURE_WIDTH * ssx);
        vertices[10] = offset_y + (BG_TEXTURE_HEIGHT * ssy);
        vertices[11] = 0f;
        // }

        // The indices for all textured quads
        indices = new short[6];
        int last = 0;
        // for(int i=0;i<30;i++)
        // {
        // We need to set the new indices for the new quad
        indices[0] = (short) (last + 0);
        indices[1] = (short) (last + 1);
        indices[2] = (short) (last + 2);
        indices[3] = (short) (last + 0);
        indices[4] = (short) (last + 2);
        indices[5] = (short) (last + 3);

        // Our indices are connected to the vertices so we need to keep them
        // in the correct order.
        // normal quad = 0,1,2,0,2,3 so the next one will be 4,5,6,4,6,7
        last = last + 4;
        // }

        // The vertex buffer.
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(indices);
        drawListBuffer.position(0);
    }

    public void UpdateSprite() {
        // Get new transformed vertices
        vertices = sprite.getTransformedVertices();

        // The vertex buffer.
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
    }

    // 표면의 크기가 변경될 때 호출. 생성 직후에도 호출. 보통 투영 모드 지정
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
//		swp = mContext.getResources().getDisplayMetrics().widthPixels;
//		shp = mContext.getResources().getDisplayMetrics().heightPixels;
//		gameLog.d("swp = "+swp+",,shp = "+shp);
        // We need to know the current width and height.
//		if (swp != width)
//			swp = width;
//		if (shp != height)
//			shp = height;

        swp = width;
        shp = height;
        gameLog.d("swp = " + swp + ",,shp = " + shp);
        cOnSurfaceChanged((int) swp, (int) shp);

        // Redo the Viewport, making it fullscreen.
        GLES20.glViewport(0, 0, (int) swp, (int) shp);

        // Clear our matrices
        for (int i = 0; i < 16; i++) {
            mtrxProjection[i] = 0.0f;
            mtrxView[i] = 0.0f;
            mtrxProjectionAndView[i] = 0.0f;
        }

        // Setup our screen width and height for normal sprite translation.
        Matrix.orthoM(mtrxProjection, 0, 0f, (int) swp, 0.0f, (int) shp, 0, 50);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mtrxView, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mtrxProjectionAndView, 0, mtrxProjection, 0, mtrxView, 0);

        // SetupScaling();
    }

    @Override
    public void onResume() {
        super.onResume();
        prevFrameTime = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        cOnTouchEvent(event);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        cOnKeyDown(keyCode, event);
        return true;
    }

    public abstract Context cGLView();

    public abstract String cOnSurfaceCreate();

    public abstract void cOnSurfaceChanged(int width, int height);

    public abstract void cUpdateLogic();

    public abstract Bitmap cOnDraw();

    public abstract void cOnKeyDown(int keyCode, KeyEvent event);

    public abstract void cOnTouchEvent(MotionEvent event);
}
