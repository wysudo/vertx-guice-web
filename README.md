# vertx-guice
Enable Verticle dependency injection in Vert.x using Guice. 

[![Build Status](https://travis-ci.org/intappx/vertx-guice.svg?branch=master)](https://travis-ci.org/intappx/vertx-guice)
[![Code Coverage](https://img.shields.io/codecov/c/github/intappx/vertx-guice.svg)](https://codecov.io/github/intappx/vertx-guice)
[![Maven Central](https://img.shields.io/maven-central/v/com.intapp/vertx-guice.svg?maxAge=2592000)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.intapp%22%20AND%20a%3A%22vertx-guice%22)

It is designed to use single injector per Vert.x instance.
It means that `Singleton' scope is supported and works as expected. This was the main reason to implement this library instead of using [vertx-guice](https://github.com/englishtown/vertx-guice) library from English Town.

### What does it provide
* `GuiceVerticleFactory` which uses Guice for verticle creation. To be used, it should be registered in Vertx and verticle should be deployed with `java-guice:` prefix. 
* `GuiceVertxLauncher` which extends default [vert.x launcher](http://vertx.io/docs/vertx-core/java/#_the_vert_x_launcher). It performs all necessary work related to the creating single injector per Vert.x instance, registering `GuiceVerticleFactory`. 
To register application specific dependencies, you can also create a sub-class of `GuiceVertxLauncher` and use it to start your application.
* `GuiceVertxDeploymentManager` which implements convenient methods to deploy verticles programmatically by specified class using `GuiceVerticleFactory` factory.
* `VertxModule` which contains binding for vertx itself and it's cached objects like EventBus, FileSystem, SharedData.
