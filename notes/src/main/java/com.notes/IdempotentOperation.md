> 幂等操作时很多在分布式事务（重试机制）、并发操作（并发操作要求状态唯一）等情况下，为了保证操作的正确性、数据和状态的一致性，需要将部分操作设计成幂等，例如状态的变更、数值性操作等

### 一、幂等操作的定义
在编程中，一个幂等操作的特点是其任意多次执行所产生的影响均与一次执行的影响相同。幂等函数，或幂等方法，是指可以使用相同参数重复执行，并能获得相同结果的函数。

### 二、幂等操作实现的技术方案
#### 1.查询操作
查询操作是天生的幂等，查询一次或者多次，在数据不变的情况下结果都是一样的。

#### 2.删除操作
删除操作也是天生幂等，删除多次，最终也就是把数据删除，数据库返回值可能有所不同

#### 3.唯一索引 --- 防止脏数据
- 对要求唯一的标记可以采取唯一值索引的方式（不用主键，因为主键一般在业务中不用做唯一标识，而是面向数据库的唯一）
- 例如对银行账户要求唯一对应，可以将它设立唯一值索引，那在插入操作的时候，有且仅有一条数据能够插入，对于重复插入操作会有异常

#### 4.token机制 --- 主要防止页面数据的重复提交
- 在页面提交中，有时可能是网络因素或者页面响应因素，连续点击表单提交、网络重发或者nginx重发等原因，导致前端请求重试，会导致请求重复提交到后端
- 每次请求头信息需要带上token信息，根据token校验，校验成功删除token，执行操作，返回新token，缓存新token
- 关于缓存token的方案，可以采用redis进行分布式缓存，可以分布式共享，也可以采用JVM本地缓存 
> 注意：redis要用删除操作来判断token，删除成功代表token校验通过，如果用select+delete来校验token，存在并发问题，不建议使用

#### 5.悲观锁
- 悲观锁机制能保证在一个时间点，一个对象只能被一个线程拥有
- 悲观锁使用时一般伴随事务一起使用，数据锁定时间可能会很长

#### 6.乐观锁
- 乐观锁只在数据更新写入的一刻锁住对象，相对于悲观锁，效率要高
- 通过版本号控制数据的操作，能够将不同版本号之间的数据操作隔离

#### 7.分布式锁
- 分布式锁有代表性的就是redis的锁，当redis是分布式结构部署的时候，通过setnx命令进行的操作，就带有分布式锁
- 插入数据或者更新数据，获取分布式锁，然后做操作，之后释放锁，其实就是为了控制多线程并发的操作

#### 8.select +insert

#### 9.状态机幂等
- 在设计单据相关的业务，或者是任务相关的业务，肯定会涉及到状态机(状态变更图)，就是业务单据上面有个状态，状态在不同的情况下会发生变更，一般情况下存在有限状态机，这时候，如果状态机已经处于下一个状态，这时候来了一个上一个状态的变更，理论上是不能够变更的


### 三、面对真实场景，如何设计幂等
- 支付场景
 支付场景一般会是接口请求，可以要求请求内容中带有source来源，seq序列号，source+seq在数据库里面做唯一索引，防止多次付款
- 其余场景后期补充
