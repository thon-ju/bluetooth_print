#import "BluetoothPrintPlugin.h"
#import "ConnecterManager.h"
#import "EscCommand.h"
#import "TscCommand.h"

@interface BluetoothPrintPlugin ()
@property(nonatomic, retain) NSObject<FlutterPluginRegistrar> *registrar;
@property(nonatomic, retain) FlutterMethodChannel *channel;
@property(nonatomic, retain) BluetoothPrintStreamHandler *stateStreamHandler;
@property(nonatomic) NSMutableDictionary *scannedPeripherals;
@end

@implementation BluetoothPrintPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:NAMESPACE @"/methods"
            binaryMessenger:[registrar messenger]];
  FlutterEventChannel* stateChannel = [FlutterEventChannel eventChannelWithName:NAMESPACE @"/state" binaryMessenger:[registrar messenger]];
  BluetoothPrintPlugin* instance = [[BluetoothPrintPlugin alloc] init];

  instance.channel = channel;
  instance.scannedPeripherals = [NSMutableDictionary new];
    
  // STATE
  BluetoothPrintStreamHandler* stateStreamHandler = [[BluetoothPrintStreamHandler alloc] init];
  [stateChannel setStreamHandler:stateStreamHandler];
  instance.stateStreamHandler = stateStreamHandler;

  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  NSLog(@"call method -> %@", call.method);
    
  if ([@"state" isEqualToString:call.method]) {
    result(nil);
  } else if([@"isAvailable" isEqualToString:call.method]) {
    
    result(@(YES));
  } else if([@"isConnected" isEqualToString:call.method]) {
    
    result(@(NO));
  } else if([@"isOn" isEqualToString:call.method]) {
    result(@(YES));
  }else if([@"startScan" isEqualToString:call.method]) {
      NSLog(@"getDevices method -> %@", call.method);
      [self.scannedPeripherals removeAllObjects];
      
      if (Manager.bleConnecter == nil) {
          [Manager didUpdateState:^(NSInteger state) {
              switch (state) {
                  case CBCentralManagerStateUnsupported:
                      NSLog(@"The platform/hardware doesn't support Bluetooth Low Energy.");
                      break;
                  case CBCentralManagerStateUnauthorized:
                      NSLog(@"The app is not authorized to use Bluetooth Low Energy.");
                      break;
                  case CBCentralManagerStatePoweredOff:
                      NSLog(@"Bluetooth is currently powered off.");
                      break;
                  case CBCentralManagerStatePoweredOn:
                      [self startScan];
                      NSLog(@"Bluetooth power on");
                      break;
                  case CBCentralManagerStateUnknown:
                  default:
                      break;
              }
          }];
      } else {
          [self startScan];
      }
      
    result(nil);
  } else if([@"stopScan" isEqualToString:call.method]) {
    [Manager stopScan];
    result(nil);
  } else if([@"connect" isEqualToString:call.method]) {
    NSDictionary *device = [call arguments];
    @try {
      NSLog(@"connect device begin -> %@", [device objectForKey:@"name"]);
      CBPeripheral *peripheral = [_scannedPeripherals objectForKey:[device objectForKey:@"address"]];
        
      self.state = ^(ConnectState state) {
        [self updateConnectState:state];
      };
      [Manager connectPeripheral:peripheral options:nil timeout:2 connectBlack: self.state];
      
      result(nil);
    } @catch(FlutterError *e) {
      result(e);
    }
  } else if([@"disconnect" isEqualToString:call.method]) {
    @try {
      [Manager close];
      result(nil);
    } @catch(FlutterError *e) {
      result(e);
    }
  } else if([@"print" isEqualToString:call.method]) {
     @try {
       
       result(nil);
     } @catch(FlutterError *e) {
       result(e);
     }
  } else if([@"printReceipt" isEqualToString:call.method]) {
       @try {
         [Manager write:[self escCommand]];
         result(nil);
       } @catch(FlutterError *e) {
         result(e);
       }
  } else if([@"printLabel" isEqualToString:call.method]) {
     @try {
       [Manager write:[self tscCommand]];
       result(nil);
     } @catch(FlutterError *e) {
       result(e);
     }
  }else if([@"printTest" isEqualToString:call.method]) {
     @try {
       
       result(nil);
     } @catch(FlutterError *e) {
       result(e);
     }
  }
}

-(NSData *)tscCommand{
    TscCommand *command = [[TscCommand alloc]init];
    [command addSize:48 :80];
    [command addGapWithM:2 withN:0];
    [command addReference:0 :0];
    [command addTear:@"ON"];
    [command addQueryPrinterStatus:ON];
    [command addCls];
    [command addTextwithX:0 withY:0 withFont:@"TSS24.BF2" withRotation:0 withXscal:1 withYscal:1 withText:@"Smarnet"];
    [command add1DBarcode:30 :30 :@"CODE128" :100 :1 :0 :2 :2 :@"1234567890"];
    [command addQRCode:20 :160 :@"L" :5 :@"A" :0 :@"www.tebibo.com"];
    UIImage *image = [UIImage imageNamed:@"gprinter.png"];
    [command addBitmapwithX:0 withY:260 withMode:0 withWidth:400 withImage:image];
    [command addPrint:1 :1];
    return [command getCommand];
}

-(NSData *)escCommand{
    EscCommand *command = [[EscCommand alloc]init];
    [command addInitializePrinter];
    [command addPrintAndFeedLines:5];
    //内容居中
    [command addSetJustification:1];
    [command addPrintMode: 0|8|16|32];
    [command addText:@"Print text\n"];
    [command addPrintAndLineFeed];
    [command addPrintMode: 0];
    [command addText:@"Welcome to use Smarnet printer!"];
    //换行
    [command addPrintAndLineFeed];
    //内容居左（默认居左）
    [command addSetJustification:0];
    [command addText:@"智汇"];
    //设置水平和垂直单位距离
    [command addSetHorAndVerMotionUnitsX:7 Y:0];
    //设置绝对位置
    [command addSetAbsolutePrintPosition:6];
    [command addText:@"网络"];
    [command addSetAbsolutePrintPosition:10];
    [command addText:@"设备"];
    [command addPrintAndLineFeed];
    NSString *content = @"Gprinter";
    //二维码
    [command addQRCodeSizewithpL:0 withpH:0 withcn:0 withyfn:0 withn:5];
    [command addQRCodeSavewithpL:0x0b withpH:0 withcn:0x31 withyfn:0x50 withm:0x30 withData:[content dataUsingEncoding:NSUTF8StringEncoding]];
    [command addQRCodePrintwithpL:0 withpH:0 withcn:0 withyfn:0 withm:0];
    [command addPrintAndLineFeed];

    [command addSetBarcodeWidth:2];
    [command addSetBarcodeHeight:60];
    [command addSetBarcodeHRPosition:2];
    [command addCODE128:'B' : @"ABC1234567890"];
    
    [command addPrintAndLineFeed];
    
    UIImage *image = [UIImage imageNamed:@"gprinter.png"];
    [command addOriginrastBitImage:image];
    [command addPrintAndFeedLines:5];
    return [command getCommand];
}

-(void)startScan {
    [Manager scanForPeripheralsWithServices:nil options:nil discover:^(CBPeripheral * _Nullable peripheral, NSDictionary<NSString *,id> * _Nullable advertisementData, NSNumber * _Nullable RSSI) {
        if (peripheral.name != nil) {
            
            NSLog(@"find device -> %@", peripheral.name);
            [self.scannedPeripherals setObject:peripheral forKey:[[peripheral identifier] UUIDString]];
            
            NSDictionary *device = [NSDictionary dictionaryWithObjectsAndKeys:peripheral.identifier.UUIDString,@"address",peripheral.name,@"name",nil,@"type",nil];
            [_channel invokeMethod:@"ScanResult" arguments:device];
        }
    }];
    
}

-(void)updateConnectState:(ConnectState)state {
    dispatch_async(dispatch_get_main_queue(), ^{
        NSNumber *ret = @0;
        switch (state) {
            case CONNECT_STATE_CONNECTING:
                NSLog(@"status -> %@", @"连接状态：连接中....");
                ret = @0;
                break;
            case CONNECT_STATE_CONNECTED:
                NSLog(@"status -> %@", @"连接状态：连接成功");
                ret = @1;
                break;
            case CONNECT_STATE_FAILT:
                NSLog(@"status -> %@", @"连接状态：连接失败");
                ret = @0;
                break;
            case CONNECT_STATE_DISCONNECT:
                NSLog(@"status -> %@", @"连接状态：断开连接");
                ret = @0;
                break;
            default:
                NSLog(@"status -> %@", @"连接状态：连接超时");
                ret = @0;
                break;
        }
        
         NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:ret,@"id",nil];
        if(_stateStreamHandler.sink != nil) {
          self.stateStreamHandler.sink([dict objectForKey:@"id"]);
        }
    });
}

@end

@implementation BluetoothPrintStreamHandler

- (FlutterError*)onListenWithArguments:(id)arguments eventSink:(FlutterEventSink)eventSink {
  self.sink = eventSink;
  return nil;
}

- (FlutterError*)onCancelWithArguments:(id)arguments {
  self.sink = nil;
  return nil;
}

@end
