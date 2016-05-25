package org.gooru.nucleus.reports.infra.routes.util;

import org.gooru.nucleus.reports.infra.responses.writers.ResponseWriterBuilder;
import org.slf4j.Logger;

import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by ashish on 26/4/16.
 */
public class RouteResponseUtility {

    public void responseHandler(final RoutingContext routingContext, final AsyncResult<Message<Object>> reply,
        final Logger LOG) {
        if (reply.succeeded()) {
            new ResponseWriterBuilder(routingContext, reply).build().writeResponse();
        } else {
            LOG.error("Not able to send message", reply.cause());
            routingContext.response().setStatusCode(500).end();
        }
    }
}
