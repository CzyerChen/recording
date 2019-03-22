- 想做定时任务方法很多，和spring boot 摩擦后，方法依旧很多
- Java自带的Timer类，允许你调度TimerTask任务
- Quartz 一个强大的调度器，比较重，带有很多配置，适合于一个综合定时任务为主的模块
- 如果只是想几个简单的定时任务，完全可以spring boot 自带的schedule，非常简便灵活
- 单线程串行执行，多线程并行执行

### 创建\执行定时任务
#### 1. spring boot schedule
- 定时任务的创建相当简单
- 放在一个被spring容器托管的对象内
- 在一个正常的方法上面使用@Scheduled注解
- 编写cron表达式  cron="0 0/5 * * ?" 代表一个每5分钟执行的任务
- 在Springboot主类上开启定时任务，使用注解@EnableScheduling,注解的作用是发现注解@Scheduled的任务并后台执行
#### 2. Timer
```java
public class TestTimer {
     public static void main(String[] args) {
            //java Timer
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    System.out.println("task  run:"+ new Date());
                }
            };
            Timer timer = new Timer();
            timer.schedule(timerTask,10,3000);
            
            
            //Schedule thread pool
            ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1000, new BasicThreadFactory.Builder().namingPattern("executor-pool-%d").daemon(true).build());
            scheduledExecutorService.scheduleAtFixedRate(()->System.out.println("task ScheduledExecutorService "+new Date()), 0, 3, TimeUnit.SECONDS);
        }
}
```

### 并行任务
- 通过以上配置，就可以开启定时任务了，但是如果想做并行任务呢，因为spring boot默认是串行执行的
- 如果想并行就需要重写任务配置类，继承SchedulingConfigurer,开启一个线程池
```text
@Configuration
@EnableScheduling
public class ScheduleConfig implements SchedulingConfigurer {
 
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
    }
 
    @Bean(destroyMethod="shutdown")
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(100);
    }
}
```

### 异步并行任务
- 如果想实现异步并行的操作，在刚才上面重写的并行的线程池上，加上异步线程池的配置
- 或者使用@EnableAsync @Async 两个注解对峙的那个方法实现异步
```java
@EnableScheduling
@Configuration
@EnableAsync(mode = AdviceMode.PROXY,proxyTargetClass = false,order = Ordered.HIGHEST_PRECEDENCE) //使用java自身的代理，而不是AspectJ
public class AsyncSchedulingConfig implements AsyncConfigurer, SchedulingConfigurer {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler(){
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(100);
        threadPoolTaskScheduler.setThreadNamePrefix("scheduler-");
        threadPoolTaskScheduler.setAwaitTerminationSeconds(60);
        threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        return threadPoolTaskScheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.setTaskScheduler(this.taskScheduler());
    }

    @Override
    public Executor getAsyncExecutor() {
        return this.taskScheduler();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }


    //相对应的APP主类当中
    public static void main(String[] args){
        AnnotationConfigApplicationContext root = new AnnotationConfigApplicationContext();
        root.register(AsyncSchedulingConfig.class);
        root.refresh();
    }
}
```

### 资源的回收/销毁
- 如果当前对象是通过spring初始化，很多情况下我们都是使用这种方式，因而可以尝试一下顺手写上
- spring在卸载（销毁）实例时，会调用实例的destroy方法
- 通过实现DisposableBean接口覆盖destroy方法实现，在destroy方法中主动关闭线程
```java   
public class AsyncSchedulingConfig implements DisposableBean {
 @Override
    public void destroy() {
        WebApplicationContext currentWebApplicationContext = ContextLoader.getCurrentWebApplicationContext();
        if(currentWebApplicationContext != null) {
            ThreadPoolTaskScheduler scheduler = (ThreadPoolTaskScheduler) currentWebApplicationContext.getBean("scheduler");
            scheduler.shutdown();
        }
    }
}
```

### 定时任务的参数
- 参数主要就是跟在@Scheduled 括号里面的东西：

1）cron：cron表达式，指定任务在特定时间执行，规则可以查看[cron表达式的内容](https://github.com/CzyerChen/recording/blob/master/basicjava/src/main/java/com.basic/Cron%E8%A1%A8%E8%BE%BE%E5%BC%8F%E7%9A%84%E8%A7%84%E5%88%99.md))

2）fixedDelay：表示上一次任务执行完成后多久再次执行，参数类型为long，单位ms,这个是跟着上一次任务执行完毕后才开始

3）fixedDelayString：与fixedDelay含义一样，只是参数类型变为String

4）fixedRate：表示按一定的频率执行任务，参数类型为long，单位ms，这个是指定频率，不管上一次任务有没有执行完毕，如果频率是1秒，就是每一秒都开启一次

5）fixedRateString: 与fixedRate的含义一样，只是将参数类型变为String

6）initialDelay：表示延迟多久再第一次执行任务，参数类型为long，单位ms

7）initialDelayString：与initialDelay的含义一样，只是将参数类型变为String

8）zone：时区，默认为当前时区


### 定时任务在生产中的应用
- 一般定时任务都会用来解决一些周期性的工作：比如定期检测、定期报表统计/数值统计、一些业务需求等
- 如果不多的任务很多情况下，会选择写在应用里面，一起维护，但是在实际产线当中这么做会存在很多问题和隐患，比如应用的多台部署，如何让任务只做一次，由于业务致使系统宕机，无关的检测定时也遭受风波
- 通过以上的思考，最好的建议就是定时任务能够于普通业务系统分离，但是结合系统复杂度和维护度可以做自己的选择
- 还有一些需求需要动态调度任务，手动启停的业务场景
#### 以下有三个问题 
问题一：能否设置部分节点执行任务，部分执行？
问题二：能否通过REST/RPC方式控制任务的开启和结束？
问题三：动态调度任务，手动启停的业务场景，怎么实现？

- 动态调度任务的话，可以使用restful修改cron配置，然后拥有默认值，可以随时替换，这样就变成了动态调度任务
- 手动启停的话，可以依靠Quartz的强大能力实现（天生支持这么干），springboot的schedule任务就会麻烦一些（曲线救国）
- 以上三个问题，最后其实落实在业务就是第三个问题
- 今天先研究spring schedule的实现，以后专题研究quartz

#### 解题思路
- 需要动态配置cron表达式，就不能把配置写在应用配置文件里面了，那就需要定义一个周期对象，记录实现定时任务的类名，cron参数，再附加一些业务可以有
```text
create  table 'cron_info'(
`id` NOT NULL AUTO_INCREMENT COMMENT '主键id',
`cron_class` varchar(255) NOT NULL COMMENT '执行任务的类',
`cron_expr` varchar(128) COMMENT 'cron表达式',
`cron_param` varchar(255) NOT NULL COMMENT 'cron表达式参数',
`remark` varchar(255) COMMENT '描述',
`status` tinyint(1) COMMENT '启停标识'，
`create_time` datetime COMMENT '创建实践',
`update_time` datetime COMMENT '更新时间'
)ENGINE=INNODB  DEFAULT CHARSET=utf8;

```
- 一旦有了上面的表设计，就可以通过restful或者RPC或者页面请求形式，对表达式进行动态修改了，那么怎么实现对任务的控制呢？
- 上面介绍的方法肯定是不行了，我们需要手动投递任务，设置触发器，不过这个控制力度是对全部的任务，或者指定一个调度线程池而言的，还不能精确到一个任务，这就是局限了，可是人家毕竟相对比较简便
- 有了默认数据，就需要写主逻辑代码啦
- 当向线程池提交任务时会返回一个ScheduleFuture接口的对象，ScheduledFuture接口继承了Delayed和Future接口，我们可以通过ScheduleFutured对象的cancel方法可以结束一个定时任务
- 那我们就需要一个HashMap 或者ConcurrentHashMap ,或者一些内存缓存技术，把我们的任务类，与对应的Future对象缓存绑定起来
- 接下来就是实现几个接口或者远程调用，调用startCronJob的方法
```java 
@Configuration
public class DynamicSchedulingConfig {

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;


    /**
     * 存放所有启动定时任务对象
     */
    private HashMap<String, ScheduledFuture<?>> scheduleMap = new HashMap<>();

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }


    /**
     * @param crons
     * 动态设置定时任务方法
     * 执行周期 启停状态 执行的类是必要的参数
     */
    public void startCronJob(List<CronInfo> crons){
        try {
            for (CronInfo cron : crons){
                ScheduledFuture<?> scheduledFuture = scheduleMap.get(cron.getCronClass());
                if (scheduledFuture != null){
                    scheduledFuture.cancel(true);
                }
            }

            for (CronInfo cron : crons){
                    //可以根据业务需求，判断这个任务是否有效等条件，判断是否执行
                    //通过反射成java类，获取类
                    ScheduledFuture<?> future = threadPoolTaskScheduler.schedule((Runnable) Class.forName(cron.getCronClass()).newInstance(), new CronTrigger(cron.getCronExpr()));
                    //不要忘了再把对象塞回去
                    scheduleMap.put(cron.getCronClass(),future);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
```









