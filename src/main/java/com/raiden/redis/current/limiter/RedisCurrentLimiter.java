package com.raiden.redis.current.limiter;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 23:51 2020/8/27
 * @Modified By:
 */
public final class RedisCurrentLimiter {

    private static final Charset CHARSET = Charset.forName("utf-8");

    private static final String CURRENT_LIMITER = "CurrentLimiter:";;

    private static String ip;;

    private static RedisTemplate redis;

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
    }

    public static boolean isAllowAccess(String path, int windowTime, TimeUnit windowTimeUnit, int maxCount){
        if (redis == null){
            throw new NullPointerException("Redis is not initialized !");
        }
        if (path == null || path.isEmpty()){
            throw new IllegalArgumentException("The path parameter cannot be empty !");
        }
        final byte[] key = new StringBuffer(CURRENT_LIMITER).append(path).toString().getBytes(CHARSET);
        long now = System.currentTimeMillis();
        long windowTimeMillis = windowTimeUnit.toMillis(windowTime);
        List<List<Object>> arrList = redis.executePipelined((RedisConnection connection) -> {
            connection.multi();
            connection.zAdd(key, now, createValue(now));
            connection.zRemRangeByScore(key, 0, now - windowTimeMillis);
            connection.zCard(key);
            connection.pExpire(key, windowTimeMillis + 1000);
            connection.exec();
            return null;
        });
        if (arrList.isEmpty()){
            throw new RuntimeException("Error return result !");
        }
        List<Object> result = arrList.get(0);
        if (result.size() != 4){
            throw new RuntimeException("Error return result !");
        }
        Object count = result.get(2);
        if (count instanceof Long){
            return ((Long) count).longValue() <= maxCount;
        }else {
            throw new RuntimeException("Error return result !");
        }
    }

    private static byte[] createValue(long now){
        return new StringBuilder(ip).append(now).append(Math.random() * 100).toString().getBytes(CHARSET);
    }
}
