package org.gooru.nucleus.reports.infra.shutdown;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gooru.nucleus.reports.infra.component.CassandraClient;

public class Finalizers implements Iterable<Finalizer> {

    private final Iterator<Finalizer> internalIterator;

    public Finalizers() {
        List<Finalizer> finalizers = new ArrayList<>();
        finalizers.add(CassandraClient.getInstance());
        internalIterator = finalizers.iterator();
    }

    @Override
    public Iterator<Finalizer> iterator() {
        return new Iterator<Finalizer>() {

            @Override
            public boolean hasNext() {
                return internalIterator.hasNext();
            }

            @Override
            public Finalizer next() {
                return internalIterator.next();
            }

        };
    }

}
