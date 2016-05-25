package org.gooru.nucleus.reports.downlod.service;

import com.datastax.driver.core.ProtocolVersion;
import com.datastax.driver.core.ResultSet;

public interface CqlCassandraDao {

	static CqlCassandraDao instance(){
		return new CqlCassandraDaoImpl();
	}
	
	ResultSet getArchievedClassMembers(String classId);

	ResultSet getArchievedClassData(String rowKey);

	ResultSet getArchievedContentTitle(String contentId);

	ProtocolVersion getClusterProtocolVersion();

	ResultSet getArchievedUserDetails(String userId);

	ResultSet getArchievedCollectionItem(String contentId);

	ResultSet getArchievedCollectionRecentSessionId(String rowKey);
	
	ResultSet getArchievedSessionData(String sessionId);
}
