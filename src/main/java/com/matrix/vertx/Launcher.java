package com.matrix.vertx;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.matrix.vertx.guice.GuiceInjector;
import com.matrix.vertx.guice.GuiceVerticleFactory;
import com.matrix.vertx.guice.GuiceVertxDeploymentManager;
import com.matrix.vertx.guice.VertxModule;
import io.vertx.core.Vertx;

/**
 * @ClassName Launcher
 * @Author wby
 * @Date 2019/9/10 10:19
 * @Version 1.0
 * @Description TODO
 **/
public class Launcher {
    public static void main(String[] args) {
        new Launcher().launch();
    }

    public void launch(){
        Vertx vertx = Vertx.vertx();
        Injector inject = Guice.createInjector(
                new VertxModule(vertx),
                new GuiceInjector());
        /**
         * init object,Use verticleFactory to locate through registration(Document core)
         */
        GuiceVerticleFactory guiceVerticleFactory = new GuiceVerticleFactory(inject);
        vertx.registerVerticleFactory(guiceVerticleFactory);

        /**
         * init object
         */
        GuiceVertxDeploymentManager deploymentManager = new GuiceVertxDeploymentManager(vertx);
        deploymentManager.deployVerticle(MainVerticle.class);
    }
}
