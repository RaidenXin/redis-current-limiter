package com.raiden.redis.current.limiter.annotation;

import com.raiden.redis.current.limiter.callbock.RedisCurrentLimitingErrorCallback;

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
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ErrorCallback {
    int priority() default 1;
    Class<? extends Throwable> exception() default Throwable.class;
    String callback() default "";
    Class<? extends RedisCurrentLimitingErrorCallback> callbackClass();
}
