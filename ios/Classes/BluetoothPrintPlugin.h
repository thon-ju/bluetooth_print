#import <Flutter/Flutter.h>
#import <CoreBluetooth/CoreBluetooth.h>
#import "ConnecterManager.h"

#define NAMESPACE @"bluetooth_print"

@interface BluetoothPrintPlugin : NSObject<FlutterPlugin, CBCentralManagerDelegate, CBPeripheralDelegate>
@property(nonatomic,copy)ConnectDeviceState state;
@end

@interface BluetoothPrintStreamHandler : NSObject<FlutterStreamHandler>
@property FlutterEventSink sink;
@end

typedef enum NSUInteger {
    CharacterSizeEnumDefault = 0,
    CharacterSizeEnumDoubleHeight = 2,
    CharacterSizeEnumDoubleWidth = 16,
    PrintModeEnumDefault = 0,
    PrintModeEnumBold = 8,
    PrintModeEnumUnderline = 128
}TextTypeEnum;
