## 03_lambda编程
### 1、延迟执行
- 所有lambda表达式都会延迟执行（在另一个线程中运行、多次运行、何时的时间点运行）
- 接受lambda表达式，检查它是否应该被调用，需要是调用它
```text
    /**
     * 功能描述: <br/>
     * 〈重写info是因为lambda表达式能够延迟执行〉
     *
     * @param   logger
     * @param message
     * @return 
     * @author claire
     * @date 2020-05-27 - 18:28
     */
    public static void info(Logger logger , Supplier<String> message){
        if(logger.isInfoEnabled()){
            logger.info(message.get());
        }
    }

```
### 2.lambda表达式参数
### 3. 选择一个函数式接口
### 4.返回函数
### 5.组合
### 6.延迟
### 7.并行操作
### 8.处理异常
### 9.lambda表达式和泛型
### 10.一元操作


### 问题
1.
2.实现withlock(mylock,()->{})
```text
  public void  withLock(ReentrantLock lock, Runnable action){
        lock.lock();
         try{
            action.run();
         }catch (Exception e){

         }
    }
    FunctionLambdaClass<String> functionLambdaClass = new FunctionLambdaClass<>();
    functionLambdaClass.withLock(new ReentrantLock(),() ->{System.out.println("aaaa");});


``` 





