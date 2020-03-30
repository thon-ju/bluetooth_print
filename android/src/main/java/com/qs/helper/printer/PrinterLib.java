//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.qs.helper.printer;

import android.graphics.Bitmap;

public class PrinterLib {
    public PrinterLib() {
    }

    public static byte[] getBitmapData(Bitmap bitmap) {
        byte temp = 0;
        int j = 7;
        int start = 0;
        if (bitmap == null) {
            return null;
        } else {
            int mWidth = bitmap.getWidth();
            int mHeight = bitmap.getHeight();
            int[] mIntArray = new int[mWidth * mHeight];
            bitmap.getPixels(mIntArray, 0, mWidth, 0, 0, mWidth, mHeight);
            bitmap.recycle();
            byte[] data = encodeYUV420SP(mIntArray, mWidth, mHeight);
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

    public static byte[] encodeYUV420SP(int[] rgba, int width, int height) {
        int frameSize = width * height;
        byte[] yuv420sp = new byte[frameSize];
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
        return yuv420sp;
    }
}
