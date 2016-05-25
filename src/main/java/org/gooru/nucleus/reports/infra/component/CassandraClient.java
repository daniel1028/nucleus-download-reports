package org.gooru.nucleus.reports.infra.component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gooru.nucleus.reports.infra.constants.ConfigConstants;
import org.gooru.nucleus.reports.infra.shutdown.Finalizer;
import org.gooru.nucleus.reports.infra.startup.Initializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ProtocolVersion;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.datastax.driver.core.policies.ExponentialReconnectionPolicy;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public final class CassandraClient implements Initializer, Finalizer {

	private static final String DEFAULT_DATA_SOURCE = "defaultDataSource";
	private static final Logger LOGGER = LoggerFactory.getLogger(CassandraClient.class);
	// All the elements in this array are supposed to be present in config file
	// as keys as we are going to initialize them with the value associated with
	// that key
	private final List<String> CassandraProperties = Arrays.asList(DEFAULT_DATA_SOURCE);
	private Map<String, Session> registry = new HashMap<String, Session>();
	private Map<String, String> keySpace = new HashMap<String, String>();
	private ProtocolVersion protocolVersion;
	private volatile boolean initialized;

	private CassandraClient() {
		// TODO Auto-generated constructor stub
	}

	public static CassandraClient getInstance() {
		return Holder.INSTANCE;
	}

	@Override
	public void initializeComponent(Vertx vertx, JsonObject config) {
		// Skip if we are already initialized
		LOGGER.debug("Initialization called upon.");
		if (!initialized) {
			LOGGER.debug("May have to do initialization");
			// We need to do initialization, however, we are running it via
			// verticle instance which is going to run in
			// multiple threads hence we need to be safe for this operation
			synchronized (Holder.INSTANCE) {
				LOGGER.debug("Will initialize after double checking");
				if (!initialized) {
					LOGGER.debug("Initializing now");
					for (String cassandraSettings : CassandraProperties) {
						JsonObject cassandraConfig = config.getJsonObject(cassandraSettings);
						if (cassandraConfig != null) {
							Session cassandraSession = initializeDataSource(cassandraConfig);
							String cassKeyspace = cassandraConfig.getString(ConfigConstants.CASSANDRA_KEYSAPCE);
							String cassCluster = cassandraConfig.getString(ConfigConstants.CASSANDRA_CLUSTER);
							if (cassCluster.equalsIgnoreCase(ConfigConstants.ARCHIVE)) {
								registry.put(ConfigConstants.ARCHIVE, cassandraSession);
								keySpace.put(ConfigConstants.ARCHIVE, cassKeyspace);
							} else if (cassCluster.equalsIgnoreCase(ConfigConstants.ANALYTICS)) {
								registry.put(ConfigConstants.ANALYTICS, cassandraSession);
								keySpace.put(ConfigConstants.ANALYTICS, cassKeyspace);
							} else if (cassCluster.equalsIgnoreCase(ConfigConstants.EVENTS)) {
								registry.put(ConfigConstants.EVENTS, cassandraSession);
								keySpace.put(ConfigConstants.EVENTS, cassKeyspace);
							}
						}
					}
					initialized = true;
				}
			}
		}
	}

	public Session getAnalyticsCassandraSession() {
		return registry.get(ConfigConstants.ANALYTICS);
	}
	public ProtocolVersion getProtocolVersion(){
		return protocolVersion;
	}
	public Session getEventsCassandraSession() {
		return registry.get(ConfigConstants.EVENTS);
	}

	public Session getArchieveCassandraSession() {
		return registry.get(ConfigConstants.ARCHIVE);
	}

	public String getAnalyticsKeyspace() {
		return keySpace.get(ConfigConstants.ANALYTICS);
	}

	public String getEventsKeyspace() {
		return keySpace.get(ConfigConstants.EVENTS);
	}

	public String getArchieveKeyspace() {
		return keySpace.get(ConfigConstants.ARCHIVE);
	}

	private Session initializeDataSource(JsonObject dbConfig) {
		// The default DS provider is hikari, so if set explicitly or not set
		// use it, else error out
		Cluster archiveCassandraCluster = Cluster.builder()
				.withClusterName(dbConfig.getString(ConfigConstants.CASSANDRA_CLUSTER))
				.addContactPoint(dbConfig.getString(ConfigConstants.CASSANDRA_SEEDS))
				.withRetryPolicy(DefaultRetryPolicy.INSTANCE)
				.withReconnectionPolicy(new ExponentialReconnectionPolicy(1000, 30000)).build();
		Session session = archiveCassandraCluster.connect(dbConfig.getString(ConfigConstants.CASSANDRA_KEYSAPCE));
		protocolVersion = archiveCassandraCluster.getConfiguration().getProtocolOptions().getProtocolVersion();
		return session;
	}

	@Override
	public void finalizeComponent() {
		for (String datasource : CassandraProperties) {
			Session ds = registry.get(datasource);
			if (ds != null) {
				if (ds instanceof Session) {
					ds.close();
				}
			}
		}
	}

	private static final class Holder {
		private static final CassandraClient INSTANCE = new CassandraClient();
	}

}
