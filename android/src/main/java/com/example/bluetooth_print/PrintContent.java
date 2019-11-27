package com.example.bluetooth_print;

import android.util.Log;
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
                        // 设置为倍高倍宽
                        esc.addSelectPrintModes(EscCommand.FONT.FONTA, emphasized, doubleheight, doublewidth, isUnderline);
                        esc.addText(content);
                        // 取消倍高倍宽
                        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
                  }else if("barcode".equals(type)){
                        esc.addSelectPrintingPositionForHRICharacters(EscCommand.HRI_POSITION.BELOW);
                        // 设置条码可识别字符位置在条码下方
                        // 设置条码高度为60点
                        esc.addSetBarcodeHeight((byte) 60);
                        // 设置条码宽窄比为2
                        esc.addSetBarcodeWidth((byte) 2);
                        // 打印Code128码
                        esc.addCODE128(esc.genCodeB(content));
                  }else if("qrcode".equals(type)){
                        // 设置纠错等级
                        esc.addSelectErrorCorrectionLevelForQRCode((byte) 0x31);
                        // 设置qrcode模块大小
                        esc.addSelectSizeOfModuleForQRCode((byte) size);
                        // 设置qrcode内容
                        esc.addStoreQRCodeData(content);
                        // 打印QRCode
                        esc.addPrintQRCode();
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

}
