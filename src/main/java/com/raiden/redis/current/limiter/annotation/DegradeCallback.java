package com.raiden.redis.current.limiter.annotation;



import com.raiden.redis.current.limiter.callbock.RedisCurrentLimitingDegradeCallback;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 16:20 2021/8/26
 * @Modified By:
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DegradeCallback {
    String callback() default "";
    Class<? extends RedisCurrentLimitingDegradeCallback> callbackClass();
}
