package com.raiden.redis.current.limiter.info;

import java.util.concurrent.TimeUnit;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 15:55 2021/8/26
 * @Modified By:
 */
public class RedisCurrentLimiterInfo {
    private int windowTime;
    private TimeUnit windowTimeUnit;
    private int maxCount;

    public int getWindowTime() {
        return windowTime;
    }

    public void setWindowTime(int windowTime) {
        this.windowTime = windowTime;
    }

    public TimeUnit getWindowTimeUnit() {
        return windowTimeUnit;
    }

    public void setWindowTimeUnit(TimeUnit windowTimeUnit) {
        this.windowTimeUnit = windowTimeUnit;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }
}
