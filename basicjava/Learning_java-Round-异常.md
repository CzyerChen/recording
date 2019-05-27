> 如果某个方法不能按照正常的途径完成任务，就可以通过另一种路径退出方法。在这种情况下会抛出一个封装了错误信息的对象



object ---> Throwable

Throwable ->Error（java运行时系统的内部错误和资源耗尽错误）,Exception

Error -->AWTError ThreadDeath 内存异常 VirtualMachineError

Exception --> 运行时报错:RuntimeException（Java 虚拟机正常运行期间抛出的异常的超类） 编译时报错/受检异常：SQLException IOException ClassNotFoundException 

RuntimeException是未受检异常 --> NullPointerException ClassCastException ArrayIndexOutOfBoundException


### 异常的处理方式 throw throws
- 抛出异常有三种形式，一是throw,一个throws，还有一种系统自动抛异常