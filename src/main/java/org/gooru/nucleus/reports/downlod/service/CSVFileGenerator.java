package org.gooru.nucleus.reports.downlod.service;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.gooru.nucleus.reports.infra.constants.ConfigConstants;

public class CSVFileGenerator {
	
	@Resource(name = "filePath")
	private Properties filePath;
	
	public static final String DEFAULT_FILE_NAME = "insights";
	public static final String FILE_REAL_PATH = "insights.file.real.path";
	public static final String FILE_APP_PATH = "insights.file.app.path";
	public static final String FIELDS_TO_TIME_FORMAT = "time_spent|timeSpent|totalTimespent|avgTimespent|timespent|totalTimeSpentInMs|.*Timespent.*";
	
	public File generateCSVReport(boolean isNewFile,String fileName, List<Map<String, Object>> resultSet) throws ParseException, IOException {

		boolean headerColumns = false;
		File csvfile = new File(setFilePath(fileName));
		PrintStream stream = generatePrintStream(isNewFile,csvfile);
		for (Map<String, Object> map : resultSet) {
			writeToStream(map,stream,headerColumns);
			headerColumns = true;
		}
		writeToFile(stream);
		return csvfile;
	}

	public File generateCSVReport(boolean isNewFile,String fileName, Map<String, Object> resultSet) throws ParseException, IOException {

		boolean headerColumns = false;
		File csvfile = new File(setFilePath(fileName));
		PrintStream stream = generatePrintStream(isNewFile,csvfile);
		writeToStream(resultSet,stream,headerColumns);
		writeToFile(stream);
		return csvfile;
	}
	
	public void includeEmptyLine(boolean isNewFile,String fileName, int lineCount) throws FileNotFoundException{
		
		File csvfile = new File(setFilePath(fileName));
		PrintStream stream = generatePrintStream(isNewFile,csvfile);
		for(int i =0; i < lineCount;i++){
			stream.println(ConfigConstants.STRING_EMPTY);
		}
		writeToFile(stream);
	}
	
	private Object appendDQ(Object key) {
	    return ConfigConstants.DOUBLE_QUOTES + key + ConfigConstants.DOUBLE_QUOTES;
	}
	
	private void writeToStream(Map<String,Object> map,PrintStream stream,boolean headerColumns){
			if (!headerColumns) {
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					stream.print(appendDQ(entry.getKey()) + ConfigConstants.COMMA);
					headerColumns = true;
				}
				// print new line
				stream.println("");
			}
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				Object value = entry.getValue();
				if(entry.getKey().matches(FIELDS_TO_TIME_FORMAT)) {
					value = convertMillisecondsToTime(((Number)value).longValue());
				}
				stream.print(appendDQ(value) + ConfigConstants.COMMA);
			}
			// print new line
			stream.println(ConfigConstants.STRING_EMPTY);
	}

	private PrintStream generatePrintStream(boolean isNewFile,File file) throws FileNotFoundException{
		PrintStream stream = null;
		File csvfile = file;
		if(isNewFile){
			stream = new PrintStream(new BufferedOutputStream(new FileOutputStream(csvfile, false)));
		}else{
			stream = new PrintStream(new BufferedOutputStream(new FileOutputStream(csvfile, true)));
		}
		return stream;
	}

	private void writeToFile(PrintStream stream){
		stream.flush();
		stream.close();
	}
	
	public String setFilePath(String file){
		
		String fileName = this.getFilePath().getProperty(FILE_REAL_PATH);
		if(file != null && (!file.isEmpty())){
			fileName += file;
		}else{
			fileName +=DEFAULT_FILE_NAME;
		}
		return fileName+ConfigConstants.CSV_EXT;
	}

	public String getFilePath(String file){
		
		String fileName = this.getFilePath().getProperty(FILE_APP_PATH);
		if(file != null && (!file.isEmpty())){
			fileName += file;
		}else{
			fileName +=DEFAULT_FILE_NAME;
		}
		return fileName;
	}

	public Properties getFilePath() {
		return filePath;
	}

	public void setFilePath(Properties filePath) {
		this.filePath = filePath;
	}

	private String convertMillisecondsToTime(long millis) {
		String time = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
			    TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
			    TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
		return time;
	}
}
