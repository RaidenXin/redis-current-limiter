-- 参数：
-- nowTime 当前时间
-- windowTime窗口时间
-- maxCount最大次数
-- expiredWindowTime 已经过期的窗口时间
-- value 请求标记
local nowTime = tonumber(ARGV[1]);
local windowTime = tonumber(ARGV[2]);
local maxCount = tonumber(ARGV[3]);
local expiredWindowTime = tonumber(ARGV[4])
local value = ARGV[5];
local key = KEYS[1];
-- 获取当前窗口的请求标志个数
local count = redis.call('ZCARD', key)
-- 比较当前已经请求的数量是否大于窗口最大请求数
if count >= maxCount then
    -- 如果大于最大请求数
    -- 删除过期的请求标志 释放窗口空间
    redis.call('ZREMRANGEBYSCORE', key, 0, expiredWindowTime)
    -- 再次获取当前窗口的请求标志个数
    local count = redis.call('ZCARD', key)
    -- 延长过期时间
    redis.call('PEXPIRE', key, windowTime + 1000)
    -- 比较释放后的大小 是否小于窗口最大请求数
    if count < maxCount then
        -- 返回200代表成功
        return 200
    else
        -- 返回500代表失败
        return 500
    end
else
     -- 插入当前访问的访问标记
    redis.call('ZADD', key, nowTime, value)
    -- 延长过期时间
    redis.call('PEXPIRE', key, windowTime + 1000)
    -- 返回200代表成功
    return 200
end
