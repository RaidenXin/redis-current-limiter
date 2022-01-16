package com.raiden.redis.current.limiter.config;

import com.raiden.redis.current.limiter.RedisCurrentLimiterInit;
import com.raiden.redis.current.limiter.aop.RedisCurrentLimitingAspect;
import com.raiden.redis.current.limiter.properties.RedisCurrentLimiterProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 23:51 2020/8/27
 * @Modified By:
 */
@EnableConfigurationProperties(RedisCurrentLimiterProperties.class)
@ConditionalOnProperty(
        name = {"redis.current-limiter.enabled"}
)
public class RedisCurrentLimiterConfiguration {

    @Bean
    public RedisCurrentLimiterInit redisCurrentLimiterInit(){
        return new RedisCurrentLimiterInit();
    }

    @Bean
    public RedisCurrentLimitingAspect redisCurrentLimitingAspect(RedisCurrentLimiterProperties properties, ApplicationContext context){
        return new RedisCurrentLimitingAspect(context, properties);
    }
}
