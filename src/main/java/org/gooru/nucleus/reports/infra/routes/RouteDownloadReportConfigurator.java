package org.gooru.nucleus.reports.infra.routes;

import org.gooru.nucleus.reports.infra.constants.ConfigConstants;
import org.gooru.nucleus.reports.infra.constants.MessagebusEndpoints;
import org.gooru.nucleus.reports.infra.constants.RouteConstants;
import org.gooru.nucleus.reports.infra.routes.util.RouteRequestUtility;
import org.gooru.nucleus.reports.infra.routes.util.RouteResponseUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

/**
 * Created by ashish on 26/4/16.
 */
final class RouteDownloadReportConfigurator implements RouteConfigurator {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteDownloadReportConfigurator.class);

    @Override
    public void configureRoutes(Vertx vertx, Router router, JsonObject config) {
        final EventBus eb = vertx.eventBus();
        final long mbusTimeout = config.getLong(ConfigConstants.MBUS_TIMEOUT, 30L);
        router.get(RouteConstants.DOWNLOAD_STATUS).handler(routingContext -> {
        	DeliveryOptions options = new DeliveryOptions().setSendTimeout(mbusTimeout * 1000);
        	routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8");
            eb.send(MessagebusEndpoints.MBEP_DOWNLOAD_REQUEST_STATUS, new RouteRequestUtility().getBodyForMessage(routingContext),
                    options, reply -> new RouteResponseUtility().responseHandler(routingContext, reply, LOGGER));
        });
        
        router.get(RouteConstants.DOWNLOAD_REQUEST).handler(routingContext -> {
        	DeliveryOptions options = new DeliveryOptions().setSendTimeout(mbusTimeout * 1000);
        	routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8");
            eb.send(MessagebusEndpoints.MBEP_DOWNLOAD_REQUEST, new RouteRequestUtility().getBodyForMessage(routingContext),
                    options, reply -> new RouteResponseUtility().responseHandler(routingContext, reply, LOGGER));
        });

    }    
}
