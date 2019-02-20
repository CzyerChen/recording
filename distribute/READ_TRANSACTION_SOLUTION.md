分布式事务的解决办法：（借鉴大神的博文在此总结）

### 1.最大努力通知型事务
#### 例子:
调用支付宝支付：我们是被动方，支付宝是主动方，被动方通过扫描二维码，发起支付请求，请求到达支付宝，支付宝通过处理返回处理结果，我们给前端展示支付状态。
如果我方宕机，就可能收不到支付宝的返回结果，支付宝会采用时间衰减策略在24小时以内重复发送支付结果信息，如果超出时间不在发送，需要被动方恢复后主动请求支付结果。

#### 方案成本：
1. 该方案成本较低，主要花费在业务查询与校对系统建设成本。
2. 校对系统是单独的，低耦合性，对账系统、订单系统、平账操作。

#### 适用范围：
1. 适用于对时间敏感性较低的业务，比如充值、转账业务，这边的时间衰减操作和超时主动请求都说明了时间不是很敏感的操作。
2. 适用于跨系统、跨业务、跨网络的服务间数据一致性保证场景。

#### 用到的服务模式为: 
可查询操作，幂等操作。

#### 方案特点：
1. 业务活动的主动方在完成业务处理后,向被动方发送通知消息(允许消息丢失);
2. 主动方可以设置时间阶梯型通知规则,在通知失败后按规则重复发起通知,直到通知N次后不再通知;
3. 主动方提供校对查询接口给被动方,被动方按需校对查询,用于恢复丢失的业务消息;


#### 应用案例
1. 支付渠道接入通知，银行转账通知、商户通知等
2. 对账通知（实时/定时）

#### 实现方法：
1. 通常采用两种方式实现
- 定时任务，使用定时作业轮询发起直接通知，通知数据要做持久化操作。
- 定时消息队列，基于定时消息发起通知，需要对通知请求数据做持久化操作，消息队列有高可用需求。

2. 优化操作：
- 增加业务管理系统，对通知失败数据进行管理，增加手动重复通知操作功能。
- 服务通用化、框架化，剥离通知逻辑与业务逻辑, 对通知队列进行区分, 不同队列使用不同通知规则，并且支持通知策略的可配置性操作等。
- 保证通知服务的可用性,必要时建立独立的数据库，注意通知数据的持久化策略、通知失败后重新发起通知的策略的优化，比如时间衰减策略的设置等。
- 主动方提供的业务查询接口及被动方暴露的通知处理业务接口均要实现幂等性。
- 业务主动方注意内存调优与流量控制。


### 2.TCC---补偿事务  try confirm cancel,是一个P2P的柔性事务处理办法（在分布式事务里面也有提到）
#### Try阶段
首先进行Try阶段，该阶段主要做资源的锁定/预留，设置一个预备的状态，冻结部分数据。
> 例如：(新增一个带增加金额字段，来表示这个交易正在继续，还没有完全完成)本来库存数量是100，要减去5个库存，不能直接100 - 5 = 95，而是要把可销售的库存设置为：100 - 5 = 95，接着在一个单独的库存冻结的字段里，设置一个5。也就是说，有5个库存是给冻结了。此时订单状态为OrderStatus.DEALING

#### Confirm阶段
- 常见的TCC框架，如：ByteTCC、tcc-transaction 均为我们实现了事务管理器。
- 能对各个子模块的try阶段执行结果有所感知。感知执行情况操作较为复杂，可以借助开源框架实现。

例子中
- 订单服务中的CONFIRM操作，是将订单状态更新为支付成功这样的确定状态。
- 库存服务中，加入正式扣除库存的操作，将临时冻结的库存真正的扣除，更新冻结字段为0，并修改库存字段为减去库存后的值。
- 同时积分服务将积分变更为增加积分之后的值，修改预增加的值为0，积分值修改为原值+预增加的100分的和。
- 发货服务也是真实发货后，修改DEALING为已发货。
- 当TCC框架感知到各个服务的TRY阶段都成功了以后，就会执行各个服务的CONFIRM逻辑。
- 各个模块内的TCC事务框架会负责跟其他服务内的TCC事务框架进行通信，依次调用各服务的CONFIRM逻辑。正式完成各服务的完整的业务逻辑的执行。

#### Cancel阶段
- CONFIRM是业务正常执行的阶段，异常分支交给CANCEL阶段执行。
- 当TCC框架感知到任何一个服务的TRY阶段执行失败，就会在和各服务内的TCC分布式事务框架进行通信的过程中，调用各个服务的CANCEL逻辑，将事务进行回滚。

例子：
- 订单服务中，当支付失败，CANCEL操作需要更改订单状态为支付失败。
- 库存服务中的CANCEL操作要将预扣减的库存加回到原库存，也就是可用库存=90+10=100。
- 积分服务要将预增加的100个积分扣除。
- 发货服务的CANCEL操作将发货订单的状态修改为发货取消。


### 3.可靠消息一致性事务---最终一致性
实际开发中，一般会将服务拆分为异步方式，一般是基于MQ进行服务间的解耦，服务发起方执行完本地业务操作后发送一条消息给到消息中间件（比如：RocketMQ、RabbitMQ、Kafka、ActiveMQ等），被动方服务从MQ中消费该消息并进行业务处理，从而形成业务上的闭环。

#### 核心流程 1：上游投递消息
- 调用开始，业务主动方预先发送一条消息到消息服务中，消息中包含后续的业务操作所必须的业务参数，消息服务接收到该消息后存储消息到消息存储中，并设置消息状态为 “待确认”。

- 如果消息存储失败则直接返回消息持久化失败，本次业务操作结束。

- 当主动方接收到消息存储结果后，开始执行本地的业务操作，根据本地事务提交的结果，调用消息服务的接口。
这里分为两种状态：

    - 如果本地事务执行成功，就调用消息服务确认消息状态，更新为“待发送”。

    - 如果本地事务执行失败，就调用消息服务删除消息（一般是逻辑删除，更新消息状态为 已回滚）

    - 当状态为第1种，消息服务就将该消息发送到MQ中，并更新消息状态为 “已发送”。

- 注意：对于消息状态的更新和投递消息到MQ中间件的操作应在同一个方法中，并开启本地事务，需要保证消息发送和本地事务同时成功同时失败。


这里还是有两种情况：

- 如果更新消息状态失败，则应当抛出异常回滚事务，不投递消息到MQ中。

- 如果投递MQ失败，（需要捕获异常），需要主动抛出异常触发本地事务回滚。

- 1.2要同时成功同时失败。

- 当状态是第2种，即本地事务执行失败。

- 业务主动方需要调用可靠消息事务的删除消息操作，消息服务从消息持久化存储中删除该消息（设置消息状态为已回滚）。

#### 核心流程2：被动方应用接收消息
- 被动方服务订阅主题后，等待MQ投递消息。

- 当消息投递，被动方服务消费该消息并执行本地业务操作，当本地业务执行成功，被动方服务调用消息服务，返回本地业务执行成功。

- 可靠消息服务根据业务唯一参数（订单号结合消息id）设置消息状态为 “已完成”。


#### 在整个流程中，需要被动放服务尽可能保证业务向最终状态推进
如果要保证最终状态可达，消息需要保证投递，

1. 保证消息可靠投递
- 主动方应用提交“待确认”消息时出错

解决：主动方会直接感知到提交失败，业务直接返回失败，不处理后续的流程

- 主动方应用执行完成本地事务之后，通知可靠消息服务确认或者删除消息阶段出错：例如通知可靠消息服务失败、本地业务执行异常、可靠消息接收到提交请求后投递消息到MQ中失败等问题，业务卡在中间态，消息持久化状态会一直处于 “待确认” 状态。

- 解决：
   - 这种僵死的状态需要依靠定时任务，定期对这种“待确认”状态相关的任务进行重做，对主动方本地业务执行进行回查操作
   - 业务主动方需要暴露回调查询接口，消息投递服务需要调用对“待确认”任务，如果主动方回复成功，就修改状态为“待发送”，同时投递消息到MQ，并更新消息状态为“已发送”。
    
    - 如果上一步主动方执行结果是失败的话，消息投递服务需要删除该消息（逻辑删除，设置消息状态为已回滚）。
    
需要保证主动方业务执行与消息发送一定同时成功，同时失败。


2.保证业务被动方对消息100%接收成功

- 消息投递成功，但业务被动方消费消息出错，如：消费失败、未收到消息投递（传说中的丢消息）等

- 解决：
    - 方案与上面的解决方案类似，在消息投递端定时任务的方式，检查消息状态。
    - 对长时间处于 “已发送” 未变更状态为 “已完成” 的消息进行重新投递操作，扫描的时间可根据业务执行时间自行调整，比如：1min。


> 对这类型消息重新投递到MQ之后，MQ会推送消息给消费方重新进行业务的处理操作。在业务层实现消费的幂等性，保证同一条消息在多次投递之后，只会进行一次完整的业务逻辑处理。


> 从消息的发送，到消息的消费阶段都能保证消息与本地事务执行状态一致，即使上下游会有短暂的状态不一致，在经过一个处理的时间窗口之后，在全局上，数据能够实现最终一致性。

### 总结

- 保证业务主动方本地事务提交失败，业务被动方不会收到消息的投递。

- 保证只要业务主动方本地事务执行成功，那么消息服务一定会投递消息给下游的业务被动方，并最终保证业务被动方一定能成功消费该消息。