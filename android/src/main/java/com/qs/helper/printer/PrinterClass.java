//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.qs.helper.printer;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.List;

public interface PrinterClass {
    int STATE_NONE = 0;
    int STATE_LISTEN = 1;
    int STATE_CONNECTING = 2;
    int STATE_CONNECTED = 3;
    int LOSE_CONNECT = 4;
    int FAILED_CONNECT = 5;
    int SUCCESS_CONNECT = 6;
    int STATE_SCANING = 7;
    int STATE_SCAN_STOP = 8;
    int MESSAGE_STATE_CHANGE = 1;
    int MESSAGE_READ = 2;
    int MESSAGE_WRITE = 3;
    byte[] CMD_CHECK_TYPE = new byte[]{27, 43};
    byte[] CMD_HORIZONTAL_TAB = new byte[]{9};
    byte[] CMD_NEWLINE = new byte[]{10};
    byte[] CMD_PRINT_CURRENT_CONTEXT = new byte[]{13};
    byte[] CMD_INIT_PRINTER = new byte[]{27, 64};
    byte[] CMD_UNDERLINE_ON = new byte[]{28, 45, 1};
    byte[] CMD_UNDERLINE_OFF = new byte[]{28, 45, 0};
    byte[] CMD_Blod_ON = new byte[]{27, 69, 1};
    byte[] CMD_BLOD_OFF = new byte[]{27, 69, 0};
    byte[] CMD_SET_FONT_24x24 = new byte[]{27, 77, 0};
    byte[] CMD_SET_FONT_16x16 = new byte[]{27, 77, 1};
    byte[] CMD_FONTSIZE_NORMAL = new byte[]{29, 33, 0};
    byte[] CMD_FONTSIZE_DOUBLE_HIGH = new byte[]{29, 33, 1};
    byte[] CMD_FONTSIZE_DOUBLE_WIDTH = new byte[]{29, 33, 16};
    byte[] CMD_FONTSIZE_DOUBLE = new byte[]{29, 33, 17};
    byte[] CMD_ALIGN_LEFT = new byte[]{27, 97, 0};
    byte[] CMD_ALIGN_MIDDLE = new byte[]{27, 97, 1};
    byte[] CMD_ALIGN_RIGHT = new byte[]{27, 97, 2};
    byte[] CMD_BLACK_LOCATION = new byte[]{12};

    boolean open(Context var1);

    boolean close(Context var1);

    void scan();

    List<Device> getDeviceList();

    void stopScan();

    boolean connect(String var1);

    boolean disconnect();

    int getState();

    void setState(int var1);

    boolean IsOpen();

    boolean write(byte[] var1);

    boolean printText(String var1);

    boolean printImage(Bitmap var1);

    boolean printUnicode(String var1);
}
