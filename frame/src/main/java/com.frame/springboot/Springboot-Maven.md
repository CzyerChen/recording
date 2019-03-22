- 用了很久但是不知所以然，有空可以了解一下它打包的原理
- 首先我们先看一下流程

### 基本打包步骤
- 纯maven项目
```text
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>2.1.1.RELEASE</version>
            </plugin>
        </plugins>
    </build>
</project>
```
- 但是我们偶尔会添加我们需要的一些外部包
```text
 <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>2.1.1.RELEASE</version>
                <configuration>
                    <includeSystemScope>true</includeSystemScope>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                           <goal>repackage</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```
- 对于需要加载前端素材
- 对于Lombok mapstruct的特殊处理


### springboot maven打包原理
