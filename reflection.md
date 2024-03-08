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

通过Field表示成员变量

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

## 获取成员方法

通过Method表示成员方法

```java
//1.获取class字节码对象
Class clazz = Class.forName("myReflection.Student");

//获取公共成员方法，包括从父类继承的方法
var methods = clazz.getMethods();
for (Method method : methods) {
    System.out.println(method);
}

System.out.println("==================");

//获取私有成员方法，不包括继承得到的方法
var declaredMethods = clazz.getDeclaredMethods();
for (Method declaredMethod : declaredMethods) {
    System.out.println(declaredMethod);
}

//获取指定的单一方法
var eat = clazz.getDeclaredMethod("eat", String.class);
System.out.println(eat);

//获取方法的修饰符
var m = eat.getModifiers();
System.out.println(m);

//获取方法的名字
var name = eat.getName();
System.out.println(name);

//获取方法的形参
var parameters = eat.getParameters();
for (Parameter parameter : parameters) {
    System.out.println(parameter);
}

//获取方法抛出的异常
var exceptionTypes = eat.getExceptionTypes();
for (Class<?> exceptionType : exceptionTypes) {
    System.out.println(exceptionType);
}

//方法运行
//参数一：方法的调用者
//参数二：调用方法时传递的实参
Student s= new Student();
eat.setAccessible(true);
//获取方法的返回值
var result = eat.invoke(s, "包子");
System.out.println(result);
```

# 动态代理

无侵入式地给代码增加额外的功能。

代理中为对象要被代理的方法。

被代理的对象和代理要实现同一个接口。

```java
/*
作用：创建一个代理
 */
public class ProxyUtil {
    /*
    给一个明星的对象，创建一个代理
     */
    public static star createProxy(bigStar bigstar){
       star s = (star) Proxy.newProxyInstance(ProxyUtil.class.getClassLoader(), //参数一：指定类加载器，去加载生成的代理类
                new Class[]{star.class}, //参数二：指定接口，用于指定代理类中包含哪些方法
                new InvocationHandler() { //参数三：指定生成的代理对象要干什么事情
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                /*
                参数一：代理对象 参数二：要运行的方法 参数三：向调用方法传递的实参
                 */
                if("sing".equals(method.getName())){
                    System.out.println("准备话筒，收钱");
                } else if ("dance".equals(method.getName())) {
                    System.out.println("准备场地，收钱");
                }
                //调用被代理对象中的方法
                return method.invoke(bigstar, args);
            }
        });
       return s;
    }
}
```

