package org.gooru.nucleus.reports.infra.db;

import org.gooru.nucleus.reports.infra.util.ExecutionResult;
import org.gooru.nucleus.reports.infra.util.MessageResponse;

/**
 * Definition of how the db interactions are going to happen This will be three
 * stepped approach. First one is not dependent on db connection being present
 * and thus can be used to check the sanity of request. Second one would be to
 * validate with respect to database, and thus requires a db connection. Third
 * one is workhorse and is actual execution.
 */
public interface DBHandler {
    ExecutionResult<MessageResponse> checkSanity();

    ExecutionResult<MessageResponse> validateRequest();

    ExecutionResult<MessageResponse> executeRequest();

    boolean handlerReadOnly();
}
