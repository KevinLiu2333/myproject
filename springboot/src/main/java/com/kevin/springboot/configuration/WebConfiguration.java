package com.kevin.springboot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Kevin
 * Date: 2017/10/19
 * Time: 9:58
 */
@Configuration
public class WebConfiguration extends WebMvcConfigurerAdapter{

    /**
     * 控制大小写不敏感的配置
     * @param configurer 配置
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        AntPathMatcher matcher = new AntPathMatcher();
        matcher.setCaseSensitive(false);
        configurer.setPathMatcher(matcher);
    }

    /**
     * 线程池配置
     * @return 线程池
     */
    @Bean
    public ExecutorService executorService() {
        ExecutorService executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), 20, 120L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000));
        return executor;
    }

}
