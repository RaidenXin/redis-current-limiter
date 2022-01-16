package com.raiden.redis.current.limiter.config;

import com.raiden.redis.current.limiter.RedisCurrentLimiterInit;
import com.raiden.redis.current.limiter.aop.RedisCurrentLimitingAspect;
import com.raiden.redis.current.limiter.properties.RedisCurrentLimiterProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 23:51 2020/8/27
 * @Modified By:
 */
public class RedisCurrentLimiterConfiguration {

    @Bean
    public RedisCurrentLimiterInit redisCurrentLimiterInit(){
        return new RedisCurrentLimiterInit();
    }

    @Bean
    @ConditionalOnBean(RedisCurrentLimiterProperties.class)
    public RedisCurrentLimitingAspect redisCurrentLimitingAspect(RedisCurrentLimiterProperties properties){
        return new RedisCurrentLimitingAspect(properties);
    }


}
