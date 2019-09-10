package com.matrix.vertx.rest;


import com.google.inject.Inject;
import com.matrix.vertx.constant.Constant;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @ClassName InfoHandler
 * @Author wby
 * @Date 2019/9/9 15:57
 * @Version 1.0
 * @Description TODO
 **/
public class InfoHandler implements Handler<RoutingContext> {
    private static final Logger log = LogManager.getLogger(InfoHandler.class);
    @Inject
    private Vertx vertx;
    @Inject
    private SharedData sharedData;

    @Override
    public void handle(RoutingContext context) {
        LocalMap<String, JsonObject> localMap = sharedData.<String, JsonObject>getLocalMap(Constant.SYSTEM_DATA_KEY);
        JsonObject body = localMap.get(Constant.SYSTEM_DATA_KEY);
        context.response()
                .setStatusCode(200)
                .putHeader(Constant.CONTENT_TYPE, Constant.APPLICATION_JSON)
                .end(body.encodePrettily());
    }
}
