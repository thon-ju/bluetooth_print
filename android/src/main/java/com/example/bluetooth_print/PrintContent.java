package com.example.bluetooth_print;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import com.gprinter.command.CpclCommand;
import com.gprinter.command.EscCommand;
import com.gprinter.command.LabelCommand;

import java.util.List;
import java.util.Map;
import java.util.Vector;

public class PrintContent {
      private static final String TAG = PrintContent.class.getSimpleName();

      /**
       * 票据打印测试页
       * @return
       */
      public static Vector<Byte> getReceipt() {
            EscCommand esc = new EscCommand();
            //初始化打印机
            esc.addInitializePrinter();
            //打印走纸多少个单位
            esc.addPrintAndFeedLines((byte) 3);
            // 设置打印居中
            esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
            // 设置为倍高倍宽
            esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);
            // 打印文字
            esc.addText("票据测试\n");
            //打印并换行
            esc.addPrintAndLineFeed();
            // 取消倍高倍宽
            esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
            // 设置打印左对齐
            esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
            // 打印文字
            esc.addText("打印文字测试:\n");
            // 打印文字
            esc.addText("欢迎使用打印机!\n");
            esc.addPrintAndLineFeed();
            esc.addText("打印对齐方式测试:\n");
            // 设置打印左对齐
            esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
            esc.addText("居左");
            esc.addHorTab();
            esc.addText("居左2");
            esc.addSelectJustification(EscCommand.JUSTIFICATION.RIGHT);
            esc.addText("居右");
            esc.addPrintAndLineFeed();
            // 设置打印居中对齐
            esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
            esc.addText("居中");
            esc.addPrintAndLineFeed();
            // 设置打印居右对齐
            esc.addSelectJustification(EscCommand.JUSTIFICATION.RIGHT);
            esc.addText("居右");
            esc.addPrintAndLineFeed();
            esc.addPrintAndLineFeed();
            // 设置打印左对齐
            esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
            esc.addText("打印Bitmap图测试:\n");
            esc.addPrintAndLineFeed();
            // 打印文字
            esc.addText("打印条码测试:\n");
            esc.addSelectPrintingPositionForHRICharacters(EscCommand.HRI_POSITION.BELOW);
            // 设置条码可识别字符位置在条码下方
            // 设置条码高度为60点
            esc.addSetBarcodeHeight((byte) 60);
            // 设置条码宽窄比为2
            esc.addSetBarcodeWidth((byte) 2);
            // 打印Code128码
            esc.addCODE128(esc.genCodeB("barcode128"));
            esc.addPrintAndLineFeed();
        /*
        * QRCode命令打印 此命令只在支持QRCode命令打印的机型才能使用。 在不支持二维码指令打印的机型上，则需要发送二维条码图片
		*/
            esc.addText("打印二维码测试:\n");
            // 设置纠错等级
            esc.addSelectErrorCorrectionLevelForQRCode((byte) 0x31);
            // 设置qrcode模块大小
            esc.addSelectSizeOfModuleForQRCode((byte) 4);
            // 设置qrcode内容
            esc.addStoreQRCodeData("www.smarnet.cc");
            // 打印QRCode
            esc.addPrintQRCode();
            //打印并走纸换行
            esc.addPrintAndLineFeed();
            // 设置打印居中对齐
            esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
            //打印fontB文字字体
            esc.addSelectCharacterFont(EscCommand.FONT.FONTB);
            esc.addText("测试完成!\r\n");
            //打印并换行
            esc.addPrintAndLineFeed();
            //打印走纸n个单位
            esc.addPrintAndFeedLines((byte) 4);
            // 开钱箱
            esc.addGeneratePlus(LabelCommand.FOOT.F2, (byte) 255, (byte) 255);
            //开启切刀
            esc.addCutPaper();
            //添加缓冲区打印完成查询
            byte [] bytes={0x1D,0x72,0x01};
            //添加用户指令
            esc.addUserCommand(bytes);
            Vector<Byte> datas = esc.getCommand();
            return datas;
      }

      /**
       * 标签打印测试页
       * @return
       */
      public static Vector<Byte> getLabel() {
            LabelCommand tsc = new LabelCommand();
            // 设置标签尺寸宽高，按照实际尺寸设置 单位mm
            tsc.addSize(60, 75);
            // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0 单位mm
            tsc.addGap(0);
            // 设置打印方向
            tsc.addDirection(LabelCommand.DIRECTION.FORWARD, LabelCommand.MIRROR.NORMAL);
            // 开启带Response的打印，用于连续打印
            tsc.addQueryPrinterStatus(LabelCommand.RESPONSE_MODE.ON);
            // 设置原点坐标
            tsc.addReference(0, 0);
            //设置浓度
            tsc.addDensity(LabelCommand.DENSITY.DNESITY4);
            // 撕纸模式开启
            tsc.addTear(EscCommand.ENABLE.ON);
            // 清除打印缓冲区
            tsc.addCls();
            // 绘制简体中文
            tsc.addText(10, 0, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                    "欢迎使用Printer");
            //打印繁体
            tsc.addUnicodeText(10,32, LabelCommand.FONTTYPE.TRADITIONAL_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"BIG5碼繁體中文字元","BIG5");
            //打印韩文
            tsc.addUnicodeText(10,60, LabelCommand.FONTTYPE.KOREAN, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"Korean 지아보 하성","EUC_KR");
//            Bitmap b = BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.gprinter);
//            // 绘制图片
//            tsc.addBitmap(10, 80, LabelCommand.BITMAP_MODE.OVERWRITE, 300, b);

            //绘制二维码
            tsc.addQRCode(10,380, LabelCommand.EEC.LEVEL_L, 5, LabelCommand.ROTATION.ROTATION_0, " www.smarnet.cc");
            // 绘制一维条码
            tsc.add1DBarcode(10, 500, LabelCommand.BARCODETYPE.CODE128, 100, LabelCommand.READABEL.EANBEL, LabelCommand.ROTATION.ROTATION_0, "SMARNET");
            // 打印标签
            tsc.addPrint(1, 1);
            // 打印标签后 蜂鸣器响
            tsc.addSound(2, 100);
            //开启钱箱
            tsc.addCashdrwer(LabelCommand.FOOT.F5, 255, 255);
            Vector<Byte> datas = tsc.getCommand();
            // 发送数据
            return  datas;
      }

      /**
       * 面单打印测试页
       * @return
       */
      public static Vector<Byte> getCPCL() {
            CpclCommand cpcl = new CpclCommand();
            cpcl.addInitializePrinter(1130, 1);
            cpcl.addJustification(CpclCommand.ALIGNMENT.CENTER);
            cpcl.addSetmag(1, 1);
            cpcl.addText(CpclCommand.TEXT_FONT.FONT_4, 0, 30, "Sample");
            cpcl.addSetmag(0, 0);
            cpcl.addJustification(CpclCommand.ALIGNMENT.LEFT);
            cpcl.addText(CpclCommand.TEXT_FONT.FONT_4, 0, 65, "Print text");
            cpcl.addText(CpclCommand.TEXT_FONT.FONT_4, 0, 95, "Welcom to use SMARNET printer!");
            cpcl.addText(CpclCommand.TEXT_FONT.FONT_13, 0, 135, "佳博智匯標籤打印機");
            cpcl.addText(CpclCommand.TEXT_FONT.FONT_4, 0, 195, "智汇");
            cpcl.addJustification(CpclCommand.ALIGNMENT.CENTER);
            cpcl.addText(CpclCommand.TEXT_FONT.FONT_4, 0, 195, "网络");
            cpcl.addJustification(CpclCommand.ALIGNMENT.RIGHT);
            cpcl.addText(CpclCommand.TEXT_FONT.FONT_4, 0, 195, "设备");
            cpcl.addJustification(CpclCommand.ALIGNMENT.LEFT);
            cpcl.addText(CpclCommand.TEXT_FONT.FONT_4, 0, 230, "Print bitmap!");
//            Bitmap bitmap = BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.gprinter);
//            cpcl.addEGraphics(0, 255, 385, bitmap);
            cpcl.addText(CpclCommand.TEXT_FONT.FONT_4, 0, 645, "Print code128!");
            cpcl.addBarcodeText(5, 2);
            cpcl.addBarcode(CpclCommand.COMMAND.BARCODE, CpclCommand.CPCLBARCODETYPE.CODE128, 50, 0, 680, "SMARNET");
            cpcl.addText(CpclCommand.TEXT_FONT.FONT_4, 0, 775, "Print QRcode");
            cpcl.addBQrcode(0, 810, "QRcode");
            cpcl.addJustification(CpclCommand.ALIGNMENT.CENTER);
            cpcl.addText(CpclCommand.TEXT_FONT.FONT_4, 0, 1010, "Completed");
            cpcl.addJustification(CpclCommand.ALIGNMENT.LEFT);
            cpcl.addPrint();
            Vector<Byte> datas = cpcl.getCommand();
            return datas;
      }

      /**
       * 票据打印对象转换
       * @return
       */
      public static Vector<Byte> mapToReceipt(List<Map<String,Object>> list) {
            EscCommand esc = new EscCommand();
            //初始化打印机
            esc.addInitializePrinter();
            //打印走纸多少个单位
            esc.addPrintAndFeedLines((byte) 3);

            // {type:'text|barcode|qrcode', content:'', size:4, align: left|center|right, weight: 0|1, width:0|1, height:0|1, underline:0|1, linefeed: 0|1}
            for (Map<String,Object> m: list) {
                  String type = (String)m.get("type");
                  String content = (String)m.get("content");
                  String align = (String)(m.get("align")==null?"left":m.get("align"));
                  int size = (int)(m.get("size")==null?4:m.get("size"));
                  int weight = (int)(m.get("weight")==null?0:m.get("weight"));
                  int width = (int)(m.get("width")==null?0:m.get("width"));
                  int height = (int)(m.get("height")==null?0:m.get("height"));
                  int underline = (int)(m.get("underline")==null?0:m.get("underline"));
                  int linefeed = (int)(m.get("linefeed")==null?0:m.get("linefeed"));

                  Log.e(TAG,"print line: " + type + " " + content);

                  EscCommand.ENABLE emphasized = weight==0?EscCommand.ENABLE.OFF:EscCommand.ENABLE.ON;
                  EscCommand.ENABLE doublewidth = width==0?EscCommand.ENABLE.OFF:EscCommand.ENABLE.ON;
                  EscCommand.ENABLE doubleheight = height==0?EscCommand.ENABLE.OFF:EscCommand.ENABLE.ON;
                  EscCommand.ENABLE isUnderline = underline==0?EscCommand.ENABLE.OFF:EscCommand.ENABLE.ON;

                  // 设置打印位置
                  esc.addSelectJustification(EscCommand.JUSTIFICATION.valueOf(align.toUpperCase()));

                  if("text".equals(type)){
                        if(content == null || content.length() == 0) {
                              continue;
                        }

                        // 设置为倍高倍宽
                        esc.addSelectPrintModes(EscCommand.FONT.FONTA, emphasized, doubleheight, doublewidth, isUnderline);
                        esc.addText(content);
                        // 取消倍高倍宽
                        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
                  }else if("barcode".equals(type)){
                        if(content == null || content.length() == 0) {
                              continue;
                        }

                        esc.addSelectPrintingPositionForHRICharacters(EscCommand.HRI_POSITION.BELOW);
                        // 设置条码可识别字符位置在条码下方
                        // 设置条码高度为60点
                        esc.addSetBarcodeHeight((byte) 60);
                        // 设置条码宽窄比为2
                        esc.addSetBarcodeWidth((byte) 2);
                        // 打印Code128码
                        esc.addCODE128(esc.genCodeB(content));
                  }else if("qrcode".equals(type)){
                        if(content == null || content.length() == 0) {
                              continue;
                        }

                        // 设置纠错等级
                        esc.addSelectErrorCorrectionLevelForQRCode((byte) 0x31);
                        // 设置qrcode模块大小
                        esc.addSelectSizeOfModuleForQRCode((byte) size);
                        // 设置qrcode内容
                        esc.addStoreQRCodeData(content);
                        // 打印QRCode
                        esc.addPrintQRCode();
                  }else if("image".equals(type)){
                        if(content == null || content.length() == 0) {
                              continue;
                        }

                        byte[] bytes = Base64.decode(content, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        esc.addRastBitImage(bitmap, 576, 0);
                  }

                  if(linefeed == 1){
                        //打印并换行
                        esc.addPrintAndLineFeed();
                  }

            }

            //打印走纸n个单位
            esc.addPrintAndFeedLines((byte) 4);
            // 开钱箱
            esc.addGeneratePlus(LabelCommand.FOOT.F2, (byte) 255, (byte) 255);
            //开启切刀
            esc.addCutPaper();
            //添加缓冲区打印完成查询
            byte [] bytes={0x1D,0x72,0x01};
            //添加用户指令
            esc.addUserCommand(bytes);
            Vector<Byte> datas = esc.getCommand();
            return datas;
      }

      /**
       * 标签打印对象转换
       * @return
       */
      public static Vector<Byte> mapToLabel(List<Map<String,Object>> list) {
            LabelCommand tsc = new LabelCommand();


            Vector<Byte> datas = tsc.getCommand();
            // 发送数据
            return  datas;
      }

      /**
       * 面单打印对象转换
       * @return
       */
      public static Vector<Byte> mapToCPCL(List<Map<String,Object>> list) {
            CpclCommand cpcl = new CpclCommand();


            Vector<Byte> datas = cpcl.getCommand();
            return datas;
      }

}
