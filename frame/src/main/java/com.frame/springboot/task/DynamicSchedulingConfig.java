package com.frame.springboot.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -03 - 22 18:55
 */
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
