package com.matrix.vertx.service.impl;

import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.matrix.vertx.service.CacheService;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @ClassName CacheServiceImpl
 * @Author wby
 * @Date 2019/9/17 14:04
 * @Version 1.0
 * @Description Caffeine本地缓存
 **/
public class CacheServiceImpl implements CacheService {
    private boolean dynamic = true;
    //ConcurrentHashMap相比hashMap线程安全相比hashTable效率更高
    private final ConcurrentMap<String, AsyncLoadingCache> cacheMap = new ConcurrentHashMap<>(16);

    //新建对象
    private Caffeine<Object, Object> cacheBuilder = Caffeine.newBuilder();

    private AsyncCacheLoader<Object, Object> cacheLoader;


    public CacheServiceImpl() {

    }

    //配置参数
    //异步自动加载
    protected AsyncLoadingCache<Object, Object> createNativeCaffeineCache(String name) {
        return this.cacheBuilder.buildAsync(this.cacheLoader);
    }

    //异步自动加载并设置刷新时间
    protected AsyncLoadingCache<Object, Object> createNativeCaffeineCache(String name, Integer minuts) {
        return Caffeine.newBuilder().refreshAfterWrite(minuts, TimeUnit.MINUTES).buildAsync(this.cacheLoader);
    }


    @Override
    public AsyncLoadingCache getCache(String name, AsyncCacheLoader cacheLoader) {
        AsyncLoadingCache cache = this.cacheMap.get(name);
        if (cache == null && this.dynamic) {
            synchronized (this.cacheMap) {
                cache = this.cacheMap.get(name);
                if (cache == null) {
                    setCacheLoader(cacheLoader);
                    setCacheName(name);
                    cache = this.cacheMap.get(name);
                }
            }
        }
        return cache;
    }

    @Override
    public AsyncLoadingCache getCache(String name, Integer minute, AsyncCacheLoader cacheLoader) {
        AsyncLoadingCache cache = this.cacheMap.get(name);
        if (cache == null && this.dynamic) {
            synchronized (this.cacheMap) {
                cache = this.cacheMap.get(name);
                if (cache == null) {
                    setCacheLoader(cacheLoader);
                    setCacheName(name, minute);
                    cache = this.cacheMap.get(name);
                }
            }
        }
        return cache;
    }

    @Override
    public AsyncLoadingCache getCache(String name) {
        AsyncLoadingCache cache = this.cacheMap.get(name);
        if (cache == null) {
            cache = cacheBuilder.buildAsync(new AsyncCacheLoader() {
                @Override
                public CompletableFuture asyncLoad(@Nonnull Object key, @Nonnull Executor executor) {
                    return null;
                }
            });
            this.cacheMap.put(name, cache);
        }
        return cache;
    }

    @Override
    public void refresh(String cache, String key) {
        AsyncLoadingCache asyncLoadingCache = this.cacheMap.get(cache);
        if (asyncLoadingCache != null) {
            asyncLoadingCache.synchronous().refresh(key);
        }
    }
    //将要存储的数据名称和缓存数据放于map中
    public void setCacheName(String name) {
        if (!this.cacheMap.containsKey(name)) {
            this.cacheMap.put(name, createNativeCaffeineCache(name));
        }
    }

    public void setCacheName(String name, Integer minute) {
        if (!this.cacheMap.containsKey(name)) {
            this.cacheMap.put(name, createNativeCaffeineCache(name, minute));
        }
    }

    //将数据放于缓存中
    public void setCacheLoader(AsyncCacheLoader<Object, Object> cacheLoader) {
        if (!nullSafeEquals(this.cacheLoader, cacheLoader)) {
            this.cacheLoader = cacheLoader;
//            refreshKnownCaches();
        }
    }

    private void refreshKnownCaches() {
        for (Map.Entry<String, AsyncLoadingCache> entry : this.cacheMap.entrySet()) {
            entry.setValue(createNativeCaffeineCache(entry.getKey()));
        }
    }


    public static boolean nullSafeEquals(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        if (o1.equals(o2)) {
            return true;
        }
        if (o1.getClass().isArray() && o2.getClass().isArray()) {
            return arrayEquals(o1, o2);
        }
        return false;
    }


    private static boolean arrayEquals(Object o1, Object o2) {
        if (o1 instanceof Object[] && o2 instanceof Object[]) {
            return Arrays.equals((Object[]) o1, (Object[]) o2);
        }
        if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
            return Arrays.equals((boolean[]) o1, (boolean[]) o2);
        }
        if (o1 instanceof byte[] && o2 instanceof byte[]) {
            return Arrays.equals((byte[]) o1, (byte[]) o2);
        }
        if (o1 instanceof char[] && o2 instanceof char[]) {
            return Arrays.equals((char[]) o1, (char[]) o2);
        }
        if (o1 instanceof double[] && o2 instanceof double[]) {
            return Arrays.equals((double[]) o1, (double[]) o2);
        }
        if (o1 instanceof float[] && o2 instanceof float[]) {
            return Arrays.equals((float[]) o1, (float[]) o2);
        }
        if (o1 instanceof int[] && o2 instanceof int[]) {
            return Arrays.equals((int[]) o1, (int[]) o2);
        }
        if (o1 instanceof long[] && o2 instanceof long[]) {
            return Arrays.equals((long[]) o1, (long[]) o2);
        }
        if (o1 instanceof short[] && o2 instanceof short[]) {
            return Arrays.equals((short[]) o1, (short[]) o2);
        }
        return false;
    }
}
