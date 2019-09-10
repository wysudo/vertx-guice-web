package com.matrix.vertx;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.matrix.vertx.constant.Constant;
import com.matrix.vertx.guice.GuiceInjector;
import com.matrix.vertx.verticle.ApiVerticle;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.core.spi.resolver.ResolverProvider;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.config.ConfigRetriever;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainVerticle extends AbstractVerticle {
    private static final Logger LOG = LogManager.getLogger(MainVerticle.class);
    @Inject
    private SharedData sharedData;
    private JsonObject configuration;
    LocalMap localMap;


    /**
     * 一般情况下，如果有些代码需要在项目启动的时候就执行，这时候就需要静态代码块。
     * 比如一个项目启动需要加载的很多配置文件等资源，我们就可以都放入静态代码块中。
     */
    static {
        System.setProperty(ResolverProvider.DISABLE_DNS_RESOLVER_PROP_NAME,"true"); //用于解析DNS
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        localMap = sharedData.getLocalMap(Constant.SYSTEM_DATA_KEY);
        this.retrieverConfig()
                .flatMap(i -> deplayAll())
                .flatMap(i -> deployDeployments())
                .subscribe(ar -> {
                   LOG.info("all verticles deployed");
                },error ->{
                    error.printStackTrace();
                    LOG.error("Fail to deploy some verticle");
                });
    }

    private Single<String> deplayAll(){
        List<String> guiceBinders = Lists.newArrayList(GuiceInjector.class.getName());
        this.configuration.put("guice_binder", guiceBinders);
        List<Class> clazzs = Lists.newArrayList(ApiVerticle.class);
        return this.deployVerticle(clazzs, this.configuration, 1);
    }

    private Single<String> deployVerticle(List<Class> clazzs, JsonObject config, int instances){
        DeploymentOptions options = new DeploymentOptions().setConfig(config).setInstances(instances).setWorker(false);
        Single<String> single = Single.just("q");
        for (Class clazz : clazzs){
            String deploymentName = String.format("%s%s", config.containsKey("guice_binder") ?  "java-guice:" : "", clazz.getName());
            single = single.flatMap(i -> vertx.rxDeployVerticle(deploymentName, options));
        }
        return single;
    }

    private Single<String> deployDeployments(){
        Object systemData = this.configuration.getValue(Constant.SYSTEM_DATA);
        JsonObject systemDataJson = null;
        if (systemData != null && systemData instanceof String){
            String systemDatas = this.configuration.getString(Constant.SYSTEM_DATA);
            File file = new File(systemDatas);
            if(file.exists()){
                LOG.info("=== file.exists()== :{} ,{} ", systemDatas, file.exists());
                try {
                    List<String> lines = Files.readLines(file, StandardCharsets.UTF_8);
                    StringBuffer sb = new StringBuffer();
                    for (String line : lines){
                        sb.append(line);
                    }
                    systemDatas = sb.toString();
                }catch (IOException e){
                    LOG.error("deployments  error: {}",e);
                }
            }else {
                systemDatas = StringEscapeUtils.unescapeJson(systemDatas);
            }
            systemDataJson = new JsonObject(systemDatas);
        }else if(systemData instanceof JsonObject){
            systemDataJson = this.configuration.getJsonObject(Constant.SYSTEM_DATA);
        }
        if (systemDataJson != null && !systemDataJson.isEmpty()) {
            LOG.info("== ENV  PIPELINE_DATA: {}", systemDataJson);
            systemDataJson.put("createTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss")));
            localMap.put(Constant.SYSTEM_DATA_KEY, systemDataJson);
        }
        return Single.just("");
    }

    private Single<Void> retrieverConfig(){
        io.vertx.reactivex.core.Future<Void> future = io.vertx.reactivex.core.Future.future();
        //此配置仓库仅仅从文件中读取配置
        ConfigStoreOptions fileStore = new ConfigStoreOptions()
                .setType("file")
                .setFormat("properties")
                .setConfig(new JsonObject().put("path","application.properties"));

        //此配置仓库将环境变量中的键值对映射成 JSON 对象传入，作为全局配置
        ConfigStoreOptions environmentStore = new ConfigStoreOptions()
                .setType("env");

        //此配置仓库将系统属性中的键值对映射成 JSON 对象传入，作为全局配置
        ConfigStoreOptions systemStore = new ConfigStoreOptions()
                .setType("sys");

        //定期地从配置仓库处读取配置,默认情况下，配置的刷新时间是 5 秒钟
        ConfigRetriever configRetriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions()
            .addStore(fileStore)
            .addStore(environmentStore)
            .addStore(systemStore));
        configRetriever.getConfig(ar ->{
            if (ar.succeeded()){
                //将数据封装成map类型
                Map<String, Object> mappedEnv = ar.result().stream()
                        .collect(Collectors.toMap(key -> convertEnv(key.getKey()), Map.Entry::getValue, (x1,x2) -> {
                            return x2;
                        }));
                this.configuration = new JsonObject(mappedEnv);

                future.complete();
            }else {
                future.fail(ar.cause());
            }
        });
        return future.rxSetHandler();
    }

    private String convertEnv(String key) {
        if (isBlank(key)) {
            return key;
        }
        return lowerCase(key).replace('_', '.');
    }
    private boolean isBlank(String key) {
        return (key == null || key.trim().equals(""));
    }

    private String lowerCase(String key) {
        if (key == null) {
            return null;
        } else {
            return key.toLowerCase();
        }
    }
}
