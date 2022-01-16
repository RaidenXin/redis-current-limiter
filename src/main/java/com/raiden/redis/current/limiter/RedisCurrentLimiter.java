package com.raiden.redis.current.limiter;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

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
        final byte[] keyByte = new StringBuffer(CURRENT_LIMITER).append(path).toString().getBytes(CHARSET);
        long now = System.currentTimeMillis();
        long windowTimeMillis = windowTimeUnit.toMillis(windowTime);
        //先获取当前窗口请求标记总数 如果超过最大值直接 返回不予许访问
        List<List<Object>> resultArr = redis.executePipelined((RedisConnection connection) -> {
            //开启事物保障这些命令同一批次串行执行
            connection.multi();
            //删除过期窗口中的 请求标记
            connection.zRemRangeByScore(keyByte, 0, now - windowTimeMillis);
            //获取当前窗口请求标记总数
            connection.zCard(keyByte);
            //设置窗口过期时间
            connection.pExpire(keyByte, windowTimeMillis + 1000);
            //执行事物
            connection.exec();
            return null;
        });
        long count = getCount(resultArr, 0, 3, 1);
        if (count >= maxCount){
            System.out.println("这里是:" + count);
            //删除过期窗口中的 请求标记
            return false;
        }else {
            //如果窗口中的访问数 比最大访问数小
            //通过通道执行命令
            resultArr = redis.executePipelined((RedisConnection connection) -> {
                //开启事物保障这些命令同一批次串行执行
                connection.multi();
                //插入一个请求标记
                connection.zAdd(keyByte, now, createValue(now));
                //删除过期窗口中的 请求标记
                connection.zRemRangeByScore(keyByte, 0, now - windowTimeMillis);
                //获取当前窗口请求标记总数
                connection.zCard(keyByte);
                //设置窗口过期时间
                connection.pExpire(keyByte, windowTimeMillis + 1000);
                //执行事物
                connection.exec();
                return null;
            });
        }
        return getCount(resultArr, 0, 4, 2) <= maxCount;
    }

    private static byte[] createValue(long now){
        return new StringBuilder(ip).append(now).append(Math.random() * 100).toString().getBytes(CHARSET);
    }

    private static long getCount(List<List<Object>> resultArr, int index,int size,int countIndex){
        if (resultArr.isEmpty()){
            throw new RuntimeException("Error return result !");
        }
        List<Object> result = resultArr.get(index);
        if (result.size() != size){
            throw new RuntimeException("Error return result !");
        }
        Object o = result.get(countIndex);
        if (o instanceof Long){
            System.out.println("返回值是：" + o);
            return ((Long) o).longValue();
        }else {
            throw new RuntimeException("Error return result !");
        }
    }
}
