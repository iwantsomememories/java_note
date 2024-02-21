# IO流

## io流体系

字节流：

- 输入流：inputStream  子类：~+inputStream
- 输出流：outputStream  子类：~+outputStream

### FileOutputStream

####使用过程：

1. 创建对象 
   1. 参数可以是String或者File
   2. 如果文件不存在会创建一个新文件，但是父级路径必须存在
   3. 如果文件已经存在，会清空原文件
2. 写数据
   1. 参数是整数且被视为ASCII码
3. 释放资源
   1. 必须释放



示例：

```java
FileOutputStream fos = new FileOutputStream("myFile/bbb/a.a.a.txt");
fos.write(97);
fos.close();
```

####write用法：

```java
void write(int b) //一次写一个字节
void write(byte[] b) //一次写一个字节数组
void write(byte[] b, int off, int len) //一次写一个字节数组的部分数据，从off开始
```

换行写：输入一个换行符，windows（\r\n）

续写：令append参数为true

### FileInputStream

####使用过程：

1. 创建对象 
   1. 文件不存在则报错
2. 读取数据
   1. 一次读一个字节，结果为ASCII码
   2. 文件末尾返回-1
3. 释放资源
   1. 必须释放

####  read用法：

```java
int read() //一次读一个字节数据
int read(byte[] buffer) //一次读取多个数据，返回值为读取到的数据长度
```

