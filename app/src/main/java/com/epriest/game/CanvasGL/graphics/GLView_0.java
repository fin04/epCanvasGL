package com.epriest.game.CanvasGL.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
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

public abstract class GLView_0 extends GLSurfaceView implements Renderer {

    public ApplicationClass appClass;

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

    private float scaleValue = 1.0f;
    private float scaleValueX = 1.0f;
    private float scaleValueY = 1.0f;

    private long frameCount = 0;
    private long totalElapsedTime = 0;
    private long prevFrameTime;

    public static long framelate = 0;
    private int gameFps = 45;

    public GLView_0(Context context) {
        super(context);
        appClass = (ApplicationClass) context.getApplicationContext();
        // appClass.game.Start();
        prevFrameTime = System.currentTimeMillis() + 100;

        this.setEGLContextClientVersion(2);
//        this.setEGLConfigChooser(new MyConfigChooser());

        // GLSurfaceView는 주기적으로 onDrawFrame를 호출하여 장면을 계속 갱신한다.
        // 디폴트가 연속모드(RENDERMODE_CONTINUOUSLY)임. 연속적으로 다시 그리기를
        // 할 필요가 없으면 랜더 모드를 RENDERMODE_WHEN_DIRTY로 변경.
        // => 최초 한번 그리기를 수행하고 다음 그리기 요청이 있을때까지는 화면을
        // 갱신하지 않는다. 다시 그리기를 요청할 때는 requestRender() 호출.
        this.setRenderer(this);
        this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        sprite = new Sprite();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Get the current time
        long timeNow = System.currentTimeMillis();

        // We should make sure we are valid and sane
        if (tempMotionEvent != null) {
            cOnTouchEvent(tempMotionEvent);
        }
        cUpdateLogic();
        tempMotionEvent = null;
        int[] textures = getImage();
        Render(mtrxProjectionAndView);
        // UpdateSprite();
        GLES20.glDeleteTextures(1, textures, 0);

        // Get the amount of time the last frame took.
        long mElapsedTime = timeNow - prevFrameTime;
        totalElapsedTime += mElapsedTime;
        if (totalElapsedTime > 1000) {
            GLView_0.framelate = frameCount;
            frameCount = 0;
            totalElapsedTime = 0;
        }

        long timeFrameIntetval = System.currentTimeMillis() - timeNow;
        int sleepfor = (int) ((1000 / gameFps) - timeFrameIntetval);
        if (timeFrameIntetval < sleepfor) {
            try {
                Thread.sleep(sleepfor);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        prevFrameTime = timeNow;
        ++frameCount;
    }

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
        init();
        gl.glClearColor(0.0f, 0.5f, 0.0f, 0.0f);
        // Create the shaders
        int vertexShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER, riGraphicTools.vs_SolidColor);
        int fragmentShader = riGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER, riGraphicTools.fs_SolidColor);

        // create empty OpenGL ES Program
        riGraphicTools.sp_SolidColor = GLES20.glCreateProgram();
        // add the vertex shader to program
        GLES20.glAttachShader(riGraphicTools.sp_SolidColor, vertexShader);
        // add the fragment shader to program
        GLES20.glAttachShader(riGraphicTools.sp_SolidColor, fragmentShader);
        // creates OpenGL ES program executables
        GLES20.glLinkProgram(riGraphicTools.sp_SolidColor);

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
        float deviceWidth = appClass.getDeviceScreenWidth();
        float deviceHeight = appClass.getDeviceScreenHeight();

        float gameCanvasWidth = appClass.getGameCanvasWidth();
        float gameCanvasHeight = appClass.getGameCanvasHeight();

        // LCD 크기에 비례한 실제 Canvas 크기 셋팅
        if (appClass.mGameOrientation == ApplicationClass.GAMECANVAS_ORIENTATION_PORTRAIT) {
            int canvasValue = (int) (gameCanvasHeight / gameCanvasWidth * 100);
            int lcdValue = (int) (deviceHeight / deviceWidth * 100);
            float canvasHeight = gameCanvasHeight;
            if (canvasValue > lcdValue)
                canvasHeight = deviceHeight * gameCanvasWidth / deviceWidth;
            appClass.setGameCanvasHeight((int) canvasHeight);
        } else if (appClass.mGameOrientation == ApplicationClass.GAMECANVAS_ORIENTATION_LANDSCAPE) {
            int canvasValue = (int) (gameCanvasWidth / gameCanvasHeight * 100);
            int lcdValue = (int) (deviceWidth / deviceHeight * 100);
            float canvasWidth = gameCanvasWidth;
            if (canvasValue > lcdValue)
                canvasWidth = deviceWidth * gameCanvasHeight / deviceHeight;
            appClass.setGameCanvasWidth((int) canvasWidth);
        }

        // Orientation is assumed portrait
        scaleValueX = deviceWidth / gameCanvasWidth;
        scaleValueY = deviceHeight / gameCanvasHeight;

        if (appClass.mGameOrientation == ApplicationClass.GAMECANVAS_ORIENTATION_PORTRAIT) {
            scaleValue = scaleValueX;
        } else if (appClass.mGameOrientation == ApplicationClass.GAMECANVAS_ORIENTATION_LANDSCAPE) {
            scaleValue = scaleValueY;
        }
    }

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

    private int[] getImage() {
//		// 비트맵 텍스쳐 생성
        Bitmap mBitmap = cOnDraw();
        /*mCanvas.drawColor(Color.WHITE);
        mCanvas.drawBitmap(mBitmap, 0, BG_TEXTURE_HEIGHT - mBitmap.getHeight(), null);*/

        // 텍스처 포인터 설정
        int[] texturenames = new int[1];
        GLES20.glGenTextures(1, texturenames, 0);
        texturenames[0] = GLUtil.loadTexture(mBitmap, texturenames, 0);

        return texturenames;
    }

    public void SetupTriangle() {
        // Our collection of vertices
        vertices = new float[4 * 3];

        // Create the vertex data
        // for(int i=0;i<30;i++)
        // {
//		int overHeight = (mGameCanvasHeight*scaleValue) -
        int offset_x = 0;// rnd.nextInt((int)deviceWidth);
        int offset_y = 0;//overCanvasHeight;// rnd.nextInt((int)deviceHeight);

            // Create the 2D parts of our 3D vertices, others are default 0.0f
//            float textureW = appClass.getTextureWidth(); //* scaleValue;
//            float textureH = appClass.getTextureHeight(); //* scaleValue;

        scaleValue = 1;

        vertices[0] = offset_x;
        vertices[1] = offset_y + (appClass.getTextureHeight() * scaleValue);
        vertices[2] = 0f;

        vertices[3] = offset_x;
        vertices[4] = offset_y;
        vertices[5] = 0f;

        vertices[6] = offset_x + (appClass.getTextureWidth() * scaleValue);
        vertices[7] = offset_y;
        vertices[8] = 0f;

        vertices[9] = offset_x + (appClass.getTextureWidth() * scaleValue);
        vertices[10] = offset_y + (appClass.getTextureHeight() * scaleValue);
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
        gameLog.d("deviceWidth = " + width + ",,deviceHeight = " + height);

        appClass.setDeviceScreenWidth(width);
        appClass.setDeviceScreenHeight(height);

        //올바른 touch 좌표를 위한 value
        appClass.mGameScaleValueWidth = appClass.getGameCanvasWidth() / width;
        appClass.mGameScaleValueHeight = appClass.getGameCanvasHeight() / height;

        cOnSurfaceChanged((int) width, (int) height);

        // Redo the Viewport, making it fullscreen.
        GLES20.glViewport(0, 0, (int) width, (int) height);

        // Clear our matrices
        for (int i = 0; i < 16; i++) {
            mtrxProjection[i] = 0.0f;
            mtrxView[i] = 0.0f;
            mtrxProjectionAndView[i] = 0.0f;
        }

        // Setup our screen width and height for normal sprite translation.
        Matrix.orthoM(mtrxProjection, 0, 0f, (int) appClass.getGameCanvasWidth(), 0.0f, (int) appClass.getGameCanvasHeight(), 0, 50);

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

    private MotionEvent tempMotionEvent;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        tempMotionEvent = event;
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        cOnKeyDown(keyCode, event);
        return true;
    }

    public abstract String cOnSurfaceCreate();

    public abstract void cOnSurfaceChanged(int width, int height);

    public abstract void cUpdateLogic();

    public abstract Bitmap cOnDraw();

    public abstract void cOnKeyDown(int keyCode, KeyEvent event);

    public abstract void cOnTouchEvent(MotionEvent event);
}
