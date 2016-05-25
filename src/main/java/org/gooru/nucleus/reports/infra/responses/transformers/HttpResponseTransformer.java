package org.gooru.nucleus.reports.infra.responses.transformers;

import java.util.HashMap;
import java.util.Map;

import org.gooru.nucleus.reports.infra.constants.MessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

class HttpResponseTransformer implements ResponseTransformer {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseTransformer.class);
    private final Message<Object> message;
    private boolean transformed = false;
    private Map<String, String> headers;
    private int httpStatus;
    private JsonObject httpBody;

    public HttpResponseTransformer(Message<Object> message) {
        this.message = message;
        if (message == null) {
            LOG.error("Invalid or null Message<Object> for initialization");
            throw new IllegalArgumentException("Invalid or null Message<Object> for initialization");
        }
        if (!(message.body() instanceof JsonObject)) {
            LOG.error("Message body should be JsonObject");
            throw new IllegalArgumentException("Message body should be JsonObject");
        }
    }

    @Override
    public void transform() {
        if (!this.transformed) {
            processTransformation();
            this.transformed = true;
        }
    }

    @Override
    public JsonObject transformedBody() {
        transform();
        return this.httpBody;
    }

    @Override
    public Map<String, String> transformedHeaders() {
        transform();
        return this.headers;
    }

    @Override
    public int transformedStatus() {
        transform();
        return this.httpStatus;
    }

    private void processTransformation() {
        JsonObject messageBody = (JsonObject) message.body();

        // First initialize the http status
        this.httpStatus = messageBody.getInteger(MessageConstants.MSG_HTTP_STATUS);

        // Then initialize the headers
        processHeaders(messageBody);
        JsonObject httpBodyContainer = messageBody.getJsonObject(MessageConstants.MSG_HTTP_BODY);
        // Now delegate the body handling
        String result = message.headers().get(MessageConstants.MSG_OP_STATUS);
        if (result != null && result.equalsIgnoreCase(MessageConstants.MSG_OP_STATUS_SUCCESS)) {
            processSuccessTransformation(httpBodyContainer);
        } else if (result != null && result.equalsIgnoreCase(MessageConstants.MSG_OP_STATUS_ERROR)) {
            processErrorTransformation(httpBodyContainer);
        } else if (result != null && result.equalsIgnoreCase(MessageConstants.MSG_OP_STATUS_VALIDATION_ERROR)) {
            processValidationErrorTransformation(httpBodyContainer);
        } else {
            LOG.error("Invalid or incorrect message header passed on for operation");
            throw new IllegalStateException("Invalid or incorrect message header passed on for operation");
        }
        // Now that we are done, mark it as transformed
        this.transformed = true;
    }

    private void processHeaders(JsonObject jsonObject) {
        JsonObject jsonHeaders = jsonObject.getJsonObject(MessageConstants.MSG_HTTP_HEADERS);
        this.headers = new HashMap<>();
        if (jsonHeaders != null && !jsonHeaders.isEmpty()) {
            Map<String, Object> headerMap = jsonHeaders.getMap();
            for (Map.Entry<String, Object> entry : headerMap.entrySet()) {
                this.headers.put(entry.getKey(), entry.getValue().toString());
            }
        }
    }

    private void processValidationErrorTransformation(JsonObject messageBody) {
        this.httpBody = messageBody.getJsonObject(MessageConstants.MSG_HTTP_VALIDATION_ERROR);
    }

    private void processErrorTransformation(JsonObject messageBody) {
        this.httpBody = messageBody.getJsonObject(MessageConstants.MSG_HTTP_ERROR);
    }

    private void processSuccessTransformation(JsonObject messageBody) {
        this.httpBody = messageBody.getJsonObject(MessageConstants.MSG_HTTP_RESPONSE);
    }

}
