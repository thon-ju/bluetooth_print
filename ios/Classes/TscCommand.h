//
//  TscCommand.h
//  Gprinter
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

typedef NS_ENUM (NSUInteger,Response){
    OFF     = 0,//关闭自动返回状态功能
    ON      = 1,//打开自动返回状态功能，每打印完一张返回一次
    BATCH   = 2,//打开自动返回状态功能，打印完毕后返回一次
};

@interface TscCommand : NSObject

/**
 * 方法说明：设置标签尺寸的宽和高
 * @param width  标签宽度
 * @param height 标签高度
 */
-(void) addSize:(int) width :(int) height;

/**
 * 方法说明：设置标签间隙尺寸 单位mm
 * @param m    间隙长度
 * @param n    间隙偏移
 */
-(void) addGapWithM:(int) m withN:(int) n;

/**
 * 方法说明：设置标签原点坐标
 * @param x  横坐标
 * @param y  纵坐标
 */
-(void) addReference:(int) x :(int)y;

/**
 * 方法说明：设置打印速度
 * @param speed  打印速度
 */
-(void) addSpeed:(int) speed;

/**
 * 方法说明：设置打印浓度
 * @param density  浓度
 */
-(void) addDensity:(int) density;

/**
 * 方法说明：设置打印方向
 * @param direction  方向
 */
-(void) addDirection:(int) direction;

/**
 * 方法说明：清除打印缓冲区
 */
-(void) addCls;

/**
 * 方法说明:在标签上绘制文字
 * @param x 横坐标
 * @param y 纵坐标
 * @param font  字体类型
 * @param rotation  旋转角度
 * @param xScal  横向放大
 * @param yScal  纵向放大
 * @param text   文字字符串
 */
-(void) addTextwithX:(int)x withY:(int)y withFont:(NSString*)font withRotation:(int)rotation withXscal:(int)xScal withYscal:(int)yScal withText:(NSString*) text;

/*
 BITMAP X, Y, width, height, mode, bitmap data
 参 数 说 明
 x 点阵影像的水平启始位置
 y 点阵影像的垂直启始位置
 width 影像的宽度，以 byte 表示
 height 影像的高度，以点(dot)表示
 mode 影像绘制模式
 0 OVERWRITE
 1 OR
 2 XOR
 bitmap data 影像数据
 */
-(void) addBitmapwithX:(int)x withY:(int) y withWidth:(int) width withHeight:(int) height withMode:(int) mode withData:(NSData*) data;

-(void)addBitmapwithX:(int)x withY:(int)y withMode:(int)mode withWidth:(int)width withImage:(UIImage *)image;

-(void)addBitmapwithX:(int)x withY:(int)y withMode:(int)mode withImage:(UIImage *)image;

/**
 * 方法说明:在标签上绘制一维条码
 * @param x 横坐标
 * @param y 纵坐标
 * @param barcodeType 条码类型
 * @param height  条码高度，默认为40
 * @param readable  是否可识别，0:  人眼不可识，1:   人眼可识
 * @param rotation  旋转角度，条形码旋转角度，顺时钟方向，0不旋转，90顺时钟方向旋转90度，180顺时钟方向旋转180度，270顺时钟方向旋转270度
 * @param narrow 默认值2，窄 bar  宽度，以点(dot)表示
 * @param wide 默认值4，宽 bar  宽度，以点(dot)表示
 * @param content   条码内容
 BARCODE X,Y,"code type",height,human readable,rotation,narrow,wide,"code"
 BARCODE 100,100,"39",40,1,0,2,4,"1000"
 BARCODE 10,10,"128",40,1,0,2,2,"124096ABCDEFZ$%+-./*"
 "code type":
 EAN13("EAN13"),
 EAN8("EAN8"),
 UPCA("UPCA"),
 ITF14("ITF14"),
 CODE39("39"),
 CODE128("128"),
 */
-(void) add1DBarcode:(int)x :(int)y :(NSString*)barcodeType :(int)height :(int)readable :(int)rotation :(int)narrow :(int)wide :(NSString*)content;

/**
 * 方法说明:在标签上绘制QRCode二维码
 * @param x 横坐标
 * @param y 纵坐标
 * @param ecclever 选择QRCODE纠错等级,L为7%,M为15%,Q为25%,H为30%
 * @param cellwidth  二维码宽度1~10，默认为4
 * @param mode  默认为A，A为Auto,M为Manual
 * @param rotation  旋转角度，QRCode二维旋转角度，顺时钟方向，0不旋转，90顺时钟方向旋转90度，180顺时钟方向旋转180度，270顺时钟方向旋转270度
 * @param content   条码内容
 * QRCODE X,Y ,ECC LEVER ,cell width,mode,rotation, "data string"
 * QRCODE 20,24,L,4,A,0,"佳博集团网站www.Gprinter.com.cn"
 */
-(void) addQRCode:(int)x :(int)y :(NSString*)ecclever :(int)cellwidth :(NSString*)mode :(int)rotation :(NSString*)content;

/**
 * 方法说明：执行打印
 * @param m
 * @param n
 */
-(void) addPrint:(int) m :(int) n;

/**
 * 方法说明:获得打印命令
 */
-(NSData*) getCommand;

/**
 * 方法说明：将字符串转成十六进制码
 * @param  str  命令字符串
 */
-(void) addStrToCommand:(NSString *) str;

-(void) addNSDataToCommand:(NSData*) data;

/**
 * 方法说明：发送一些TSC的固定命令，在cls命令之前发送
 */
-(void) addComonCommand;

/**
 * 方法说明:打印自检页，打印测试页
 */
-(void) addSelfTest;

/**
 * 方法说明 :查询打印机型号
 */
-(void) queryPrinterType;

/**
 * 方法说明:设置打印机剥离模式
 * @param peel ON/OFF  是否开启
 */
-(void) addPeel:(NSString *) peel;

/**
 * 方法说明:设置打印机撕离模式
 * @param tear ON/OFF 是否开启
 */
-(void) addTear:(NSString *) tear;

/**
 * 方法说明:设置切刀是否有效
 * @param cutter 是否开启 OFF/pieces (0<=pieces<=127)设定几张标签切一次
 */
-(void) addCutter:(NSString *) cutter;

/**
 * 方法说明:设置切刀半切是否有效
 * @param cutter  是否开启
 */
-(void) addPartialCutter:(NSString *) cutter;

/**
 * 方法说明：设置蜂鸣器
 * @param level 频率
 * @param interval  时间ms
 */
-(void) addSound:(int) level :(int) interval;

/**
 * 方法说明：打开钱箱命令,CASHDRAWER m,t1,t2
 * @param m  钱箱号 m      0，48  钱箱插座的引脚2        1，49  钱箱插座的引脚5
 * @param t1   高电平时间0 ≤ t1 ≤ 255输出由t1和t2设定的钱箱开启脉冲到由m指定的引脚
 * @param t2   低电平时间0 ≤ t2 ≤ 255输出由t1和t2设定的钱箱开启脉冲到由m指定的引脚
 */
-(void) addCashdrawer:(int) m :(int) t1 :(int) t2;

/**
 * 方法说明:在标签上绘制黑块，画线
 * @param x 起始横坐标
 * @param y 起始纵坐标
 * @param width 线宽，以点(dot)表示
 * @param height 线高，以点(dot)表示
 */
-(void) addBar:(int) x :(int) y :(int) width :(int) height;

/**
 * 方法说明:在标签上绘制矩形
 * @param xStart 起始横坐标
 * @param yStart 起始纵坐标
 * @param xEnd 终点横坐标
 * @param yEnd 终点纵坐标
 * @param lineThickness 矩形框线厚度或宽度，以点(dot)表示
 */
-(void) addBox:(int) xStart :(int) yStart :(int) xEnd :(int) yEnd :(int) lineThickness;

/**
 * 方法说明:查询打印机状态<ESC>!?
	*询问打印机状态指令为立即响应型指令，该指令控制字符是以<ESC> (ASCII 27=0x1B, escape字符)为控制字符.!(ASCII 33=0x21),?(ASCII 63=0x3F)
	*即使打印机在错误状态中仍能透过 RS-232  回传一个 byte  资料来表示打印机状态，若回传值为 0  则表示打印
	*机处于正常的状态
 */
-(void) queryPrinterStatus;

/**
 * 方法说明:将指定的区域反向打印（黑色变成白色，白色变成黑色）
 * @param xStart 起始横坐标
 * @param yStart 起始横坐标
 * @param xWidth X坐标方向宽度，dot为单位
 * @param yHeight Y坐标方向高度，dot为单位
 */
-(void) addReverse:(int) xStart :(int) yStart :(int) xWidth :(int) yHeight;

/**
 *  方法说明: 打印机打印完成时，自动返回状态。可用于实现连续打印功能
 *  @param response  自动返回状态  <a>@see Response</a>
 *                  OFF     关闭自动返回状态功能
 *                  ON      开启自动返回状态功能
 *                  BATCH   全部打印完成后返回状态
 */
-(void)addQueryPrinterStatus:(Response)response;

/**
 *  方法说明: 查询打印机电量。
 *  <p>返回值:  31（低电量）；32 (中电量)；33 (高电量)；35 (正在充电)</p>
 *
 */
-(void)queryElectricity;
@end
