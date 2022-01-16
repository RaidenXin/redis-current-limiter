package com.raiden.redis.current.limiter.aop;

import com.raiden.redis.current.limiter.RedisCurrentLimiter;
import com.raiden.redis.current.limiter.annotation.CurrentLimiting;
import com.raiden.redis.current.limiter.annotation.DegradeCallback;
import com.raiden.redis.current.limiter.annotation.ErrorCallback;
import com.raiden.redis.current.limiter.callbock.RedisCurrentLimitingDegradeCallback;
import com.raiden.redis.current.limiter.chain.ErrorCallbackChain;
import com.raiden.redis.current.limiter.info.RedisCurrentLimiterInfo;
import com.raiden.redis.current.limiter.properties.RedisCurrentLimiterProperties;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 23:51 2020/8/27
 * @Modified By:
 */
@Aspect
public class RedisCurrentLimitingAspect {

    private Map<String, RedisCurrentLimiterInfo> config;
    private ApplicationContext context;

    private ConcurrentHashMap<Method, ErrorCallbackChain> errorCallbackChainCache;

    private ConcurrentHashMap<Method, RedisCurrentLimitingDegradeCallback> degradeCallbackCache;

    public RedisCurrentLimitingAspect(ApplicationContext context, RedisCurrentLimiterProperties properties){
        this.context = context;
        this.config = properties.getConfig();
        this.errorCallbackChainCache = new ConcurrentHashMap<>();
        this.degradeCallbackCache = new ConcurrentHashMap<>();
    }

    @Pointcut("@annotation(com.raiden.redis.current.limiter.annotation.CurrentLimiting) || @within(com.raiden.redis.current.limiter.annotation.CurrentLimiting)")
    public void intercept(){}

    @Around("intercept()")
    public Object currentLimitingHandle(ProceedingJoinPoint joinPoint) throws Throwable{
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CurrentLimiting annotation = AnnotationUtils.findAnnotation(method, CurrentLimiting.class);
        if (annotation == null){
            annotation = method.getDeclaringClass().getAnnotation(CurrentLimiting.class);
        }
        String path = annotation.value();
        //如果没有配置 资源 直接 放过
        //如果没有找到限流配置 也放过
        RedisCurrentLimiterInfo info;
        if (path != null && !path.isEmpty() && (info = config.get(path)) != null){
            try {
                boolean allowAccess = RedisCurrentLimiter.isAllowAccess(path, info.getWindowTime(), info.getWindowTimeUnit(), info.getMaxCount());
                if (allowAccess){
                    return joinPoint.proceed();
                }else {
                    //获取降级处理器
                    RedisCurrentLimitingDegradeCallback currentLimitingDegradeCallback = degradeCallbackCache.get(method);
                    if (currentLimitingDegradeCallback == null){
                        degradeCallbackCache.putIfAbsent(method, getRedisCurrentLimitingDegradeCallback(annotation));
                    }
                    currentLimitingDegradeCallback = degradeCallbackCache.get(method);
                    return currentLimitingDegradeCallback.callback();
                }
            }catch (Throwable e){
                //如果报错 交给 错误回调
                ErrorCallbackChain errorCallbackChain = errorCallbackChainCache.get(method);
                if (errorCallbackChain == null){
                    ErrorCallback[] errorCallbacks = annotation.errorCallback();
                    if (errorCallbacks.length == 0){
                        throw e;
                    }
                    errorCallbackChainCache.putIfAbsent(method, new ErrorCallbackChain(errorCallbacks, context));
                }
                errorCallbackChain = errorCallbackChainCache.get(method);
                return errorCallbackChain.execute(e);
            }
        }
        return joinPoint.proceed();
    }

    private RedisCurrentLimitingDegradeCallback getRedisCurrentLimitingDegradeCallback(CurrentLimiting annotation) throws IllegalAccessException, InstantiationException {
        DegradeCallback degradeCallback = annotation.degradeCallback();
        String callback = degradeCallback.callback();
        if (callback == null || callback.isEmpty()){
            return degradeCallback.callbackClass().newInstance();
        }else {
            return context.getBean(degradeCallback.callback(), degradeCallback.callbackClass());
        }
    }
}
