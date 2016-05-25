package org.gooru.nucleus.reports.infra.routes.util;

import java.util.List;
import java.util.Map;

import org.gooru.nucleus.reports.infra.constants.MessageConstants;

import io.netty.handler.codec.http.QueryStringDecoder;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by ashish on 26/4/16.
 */
public class RouteRequestUtility {

    /*
     * If the incoming request is POST or PUT, it is expected to have a payload
     * of JSON which is returned. In case of GET request, any query parameters
     * will be used to create a JSON body. Note that only query string is used
     * and not path matchers. In case of no query parameters send out empty Json
     * object, but don't send null
     */

    public JsonObject getBodyForMessage(RoutingContext routingContext) {
        JsonObject httpBody, result = new JsonObject();
        if (routingContext.request().method().name().equals(HttpMethod.POST.name()) || routingContext.request().method()
            .name().equals(HttpMethod.PUT.name())) {
            httpBody = routingContext.getBodyAsJson();
        } else if (routingContext.request().method().name().equals(HttpMethod.GET.name())) {
            httpBody = new JsonObject();
            String uri = routingContext.request().query();
            if (uri != null) {
                QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri, false);
                Map<String, List<String>> prms = queryStringDecoder.parameters();
                if (!prms.isEmpty()) {
                    for (Map.Entry<String, List<String>> entry : prms.entrySet()) {
                        httpBody.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        } else {
            httpBody = new JsonObject();
        }
        result.put(MessageConstants.MSG_HTTP_BODY, httpBody);
        // NOTE that this is going to be a system user id which needs to be hard coded during Auth flow
        result.put(MessageConstants.MSG_USER_ID, (String) routingContext.get(MessageConstants.MSG_USER_ID));
        // And this should be client id which should come from incoming request header
        result.put(MessageConstants.MSG_HEADER_CLIENTID,
            (String) routingContext.get(MessageConstants.MSG_HEADER_CLIENTID));
        return result;
    }
}