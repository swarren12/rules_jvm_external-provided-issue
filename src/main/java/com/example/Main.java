package com.example;


import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;

import org.checkerframework.checker.units.qual.kg;

public class Main
{
    public static void main(final String[] args)
    {
        final Vertx vertx = Vertx.vertx();

        final HttpServerOptions serverOptions = new HttpServerOptions()
                .setRegisterWebSocketWriteHandlers(true)
                .setPort(0);

        final HttpServer httpServer = vertx.createHttpServer(serverOptions);

        final Router router = Router.router(vertx);
        router.route().handler(rc -> rc.response().end("Hello World!"));

        final @kg int weight = 100;

        httpServer.requestHandler(router)
                  .listen()
                  .onSuccess(server -> {
                      System.out.println("Created HTTP server on port " + server.actualPort());
                      System.exit(0);
                  })
                  .onFailure(err -> {
                      System.err.println("Creation failed: " + err.getMessage());
                      System.exit(1);
                  });
    }

}
