package org.gooru.nucleus.reports.infra.util;

import org.gooru.nucleus.reports.infra.constants.HttpConstants;
import org.gooru.nucleus.reports.infra.constants.MessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;

/**
 * Created by ashish on 6/1/16.
 */
public final class MessageResponse {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageResponse.class);
    private final DeliveryOptions deliveryOptions;
    private final JsonObject reply;
    private final JsonObject event;

    // Private constructor
    private MessageResponse(JsonObject response) {
        this.deliveryOptions = new DeliveryOptions()
            .addHeader(MessageConstants.MSG_OP_STATUS, response.getString(MessageConstants.MSG_OP_STATUS));
        this.reply = response.getJsonObject(MessageConstants.RESP_CONTAINER_MBUS);
        this.event = response.getJsonObject(MessageConstants.RESP_CONTAINER_EVENT);
    }

    public DeliveryOptions deliveryOptions() {
        return this.deliveryOptions;
    }

    public JsonObject reply() {
        return this.reply;
    }

    public JsonObject event() {
        return this.event;
    }

    // Public builder with validations
    public static class Builder {
        private String status;
        private HttpConstants.HttpStatus httpStatus;
        private JsonObject responseBody;
        private final JsonObject headers;
        private JsonObject eventData;

        public Builder() {
            this.headers = new JsonObject();
        }

        // Setters for global headers
        public Builder successful() {
            this.status = MessageConstants.MSG_OP_STATUS_SUCCESS;
            return this;
        }

        public Builder failed() {
            this.status = MessageConstants.MSG_OP_STATUS_ERROR;
            return this;
        }

        public Builder validationFailed() {
            this.status = MessageConstants.MSG_OP_STATUS_VALIDATION_ERROR;
            return this;
        }

        // Setters for http status code
        public Builder setStatusCreated() {
            this.httpStatus = HttpConstants.HttpStatus.CREATED;
            return this;
        }

        public Builder setStatusOkay() {
            this.httpStatus = HttpConstants.HttpStatus.SUCCESS;
            return this;
        }

        public Builder setStatusNoOutput() {
            this.httpStatus = HttpConstants.HttpStatus.NO_CONTENT;
            return this;
        }

        public Builder setStatusBadRequest() {
            this.httpStatus = HttpConstants.HttpStatus.BAD_REQUEST;
            return this;
        }

        public Builder setStatusForbidden() {
            this.httpStatus = HttpConstants.HttpStatus.FORBIDDEN;
            return this;
        }

        public Builder setStatusNotFound() {
            this.httpStatus = HttpConstants.HttpStatus.NOT_FOUND;
            return this;
        }

        public Builder setStatusInternalError() {
            this.httpStatus = HttpConstants.HttpStatus.ERROR;
            return this;
        }

        public Builder setStatusHttpCode(HttpConstants.HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
            return this;
        }

        // Setters for headers
        public Builder setContentTypeJson() {
            this.headers.put(HttpConstants.HEADER_CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
            return this;
        }

        public Builder setHeader(String key, String value) {
            if (key == null || value == null) {
                return this;
            }
            if (key.equalsIgnoreCase(HttpConstants.HEADER_CONTENT_LENGTH)) {
                // Do not allow content length to be setup, it should be handled
                // in gateway
                return this;
            }
            this.headers.put(key, value);
            return this;
        }

        // Setters for Response body, interpreted as per status of message
        public Builder setResponseBody(JsonObject responseBody) {
            this.responseBody = responseBody;
            return this;
        }

        public Builder setEventData(JsonObject eventData) {
            this.eventData = eventData;
            return this;
        }

        public MessageResponse build() {
            JsonObject result;
            if (this.httpStatus == null || this.status == null) {
                LOGGER.error("Can't create response with invalid status or http status. Will return internal error");
                result = buildErrorResponse();
            } else {
                result = new JsonObject();
                result.put(MessageConstants.MSG_OP_STATUS, this.status)
                    .put(MessageConstants.RESP_CONTAINER_MBUS, buildResponseContainer());

                if (this.eventData != null && !this.eventData.isEmpty()) {
                    result.put(MessageConstants.RESP_CONTAINER_EVENT, this.eventData);
                }
            }
            return new MessageResponse(result);
        }

        private JsonObject buildErrorResponse() {
            JsonObject result =
                new JsonObject().put(MessageConstants.MSG_OP_STATUS, MessageConstants.MSG_OP_STATUS_ERROR);
            result.put(MessageConstants.RESP_CONTAINER_MBUS,
                new JsonObject().put(MessageConstants.MSG_HTTP_STATUS, HttpConstants.HttpStatus.ERROR.getCode())
                    .put(MessageConstants.MSG_HTTP_BODY,
                        new JsonObject().put(MessageConstants.MSG_OP_STATUS_ERROR, new JsonObject())));
            return result;
        }

        private JsonObject buildResponseContainer() {
            JsonObject result = new JsonObject();
            result.put(MessageConstants.MSG_HTTP_STATUS, this.httpStatus.getCode())
                .put(MessageConstants.MSG_HTTP_HEADERS, this.headers)
                .put(MessageConstants.MSG_HTTP_BODY, buildHttpBody());
            return result;
        }

        private JsonObject buildHttpBody() {
            JsonObject result = new JsonObject();
            if (this.responseBody == null) {
                this.responseBody = new JsonObject();
            }
            switch (this.status) {
            case MessageConstants.MSG_OP_STATUS_SUCCESS:
                result.put(MessageConstants.MSG_HTTP_RESPONSE, responseBody);
                break;
            case MessageConstants.MSG_OP_STATUS_ERROR:
                result.put(MessageConstants.MSG_HTTP_ERROR, responseBody);
                break;
            case MessageConstants.MSG_OP_STATUS_VALIDATION_ERROR:
                result.put(MessageConstants.MSG_HTTP_VALIDATION_ERROR, responseBody);
                break;
            }
            return result;
        }
    }
}
