> 代码见[SqlCostInterceptor]

> 利用jdk动态代理的思想，实现拦截与增强


### 实现拦截器
- 实现Interceptor接口
- 使用注解@Intercepts与@Signature，@Intercepts中可以定义多个@Signature，一个@Signature表示符合如下条件的方法才会被拦截
- 在ibatis中有四个接口可以实现拦截，我们这边使用invocation的statementHandler
- ParameterHandler负责入参的处理
- ResultSetHandler用于处理结果
- Executor的update与query方法可能用到MyBatis的一二级缓存从而导致统计的并不是真正的SQL执行时间
- StatementHandler的update与query方法无论如何都会统计到PreparedStatement的execute方法执行时间
- plugin方法，是为目标接口生成代理,MyBatis的Plugin类已经为我们提供了wrap方法
```text
public class Plugin implements InvocationHandler {
    private final Object target;
    private final Interceptor interceptor;
    private final Map<Class<?>, Set<Method>> signatureMap;

    private Plugin(Object target, Interceptor interceptor, Map<Class<?>, Set<Method>> signatureMap) {
        this.target = target;
        this.interceptor = interceptor;
        this.signatureMap = signatureMap;
    }

//Proxy.newProxyInstance是JDK实现动态代理的代码，将类加载器，接口的名称传递过去
    public static Object wrap(Object target, Interceptor interceptor) {
        Map<Class<?>, Set<Method>> signatureMap = getSignatureMap(interceptor);
        Class<?> type = target.getClass();
        Class<?>[] interfaces = getAllInterfaces(type, signatureMap);
        return interfaces.length > 0 ? Proxy.newProxyInstance(type.getClassLoader(), interfaces, new Plugin(target, interceptor, signatureMap)) : target;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            Set<Method> methods = (Set)this.signatureMap.get(method.getDeclaringClass());
            return methods != null && methods.contains(method) ? this.interceptor.intercept(new Invocation(this.target, method, args)) : method.invoke(this.target, args);
        } catch (Exception var5) {
            throw ExceptionUtil.unwrapThrowable(var5);
        }
    }

......
```

- 书写完以上拦截器代码之后，需要将拦截器注入，mybatis来执行拦截
```text
    @Bean
    public SqlCostInterceptor sqlCostInterceptor(){
        SqlCostInterceptor sqlCostInterceptor = new SqlCostInterceptor();
        return sqlCostInterceptor;
    }
```


### mybatis的插件是mybatis之所以好用的一个重要原因
- 实现扩展的几个接口
```text
Executor（update、query、flushStatements、commint、rollback、getTransaction、close、isClosed）

ParameterHandler（getParameterObject、setParameters）

ResultSetHandler（handleResultSets、handleOutputParameters）

StatementHandler（prepare、parameterize、batch、update、query）
```