package org.gooru.nucleus.reports.infra.startup;

/**
 * This package will contain the machinery which needs to be initialized at
 * application startup. The verticle which is responsible for bootstraping the
 * application, should use this package to initialize rest of the machinery. The
 * primary interface exposed is Initializer which takes vertx and config as
 * parameters and does the required initialization of specific component. Each
 * individual component needs to register itself in Initializers which would be
 * used by main verticle to do the initialization of each component
 */