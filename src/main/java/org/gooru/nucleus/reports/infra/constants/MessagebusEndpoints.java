package org.gooru.nucleus.reports.infra.constants;

public final class MessagebusEndpoints {

    public static final String MBEP_DOWNLOAD_REQUEST = "org.gooru.nucleus.reports.download.request";

    public static final String MBEP_DOWNLOAD_REQUEST_STATUS = "org.gooru.nucleus.reports.download.status";
    
    private MessagebusEndpoints() {
        throw new AssertionError();
    }
}
