package com.raiden.redis.current.limiter.callbock;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 16:21 2021/8/26
 * @Modified By:
 */
public interface RedisCurrentLimitingDegradeCallback {

    <T> T callback();
}
