package com.clickbait_filter.services;

import java.sql.CallableStatement;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import com.clickbait_filter.dao.DBProcedureProps;
import com.clickbait_filter.exceptions.ExternalErrorResponseException;
import com.microsoft.sqlserver.jdbc.SQLServerCallableStatement;

public interface IContentManagmentService {

	public SQLServerCallableStatement populateProperties(List<DBProcedureProps> passedProperties,
			JSONObject requestParams, CallableStatement stmt, DatabaseMetaData dbmd, String sql)
			throws ExternalErrorResponseException;

	public boolean getJSONResponse(JSONArray responseArray, JSONObject responseObject, CallableStatement stmt,
			SQLServerCallableStatement pStmt) throws ExternalErrorResponseException, SQLException;
}
