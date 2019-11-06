package com.clickbait_filter;

import java.sql.DatabaseMetaData;
import java.util.HashMap;

import org.springframework.stereotype.Component;

@Component
public class DataSchema {
	public DataSchema() {
	}

	private HashMap<String, DatabaseMetaData> methodShemas = new HashMap<String, DatabaseMetaData>();

	public void setMethodShema(String methodName, DatabaseMetaData schema) {
		methodShemas.put(methodName, schema);
	}

	public DatabaseMetaData getMethodShema(String methodName) {
		return methodShemas.getOrDefault(methodName, null);
	}
}