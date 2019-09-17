package com.matrix.vertx.rest;

import com.alibaba.fastjson.JSONArray;
import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.google.inject.Inject;
import com.matrix.vertx.constant.Constant;
import com.matrix.vertx.service.CacheService;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @ClassName CacheHandler
 * @Author wby
 * @Date 2019/9/17 16:03
 * @Version 1.0
 * @Description TODO
 **/
public class CacheHandler implements Handler<RoutingContext> {
    private static final Logger log = LogManager.getLogger(CacheHandler.class);
    @Inject
    private Vertx vertx;
    @Inject
    private CacheService cacheService;

    AsyncCacheLoader cacheLoader = new AsyncCacheLoader<String, JSONArray>() {
        //异步加载
        @Nonnull
        @Override
        public CompletableFuture<JSONArray> asyncLoad(@Nonnull String key, @Nonnull Executor executor) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return getIndexList(key).toFuture().get();
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            });
        }
        //异步重载
        @Nonnull
        @Override
        public CompletableFuture<JSONArray> asyncReload(@Nonnull String key, @Nonnull JSONArray oldValue, @Nonnull Executor executor) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return getIndexList(key).toFuture().get();
                }catch (Exception e){
                    return oldValue;
                }
            });
        }
    };

    @Override
    public void handle(RoutingContext context) {
        AsyncLoadingCache async = cacheService.getCache("cacheName", cacheLoader);
        async.get("key").whenComplete((a,e) ->{
            log.info("cache  data : {}", a);
            context.response()
                    .setStatusCode(200).
                    putHeader(Constant.CONTENT_TYPE, Constant.APPLICATION_JSON)
                    .end(a.toString());
        });
    }

    public Flowable<JSONArray> getIndexList(String key){
        return Flowable.create(e ->{

        }, BackpressureStrategy.BUFFER);
    }
}
