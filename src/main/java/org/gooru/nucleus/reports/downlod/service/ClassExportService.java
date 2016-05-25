package org.gooru.nucleus.reports.downlod.service;

import java.io.File;

public interface ClassExportService {

	static ClassExportService instance(){
		 return new ClassExportServiceImpl();
	}
	
	File exportCsv(String classId, String courseId, String unitId, String lessonId, String collectionId, String type, String userId);

}
