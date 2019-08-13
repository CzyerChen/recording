> 非常意外的机会，使用到了Easypoi的模板功能

> 意思是将定制好的模板excel动态填写好数据，并导出

> 目前只是用在导出输入导入模板中使用，但是如果这涉及到自定义的话，能够很好的将定制与数据剥离，这种定制可以交给给专业人员，只要会基本的语法即可，减少数据结果修改带来的维护难度

### 简单的使用步骤
- 使用步骤非常简单，一方面是初始化数据的准备，数据实体类，另一方面就是普通的Excel导出，添加对应的头，通过流的方式写到前端页面
- 步骤一：添加依赖
```text
        <!-- easypoi -->
        <dependency>
            <groupId>cn.afterturn</groupId>
            <artifactId>easypoi-base</artifactId>
            <version>3.2.0</version>
        </dependency>
        <dependency>
            <groupId>cn.afterturn</groupId>
            <artifactId>easypoi-annotation</artifactId>
            <version>3.2.0</version>
        </dependency>
        <dependency>
            <groupId>cn.afterturn</groupId>
            <artifactId>easypoi-web</artifactId>
            <version>3.2.0</version>
        </dependency>
```
- 步骤二：定义Excel实体类的注解
```text
注意必须要有空构造函数，否则会报错“对象创建错误”
```
- 注解@Excel，其他还有@ExcelCollection，@ExcelEntity ，@ExcelIgnore，@ExcelTarget
- @ExcelTarget 用于表示表实体类
- @Excel 用于标志字段
- 步骤三：最重要的就是以上两个注解，其次就是注解内部的一些字段
- 常用的也就是name，表示列名
- width 表示列宽，用于导出的美观性
- 另外就是表示时间类型的databaseFormat
- 具体实体类定义 ：
```text

```
```
java.io.IOException: Failed to read zip entry source
    at org.apache.poi.openxml4j.opc.ZipPackage.<init>(ZipPackage.java:106) ~[poi-ooxml-3.15.jar:3.15]
    at org.apache.poi.openxml4j.opc.OPCPackage.open(OPCPackage.java:342) ~[poi-ooxml-3.15.jar:3.15]
    at org.apache.poi.util.PackageHelper.open(PackageHelper.java:37) ~[poi-ooxml-3.15.jar:3.15]
    at org.apache.poi.xssf.usermodel.XSSFWorkbook.<init>(XSSFWorkbook.java:285) ~[poi-ooxml-3.15.jar:3.15]
    at com.fish.cdc.data.std.controller.DemoController.importData(DemoController.java:51) ~[classes/:?]
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[?:1.8.0_144]
.....................
Caused by: java.io.IOException: ZIP entry size is too large
    at org.apache.poi.openxml4j.util.ZipInputStreamZipEntrySource$FakeZipEntry.<init>(ZipInputStreamZipEntrySource.java:122) ~[poi-ooxml-3.15.jar:3.15]
    at org.apache.poi.openxml4j.util.ZipInputStreamZipEntrySource.<init>(ZipInputStreamZipEntrySource.java:56) ~[poi-ooxml-3.15.jar:3.15]
    at org.apache.poi.openxml4j.opc.ZipPackage.<init>(ZipPackage.java:99) ~[poi-ooxml-3.15.jar:3.15]
    ... 93 more
```
- maven 对于Excel等文件，在打包的时候会遭到损坏，而导致文件读取有问题,无法正常解析，需要配置maven resources
```text
<resources>
        <resource>
            <directory>src/main/resources</directory>
            <filtering>true</filtering>
            <excludes>
                <exclude>**/*.xlsx</exclude>
            </excludes>
        </resource>
        <resource>
            <directory>src/main/resources</directory>
            <filtering>false</filtering>
            <includes>
                <include>**/*.xlsx</include>
            </includes>
        </resource>
</resources>
```
https://stackoverflow.com/questions/25711507/poi-zip-entry-size-is-too-large

https://stackoverflow.com/questions/46796874/java-io-ioexception-failed-to-read-zip-entry-source



- springBoot 使用easypoi，在linux服务器上无法 导出excel问题
https://blog.csdn.net/zx1323/article/details/79401005

