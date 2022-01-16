-- 参数：
-- nowTime 当前时间
-- windowTime窗口时间
-- maxCount最大次数
-- expiredWindowTime 已经过期的窗口时间
-- value 请求标记
-- 删除过期的请求标志
local nowTime = tonumber(ARGV[1]);
local windowTime = tonumber(ARGV[2]);
local maxCount = tonumber(ARGV[3]);
local expiredWindowTime = tonumber(ARGV[4])
local value = ARGV[5];
local key = KEYS[1];
redis.call('ZREMRANGEBYSCORE', key, 0, expiredWindowTime)
-- 获取当前窗口的请求标志个数
local count = redis.call('ZCARD', key)
if count >= maxCount then
    redis.call('PEXPIRE', key, windowTime + 1000)
    return 500
else
    redis.call('ZADD', key, nowTime, value)
    redis.call('PEXPIRE', key, windowTime + 1000)
    return 200
end
