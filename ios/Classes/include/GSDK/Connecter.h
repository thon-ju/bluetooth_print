//
//  Connecter.h
//  GSDK
//
//  Created by 猿史森林 on 2018/6/16.
//  Copyright © 2018年 Smarnet. All rights reserved.
//
#import <Foundation/Foundation.h>
#import "ConnecterBlock.h"

@interface Connecter:NSObject

//读取数据
@property(nonatomic,copy)ReadData readData;
//连接状态
@property(nonatomic,copy)ConnectDeviceState state;

/**
 * 方法说明: 连接
 */
-(void)connect;

/**
 *  方法说明: 连接到指定设备
 *  @param connectState 连接状态
 */
-(void)connect:(void(^)(ConnectState state))connectState;

/**
 * 方法说明: 关闭连接
 */
-(void)close;

/**
 *  发送数据
 *  向输出流中写入数据
 */
-(void)write:(NSData *)data receCallBack:(void(^)(NSData *data))callBack;
-(void)write:(NSData *)data;

/**
 *  读取数据
 *  @parma data 读取到的数据
 */
-(void)read:(void(^)(NSData *data))data;

@end
