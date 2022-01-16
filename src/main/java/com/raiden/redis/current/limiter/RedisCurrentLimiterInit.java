package com.raiden.redis.current.limiter;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.Optional;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 23:51 2020/8/27
 * @Modified By:
 */
public class RedisCurrentLimiterInit implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, RedisTemplate> beansOfType = applicationContext.getBeansOfType(RedisTemplate.class);
        Optional<RedisTemplate> first = beansOfType.values().stream().findFirst();
        if (first.isPresent()){
            RedisCurrentLimiter.init(first.get());
        }
    }
}
