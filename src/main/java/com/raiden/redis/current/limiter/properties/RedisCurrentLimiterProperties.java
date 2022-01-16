package com.raiden.redis.current.limiter.properties;

import com.raiden.redis.current.limiter.info.RedisCurrentLimiterInfo;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 17:55 2021/11/6
 * @Modified By:
 */
@ConfigurationProperties(prefix = "redis.current-limiter")
public class RedisCurrentLimiterProperties {

    private Map<String, RedisCurrentLimiterInfo> config;

    public RedisCurrentLimiterProperties(){
        config = new HashMap<>();
    }

    public Map<String, RedisCurrentLimiterInfo> getConfig() {
        return config;
    }

    public void setConfig(Map<String, RedisCurrentLimiterInfo> config) {
        this.config = config;
    }
}
