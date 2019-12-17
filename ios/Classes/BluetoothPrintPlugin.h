#import <Flutter/Flutter.h>
#import <CoreBluetooth/CoreBluetooth.h>

#define NAMESPACE @"bluetooth_print"

@interface BluetoothPrintPlugin : NSObject<FlutterPlugin, CBCentralManagerDelegate, CBPeripheralDelegate>
@end

@interface FlutterBlueStreamHandler : NSObject<FlutterStreamHandler>
@property FlutterEventSink sink;
@end