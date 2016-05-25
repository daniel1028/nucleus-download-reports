package org.gooru.nucleus.reports.infra.responses.transformers;

/**
 * This package contains machinery to use the Message coming from Message Bus from other handlers and carving out an Http
 * response out of it.
 * To carve out Http response, the machinery should provide
 * 1. Status
 * 2. Http headers
 * 3. Response body (if any)
 * <p>
 * The implementation contract is provided by ResponseTransformer and is implemented by HttpResponseTransformer. Here is how
 * the Message should look like:
 * <p>
 * ----------------------|
 * Message Headers      |
 * ---------------------|
 * |
 * Message Body         |
 * |
 * ---------------------|
 * <p>
 * If header contains header named MessageConstants.MSG_OP_STATUS with header value MessageConstants.MSG_OP_STATUS_SUCCESS
 * then request is successful.
 * <p>
 * Following key values should be present in JSON object.
 * key: MessageConstants.MSG_HTTP_STATUS
 * value: Status code which needs to be sent as Http status code. Type is int.
 * <p>
 * key: MessageConstants.MSG_HTTP_HEADERS
 * value: Http headers that need to be sent. Type is Json object which contains key as header names and values as header values.
 * The keys and values should be string.
 * <p>
 * key: MessageConstants.MSG_HTTP_BODY
 * value: This is envelope of body and it could contain either of MessageConstants.MSG_HTTP_RESPONSE, or MessageConstants.MSG_HTTP_ERROR
 * or MessageConstants.MSG_HTTP_VALIDATION_ERROR. Which one should be read is dependent on value of MessageConstants.MSG_OP_STATUS
 * message header as outlined above. In case value is MessageConstants.MSG_OP_STATUS_SUCCESS, MessageConstants.MSG_HTTP_RESPONSE is used.
 * In case value is MessageConstants.MSG_OP_STATUS_VALIDATION_ERROR, MessageConstants.MSG_HTTP_VALIDATION_ERROR is used.
 * In case value is MessageConstants.MSG_OP_STATUS_ERROR, MessageConstants.MSG_HTTP_ERROR is used
 * <p>
 * key: MessageConstants.MSG_HTTP_RESPONSE
 * value: Json Object which should be sent as is to response
 * <p>
 * key: MessageConstants.MSG_HTTP_ERROR
 * value: Json Object which should be sent as is to response
 * <p>
 * key: MessageConstants.MSG_HTTP_VALIDATION_ERROR
 * value: Json Array of Json objects which may contain keys as field names and values as errors on those fields
 */