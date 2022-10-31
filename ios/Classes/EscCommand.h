//
//  EscCommand.h
//  Gprinter
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface EscCommand : NSObject

/**
 * 方法说明：打印UPCA条码
 * @param content 数据范围0-9，长度为11位
 */
-(void) addUPCAtest:(NSString*) content;

/**
 * 方法说明：插入文字
 * @param text 文本
 */
-(void) addText:(NSString*) text;

/**
 * 方法说明:获得打印命令
 * @return NSData*
 */
-(NSData*) getCommand;

/**
 * 方法说明：将字符串转成十六进制码
 * @param  str  命令字符串
 */
-(void) addStrToCommand:(NSString *) str;

/**
 * 方法说明：打印机初始化，必须是第一个打印命令0x1b,0x40
 */
-(void) addInitializePrinter;

/**
 * 方法说明：打印并且走纸多少行，默认为8行，打印完内容后发送
 * @param n 行数
 */
-(void) addPrintAndFeedLines:(int) n;

/**
 *  方法说明：换行
 */
-(void)addPrintAndLineFeed;

/**
 * 方法说明：设置打印模式，0x1B 0x21 n(0-255)，根据n的值设置字符打印模式
 *@param n 二进制默认为00000000(0X0),10001000(0X88)表示下划线和加粗，00001000(0X08)表示加粗，10000000(0X80)表示下划线
 *
 * Set the print mode, where n can be either:
 *   0   (00000000) means default
 *   8   (00001000) means bold
 *   128 (10000000) means underline
 *   136 (10001000) means underline and bold
 */
-(void) addPrintMode:(int) n;

/**
 * 方法说明：设置国际字符集，默认为美国0
 * @param n 字符集编号
 */
-(void) addSetInternationalCharacterSet:(int) n;
/**
 * 方法说明：设置字符是否旋转90°，默认为0
 * @param n 是否旋转
 */
-(void) addSet90ClockWiseRotatin:(int) n;

/**
 * 方法说明：设置对齐方式
 * @param n 左 中 右对齐，0左对齐,1中间对齐,2右对齐
 */
-(void) addSetJustification:(int) n;

/**
 * 方法说明：开钱箱
 * @param m  钱箱引脚号
 * @param t1          高电平时间
 * @param t2          低电平时间
 */
-(void) addOpenCashDawer:(int) m :(int) t1 :(int) t2;

/**
 * 方法说明：蜂鸣器
 * @param m  报警灯和鸣叫次数
 * @param t  时间
 * @param n  方式
 */
-(void) addSound:(int) m :(int) t :(int) n;

/**
 * 方法说明：设置行间距，不用设置，打印机默认为30
 * @param n  行间距高度，包含文字
 */
-(void) addLineSpacing:(int) n;

/**
 * 方法说明：设置倒置模式 选择/取消倒置打印模式。
 * @param n  是否倒置，默认为0， 0 ≤n ≤255 当n的最低位为0时，取消倒置打印模式。当n的最低位为1时，选择倒置打印模式。只有n的最低位有效；
 */
-(void) addSetUpsideDownMode:(int) n;

/**
 * 方法说明：设置字符放大，限制为不放大和放大2倍，n=0x11
 * @param n = width | height 宽度放大倍数，0 ≤n ≤255 （1 ≤ 纵向放大倍数 ≤8，1 ≤ 横向放达倍数 ≤8）[描述]   用0 到2 位选择字符高度，4 到7 位选择字符宽度
 *
 * Set character size, where n can be either:
 *   0  (0000) means default
 *   2  (0010) means double height
 *   16 (1000) means double width
 *   18 (1010) means double height and width
 */
-(void) addSetCharcterSize:(int) n;

/**
 * 方法说明：设置反白模式，黑白反显打印模式
 * @param n  是否反白，当n的最低位为0时，取消反显打印模式。当n的最低位为1时，选择反显打印模式。只有n的最低位有效；
 */
-(void) addSetReverseMode:(int) n;


/**
 * 方法说明：打印机实时状态请求
 * @param n  PRINTER_STATUS  打印机状态 1 ≤n ≤4
 *                PRINTER_OFFLINE 脱机状态
 *                PRINTER_ERROR   错误状态
 *                PRINTER_PAPER   纸张状态
 */
-(void) queryRealtimeStatus:(int) n;

/**
 * 方法说明：设置切纸后走纸
 * @param n  走纸距离
 */
-(void) addCutPaperAndFeed:(int) n;

/**
 * 方法说明：切纸 0全切纸，1是半切
 */
-(void) addCutPaper:(int) m;
/**
 * 方法说明：设置条码可识别字符，选择HRI字符的打印位置
 * @param n  可识别字符位置，0, 48  不打印 1, 49  条码上方 2, 50  条码下方 3, 51  条码上、下方都打印
 */
-(void) addSetBarcodeHRPosition:(int) n;

/**
 * 方法说明：设置条码字符种类，选择HRI使用字体
 */
-(void) addSetBarcodeHRIFont;
/**
 * 方法说明：设置条码高度
 * @param n 高度 条码高度为n点，默认为40
 */
-(void) addSetBarcodeHeight:(int) n;

/**
 * 方法说明：设置条码单元宽度，不用设置，使用打印机内部默认值
 * @param n 条码宽度 2 ≤n ≤6
 */
-(void) addSetBarcodeWidth:(int) n;

/**
 * 方法说明：打印EAN13码
 * @param content 数据范围0-9，长度为12位
 */
-(void) addEAN13:(NSString*) content;

/**
 * 方法说明：打印EAN8码
 * @param content 数据范围0-9，长度为7位
 */
-(void) addEAN8:(NSString*) content;

/**
 * 方法说明：打印UPCA条码
 * @param content 数据范围0-9，长度为11位
 */
-(void) addUPCA:(NSString*) content;

/**
 * 方法说明:打印ITF14条码
 * @param content 数据范围 0-9   数据长度   偶数个
 */
-(void) addITF:(NSString*) content;

/**
 * 方法说明:打印CODE39条码
 * @param content  数据范围0-9 A-Z SP $ % + - . / ，*为 (开始/结束字符)
 */
-(void) addCODE39:(NSString*) content;


/**
 * 方法说明：打印CODE128码
 * 采用的是1D 6B 49 n的命令格式，n为后面所有打印字符串的长度，包括了字符集的声明。默认使用CODEB字符集:  {B , 0x7B, 0x42
 * @param charset  ,CODEB字符集有 {A  {B  {C，charset默认值用B ，实际上，字符集可以插入到content中，一个content可以有多个字符集定义。
 * @param content 数据范围0x00-0x7f
 */
-(void) addCODE128:(char) charset :(NSString*) content;

/**
 * 方法说明：打印CODE128码支持混合{A {B {C打印，支持58mm纸打印超过14位的奇数数字，例如15位 17位 19位的数字串
 * 采用的是1D 6B 49 n的命令格式，n为后面所有打印字符串的长度，包括了字符集的声明。默认使用CODEB字符集:  {B , 0x7B, 0x42
 * CODEB字符集有 {A  {B  {C，默认值用B ，实际上，字符集可以插入到data中，一个data可以有多个字符集定义。
 * @param height 条码高度
 * @param width 条码宽度
 * @param data 数据范围0x00-0x7f，全部为16进制
 */
-(void) addCODE128ABC:(int) height :(int) width :(NSData*) data;


/**
 * 方法说明：addNVLOGO
 * @param n 序号，1<=n<=20 m默认为0
 */
-(void) addNVLOGO:(int) n :(int) m;

/*
 十六进制码 1D 76 30 m xL xH yL yH d1...dk
 0 ≤ m ≤ 3, 48 ≤ m ≤ 51
 0 ≤ xL ≤ 255
 0 ≤ xH ≤ 255
 0 ≤ yL ≤ 255
 0 ≤ yH ≤ 255
 0 ≤ d ≤ 255
 k = ( xL + xH × 256) × ( yL + yH × 256) ( k ≠ 0)
 参 数 说 明
 m 模式
 0, 48 正常
 1, 49 倍宽
 2, 50 倍高
 3, 51 倍宽、倍高
 xL、 xH表示水平方向位图字节数（ xL+ xH × 256）
 yL、 yH表示垂直方向位图点数（  yL+ yH × 256）
 data 影像数据
 */
-(void) addESCBitmapwithM:(int) m withxL:(int) xL withxH:(int) xH withyL:(int) yL
                   withyH:(int) yH withData:(NSData*) data;

/**
 *  方法说明：打印光栅位图
 *  @param image 图片
 *  @param width 图片宽度
 */
-(void)addOriginrastBitImage:(UIImage *)image width:(int)width;

/**
 *  方法说明：打印光栅位图
 *  @param image 图片
 */
-(void)addOriginrastBitImage:(UIImage *)image;

/*
 正确的ESC指令下QRCode打印流程为四步：
 1、设定QRCode大小；（可以省略）
 2、设定纠错等级；（一般无需设定，忽略）
 3、将QRCode对应的文字信息存入打印机缓存中；（必须要有）
 4、发送打印QRCode命令。
 一共有四个命令对应上述四步。
 */
/*
 1、设定QRCode大小；（可以省略）
 [格式] ASCII码 GS ( K pL pH cn fn n
 十六进制码 1D 28 6B 03 00 31 43 n
 [范围] (pL+pH×256)=3 (pL=3,pH=0)
 cn=49
 fn=67
 1 ≤n≤16
 [默认值] n=3
 */
-(void) addQRCodeSizewithpL:(int) pL withpH:(int) pH withcn:(int) cn
                    withyfn:(int) fn withn:(int) n;

/*
 2、设定纠错等级；（一般无需设定，忽略）
 [格式] ASCII码 GS ( K pL pH cn fn m d1...dk
 十六进制码 1D 28 6B 03 00 31 45 n
 [范围] (pL+pH×256)=3 (pL=3,pH=0)
 cn=49
 fn=69
 48≤n≤51
 [默认值] n=48
 */
-(void) addQRCodeLevelwithpL:(int) pL withpH:(int) pH withcn:(int) cn
                     withyfn:(int) fn withn:(int) n;

/*
 3、将QRCode对应的文字信息存入打印机缓存中；（必须要有）
 [格式] ASCII码 GS ( K pL pH cn fn m d1...dk
 十六进制码 1D 28 6B pL pH 31 50 30 d1...dk
 [范围] 4≤(pL+pH×256)≤7092 (0≤pL≤255,0≤pH≤27)
 cn=49
 fn=80
 m=48
 k=(pL+pH×256)-3
 [描述] 存储QR CODE数据（d1...dk)到符号存储区
 [注释] • 将QRCode的数据存储到打印机中
 • 执行esc @或打印机掉电后，恢复默认值
 */
-(void) addQRCodeSavewithpL:(int) pL withpH:(int) pH withcn:(int) cn
                    withyfn:(int) fn withm:(int) m withData:(NSData*) data;

/*
 4、发送打印QRCode命令。
 [格式] ASCII码 GS ( K pL pH cn fn m
 十六进制码 1D 28 6B 03 00 31 51 m
 [范围] (pL+pH×256)=3 (pL=3,pH=0)
 cn=49
 fn=81
 m=48
 [描述] 打印QRCode条码，在发送此命令之前，需通过（ K< Function 180）命令将QRCode数据存储在打印机中。
 */
-(void) addQRCodePrintwithpL:(int) pL withpH:(int) pH withcn:(int) cn
                     withyfn:(int) fn withm:(int) m;

/**
 * 方法说明：设置是否汉字字体
 * @param n  是否倍宽 是否倍高 是否下划线
 */
-(void) addSetKanjiFontMode:(int) n;

/**
 * 方法说明：设置汉字有效
 */
-(void) addSelectKanjiMode;

/**
 * 方法说明：设置汉字下划线
 * @param n 根据 n 的值，选择或取消汉字的下划线：
 * n     功能
 * 0, 48 取消汉字下划线
 * 1, 49 选择汉字下划线（1 点宽）
 * 2, 50 选择汉字下划线（2点宽）
 */
-(void)addSetKanjiUnderLine:(int) n;

/**
 * 方法说明：设置汉字无效
 */
-(void) addCancelKanjiMode;

/**
 * 方法说明：设置字符右间距
 * @param  n  间距长度
 * 0 ≤ n≤255
 * [描述] 设置字符的右间距为[n×横向移动单位或纵向移动单位]英寸。
 * [注释] • 当字符放大时，右间距随之放大相同的倍数。
 * • 此命令设置的值在页模式和标准模式下是相互独立的。
 * • 横向或纵向移动单位由GS P指定。改变横向或纵向移动单位不改变当前右间距。
 * • GS P 命令可改变水平（和垂直）运动单位。但是该值不得小于最小水平移动量，
 * 并且必须为最小水平移动量的偶数单位。
 * • 标准模式下，使用横向移动单位。
 * • 最大右间距是31 .91 毫米（255/203 英寸） 。 任何超过这个值的设置都自动转换为最
 * 大右间距。
 * [默认值] n = 0
 */
-(void) addSetCharacterRightSpace:(int) n;

/**
 * 方法说明：设置汉字左右间距
 * @param n1  左间距 0 ≤ n1 ≤ 255
 * @param n2 右间距 0 ≤ n2 ≤ 255
 */
-(void) addSetKanjiLefttandRightSpace:(int) n1 :(int) n2;


/**
 * 方法说明：设置加粗模式
 * @param n  是否加粗
 * 0 ≤ n ≤ 255
 * [描述] 选择或取消加粗模式
 * 当n的最低位为0时，取消加粗模式。
 * 当n的最低位为1 时，选择加粗模式。
 * [注释] • n只有最低位有效。
 * • ESC ! 同样可以选择/取消加粗模式，最后接收的命令有效。
 * [默认值] n = 0
 */
-(void) addTurnEmphasizedModeOnOrOff:(int) n;

/**
 * 方法说明：设置加重模式
 * @param n  是否加重
 * 0 ≤ n ≤ 255
 * [描述] 选择/取消双重打印模式。
 * • 当n的最低位为0时，取消双重打印模式。
 * • 当n的最低位为1 时，选择双重打印模式。
 * [注释] • n只有最低位有效。
 * • 该命令与加粗打印效果相同。
 * [默认值] n = 0
 */
-(void) addTurnDoubleStrikeOnOrOff:(int) n;

/**
 * 方法说明：发送16进制数据，例如data=0x0A或data=0x0D
 */
-(void) addNSDataToCommand:(NSData*) data;

/**
 *  方法说明：设置水平和垂直单位距离
 *  @param x    水平单位
 *  @param y    垂直单位
 */
-(void)addSetHorAndVerMotionUnitsX:(int)x Y:(int)y;

/**
 *  方法说明：设置绝对打印位置
 *  @param n    与起始打印位置距离
 */
-(void)addSetAbsolutePrintPosition:(int)n;

/**
 * 方法说明：设置打印宽度
 *
 * @param width
 *            打印宽度
 */
-(void)addSetPrintingAreaWidth:(int)width;


/**
 *  方法说明: 票据默认倍高（重启不失效）
 *  @param n1 宽      默认为0，翻倍为1
 *  @param n2 高      默认为0，翻倍为1
 *  @param n3 汉字宽   默认为0，翻倍为1
 *  @param n4 汉字高   默认为0，翻倍为1
 */
-(void)receiptDoubleHeightOrDefaultPrintN1:(int)n1 N2:(int)n2 N3:(int)n3 N4:(int)n4;

/**
 *  方法说明: 设置默认代码页
 *  @param n 代码页 n的参数范围为（0-10，16-32，50-89）
 */
-(void)setDefaultCodePage:(int)n;

/**
 *  方法说明: 波特率
 *  @param baudRate 波特率
 */
-(void)setBaudRate:(int)baudRate;

/**
 *  方法说明: 查询打印机电量。
 *  <p>返回值:  31（低电量）；32 (中电量)；33 (高电量)；35 (正在充电)</p>
 *
 */
-(void)queryElectricity;
@end

