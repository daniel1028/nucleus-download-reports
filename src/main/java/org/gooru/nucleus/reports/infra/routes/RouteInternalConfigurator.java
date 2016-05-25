package org.gooru.nucleus.reports.infra.routes;

import org.gooru.nucleus.reports.infra.constants.RouteConstants;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.dropwizard.MetricsService;
import io.vertx.ext.web.Router;

/**
 * Created by ashish on 26/4/16.
 */
final class RouteInternalConfigurator implements RouteConfigurator {
    @Override
    public void configureRoutes(Vertx vertx, Router router, JsonObject config) {
        final MetricsService metricsService = MetricsService.create(vertx);
        router.route(RouteConstants.INTERNAL_METRICS_ROUTE).handler(routingContext -> {
            JsonObject ebMetrics = metricsService.getMetricsSnapshot(vertx);
            routingContext.response().end(ebMetrics.toString());
        });

    }
}
