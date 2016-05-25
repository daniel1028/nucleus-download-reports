package org.gooru.nucleus.reports.infra.responses.transformers;

import io.vertx.core.eventbus.Message;

public final class ResponseTransformerBuilder {

    public static ResponseTransformer build(Message<Object> message) {
        return new HttpResponseTransformer(message);
    }

    private ResponseTransformerBuilder() {
        throw new AssertionError();
    }
}
