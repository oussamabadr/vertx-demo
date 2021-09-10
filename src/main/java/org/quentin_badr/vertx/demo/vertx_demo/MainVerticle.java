package org.quentin_badr.vertx.demo.vertx_demo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

  public static void main(String[] args) {
    Vertx.vertx().deployVerticle(MainVerticle.class.getName());
  }

  @Override
  public void start(Promise<Void> startPromise) {
    vertx.createHttpServer().requestHandler(req -> req.response()
      .putHeader("content-type", "text/plain")
      .end("Hello from Vert.x!")).listen(8888, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        LOGGER.info("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }


}
