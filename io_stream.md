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

## 字符集

### ASCii

英文一个字节

编码规则：前面补0，补齐8位

### GBK

英文一个字节，完全兼容ASCii，一定以0开头

编码规则：前面补0，补齐8位

汉字两个字节，高位字节一定以1开头，转换成十进制是一个负数

### Unicode

UTF-16编码规则：用2~4个字节保存

UTF-32编码规则：用4个字节保存

UTF-8编码规则：用1~4个字节保存，ASCII-2，简体中文-3

### 字符流

字符流 = 字节流 + 字符集

Reader、Writer及其子类

方法按字符读写数据

####输入流

1. 创建字符输入流对象（底层：关联文件，并创建缓冲区）
2. 读取数据
   1. 判断缓冲区是否有数据可以读取
   2. 若缓冲区中没有数据：从文件中获取数据，装到缓冲区中，每次尽可能装满。若文件中也没有数据了，返回-1
   3. 缓冲区中有数据：就从缓冲区中读取
3. 关闭资源

#### 输出流

1. 创建字符输出流对象（底层：关联文件，并创建缓冲区）
2. 写数据
   1. 将数据写入缓冲区
   2. 缓冲区已满，则写入本地文件；close方法；flush方法，可以继续写
3. 关闭资源

## 练习

### 练习一—拷贝文件夹

```java
	/*
    拷贝文件夹
     */
    public void test1() throws IOException {
        //1.数据源
        File src = new File("数据源");
        //2.目的地
        File dst = new File("目的地");
        //3.拷贝函数
        copydir(src, dst);

    }

    private void copydir(File src, File dst) throws IOException {
        dst.mkdirs();
        //递归
        //1.进入数据源
        File[] files = src.listFiles();
        //2.遍历数组
        for(File file : files){
            if(file.isFile()){
                //3.判断文件，拷贝
                FileInputStream fis = new FileInputStream(file);
                FileOutputStream fos = new FileOutputStream(new File(dst, file.getName()));
                byte[] bytes = new byte[1024];
                int len;
                while((len = fis.read(bytes)) != -1)
                {
                    fos.write(bytes, 0, len);
                }
                fos.close();
                fis.close();
            }else {
                //4.判断文件夹，递归
                copydir(file, new File(dst, file.getName()));

            }
        }

    }
```

### 练习二—文件的加解密

```java
    /*
    文件的加密,解密操作为对加密文件再进行一次加密
     */
    public void test2() throws IOException {
        //1.创建对象关联原始文件
        FileInputStream fis = new FileInputStream("myFile/trump.png");
        //2.创建对象关联加密文件
        FileOutputStream fos = new FileOutputStream("myFile/trump_ency.png");
        //3.加密处理
        int b;
        while((b = fis.read()) != -1){
            fos.write(b^2);
        }
        //4.释放资源
        fos.close();
        fis.close();
    }
```

### 练习三—修改文件数据

```java
    /*
    修改文件中的数据（对文件中的数据进行排序）
     */
    public void test3() throws IOException {
        //1.读取数据
        FileReader fr = new FileReader("myFile/ccc.txt");
        StringBuilder sb = new StringBuilder();
        int ch;
        while ((ch = fr.read()) != -1){
            sb.append((char)ch);
        }
        fr.close();
        System.out.println(sb);
        //2.排序
        /*
        String str = sb.toString();
        String[] arrstr = str.split("-");
        ArrayList<Integer> list = new ArrayList<>();
        for(String s : arrstr){
            int i = Integer.parseInt(s);
            list.add(i);
        }
        Collections.sort(list);
        System.out.println(list);
        */
        Integer[] array = Arrays.stream(sb.toString()
                        .split("-"))
                .map(Integer::parseInt)
                .sorted()
                .toArray(Integer[]::new);
        //3.写回
        /*
        FileWriter fw = new FileWriter("myFile/ccc.txt");
        for(int i = 0; i < list.size(); i++){
            if(i == list.size() -1){
                fw.write(list.get(i) + "");
            } else{
                fw.write(list.get(i) + "-");
            }
        }
        fw.close();
        */
        FileWriter fw = new FileWriter("myFile/ccc.txt");
        String s = Arrays.toString(array).replace("," , "-");
        String substring = s.substring(1, s.length() - 1);
        fw.write(substring);
        fw.close();
    }
```

## 高级流

### 缓冲流

**字节缓冲流：**底层自带长度为8192字节的缓冲区提高性能，由基本流包装而来，创建时需要关联基本流

例：字节缓冲流拷贝文件

```java
    /*
    字节缓冲流拷贝文件，一次读写一个字节
     */
    public void test4() throws IOException {
        //1.创建缓冲流对象
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream("myFile/ccc.txt"));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("myFile/ccc_copy.txt"));
        //2.循环读写到目的地
        int b;
        while ((b = bis.read()) != -1){
            bos.write(b);
        }
        //3.释放资源
        bos.close();
        bis.close();
    }
```



字节缓冲流提高效率原理：

![](D:\code\java\java_note\653f22c34f58f74815dff90db5d3c5a.png)

**字符缓冲流：**底层自带长度为8192字符的缓冲区提高性能，由基本流包装而来，创建时需要关联基本流。注：对于字符流提升不明显

特有方法：

输入流：public String readline()——读取一行数据，遇到回车换行结束。若无数据可读，返回null

输出流：public void newLine()——跨平台的换行

### 转换流

![](D:\code\java\java_note\11f5a7f39aaf9c23e6ffcb694610eee.png)

#### 基本用法：

1.利用转换流按指定字符编码读取数据：

```java
//1.创建对象并指定字符编码
FileReader fr = new FileReader("myFile/gbk.txt", Charset.forName("GBK"));
//2.读取数据
int ch;
while ((ch = fr.read()) != -1)
   System.out.print((char) ch);
//3.释放资源
fr.close();
```

2.利用转换流按指定字符编码写出数据：

```java
//1.创建输出流对象
FileWriter fw = new FileWriter("", Charset.forName("GBK"));
//2.写出数据
fw.write("你好");
//3.释放资源
fw.close();
```

3.转换文件格式：

```java
FileReader fr = new FileReader("", Charset.forName("GBK"));
FileWriter fw = new FileWriter("", Charset.forName("UTF-8"));
int ch;
while ((ch = fr.read()) != -1)
    fw.write(ch);
fw.close();
fr.close();
```

4.利用字节流读取文件中的数据，每次读一整行：

```java
BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("myFile/gbk.txt"), "GBK"));
String line;
while((line = br.readLine()) != null){
    System.out.println(line);
}
br.close();
```

### 序列化流

![](D:\code\java\java_note\ZOOFBE}2Y3PW2~]S[61G3HI.png)

![](D:\code\java\java_note\S%2N7~129{0%R}0G{J0~$F2.png)

#### 基本用法：

1.利用序列化流将对象写入本地文件：

```java
//1.创建对象
student stu = new student("zhangsan", 23);
//2.创建序列化流的对象/对象操作输出流
ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("myFile/a.txt"));
//3.写出数据
oos.writeObject(stu);
//4.释放资源
oos.close();
//注：对象需要实现Serializable（标记型接口，无抽象方法）
```

2.利用反序列化流读取本地文件中的对象：

```java
//1.创建反序列化流对象
ObjectInputStream  ois = new ObjectInputStream(new FileInputStream("myFile/a.txt"));
//2.读取数据
Object o = ois.readObject();
//3.打印对象
System.out.println(o);
//4.关闭资源
ois.close();
```

注：支持序列化的对象会计算得到一个long类型的版本号，可以手动设置。

注：使用transient关键字标记的成员变量不参与序列化过程。

3.序列化并反序列化多个对象：

```java
/*
将多个自定义对象序列化到文件中，且对象个数不确定
 */

//1.序列化多个对象(利用数组)
student s1 = new student("zhangsan", 23, "南京");
student s2 = new student("lisi", 24, "天津");
student s3 = new student("wangwu", 21, "山东");

ArrayList<student> list = new ArrayList<>();
list.add(s1);
list.add(s2);
list.add(s3);
ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("myFile/a.txt"));
oos.writeObject(list);

oos.close();

//2.反序列化对象
ObjectInputStream ois = new ObjectInputStream(new FileInputStream("myFile/a.txt"));

ArrayList<student> list1 = (ArrayList<student>) ois.readObject();

for (student student : list1) {
    System.out.println(student);
}

ois.close();
```

### 打印流

#### 字节打印流

一般是指:PrintStream, PrintWriter两个类。

1. 只操作文件目的地。不操作数据源
2. 特有的写出方法，可以实现数据原样写出
3. 特有的写出方法，可以实现自动刷新，自动换行

基本用法：

```java
PrintStream ps = new PrintStream(new FileOutputStream("myFile/a.txt"), true, "UTF-8");
ps.println(97);
ps.print(true);
ps.printf("%s伯块", "3");
ps.close();
```

#### 字符打印流

构造方法、成员方法与字节打印流类似。由于字符打印流底层具有缓冲区，需要设置自动刷新。

基本用法：

```java
//1.创建字符打印流的对象
PrintWriter pw = new PrintWriter(new FileWriter("myFile/a.txt"), true);

//2.写出数据
pw.println("你好");
pw.print(true);
pw.printf("%s伯块", "3");

//3.释放资源
pw.close();
```

注：System.out为PrintStream类对象，在虚拟机启动时由虚拟机创建，默认指向控制台。它是系统的标准输出流，不能关闭。

### 压缩流

#### 解压缩流

压缩包里的每一个文件在java中都是一个ZipEntry，解压的本质为将每一个ZipEntry按层级拷贝到目的地中。

1.解压方法：

```java
//1.创建一个解压缩流
ZipInputStream zip = new ZipInputStream(new FileInputStream(src));
//2.获取压缩包里的每一个zipentry对象
ZipEntry entry;
while((entry = zip.getNextEntry()) != null){
    System.out.println(entry);
    if(entry.isDirectory()){
        //文件夹
        File file = new File(dst, entry.toString());
        file.mkdirs();
    } else {
        //文件
        File file = new File(dst, entry.toString());
        FileOutputStream fos = new FileOutputStream(file);
        int b;
        while((b = zip.read()) != -1){
            fos.write(b);
        }
        fos.close();
        zip.closeEntry();
    }
}
zip.close();
```

2.压缩方法：

```java
/*
压缩单个文件
*/

//1.创建压缩流关联压缩包
ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(new File(dst, "a.zip")));
//2.创建zipentry对象
ZipEntry entry = new ZipEntry("a.txt");
//3.把zipentry对象放到压缩包中
zos.putNextEntry(entry);
//4.将src文件中的数据写到压缩包当中
FileInputStream fis = new FileInputStream(src);
int b;
while ((b = fis.read()) != -1){
    zos.write(b);
}
zos.closeEntry();;
zos.close();
```

```java
/*
压缩文件夹
*/
public void test14() throws IOException{
    //1.创建file对象表示要压缩的文件夹
    File src = new File("myFile");
    //2.创建File对象表示压缩包文件
    File dst = new File(src.getName() + ".zip");
    //3.创建压缩流
    ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(dst));
    //4.调用压缩方法
    tozip(src, zos, src.getName());
    //5.释放资源
    zos.close();
}

public void tozip(File src, ZipOutputStream zos, String name) throws IOException {
    File[] files = src.listFiles();
    for (File file : files) {
        if(file.isFile()){
            ZipEntry entry = new ZipEntry(name + "\\" + file.getName());
            zos.putNextEntry(entry);
            FileInputStream fis = new FileInputStream(file);
            int b;
            while((b = fis.read()) != -1){
                zos.write(b);
            }
            fis.close();
            zos.closeEntry();
        } else {
            tozip(file, zos, name + "\\" + file.getName());
        }
    }
}
```

## 配置文件

**Properties**——为hashtable非泛型子类

写入配置文件

```java
//1.创建集合对象
Properties prop = new Properties();

//2.添加对象
//一般只添加字符串
prop.put("aaa", "111");
prop.put("bbb", "222");
prop.put("ccc", "333");
prop.put("ddd", "444");

//3.将数据以键值对的形式写到本地文件中
FileOutputStream fos = new FileOutputStream("myFile\\a.properties");
prop.store(fos, "test");
fos.close();
```

读取配置文件

```Java
//1.创建集合
Properties prop = new Properties();
//2.读取本地配置文件中的数据
FileInputStream fis = new FileInputStream("myFile\\a.properties");
prop.load(fis);
fis.close();
//3.打印集合
System.out.println(prop);
```
