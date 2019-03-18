package learningpattern.springdesignpattern;


import learningpattern.springdesignpattern.domain.Admin;
import learningpattern.springdesignpattern.domain.Server;
import learningpattern.springdesignpattern.domain.StartApplicationCommand;

/**
 *允许封装请求（ServerCommand） 传递给Server,可以更好的处理请求
 * beanFactory 应用程序上下文会启动后置处理器，并对Bean进行一些操作
 * BeanFactoryPostProcessor是命令
 * PostProcessorRegistrationDelegate是调用者
 * ConfigurableListableBeanFactory是接收器
 *
 */
public class OrderModel {

    public static void main(String[] args){
        Admin admin = new Admin();
        Server server = new Server();
        StartApplicationCommand startApplicationCommand = new StartApplicationCommand(server);
        admin.addCommand(startApplicationCommand);
        admin.start();
    }
}
