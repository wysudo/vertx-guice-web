package com.matrix.vertx.guice;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.file.FileSystem;
import io.vertx.core.shareddata.SharedData;


/**
 * @ClassName VertxModule
 * @Author wby
 * @Date 2019/9/9 11:58
 * @Version 1.0
 * @Description TODO
 **/
public class VertxModule extends AbstractModule {
    private final Vertx vertx;

    public VertxModule(Vertx vertx){
        this.vertx = Preconditions.checkNotNull(vertx);
    }

    @Override
    protected void configure() {
        bind(Vertx.class).toInstance(this.vertx);
        bind(EventBus.class).toInstance(this.vertx.eventBus());
        bind(FileSystem.class).toInstance(this.vertx.fileSystem());
        bind(SharedData.class).toInstance(this.vertx.sharedData());
    }
}
