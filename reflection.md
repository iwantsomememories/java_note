# 反射

## 获取class对象

1. Class.forName("全类名")——源代码阶段获取类的字节码对象
2. 类名.class——加载阶段
3. 对象.getClass()——运行阶段

```java
//1.第一种方式
//包名+类名
Class clazz1 = Class.forName("myReflection.Student");

System.out.println(clazz1);

//2.第二种方式
var clazz2 = Student.class;

System.out.println(clazz1==clazz2);

//3.第三种方式
Student s = new Student();
Class clazz3 = s.getClass();

System.out.println(clazz2==clazz3);
```

## 获取构造方法

通过Constructor表示构造方法

```java
//1.获取class字节码对象
Class clazz = Class.forName("myReflection.Student");

//2.获取构造方法
var cons = clazz.getConstructors();
for (Constructor con : cons) {
    System.out.println(con);
}

System.out.println("==================");

var cons2 = clazz.getDeclaredConstructors();
for (Constructor con : cons2) {
    System.out.println(con);
}

System.out.println("==================");

var con3 = clazz.getDeclaredConstructor();
System.out.println(con3);

System.out.println("==================");

var con4 = clazz.getDeclaredConstructor(int.class);
System.out.println(con4);

System.out.println("==================");

var con5 = clazz.getDeclaredConstructor(String.class, int.class);
System.out.println(con5);

System.out.println("==================");

var m = con5.getModifiers();
System.out.println(m);

System.out.println("==================");

var parameters = con5.getParameters();
for (Parameter parameter : parameters) {
    System.out.println(parameter);
}

//临时取消权限校验
con5.setAccessible(true);
var o = (Student)con5.newInstance("张三", 23);
System.out.println(o);
```

## 获取成员变量

用field对象表示成员变量

```java
//1.获取class字节码对象
Class clazz = Class.forName("myReflection.Student");

//2.获取成员变量
var fields = clazz.getFields();
for (Field field : fields) {
    System.out.println(field);
}

//获取单个成员变量
var f = clazz.getField("gender");
System.out.println(f);

//获取权限修饰符
int m = f.getModifiers();
System.out.println(m);

//获取成员变量名
String n = f.getName();
System.out.println(n);

//获取成员变量类型
Class<?> type = f.getType();
System.out.println(type);

//获取对象成员变量的值
Student s = new Student("zhangsan", 23, "男");
var value = f.get(s);
System.out.println(value);

//修改对象成员变量的值
f.set(s, "女");
System.out.println(s);
```