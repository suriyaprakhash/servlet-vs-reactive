//package com.suriyaprakhash.learn.reactive_web.hugefile.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
//import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.task.AsyncTaskExecutor;
//import org.springframework.scheduling.annotation.AsyncConfigurer;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import org.springframework.web.context.request.NativeWebRequest;
//import org.springframework.web.context.request.async.CallableProcessingInterceptor;
//import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//import org.springframework.web.context.request.async.TimeoutCallableProcessingInterceptor;
//
//import java.util.concurrent.Callable;
//
//@Configuration
//@EnableAsync
//@EnableScheduling
//@Slf4j
//public class AsyncConfiguration implements AsyncConfigurer {
//
//
//    @Override
//    @Bean(name = "suriyaExecutor")
//    public AsyncTaskExecutor getAsyncExecutor() {
//        log.debug("Creating Async Task Executor");
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(20);
//        executor.setThreadGroupName("suriya-async-tg");
//        executor.setThreadNamePrefix("suriya-async-");
//        executor.setMaxPoolSize(30);
//        executor.setQueueCapacity(25);
//        return executor;
//    }
//
//    @Override
//    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
//        return new SimpleAsyncUncaughtExceptionHandler();
//    }
//
//    /** Configure async support for Spring MVC. */
//    @Bean
//    public WebMvcConfigurer webMvcConfigurerConfigurer(@Qualifier("suriyaExecutor") AsyncTaskExecutor taskExecutor,
//                                                       CallableProcessingInterceptor callableProcessingInterceptor) {
//        return new WebMvcConfigurer() {
//            @Override
//            public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
//                configurer.setDefaultTimeout(360000).setTaskExecutor(taskExecutor);
//                configurer.registerCallableInterceptors(callableProcessingInterceptor);
//                WebMvcConfigurer.super.configureAsyncSupport(configurer);
//            }
//        };
//    }
//
//    @Bean
//    public CallableProcessingInterceptor callableProcessingInterceptor() {
//        return new TimeoutCallableProcessingInterceptor() {
//            @Override
//            public <T> Object handleTimeout(NativeWebRequest request, Callable<T> task) throws Exception {
//                log.error("timeout!");
//                return super.handleTimeout(request, task);
//            }
//        };
//    }
//}