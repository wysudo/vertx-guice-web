package com.matrix.vertx.rest;

import com.google.inject.Inject;
import com.matrix.vertx.constant.Constant;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @ClassName ApiHandler
 * @Author wby
 * @Date 2019/9/9 16:14
 * @Version 1.0
 * @Description TODO
 **/
public class ApiHandler implements Handler<RoutingContext> {
    private static final Logger log = LogManager.getLogger(ApiHandler.class);
    @Inject
    private SharedData sharedData;
    LocalMap localMap;
    @Override
    public void handle(RoutingContext context) {
        JsonObject body= context.getBodyAsJson();
        localMap = sharedData.getLocalMap(Constant.SYSTEM_DATA_KEY);
        body.put("createTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss")));
        localMap.put(Constant.SYSTEM_DATA_KEY, body);

        context.response()
                .setStatusCode(200)
                .putHeader(Constant.CONTENT_TYPE,Constant.APPLICATION_JSON)
                .end(body.encodePrettily());
    }
}
