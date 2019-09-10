package com.matrix.vertx.guice;

import com.google.common.base.Preconditions;
import com.google.inject.Injector;
import io.vertx.core.Verticle;
import io.vertx.core.impl.verticle.CompilingClassLoader;
import io.vertx.core.spi.VerticleFactory;

public class GuiceVerticleFactory implements VerticleFactory {
    public static final String PREFIX = "java-guice";
    private final Injector injector;

    public GuiceVerticleFactory(Injector injector) {
        this.injector = Preconditions.checkNotNull(injector);
    }

    @Override
    public String prefix() {
        return PREFIX;
    }

    /**
     * @param verticleName
     * @param classLoader
     * @return
     * @throws Exception
     * Call createVerticle to create an verticle
     * When deploying verticle(s) using a name, the name is used to select the actual verticle factory that will instantiate the verticle(s).
     * Verticle names can have a prefix - which is a string followed by a colon, which if present will be used to look-up the factory,
     */
    @Override
    public Verticle createVerticle(String verticleName, ClassLoader classLoader) throws Exception {
        //删除已有的前缀
        verticleName = VerticleFactory.removePrefix(verticleName);
        Class clazz;
        /**
         * Judge the verticleName
         * loadClass is used to load a class with the specified name
         * CompilingClassLoader is a Custom class loader
         * e.g. https://www.programcreek.com/java-api-examples/index.php?api=io.vertx.core.spi.VerticleFactory
         */
        if (verticleName.endsWith(".java")) {
            CompilingClassLoader compilingLoader = new CompilingClassLoader(classLoader, verticleName);
            String className = compilingLoader.resolveMainClassName();
            clazz = compilingLoader.loadClass(className);
        } else {
            clazz = classLoader.loadClass(verticleName);
        }

        return (Verticle) this.injector.getInstance(clazz);
    }
}
