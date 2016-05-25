package org.gooru.nucleus.reports.infra.bootstrap;

import java.util.ArrayList;
import java.util.List;

import org.gooru.nucleus.reports.infra.deployment.VerticleRegistry;
import org.gooru.nucleus.reports.infra.shutdown.Finalizer;
import org.gooru.nucleus.reports.infra.shutdown.Finalizers;
import org.gooru.nucleus.reports.infra.startup.Initializer;
import org.gooru.nucleus.reports.infra.startup.Initializers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * This is the main deploy verticle which is responsible to deploy all other
 * verticles. Once the deployment of verticles is done, it will also initialize
 * the application initializers.
 *
 * @author ashish
 */
public class DeployVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeployVerticle.class);

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Future<Void> deployFuture = Future.future();
        Future<Void> startApplicationFuture = Future.future();

        deployVerticles(deployFuture);
        startApplication(startApplicationFuture);

        CompositeFuture.all(deployFuture, startApplicationFuture).setHandler(result -> {
            if (result.succeeded()) {
                LOGGER.info("All verticles deployed and application started successfully");
                startFuture.complete();
            } else {
                LOGGER.error("Deployment or app startup failure", result.cause());
                startFuture.fail(result.cause());

                // Not much options now, no point in continuing
                Runtime.getRuntime().halt(1);
            }
        });
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        shutdownApplication(stopFuture);
    }

    private void startApplication(Future<Void> startApplicationFuture) {
        vertx.executeBlocking(future -> {
            Initializers initializers = new Initializers();
            try {
                for (Initializer initializer : initializers) {
                    initializer.initializeComponent(vertx, config());
                }
                future.complete();
            } catch (IllegalStateException ie) {
                LOGGER.error("Error initializing application", ie);
                future.fail(ie);
            }
        }, result -> {
            if (result.succeeded()) {
                LOGGER.info("All verticles deployed and application started successfully");
                startApplicationFuture.complete();
            } else {
                LOGGER.warn("App startup failure", result.cause());
                startApplicationFuture.fail(result.cause());
            }
        });
    }

    private void shutdownApplication(Future<Void> shutdownApplicationFuture) {
        vertx.executeBlocking(future -> {
            Finalizers finalizers = new Finalizers();
            for (Finalizer finalizer : finalizers) {
                finalizer.finalizeComponent();
            }
            future.complete();
        }, result -> {
            if (result.succeeded()) {
                LOGGER.info("Component finalization for application shutdown done successfully");
                shutdownApplicationFuture.complete();
            } else {
                LOGGER.warn("App shutdown failure", result.cause());
                shutdownApplicationFuture.fail(result.cause());
            }
        });
    }

    private void deployVerticles(Future<Void> future) {
        VerticleRegistry registry = new VerticleRegistry();
        List<Future> futures = new ArrayList<>();
        for (String verticleName : registry) {
            Future<String> deployFuture = Future.future();
            futures.add(deployFuture);

            JsonObject config = config().getJsonObject(verticleName);
            if (config.isEmpty()) {
                vertx.deployVerticle(verticleName, deployFuture.completer());
            } else {
                DeploymentOptions options = new DeploymentOptions(config);
                vertx.deployVerticle(verticleName, options, deployFuture.completer());
            }
        }
        CompositeFuture.all(futures).setHandler(result -> {
            if (result.succeeded()) {
                LOGGER.info("All verticles deployed successfully");
                future.complete();
            } else {
                LOGGER.warn("Deployment failure", result.cause());
                future.fail(result.cause());
            }
        });
    }

}
