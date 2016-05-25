package org.gooru.nucleus.reports.infra.deployment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gooru.nucleus.reports.infra.bootstrap.DeployVerticle;

/**
 * This is repository for verticles which needs to be deployed by
 * {@link DeployVerticle}
 *
 * @author ashish
 */
public class VerticleRegistry implements Iterable<String> {

    private static final String DOWNLOAD_REQUEST_VERTICLE = "org.gooru.nucleus.reports.download.DownloadReportVerticle";

    private static final String HTTP_VERTICLE = "org.gooru.nucleus.reports.infra.server.HttpVerticle";

    private final Iterator<String> internalIterator;

    public VerticleRegistry() {
        List<String> initializers = new ArrayList<>();
        initializers.add(DOWNLOAD_REQUEST_VERTICLE);
        initializers.add(HTTP_VERTICLE);
        internalIterator = initializers.iterator();
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {

            @Override
            public boolean hasNext() {
                return internalIterator.hasNext();
            }

            @Override
            public String next() {
                return internalIterator.next();
            }

        };
    }

}
