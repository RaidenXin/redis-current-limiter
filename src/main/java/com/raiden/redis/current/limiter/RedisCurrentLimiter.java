package com.raiden.redis.current.limiter;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;


/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 23:51 2020/8/27
 * @Modified By:
 */
public final class RedisCurrentLimiter {

    private static final String CURRENT_LIMITER = "CurrentLimiter:";;

    private static String ip;;

    private static RedisTemplate redis;

    private static ResourceScriptSource resourceScriptSource;

    protected static void init(RedisTemplate redisTemplate){
        if (redisTemplate == null){
            throw new NullPointerException("The parameter cannot be null");
        }
        try {
            ip = Inet4Address.getLocalHost().getHostAddress().replaceAll("\\.", "");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        redis = redisTemplate;
        //lua文件存放在resources目录下的redis文件夹内
        resourceScriptSource = new ResourceScriptSource(new ClassPathResource("redis/redis-current-limiter.lua"));
    }

    public static boolean isAllowAccess(String path, int windowTime, TimeUnit windowTimeUnit, int maxCount){
        if (redis == null){
            throw new NullPointerException("Redis is not initialized !");
        }
        if (path == null || path.isEmpty()){
            throw new IllegalArgumentException("The path parameter cannot be empty !");
        }
        final String key = new StringBuffer(CURRENT_LIMITER).append(path).toString();
        long now = System.currentTimeMillis();
        long windowTimeMillis = windowTimeUnit.toMillis(windowTime);
        //调用lua脚本并执行
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);//返回类型是Long
        redisScript.setScriptSource(resourceScriptSource);
        Long result = (Long) redis.execute(redisScript, Arrays.asList(key), now, windowTimeMillis, maxCount, now - windowTimeMillis, createValue(now));
        return result.intValue() == 200;
    }

    private static String createValue(long now){
        return new StringBuilder(ip).append(now).append(Math.random() * 100).toString();
    }
}
