package org.gooru.nucleus.reports.infra.shutdown;

/**
 * This package will contain the machinery which needs to be finalized at
 * application shutdown. The verticle which is responsible for bootstraping the
 * application, should use this package to finalize rest of the machinery. The
 * primary interface exposed is Finalizer which does the required finalization
 * of specific component. Each individual component needs to register itself in
 * Finalizers which would be used by main verticle to do the finalization of
 * each component
 */