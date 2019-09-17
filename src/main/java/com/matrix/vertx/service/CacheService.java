package com.matrix.vertx.service;

import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;

/**
 * @ClassName CacheService
 * @Author wby
 * @Date 2019/9/17 11:31
 * @Version 1.0
 * @Description TODO
 **/
public interface CacheService {
    AsyncLoadingCache getCache(String name, AsyncCacheLoader cacheLoader);

    AsyncLoadingCache getCache(String name, Integer minute, AsyncCacheLoader cacheLoader);

    AsyncLoadingCache getCache(String name);

    /**
     * @param cache @see Constant.INDEX_CACHE_KEY
     *              Constant.ALERT_CACHE_KEY
     * @param key
     */
    void refresh(String cache, String key);
}
