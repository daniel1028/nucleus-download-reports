package org.gooru.nucleus.reports.infra.responses.writers;

import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.web.RoutingContext;

public class ResponseWriterBuilder {
    private final AsyncResult<Message<Object>> message;
    private final RoutingContext routingContext;

    public ResponseWriterBuilder(RoutingContext routingContext, AsyncResult<Message<Object>> message) {
        if (routingContext == null || message == null) {
            throw new IllegalArgumentException(
                "Invalid or null routing context or message for Response Writer creation");
        }
        this.routingContext = routingContext;
        this.message = message;
    }

    public ResponseWriter build() {
        return new HttpServerResponseWriter(this.routingContext, this.message);
    }
}
