package com.frame.springboot.task;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.*;
import org.springframework.core.Ordered;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.Executor;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -03 - 22 17:19
 */
@EnableScheduling
@Configuration
@EnableAsync(mode = AdviceMode.PROXY,proxyTargetClass = false,order = Ordered.HIGHEST_PRECEDENCE) //使用java自身的代理，而不是AspectJ
public class AsyncSchedulingConfig implements AsyncConfigurer, SchedulingConfigurer, DisposableBean {

    @Bean(name = "scheduler")
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

    @Override
    public void destroy() {
        WebApplicationContext currentWebApplicationContext = ContextLoader.getCurrentWebApplicationContext();
        if(currentWebApplicationContext != null) {
            ThreadPoolTaskScheduler scheduler = (ThreadPoolTaskScheduler) currentWebApplicationContext.getBean("scheduler");
            scheduler.shutdown();
        }
    }
}
