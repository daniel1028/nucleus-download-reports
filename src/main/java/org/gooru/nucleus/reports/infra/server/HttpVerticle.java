package org.gooru.nucleus.reports.infra.server;

import org.gooru.nucleus.reports.infra.constants.ConfigConstants;
import org.gooru.nucleus.reports.infra.routes.RouteConfiguration;
import org.gooru.nucleus.reports.infra.routes.RouteConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

public class HttpVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpVerticle.class);

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        LOGGER.info("Starting HttpVerticle.");
        final HttpServer httpServer = vertx.createHttpServer();

        // Register the routes
        final Router router = Router.router(vertx);
        configureRoutes(router);

        // If the port is not present in configuration then we end up
        // throwing as we are casting it to int. This is what we want.
        final int port = config().getInteger(ConfigConstants.HTTP_PORT);
        LOGGER.info("Http server starting on port {}", port);
        httpServer.requestHandler(router::accept).listen(port, result -> {
            if (result.succeeded()) {
                LOGGER.info("HTTP Server started successfully");
                startFuture.complete();
            } else {
                LOGGER.error("Not able to start HTTP Server", result.cause());
                startFuture.fail(result.cause());
            }
        });

    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        // Currently a no op
        stopFuture.complete();
    }

    private void configureRoutes(final Router router) {
        RouteConfiguration rc = new RouteConfiguration();
        for (RouteConfigurator configurator : rc) {
            configurator.configureRoutes(vertx, router, config());
        }
    }

}
