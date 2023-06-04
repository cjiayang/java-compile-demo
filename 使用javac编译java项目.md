[toc]

本文是个练习项目，主要练习javac命令原生编译java项目。在Windows环境操作，使用vscode编辑器。原文参考：https://imshuai.com/using-javac

# 使用javac命令编译java项目

IDE或maven等工具已将Java程序的编译代劳。但工具越高级，隐藏的细节就越多，一旦出现问题就懵逼，归根到底还是基础概念不牢靠。返璞归真，回到最原始的地方 `javac`，会让问题豁然开朗。下面就一步一步演示用 `javac`和 `java`徒手编译运行一个常规工程。

## Hello World练手

首先，写个最简单的HelloWorld类

```
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}

```

打开控制台，执行编译命令

```powershell
javac .\HelloWorld.java
```

可以看到，当前目录生成了HelloWorld.class文件

```powershell
-a----          2023-6-4     15:54            427 HelloWorld.class
-a----          2023-6-4     15:54            129 HelloWorld.java
```

执行 `java`命令，正确打印出Hello, World!

```powershell
PS G:\workspace\vscode\demo\java\compile> java HelloWorld
Hello, World!
```

> **注意：java命令后传的是main函数所在的类名，而不是class文件；java会根据类名自动去找class文件 。**

## 带包名的Hello World

一切都很顺利，我们加一个包名 `tech.jyou.compiledemo`

```powershell
mkdir -p tech/jyou/compiledemo
```

创建HelloWorld文件

```java
package tech.jyou.compiledemo;

public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

执行编译

```powershell
javac .\tech\jyou\compiledemo\HelloWorld.java
```

可以看到在 `tech.jyou.compiledemo`目录下生成了 `HelloWorld.class文件`

```powershell
PS G:\workspace\vscode\demo\java\compile> ls .\tech\jyou\compiledemo\
-a----          2023-6-4     16:42            449 HelloWorld.class
-a----          2023-6-4     16:34            163 HelloWorld.java
```

我们直接执行 `java`命令，发现无法加载到类

```powershell
PS G:\workspace\vscode\demo\java\compile> java .\tech\jyou\compiledemo\HelloWorld                                
错误: 找不到或无法加载主类 .\tech\jyou\compiledemo\HelloWorld
```

想了想，**HelloWorld已经有自己的包名了，所以它的名字不在是没有姓氏**的 `HelloWorld`，新名字叫 `tech.jyou.compiledemo.HelloWorld`，那么传给 `java`自然要用新名字，再试一试

```powershell
PS G:\workspace\vscode\demo\java\compile> java .\tech\jyou\compiledemo\tech.jyou.compiledemo.HelloWorld
错误: 找不到或无法加载主类 .\tech\jyou\compiledemo\tech.jyou.compiledemo.HelloWorld
```

依旧报错，不绕弯了，直接执行正确的命令：

```powershell
PS G:\workspace\vscode\demo\java\compile> java tech.jyou.compiledemo.HelloWorld
Hello, World!
```

或者：

```powershell
PS G:\workspace\vscode\demo\java\compile> java -cp 'G:\workspace\vscode\demo\java\compile' tech.jyou.compiledemo.HelloWorld
Hello, World
```

**上面的步骤，说明了两点：**

1. 增加了package名，所以class名也变了，行不改名坐不改姓，自然要带上姓（即全限定名）。
2. Java  **会根据包名对应出目录结构，并从class path搜索该目录去找class文件** 。由于默认的class path是当前目录，所以 `tech.jyou.compiledemo.HelloWorld`必须存储在 `.\tech\jyou\compiledemo\HelloWorld.class`

## 存在依赖的多个文件编译

包名解决了，我们再加个依赖调用

首先，抽取一个 `HelloService`：

```powershell
package tech.jyou.compiledemo.service;

public class HelloService {
    public void printHello() {
        System.out.println("Hello, World!");
    }
}
```

然后，修改 `HelloWorld.java`，完成say hello：

```powershell
package tech.jyou.compiledemo;

import tech.jyou.compiledemo.service.HelloService;

public class HelloWorld {
    public static void main(String[] args) {
        HelloService hService = new HelloService();
        hService.printHello();
    }
}
```

依次执行编译和运行，发现一切正常

```powershell
PS G:\workspace\vscode\demo\java\compile> javac .\tech\jyou\compiledemo\service\HelloService.java
PS G:\workspace\vscode\demo\java\compile> javac .\tech\jyou\compiledemo\HelloWorld.java
PS G:\workspace\vscode\demo\java\compile> java tech.jyou.compiledemo.HelloWorld
Hello, World!
```

直觉上，要先编译 `HelloService.java`，再编译 `HelloWorld.java`，我们能否直接编 `HelloWorld.java`呢？

```powershell
javac .\tech\jyou\compiledemo\HelloWorld.java
```

一切正常

以上说明 `javac`命令可以自动解决编译依赖关系。

## 源文件(src)和编译文件(target)分离

可以看到 **class文件生成到了与java文件相同的目录里** 。class文件和java源文件放在一起，很不清爽，能否像IDE里那样：java文件放到src目录，class文件放到target目录？下面我们试一试。

先创建src和target目录，并将原来的java文件都移动到src目录：

```
PS G:\workspace\vscode\demo\java\compile> mkdir src
PS G:\workspace\vscode\demo\java\compile> mkdir target
PS G:\workspace\vscode\demo\java\compile> mv tech src
```

然后编译，`-d`参数指定到target目录，可以看到，正常编译到target目录下。

```powershell
PS G:\workspace\vscode\demo\java\compile> javac -d .\target .\src\tech\jyou\compiledemo\HelloWorld.java .\src\tech\jyou\compiledemo\service\HelloService.java
```

怎么运行呢？直接在当前目录运行是不行了，毕竟多了一层target目录，进入target目录运行，妥妥的：

```powershell
PS G:\workspace\vscode\demo\java\compile\target> java tech.jyou.compiledemo.HelloWorld  
Hello, World!
```

除了进入 `target`目录以外，更常用的方法是通过 `-classpath`（或简写为 `-cp`）选项设置 **类路径** ：

```powershell
PS G:\workspace\vscode\demo\java\compile> java -cp .\target\ tech.jyou.compiledemo.HelloWorld
Hello, World!
```

## 类路径CLASSPATH

上面演示了通过 `-cp`设置类路径。下面再进一步研究一下类路径。

类路径，是JRE搜索用户级class文件或其他资源的路径，`javac`或 `java`等工具都可以指定类路径。如果没有设置，默认的类路径就是当前目录。 **但如果设置了类路径，默认值就被覆盖了，所以如果想保留当前目录为类路径，需要同时将 `.`加入** ，有点像默认构造函数的感觉。

类路径，可以通过环境变量 `CLASSPATH`或 `-cp`参数设置，后者会覆盖前者。推荐通过 `-cp`设置，它只会影响当前进程。

类路径类似操作系统里的 `path`概念，不过它是java工具搜索class文件的路径。同样的，类路径可以是多个，并通过分号分隔：

```
export CLASSPATH=path1:path2:...
```

或者：

```
sdkTool -classpath path1:path2:...
```

sdkTool可以是 java, javac, javadoc等。

类路径不仅可以是目录，还也可以是jar包或zip包。

类路径的设置是有顺序的，java会优先在靠前的类路径里搜索。这一点和操作系统的 `path`类似。

类路径可以用通配符 `*`匹配jar或zip，但

1. 通配符只匹配jar或zip，比如path/*只是将下面的jar或zip加入类路径，但path本身不加入类路径。
2. 通配符不递归搜索，即指匹配第一层目录下的jar或zip。
3. 通配符匹配到的jar或zip，加入到classpath的顺序是不确定的。因此，更稳妥的做法是显示的枚举所有jar或zip。
4. 通配符适用于 `CLASSPATH`变量或 `-cp`参数，但不适用于jar包的manifest文件。

## 真实项目-依赖三方包(classpath)

project里的java项目有较多的包结构，`还有jar包依赖`，如何编译呢？

```
├── lib
│   ├── logback-classic-1.2.3.jar
│   ├── logback-core-1.2.3.jar
│   └── slf4j-api-1.7.26.jar
├── resources
│   └── logback.xml
├── src
│   └── tech
│       └── jyou
│           └── compiledemo
│               ├── HelloWorld.java
│               └── service
│                   ├── IGreetingService.java
│                   └── impl
│                       ├── AlienGreetingService.java
│                       ├── CatGreetingService.java
│                       ├── DogGreetingService.java
│                       └── HumanGreetingService.java
└── target
```

最直接的办法，跟刚才一样，只不过体力活多一些，要枚举所有的java文件，同时使用通配符将lib下的jar加入类路径：

```
PS G:\workspace\vscode\demo\java\compile\project> javac -cp '.\lib\*' `
>>  -d target `
>> .\src\tech\jyou\compiledemo\HelloWorld.java `
>> .\src\tech\jyou\compiledemo\service\IGreetingService.java `
>> .\src\tech\jyou\compiledemo\service\impl\*.java `
```

编译成功，`java`命令运行一下（注意：target和lib下的jar都需要加入类路径，Windows环境用";"分隔）：

```

PS G:\workspace\vscode\demo\java\compile\project> java -cp "target;lib/*" 'tech.jyou.compiledemo.HelloWorld'
21:49:31.052 [main] ERROR tech.jyou.compiledemo.HelloWorld - Nobody at home!
```

## javac语法

Oracle的[官方文档介绍 `javac`如下](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/javac.html)：

> Reads Java class and interface definitions and compiles them into bytecode and class files.

javac的语法如下：

```
javac [ options ] [ sourcefiles ] [ classes] [ @argfiles ]
```

* options：是一些参数，比如-cp，-d
* sourcefiles：就是编译的java文件，如 `HelloWorld.java`，可以是多个，并用空格隔开
* classes：用来处理处理注解。暂时没搞懂怎么用
* @argfiles，就是包含option或java文件列表的文件路径，用@符号开头，就像上面的@javaOptions.txt和@javaFiles.txt

## 总结

命令还是用Linux的舒服，Windows下还是有很多不兼容，可能还是不熟悉的缘故，不过想想，工具还是得用熟悉的，不然坑多。

将 `javac`的基本用法总结如下：

1. `-cp`参数设置类路径，基本用法是将编译时依赖的jar包加入类路径。并可用 `*`通配jar包。
2. `-d` 参数用来设置class文件编译到单独目录，并根据包名创建子目录。
3. 理论上将java文件的路径全部传给 `javac`即可，但操作上，可以通过find命令将文件列表输出到文件中，通过@argfiles参数传递。
