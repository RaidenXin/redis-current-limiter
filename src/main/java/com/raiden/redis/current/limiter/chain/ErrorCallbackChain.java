package com.raiden.redis.current.limiter.chain;

import com.raiden.redis.current.limiter.annotation.ErrorCallback;
import com.raiden.redis.current.limiter.callbock.RedisCurrentLimitingErrorCallback;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 16:33 2021/8/26
 * @Modified By:
 */
public class ErrorCallbackChain implements CallbackChain<Throwable>{


    private List<ErrorCallbackChainEntry> chain;

    public ErrorCallbackChain(ErrorCallback[] errorCallbacks, ApplicationContext context){
        this.chain = Stream.of(errorCallbacks).sorted((e1,e2) -> Integer.compare(e1.priority(), e2.priority())).map(e -> {
            RedisCurrentLimitingErrorCallback currentLimitingErrorCallback;
            String callback = e.callback();
            if (callback == null || callback.isEmpty()){
                try {
                    currentLimitingErrorCallback = e.callbackClass().newInstance();
                } catch (Exception exception) {
                    throw new RuntimeException(exception);
                }
            }else {
                currentLimitingErrorCallback = context.getBean(callback, e.callbackClass());
            }
            return new ErrorCallbackChainEntry(e, currentLimitingErrorCallback);
        }).collect(Collectors.toList());
    }

    public <T> T execute(Throwable throwable){
        Optional<ErrorCallbackChainEntry> first = chain.stream().filter(e -> e.isMatching(throwable)).findFirst();
        if (first.isPresent()){
            return first.get().execute(throwable);
        }
        throw new RuntimeException(throwable);
    }

    private class ErrorCallbackChainEntry implements CallbackChain<Throwable>{

        private Class<? extends Throwable> exception;
        private RedisCurrentLimitingErrorCallback callback;

        public ErrorCallbackChainEntry(ErrorCallback errorCallback,RedisCurrentLimitingErrorCallback callback){
             this.exception = errorCallback.exception();
             this.callback = callback;
        }

        public boolean isMatching(Throwable e){
            return exception.isInstance(e);
        }

        @Override
        public <T> T execute(Throwable throwable) {
            return callback.callback(throwable);
        }
    }
}
