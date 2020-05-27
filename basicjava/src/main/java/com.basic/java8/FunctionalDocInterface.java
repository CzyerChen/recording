/**
 * Author:   claire
 * Date:    2020-05-25 - 19:33
 * Description: 功能性文档类接口
 * History:
 * <author>          <time>                   <version>          <desc>
 * claire          2020-05-25 - 19:33          V1.3.6           功能性文档类接口
 */
package com.basic.java8;

/**
 * 功能简述 <br/> 
 * 〈功能性文档类接口〉
 *  @FunctionalInterface：必须有且仅有一个抽象方法，允许定义静态方法，允许定义默认方法，允许Object中的public方法，此注解不是必须的，只是便于编译器检查
 *
 *
 * @author claire
 * @date 2020-05-25 - 19:33
 * @since 1.0.0
 */
@FunctionalInterface
public interface FunctionalDocInterface {
    //抽象方法,有且仅有一个
    public abstract void show(String param);

    @Override
    public boolean equals(Object param);

    public default void methodDefault(){

    }

    public static void methodStatistic(){

    }
}
