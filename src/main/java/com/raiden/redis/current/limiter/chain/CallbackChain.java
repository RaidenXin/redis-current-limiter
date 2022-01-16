package com.raiden.redis.current.limiter.chain;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 17:32 2021/8/26
 * @Modified By:
 */
public interface CallbackChain<E> {

    <T> T execute(E e);
}
