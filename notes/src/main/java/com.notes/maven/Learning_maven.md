```text
mvnw全名是Maven Wrapper,它的原理是在maven-wrapper.properties文件中记录你要使用的Maven版本，

当用户执行mvnw clean 命令时，发现当前用户的Maven版本和期望的版本不一致，那么就下载期望的版本，

然后用期望的版本来执行mvn命令

```


### 方法一：Pom.Xml中添加Plugin声明
```text
<plugin>
    <groupId>com.rimerosolutions.maven.plugins</groupId>
    <artifactId>wrapper-maven-plugin</artifactId>
    <version>0.0.4</version>
</plugin>

```
- 执行mvn wrapper:wrapper 时，会帮我们生成mvnw.bat、mvnw、maven/maven-wrapper.jar、maven/maven-wrapper.properties


### 方法二：直接执行Goal
- mvn -N io.takari:maven:wrapper -Dmaven=3.6.0


### 注意
```text
1、使用了新的Maven ,如果settings.xml没有放在当前用户下的.m2目录下，那么执行mvnw时不会去读取你原来的settings.xml文件

2、在mvnw.bat中有如下的一段脚本：
if exist "%M2_HOME%\bin\mvn.cmd" goto init，
意思是如果找到mvn.cmd就执行初始化操作，但是Maven早期版本不叫mvn.cmd，而是叫mvn.bat，
所以会报"Error: M2_HOME is set to an invalid directory"错误，
自行修改后缀即可
```