//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.qs.helper.printer;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class PrintService {
    public static boolean isFUll = false;
    public static int imageWidth = 48;

    public PrintService() {
    }

    public byte[] getText(String textStr) {
        Object var2 = null;

        byte[] send;
        try {
            send = textStr.getBytes("GBK");
        } catch (UnsupportedEncodingException var4) {
            send = textStr.getBytes();
        }

        return send;
    }

    public byte[] getImage(Bitmap bitmap) {
        int mWidth = bitmap.getWidth();
        int mHeight = bitmap.getHeight();
        bitmap = resizeImage(bitmap, imageWidth * 8, mHeight);
        byte[] bt = PrinterLib.getBitmapData(bitmap);
        bitmap.recycle();
        return bt;
    }

    public byte[] getTextUnicode(String textStr) {
        byte[] send = string2Unicode(textStr);
        return send;
    }

    private static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width > w) {
            float scaleWidth = (float)w / (float)width;
            float scaleHeight = (float)h / (float)height + 24.0F;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleWidth);
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
            return resizedBitmap;
        } else {
            Bitmap resizedBitmap = Bitmap.createBitmap(w, height + 24, Config.RGB_565);
            Canvas canvas = new Canvas(resizedBitmap);
            Paint paint = new Paint();
            canvas.drawColor(-1);
            canvas.drawBitmap(bitmap, (float)((w - width) / 2), 0.0F, paint);
            return resizedBitmap;
        }
    }

    private static byte[] string2Unicode(String s) {
        byte[] bytes;
        try {
            new StringBuffer("");
            bytes = s.getBytes("unicode");
            byte[] bt = new byte[bytes.length - 2];
            int i = 2;

            for(int j = 0; i < bytes.length - 1; j += 2) {
                bt[j] = (byte)(bytes[i + 1] & 255);
                bt[j + 1] = (byte)(bytes[i] & 255);
                i += 2;
            }

            return bt;
        } catch (Exception var7) {
            try {
                bytes = s.getBytes("GBK");
                return bytes;
            } catch (UnsupportedEncodingException var6) {
                var6.printStackTrace();
                return null;
            }
        }
    }

    private byte[] StartBmpToPrintCode(Bitmap bitmap) {
        byte temp = 0;
        int j = 7;
        int start = 0;
        if (bitmap == null) {
            return null;
        } else {
            int mWidth = bitmap.getWidth();
            int mHeight = bitmap.getHeight();
            int[] mIntArray = new int[mWidth * mHeight];
            byte[] data = new byte[mWidth * mHeight];
            bitmap.getPixels(mIntArray, 0, mWidth, 0, 0, mWidth, mHeight);
            this.encodeYUV420SP(data, mIntArray, mWidth, mHeight);
            byte[] result = new byte[mWidth * mHeight / 8];

            int aHeight;
            for(aHeight = 0; aHeight < mWidth * mHeight; ++aHeight) {
                temp += (byte)(data[aHeight] << j);
                --j;
                if (j < 0) {
                    j = 7;
                }

                if (aHeight % 8 == 7) {
                    result[start++] = temp;
                    temp = 0;
                }
            }

            if (j != 7) {
                result[start++] = temp;
            }

            aHeight = 24 - mHeight % 24;
            int perline = mWidth / 8;
            byte[] add = new byte[aHeight * perline];
            byte[] nresult = new byte[mWidth * mHeight / 8 + aHeight * perline];
            System.arraycopy(result, 0, nresult, 0, result.length);
            System.arraycopy(add, 0, nresult, result.length, add.length);
            byte[] byteContent = new byte[(mWidth / 8 + 4) * (mHeight + aHeight)];
            byte[] bytehead = new byte[]{31, 16, (byte)(mWidth / 8), 0};

            for(int index = 0; index < mHeight + aHeight; ++index) {
                System.arraycopy(bytehead, 0, byteContent, index * (perline + 4), 4);
                System.arraycopy(nresult, index * perline, byteContent, index * (perline + 4) + 4, perline);
            }

            return byteContent;
        }
    }

    public void encodeYUV420SP(byte[] yuv420sp, int[] rgba, int width, int height) {
        int frameSize = width * height;
        int[] U = new int[frameSize];
        int[] V = new int[frameSize];
        int uvwidth = width / 2;
        boolean bits = true;
        int index = 0;
        boolean f = false;

        for(int j = 0; j < height; ++j) {
            for(int i = 0; i < width; ++i) {
                int r = (rgba[index] & -16777216) >> 24;
                int g = (rgba[index] & 16711680) >> 16;
                int b = (rgba[index] & '\uff00') >> 8;
                int y = (66 * r + 129 * g + 25 * b + 128 >> 8) + 16;
                int u = (-38 * r - 74 * g + 112 * b + 128 >> 8) + 128;
                int v = (112 * r - 94 * g - 18 * b + 128 >> 8) + 128;
                byte temp = (byte)(y < 0 ? 0 : (y > 255 ? 255 : y));
                yuv420sp[index++] = (byte)(temp > 0 ? 1 : 0);
            }
        }

        f = false;
    }

    public void createFile(String path, byte[] content) throws IOException {
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(content);
        fos.close();
    }
}
