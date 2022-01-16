package com.raiden.redis.current.limiter;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 23:51 2020/8/27
 * @Modified By:
 */
public class RedisCurrentLimiterInit implements InitializingBean {

    private RedisTemplate<String, Object> redisTemplate;

    public RedisCurrentLimiterInit(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void afterPropertiesSet() {
        RedisCurrentLimiter.init(this.redisTemplate);
    }
}
