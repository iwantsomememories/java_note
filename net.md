# 网络编程

## 基本架构

C/S：客户端/服务器架构

B/S：浏览器/服务器架构

## 基本要素

- IP：设备在网络中的地址，唯一的标识。

​	IPv4——32bit，4组，IPv6——128bit，8组，冒分16进制

```java
//InetAddress的基本使用
var address = InetAddress.getByName("fanqiechaodan");//主机名或IP地址
System.out.println(address);

String name = address.getHostName();
System.out.println(name);

String ip = address.getHostAddress();
System.out.println(ip);
```

- 端口号：应用程序在设备中唯一的标识。

  0\~65535，其中0\~1024被一些知名网络服务及应用使用

- 协议：数据在网络中传输的规则。

​	**UDP：**面向无连接的协议。速度快，大小限制为64K，数据易丢失。

​	**TCP：**面向连接，速度慢，大小没有限制。

## UDP协议

发送数据：

1. 创建发送端的DatagramSocket对象
2. 数据打包（DatagramPacket）
3. 发送数据
4. 释放资源

```Java
//1.创建发送端的DatagramSocket对象
//绑定端口，
//空参：所有可用端口随机选择一个
//有参：指定端口号进行绑定
DatagramSocket ds = new DatagramSocket();

//2.打包数据
String str = "你好啊";
byte[] bytes = str.getBytes();

var address = InetAddress.getByName("127.0.0.1");

int port = 10086;

DatagramPacket dp = new DatagramPacket(bytes, bytes.length, address, port);

//3.发送数据
ds.send(dp);

//4.释放资源
ds.close();
```

接收数据：

1. 创建接收端的DatagramSocket对象
2. 接收打包好的数据
3. 解析数据包
4. 释放资源

```java
//1.创建接收端的DatagramSocket对象
//接收时必须绑定端口，且一定要与发送端目的端口保持一致
DatagramSocket ds = new DatagramSocket(10086);

//2.接收数据包
byte[] bytes = new  byte[1024];
DatagramPacket dp = new DatagramPacket(bytes, bytes.length);

//该方法是阻塞的（等待发送端发送消息）
ds.receive(dp);

//3.解析数据包
var data = dp.getData();
int length = dp.getLength();
var address = dp.getAddress();
var port = dp.getPort();

System.out.println("接收到数据"+new String(data, 0, length));
System.out.println("数据是从"+address+"这台电脑中的"+port+"这个端口发出的");

//4.释放资源
ds.close();
```

通信方式：

1. 单播

   代码如上

2. 组播

   组播地址为224.0.0.0\~239.255.255.255，其中224.0.0.0\~224.0.0.255为预留的组播地址

   ```Java
   public static void main(String[] args) throws IOException {
       /*
       组播发送端
        */
       //创建MulticastSocket对象
       MulticastSocket ms = new MulticastSocket();
   
       //创建DatagramPacket对象，指定组播地址
       String s = "你好";
       byte[] bytes = s.getBytes();
       InetAddress address = InetAddress.getByName("224.0.0.2");
       int port = 10000;
   
       DatagramPacket dp = new DatagramPacket(bytes, bytes.length, address, port);
   
       //发送数据
       ms.send(dp);
   
       //释放资源
       ms.close();
   }
   public static void main(String[] args) throws IOException {
       /*
       组播接收端
        */
       //创建MulticastSocket对象
       MulticastSocket ms = new MulticastSocket(10000);
   
       //将当前本机，添加到组播接收ip这一组当中
       InetAddress address = InetAddress.getByName("224.0.0.2");
       ms.joinGroup(address);
   
       //创建接收数据包
       byte[] bytes = new byte[1024];
       DatagramPacket dp = new DatagramPacket(bytes, bytes.length);
   
       //接收数据
       ms.receive(dp);
   
       //解析数据
       var data = dp.getData();
       var length = dp.getLength();
       String ip = dp.getAddress().getHostAddress();
       String name = dp.getAddress().getHostName();
   
       System.out.println("ip为："+ip+"，主机名为："+name+"的人，发送了数据："+new String(data, 0, length));
   
       //释放资源
       ms.close();
   }
   ```

3. 广播

   广播地址——255.255.255.255

## TCP协议

它是一种可靠的网路协议，在通信的两端各建立一个Socket对象

通信之前要保证连接已建立

通过Socket产生IO流来进行网络通信

**代码实现：**

客户端向服务器发送数据

客户端：

1. 创建客户端的Socket对象与指定服务端连接
2. 获取输出流，写数据
3. 释放资源

服务器：

1. 创建服务器端的Socket对象
2. 监听客户端连接，返回一个Socket对象
3. 获取输入流，读数据，并把数据显示在控制台
4. 释放资源