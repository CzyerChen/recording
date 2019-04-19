> 我从之前的学习中认识到了充血模式，一种面向对象的经典模型，但并没有在应用得以很好地实现

> 什么样的问题，真的无法实现嘛？

> [参考文章](https://www.infoq.cn/article/2013/11/spring-web-flaw)

### Spring的现状
- Spring一个即自动配置，依赖注入，动态代理，单一职责原则，关注点分离原则，其他框架适配于一体的框架，是不少开发者的最爱
- 但是它的分层设计包含了web,service,dao,多层，通过庞大的service层进行对象的控制，对象蜕化为简单的数据结构
- Spring中服务层的问题：业务零碎地散落在服务层，查找困难，一个领域对应一个服务层，违反单一职责：单一职责原则表明每个类都应该只有一个职责，这个职责应该完全被这个类所封装。它的所有服务都应该与这个职责保持一致

### 怎么改Spring
- 应用的业务逻辑从服务层移动到领域模型类中:根据合理的方式划分代码的职责。服务层会负责应用的逻辑，而领域模型类则负责业务逻辑
- 应用的业务逻辑只会位于一处。如果需要验证特定的业务规则是如何实现的，我们总是知道该去哪里寻找
- 服务层的源代码将会变得更加整洁，再不会包含任何复制粘贴的代码了
- 将特定于实体的服务划分为更小的服务，每个服务只有一个目标,每个服务类细化到某一对象的操作，不涉及别的对象


### 再利用JPA谈谈数据层
- 刚才有提到，Spring将实体类退化成了简单的数据结构，也有人会使用一些事务脚本来实现领域逻辑，但是这毕竟复杂难以维护，也只是想想就好
- 对于现有Spring去操作数据大致设计流程如下：
```text
设计表结构  ->   
按表建立实体类 ->  
生成每个属性的setter和getter -> 
建立一个service和实现类 -> 
在service的方法中实现业务逻辑，查询、组合、计算实体需要的属性值 -> 
调用实体的setter方法设置值 -> 使用持久化框架提供的方法执行数据最终的持久化
```
- 调用链相当长，我们是为了达到数据操作而包上了这一对封装，而不是我想执行insert而update对象是一个核心了
- 如果简单设计CRUD，可以不谈面向对象，使用这种设计也就能实现了，成本也很低，效率也高
- 但是如果想要做到面向对象，这样是不正确的
- 借用一个例子，用户实体User ,手机号对象Phone,每个用户有一个手机号，手机号可以判断是否符合规范，修改手机号等操作
```text
通常情况下，我们会有一个User,
里面有一个String标识Phone,
然后对于手机号的合法判断和修改操作，都会连着User这个实体去执行，
并且在UserService中去实现，而且每一个需要或者可能判断用户号码合理合法的场景，比如一个是存入，一个是发短信前等等

结合上面所获得的知识，发现用户和手机号两个实体融在了一起，并且手机号这个对象的操作还要连带着用户的其他信息，
手机号的合法判断的方法按道理属于手机号自身，但是被放在不同的服务层结合不同的也去做重复判断，并不合理
```
- 以上结合JPA可以怎么设计呢？
```text
@Entity
@Table(name = "test_table")
public class User {

    @Id
    private int id;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="number", column=@Column(name="手机号字段名", columnDefinition="varchar(20)"))
    })
    private Phone phone;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }
}


@Embeddable
public class Phone {

    private String number;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isValidNumber(){
        if(11 == this.number.length()){
            return true;
        }
        return false;
    }

}
```

### 总结
从JPA的疑问回到JPA的设计，了解了一下DDD CQRS的架构，以及目前的一些微服务，对失血、充血、贫血、胀血的模型也有了一定了解，
对现在业务中用到的贫血模式的优缺点有了一定的了解，对充血模式的面向对象设计也有了新的认识
这一些偏理论的东西，不清楚实际运用会涉及到多少，但是就是有所积累，在日后的考虑和学习中有所借鉴


