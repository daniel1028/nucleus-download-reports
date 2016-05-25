package org.gooru.nucleus.reports.infra.responses.writers;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.gooru.nucleus.reports.infra.constants.HttpConstants;
import org.gooru.nucleus.reports.infra.responses.transformers.ResponseTransformer;
import org.gooru.nucleus.reports.infra.responses.transformers.ResponseTransformerBuilder;

import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

class HttpServerResponseWriter implements ResponseWriter {

    private final RoutingContext routingContext;
    private final AsyncResult<Message<Object>> message;

    public HttpServerResponseWriter(RoutingContext routingContext, AsyncResult<Message<Object>> message) {
        this.routingContext = routingContext;
        this.message = message;
    }

    @Override
    public void writeResponse() {
        ResponseTransformer transformer = ResponseTransformerBuilder.build(message.result());
        final HttpServerResponse response = routingContext.response();
        // First set the status code
        response.setStatusCode(transformer.transformedStatus());
        // Then set the headers
        Map<String, String> headers = transformer.transformedHeaders();
        if (headers != null && !headers.isEmpty()) {
            // Never accept content-length from others, we do that
            headers.keySet().stream()
                .filter(headerName -> !headerName.equalsIgnoreCase(HttpConstants.HEADER_CONTENT_LENGTH))
                .forEach(headerName -> response.putHeader(headerName, headers.get(headerName)));
        }
        // Then it is turn of the body to be set and ending the response
        final String responseBody =
            ((transformer.transformedBody() != null) && (!transformer.transformedBody().isEmpty())) ?
                transformer.transformedBody().toString() : null;
        if (responseBody != null) {
            // As of today, we always serve JSON
            response.putHeader(HttpConstants.HEADER_CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
            response.putHeader(HttpConstants.HEADER_CONTENT_LENGTH,
                Integer.toString(responseBody.getBytes(StandardCharsets.UTF_8).length));
            response.end(responseBody);
        } else {
            response.end();
        }
    }
}
