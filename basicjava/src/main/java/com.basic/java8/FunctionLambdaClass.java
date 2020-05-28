/**
 * Author:   claire
 * Date:    2020-05-25 - 19:47
 * Description: lambda 功能展示
 * History:
 * <author>          <time>                   <version>          <desc>
 * claire          2020-05-25 - 19:47          V1.3.6           lambda 功能展示
 */
package com.basic.java8;

import org.slf4j.Logger;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 功能简述 <br/>
 * 〈lambda 功能展示〉
 *
 * @author claire
 * @date 2020-05-25 - 19:47
 * @since 1.3.6
 */
public class FunctionLambdaClass<T> {
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

    public void  withLock(ReentrantLock lock, Runnable action){
        lock.lock();
        try{
            action.run();
        }catch (Exception e){

        }
    }
    public static TemporalAdjuster next(Predicate<LocalDate> action) {
        return TemporalAdjusters.ofDateAdjuster(date -> {
            date = date.plusDays(1);
            boolean condition = action.test(date);
            while (condition) {
                date = date.plusDays(1);
                condition = action.test(date);
            }
            return date;
        });
    }


    public static String[] filter(String[] content,Predicate<String> action){
        List<String> list = new ArrayList<>();
        for(String str : content){
            if(action.test(str)){
              list.add(str);
            }
        }
         return list.toArray(new String[0]);
    }

  
}
