package com.raiden.redis.current.limiter.annotation;

import com.raiden.redis.current.limiter.config.RedisCurrentLimiterConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 23:51 2020/8/27
 * @Modified By:
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(RedisCurrentLimiterConfiguration.class)
public @interface EnableRedisCurrentLimiter {
}
