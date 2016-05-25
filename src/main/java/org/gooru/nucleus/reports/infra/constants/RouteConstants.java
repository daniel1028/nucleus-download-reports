package org.gooru.nucleus.reports.infra.constants;

/**
 * Created by ashish on 26/4/16.
 */
public final class RouteConstants {

    private static final String API_VERSION = "v1";
    private static final String API_BASE_ROUTE = "/api/nucleus-download-reports/" + API_VERSION + '/';
    private static final String CLASS = "class";
    private static final String COURSE = "course";
    private static final char SLASH = '/';
    private static final char COLON = ':';

    public static final String CLASS_ID = "classId";
    public static final String COURSE_ID = "courseId";

    public static final String INTERNAL_METRICS_ROUTE = API_BASE_ROUTE + "internal/metrics";
    
    public static final String DOWNLOAD_REQUEST = API_BASE_ROUTE + CLASS + SLASH + COLON + CLASS_ID + SLASH + COURSE + SLASH + COLON + COURSE_ID + SLASH + "export";
    public static final String DOWNLOAD_STATUS =  API_BASE_ROUTE + CLASS + SLASH + COLON + CLASS_ID + SLASH + COURSE + SLASH + COLON + COURSE_ID + SLASH + "status";

    private RouteConstants() {
        throw new AssertionError();
    }
}
