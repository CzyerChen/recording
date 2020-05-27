/**
 * Author:   claire
 * Date:    2020-05-25 - 19:47
 * Description: lambda 功能展示
 * History:
 * <author>          <time>                   <version>          <desc>
 * claire          2020-05-25 - 19:47          V1.3.6           lambda 功能展示
 */
package com.basic.java8;

/**
 * 功能简述 <br/>
 * 〈lambda 功能展示〉
 *
 * @author claire
 * @date 2020-05-25 - 19:47
 * @since 1.3.6
 */
public class FunctionLambdaClass {
    public static void printContent(String content, FunctionalDocInterface functional) {
        functional.show(content);
    }

    public static Runnable uncheck(RunnableEx runner) {
        return () -> {
            try {
                runner.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    public static Runnable andThen(Runnable r1 ,Runnable r2){
        return ()->{
            r1.run();
            r2.run();
        };
    }

}
