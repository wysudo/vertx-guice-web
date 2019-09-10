package com.matrix.vertx.verticle;

import com.google.inject.Inject;
import com.matrix.vertx.rest.ApiHandler;
import com.matrix.vertx.rest.InfoHandler;
import io.vertx.core.Future;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.CookieHandler;
import io.vertx.reactivex.ext.web.handler.SessionHandler;
import io.vertx.reactivex.ext.web.handler.StaticHandler;
import io.vertx.reactivex.ext.web.sstore.LocalSessionStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @ClassName ApiVerticle
 * @Author wby
 * @Date 2019/9/9 14:55
 * @Version 1.0
 * @Description TODO
 **/
public class ApiVerticle extends AbstractVerticle {
    private static final Logger log = LogManager.getLogger(ApiVerticle.class);
    @Inject
    private Router router;
    @Inject
    private InfoHandler infoHandler;
    @Inject
    private ApiHandler apiHandler;

    @Override
    public void start(Future<Void> startFuture) throws Exception{
        super.start(startFuture);
        int serverPort = config().getInteger("server.port",8080);
        //获取请求的消息体，限制消息体大小，或者处理文件上传
        router.route().handler(BodyHandler.create());
        router.route().handler(CookieHandler.create());
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));
        router.get("/api/info").handler(infoHandler);
        router.post("/api/init").handler(apiHandler);

        // 处理静态资源，例如您的登录页
        router.route().handler(StaticHandler.create());

        vertx.createHttpServer()
                .requestHandler(router)
                .rxListen(serverPort)
                .subscribe(success -> {
                    log.info("端口号为：" + serverPort);
                }, error -> {
                    error.printStackTrace();
                });
    }
}
