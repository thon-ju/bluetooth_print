#import "BluetoothPrintPlugin.h"
#import "ConnecterManager.h"
#import "EscCommand.h"

@interface BluetoothPrintPlugin ()
@property(nonatomic, retain) NSObject<FlutterPluginRegistrar> *registrar;
@property(nonatomic, retain) FlutterMethodChannel *channel;
@property(nonatomic, retain) BluetoothPrintStreamHandler *stateStreamHandler;
@property(nonatomic,strong)NSMutableArray *devices;
@property(nonatomic,strong)NSMutableDictionary *dicts;
@property (strong, nonatomic) IBOutlet UITableView *deviceList;
@end

@implementation BluetoothPrintPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:NAMESPACE @"/methods"
            binaryMessenger:[registrar messenger]];
  FlutterEventChannel* stateChannel = [FlutterEventChannel eventChannelWithName:NAMESPACE @"/state" binaryMessenger:[registrar messenger]];
  BluetoothPrintPlugin* instance = [[BluetoothPrintPlugin alloc] init];

  instance.channel = channel;
    
  // STATE
  BluetoothPrintStreamHandler* stateStreamHandler = [[BluetoothPrintStreamHandler alloc] init];
  [stateChannel setStreamHandler:stateStreamHandler];
  instance.stateStreamHandler = stateStreamHandler;

  [registrar addMethodCallDelegate:instance channel:channel];
}

-(NSMutableArray *)devices {
    if (!_devices) {
        _devices = [[NSMutableArray alloc]init];
    }
    return _devices;
}

-(NSMutableDictionary *)dicts {
    if (!_dicts) {
        _dicts = [[NSMutableDictionary alloc]init];
    }
    return _dicts;
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
  }else if([@"getDevices" isEqualToString:call.method]) {
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
                      [self startScane];
                      NSLog(@"Bluetooth power on");
                      break;
                  case CBCentralManagerStateUnknown:
                  default:
                      break;
              }
          }];
      } else {
          [self startScane];
      }
      
      [Manager scanForPeripheralsWithServices:nil options:nil discover:^(CBPeripheral * _Nullable peripheral, NSDictionary<NSString *,id> * _Nullable advertisementData, NSNumber * _Nullable RSSI) {
          if (peripheral.name != nil) {
              NSLog(@"name -> %@",peripheral.name);
          }
      }];
    result(nil);
  } else if([@"connect" isEqualToString:call.method]) {
    FlutterStandardTypedData *data = [call arguments];
      
    @try {
        
      result(nil);
    } @catch(FlutterError *e) {
      result(e);
    }
  } else if([@"disconnect" isEqualToString:call.method]) {
    NSString *remoteId = [call arguments];
    @try {
        
      result(nil);
    } @catch(FlutterError *e) {
      result(e);
    }
  } 
}

-(void)startScane {
    [Manager scanForPeripheralsWithServices:nil options:nil discover:^(CBPeripheral * _Nullable peripheral, NSDictionary<NSString *,id> * _Nullable advertisementData, NSNumber * _Nullable RSSI) {
        if (peripheral.name != nil) {
            NSLog(@"name -> %@",peripheral.name);
            NSUInteger oldCounts = [self.dicts count];
            [self.dicts setObject:peripheral forKey:peripheral.identifier.UUIDString];
            if (oldCounts < [self.dicts count]) {
                [_deviceList reloadData];
            }
        }
    }];
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
