package org.gooru.nucleus.reports.infra.routes;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

/**
 * Created by ashish on 26/4/16.
 */
public interface RouteConfigurator {
    void configureRoutes(Vertx vertx, Router router, JsonObject config);
}
