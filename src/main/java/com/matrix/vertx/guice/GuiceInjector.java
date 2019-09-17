package com.matrix.vertx.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.Router;

/**
 * @ClassName GuiceInjector
 * @Author wby
 * @Date 2019/9/9 14:46
 * @Version 1.0
 * @Description @Provides 用以标注 Module 类中的方法，
 * 它的作用是 标注该 Module 可以向外界提供的类的实例对象的方法
 **/
public class GuiceInjector extends AbstractModule {
    @Provides
    @Singleton
    public io.vertx.reactivex.core.Vertx getVertx(Vertx vertx) {
        return io.vertx.reactivex.core.Vertx.newInstance(vertx);
    }

    @Provides
    @Singleton
    public Router getRouter(io.vertx.reactivex.core.Vertx vertx) {
        return Router.router(vertx);
    }

    @Provides
    @Singleton
    public JsonObject getConfig(Vertx vertx) {
        JsonObject jsonObject = vertx.getOrCreateContext().config();
        return jsonObject;
    }

    @Override
    protected void configure() {

    }
}
