# BluetoothServer
经典蓝牙 C/S 基础
#### 请忽略 bluetoothclient 部分这是开始的时候测试用的
## APP 是主要项目
    主要内容在 bluetoothsupport 包内
    BluetoothServer 类 创建一个蓝牙通信的服务端
    BluetoothClient 类 创建一个蓝牙通信的客户端
    BaseBluetoothCS 是上面两个类的父类 负责一些共有的方法 如：蓝牙硬件的检查、权限请求等

    runnables 包内是各种线程的 runnable 实现
    AcceptRunnable 是 BluetoothServer 实现负责处理等待连接请求的线程
    ConnectRunnbale 是 BluetoothClient 实现连接服务端请求的线程
    ListenRunnable 是监听已经建立连接的 socket inputstream 的线程
    以上三个线程都会阻塞
    SendMsgRunnable 是发送文字信息的线程 随用随创建 不阻塞

    BluetootServer/Client 内部持有线程池
    
    
##  结构介绍
    首先建立两端（client 与 server） client 执行 discover 操作 ，然后连接扫描到的 server
    这里通过 BluetoothClient 执行 connectRunnable 获取连接 socket 
    服务端 BluetoothServer 执行 acceptRunnable 等待连接 连接后获取 socket
    在完成连接后 两端分别开启 listenRunnable 来监听socket 的 inputstream 来获取对方发来的信息 ，
    并通过handler 返回主线程 再通过回调接口通知 UI。
    
    
