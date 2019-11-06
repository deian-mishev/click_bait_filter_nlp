package com.clickbait_filter.web.impl;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.clickbait_filter.security.Auth;
import com.clickbait_filter.web.ContentManagementApi;
import com.microsoft.sqlserver.jdbc.SQLServerCallableStatement;
import com.clickbait_filter.services.IContentManagmentService;
import com.clickbait_filter.Utils;
import com.clickbait_filter.dao.DBProcedureProps;
import com.clickbait_filter.exceptions.ExternalErrorResponseException;
//import com.bulpros.DataSchema;

@CrossOrigin
@RestController
@Validated
public class ContentManagementApiController implements ContentManagementApi {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private IContentManagmentService contentManagmentService;

	@Autowired
	protected DataSource datasource;

//	@Autowired
//	protected DataSchema dataSchema;

	@Value("${spring.datasource.databaseName}")
	private String databaseName;

	public ContentManagementApiController() {
		// used by spring
	}

	public ResponseEntity<String> procedureHandling(@RequestAttribute("auth") Auth auth, @RequestBody String body)
			throws ExternalErrorResponseException {
		JSONObject bodyJson = new JSONObject(body);
		System.out.println(body.trim());
		String methodName = bodyJson.getString("method");
		JSONObject requestParams = bodyJson.getJSONObject("params");
		if (bodyJson.has("meta")) {
			JSONObject requestMeta = bodyJson.getJSONObject("meta");

			if (!requestMeta.toString().equals("{}")) {
				requestParams.put("meta", requestMeta);
			}
		}

		// DB Request String
		String sql = "EXEC " + Utils.getProcedureName(methodName) + " ";

		// Response Properties
		JSONArray responseArray = new JSONArray();
		JSONObject responseObject = new JSONObject();
		Boolean isObject = false;

		List<DBProcedureProps> passedProperties = new ArrayList<>();

		try (Connection conn = datasource.getConnection();) {
			DatabaseMetaData dbmd = conn.getMetaData();
			
//			if (dbmd == null) {
//				dbmd = conn.getMetaData();
//				dataSchema.setMethodShema(methodName, dbmd);
//			}

			String[] procedureComponents = methodName.split(Pattern.quote("."));
			ResultSet res = dbmd.getProcedureColumns(databaseName, procedureComponents[0],
					String.join(".", Arrays.copyOfRange(procedureComponents, 1, procedureComponents.length)), null);

			while (res.next()) {
				// A bit cryptic but that's all we need
				String propName = res.getString(4).substring(1);
				if (!propName.equals("RETURN_VALUE") && requestParams.has(propName)) {
					passedProperties.add(new DBProcedureProps(propName, res.getString(7), res.getString(21)));
				}
			}

			res.close();
			sql = Utils.buildSQLString(passedProperties, sql);

			CallableStatement stmt = conn.prepareCall(sql);
			SQLServerCallableStatement pStmt = contentManagmentService.populateProperties(passedProperties,
					requestParams, stmt, dbmd, sql);

			pStmt.executeQuery();
			isObject = contentManagmentService.getJSONResponse(responseArray, responseObject, stmt, pStmt);

		} catch (SQLException ex) {
			if (ex.getErrorCode() == 0) {
				return new ResponseEntity<String>("{}".toString(), HttpStatus.OK);
			} else {
				logger.error(body + " : " + ex.getMessage());
				throw new ExternalErrorResponseException(ex);
			}
		}

		if (isObject) {
			return new ResponseEntity<String>(responseObject.toString(), HttpStatus.OK);
		}

		return new ResponseEntity<String>(responseArray.toString(), HttpStatus.OK);
	}

}