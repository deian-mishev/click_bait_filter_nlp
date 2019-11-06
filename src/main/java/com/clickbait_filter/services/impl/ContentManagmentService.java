package com.clickbait_filter.services.impl;

import java.sql.CallableStatement;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.clickbait_filter.Utils;
import com.clickbait_filter.dao.DBField;
import com.clickbait_filter.dao.DBProcedureProps;
import com.clickbait_filter.services.IContentManagmentService;
import com.microsoft.sqlserver.jdbc.SQLServerCallableStatement;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.clickbait_filter.exceptions.ExternalErrorResponseException;
import org.springframework.beans.factory.annotation.Value;

@Service
public class ContentManagmentService implements IContentManagmentService {

	@Value("${spring.datasource.databaseName}")
	private String databaseName;

	public SQLServerDataTable createTvp(JSONObject value, List<DBField> fields) throws ExternalErrorResponseException {
		return null;
	}

	public void populateRowFromArray(Object property, int dataType, SQLServerDataTable sourceDataTable, ResultSet rs,
			String columnName) throws ExternalErrorResponseException, SQLException {
		String arr = property.toString();
		JSONArray madeArray = Utils.isJSONArray(arr);
		List<String> requiredProps = new ArrayList<String>();
		requiredProps.add(columnName);
		if (madeArray != null && madeArray.length() > 0) {
			// CASE: [{},{}]
			for (int i = 0; i < madeArray.length(); i++) {
				String objString = madeArray.get(i).toString();

				if (Utils.isJSONValid(objString)) {
					JSONObject jsonObject = new JSONObject(objString);
					List<Object> passedObject = new ArrayList<Object>();
					while (rs.next()) {
						String name = rs.getString(4);
						int type = rs.getInt(5);
						requiredProps.add(name);
						sourceDataTable.addColumnMetadata(name, type);
					}
					for (String key : requiredProps) {
						if (jsonObject.has(key)) {
							passedObject.add(jsonObject.get(key));
						} else {
							passedObject.add(null);
						}
					}
					sourceDataTable.addRow(passedObject.toArray(new Object[passedObject.size()]));
				} else if (Utils.isNotEmptyString(objString)) {
					Object[] values = objString.split(",");
					int length = values.length;
					for (int j = 0; j < length; j++) {
						String a = values[j].toString();
						a = a.toString().replaceAll("\"", "");
						if (a.equals("null")) {
							sourceDataTable.addRow(new Object[] { null });
						} else {
							if (dataType == Types.NVARCHAR || dataType == Types.CHAR) {
								sourceDataTable.addRow(new Object[] { a });
							} else {
								int result = Integer.parseInt(a);
								sourceDataTable.addRow(new Object[] { result });
							}
						}
					}
				} else {
					sourceDataTable.addRow(new Object[] { null });
				}
			}
		} else {
			sourceDataTable.addRow(new Object[] { null });
		}
	}

	public SQLServerDataTable buildSQLTable(String columType, String columDomain, String value, DatabaseMetaData dbmd)
			throws SQLException, ExternalErrorResponseException {
		SQLServerDataTable sourceDataTable = new SQLServerDataTable();

		String tableName = columType.substring(0, columType.length() - 2);

		List<Object> orderedColumnNames = new ArrayList<Object>();
		Boolean isArray = false;

		ResultSet rs = dbmd.getColumns(databaseName, columDomain, tableName, "%");
		while (rs.next()) {

			String columnName = rs.getString(4);
			int dataType = rs.getInt(5);
			sourceDataTable.addColumnMetadata(columnName, dataType);
			Object property = Utils.getPropertyFromJSON(value, columnName);
			isArray = Utils.isArray(property);
			if (isArray) {
				populateRowFromArray(property, dataType, sourceDataTable, rs, columnName);
			} else {
				orderedColumnNames.add(property);
			}
		}

		if (!isArray) {
			sourceDataTable.addRow(orderedColumnNames.toArray(new Object[orderedColumnNames.size()]));
		}

		return sourceDataTable;
	}

	public SQLServerCallableStatement populateProperties(List<DBProcedureProps> passedProperties,
			JSONObject requestParams, CallableStatement stmt, DatabaseMetaData dbmd, String sql)
			throws ExternalErrorResponseException {

		SQLServerCallableStatement pStmt = null;

		try {
			pStmt = stmt.unwrap(SQLServerCallableStatement.class);

			for (int i = 0; i < passedProperties.size(); i++) {
				DBProcedureProps passedProp = passedProperties.get(i);

				String columName = passedProp.getName();
				String columType = passedProp.getType();
				String columDomain = passedProp.getDomain();

				String value = requestParams.get(columName).toString();

				if (Utils.isJSONValid(value)) {
					if (columType.contains("TT")) {
						SQLServerDataTable sourceDataTable = buildSQLTable(columType, columDomain, value, dbmd);
						pStmt.setStructured(i + 1, "[" + columDomain + "].[" + columType + "]", sourceDataTable);
					}
				} else {
					pStmt.setObject(i + 1, value);
				}
			}
		} catch (SQLException ex) {
			throw new ExternalErrorResponseException(ex);
		}

		return pStmt;
	}

	public boolean getJSONResponse(JSONArray responseArray, JSONObject responseObject, CallableStatement stmt,
			SQLServerCallableStatement pStmt) throws ExternalErrorResponseException, SQLException {
		String resultSetName = null;
		Boolean wasSingle = false;
		Boolean isObject = false;
		Boolean results = true;
		do {
			JSONArray result = new JSONArray();
			if (results) {
				ResultSet rs = stmt.getResultSet();
				while (rs.next()) {
					ResultSetMetaData resultSetMetadata = rs.getMetaData();

					int count = resultSetMetadata.getColumnCount();

					JSONObject entry = new JSONObject();
					Boolean isResultSet = false;
					for (int i = 1; i <= count; i++) {
						String columnName = resultSetMetadata.getColumnName(i);
						if (!wasSingle) {
							wasSingle = columnName.equals("single") && rs.getInt(columnName) == 1;
							if (wasSingle) {
								continue;
							}
						}
						isResultSet = columnName.equals("resultSetName") || columnName.equals("resultset");
						if (isResultSet) {
							isObject = true;
							resultSetName = rs.getString(columnName);
						} else {
							entry.put(columnName, rs.getObject(columnName));
						}
					}
					if (isObject && !isResultSet) {
						if (wasSingle) {
							wasSingle = false;
							responseObject.put(resultSetName, entry);
						} else {
							responseObject.append(resultSetName, entry);
						}
					} else if (!isResultSet) {
						result.put(entry);
					}
				}
				rs.close();
			}
			if (!isObject) {
				responseArray.put(result);
			}
			results = pStmt.getMoreResults();
			if (!responseObject.has(resultSetName)) {
				responseObject.put(resultSetName, wasSingle ? new JSONObject() : new JSONArray());
			}
		} while (results);

		return isObject;
	}
}
