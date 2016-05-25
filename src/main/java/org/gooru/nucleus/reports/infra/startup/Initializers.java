package org.gooru.nucleus.reports.infra.startup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gooru.nucleus.reports.infra.component.CassandraClient;
import org.gooru.nucleus.reports.infra.component.UtilityManager;

public class Initializers implements Iterable<Initializer> {

    private final Iterator<Initializer> internalIterator;

    public Initializers() {
        List<Initializer> initializers = new ArrayList<>();
        initializers.add(CassandraClient.getInstance());
        initializers.add(UtilityManager.getInstance());
        internalIterator = initializers.iterator();
    }

    @Override
    public Iterator<Initializer> iterator() {
        return new Iterator<Initializer>() {

            @Override
            public boolean hasNext() {
                return internalIterator.hasNext();
            }

            @Override
            public Initializer next() {
                return internalIterator.next();
            }

        };
    }

}
