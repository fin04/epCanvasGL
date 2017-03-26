package com.epriest.game.CanvasGL.util;

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
// 		float tX =  (_touch.downX*appClass.axis.lcdValW)/100;
// 		float tY =  (_touch.downY*appClass.axis.lcdValH)/100;

// 		targetX = (targetX*appClass.axis.lcdValW)/100;
// 		targetY = (targetY*appClass.axis.lcdValH)/100;
// 		targetW = (targetW*appClass.axis.lcdValW)/100;
// 		targetH = (targetH*appClass.axis.lcdValH)/100;

// 		logline.msg("-=0-="+targetX+","+targetY+","+targetW+","+targetH);
// 		logline.msg("-=1-="+_touch.downX+","+_touch.downY+","+targetW+","+targetH);

        if(touch.axisX < targetX || touch.axisY < targetY ||
                touch.axisX > targetX+targetW || touch.axisY > targetY+targetH){
            return false;
        }
        return true;
    }

}
