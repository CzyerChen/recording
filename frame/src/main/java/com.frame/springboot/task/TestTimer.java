package com.frame.springboot.task;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -03 - 22 17:05
 */
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
