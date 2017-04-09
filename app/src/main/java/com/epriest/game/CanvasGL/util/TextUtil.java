package com.epriest.game.CanvasGL.util;

import android.content.Context;
import android.graphics.Paint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by darka on 2016-11-21.
 */

public class TextUtil {
    public static String getStringToAsset(Context context, String path) throws IOException {
        StringBuilder buf = new StringBuilder();
        InputStream json = context.getAssets().open(path);
        BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
        String result;

        while ((result = in.readLine()) != null) {
            buf.append(result + "\n");
        }

        in.close();
        return buf.toString();
    }

    // 배열을 화면에, 요소별로 알기 쉽게 출력
    public static void dumpArray(String[] array) {
        for (int i = 0; i < array.length; i++)
            System.out.format("array[%d] = %s%n", i, array[i]);
    }

    public static String setMultiLineText(String text, int textSize, int width) {
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        String str = "";
        String result = "";
        width -= textSize;
        for (int i = 0; i < text.length(); i++) {
            str += text.charAt(i);
            if (paint.measureText(str) >= width) {
                gameLog.d(str + "," + paint.measureText(str));
                result += str + "\r\n";
                str = "";
            }
        }
        result += str;
        return result;

    }
}
