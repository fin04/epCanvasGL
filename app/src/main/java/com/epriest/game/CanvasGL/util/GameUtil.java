package com.epriest.game.CanvasGL.util;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

/**
 * Created by darka on 2016-11-25.
 */

public class GameUtil {

    /**
     * int 변수 반환
     * @param num : 변수범위 (0~num-1)
     * @return : int
     */
    public static int randNum(int num){
        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis());
        return rand.nextInt(num);
    }

    public static int getRandomInt(int minNum, int maxNum){
        int iRand = new Random().nextInt(maxNum-minNum);
        return iRand+minNum;
    }

    public static boolean equalsTouch(TouchData.Touch touch,
                                      float targetX, float targetY, float targetW, float targetH){

        if(touch.mLastTouchX < targetX || touch.mLastTouchY < targetY ||
                touch.mLastTouchX > targetX+targetW || touch.mLastTouchY > targetY+targetH){
            return false;
        }
        return true;
    }

    public static String getAssetString(Context con, String fileName){
        String json = null;
        try {
            InputStream inputStream = con.getAssets().open(fileName);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

}
