package com.raiden.redis.current.limiter.callbock;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 15:02 2021/8/26
 * @Modified By:
 */
public interface RedisCurrentLimitingErrorCallback {

    <T> T callback(Throwable e);
}
