package com.clickbait_filter.web;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.Test;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import com.clickbait_filter.security.Auth;
import org.junit.runner.RunWith;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.clickbait_filter.ApplicationInitializer;
import com.clickbait_filter.DataSchema;
import com.clickbait_filter.Utils;
import com.clickbait_filter.exceptions.ExternalErrorResponseException;

import com.clickbait_filter.services.impl.ContentManagmentService;
import com.clickbait_filter.web.impl.ContentManagementApiController;

@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ContextConfiguration(initializers = ApplicationInitializer.class)
public class ContentManagementApiTests {

	private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

	private static DataSchema dataSchema = new DataSchema();
	private static ContentManagementApiController contentApiController = new ContentManagementApiController();
	private static ContentManagementApi contentApiProxy;

	private static Auth auth = new Auth();

	@BeforeClass
	public static void initialize() throws ExternalErrorResponseException {
		// Replace by mocks with reflections if possible
		// DO NOT RUN TEST ON PRODUCTION //
		ContentManagmentService contentManagmentService = new ContentManagmentService();

		JSONObject rcObject = Utils.readJsonFile("user.home", "/.consent_management_back_db_devrc");
		JSONObject db = rcObject.getJSONObject("db");

		String database = db.getString("database");
		String server = db.getString("server");
		String port = db.getString("port");

		DataSource datasource = DataSourceBuilder.create()
				.url("jdbc:sqlserver://" + server + ":" + port + ";integratedSecurity=true;").driverClassName(DRIVER)
				.build();

		try (Connection conn = datasource.getConnection()) {
			Statement stmt = conn.createStatement();

			stmt = conn.createStatement();

			String sql = "DROP DATABASE " + database;
			stmt.executeUpdate(sql);
			sql = "CREATE DATABASE " + database;
			stmt.executeUpdate(sql);
			stmt.close();

			String parent = System.getProperty("user.dir");
			parent = parent.replaceFirst("ccms-db-api-java", "");
			Path nodeProject = Paths.get(parent, "/ccms-db-api");
			File file = new File(nodeProject.toString());
			if (file.exists()) {
				ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c",
						"cd " + file.getAbsolutePath() + " && npm run start");
				builder.redirectErrorStream(true);
				Process p = builder.start();
				BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				System.out.println("");
				while (true) {
					line = r.readLine();
					if (line == null) {
						break;
					}
					System.out.println(line);
				}
				System.out.println("");
			}

		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		datasource = DataSourceBuilder.create().url(
				"jdbc:sqlserver://" + server + ":" + port + ";databaseName=" + database + ";integratedSecurity=true;")
				.driverClassName(DRIVER).build();

		ReflectionTestUtils.setField(contentApiController, "dataSchema", dataSchema);
		ReflectionTestUtils.setField(contentApiController, "contentManagmentService", contentManagmentService);
		ReflectionTestUtils.setField(contentApiController, "datasource", datasource);

		// arrange aspects
		AspectJProxyFactory factory = new AspectJProxyFactory(contentApiController);
		contentApiProxy = factory.getProxy();
	}

	@Test
	public void A_user_identity_check() throws ExternalErrorResponseException {
		String body = "{\"method\":\"user.identity.check\",\"params\":{\"username\":\"sa\",\"password\":\"780a3ce153ba5d4d931397bb009c2ab4\",\"channel\":\"Web\"},\"meta\":{\"db\":\"bulpros\"}}";
		// Act
		ResponseEntity<String> response = contentApiProxy.procedureHandling(auth, body);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void B_user_permissions_fetch() throws ExternalErrorResponseException {
		String body = "{\"method\":\"user.permission.fetch\",\"params\":{\"userId\":\"5\"},\"meta\":{\"userId\":\"5\",\"db\":\"bulpros\",\"refreshExp\":1543499365252,\"iat\":1543499359,\"exp\":1543499959}}";

		// Act
		ResponseEntity<String> response = contentApiProxy.procedureHandling(auth, body);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void C_subject_dataSubject_fetch() throws ExternalErrorResponseException {
		String body = "{\"method\":\"subject.dataSubject.fetch\",\"params\":{},\"meta\":{\"userId\":\"5\",\"db\":\"bulpros\",\"refreshExp\":1543499365252,\"iat\":1543499359,\"exp\":1543499959}}";

		// Act
		ResponseEntity<String> response = contentApiProxy.procedureHandling(auth, body);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void D_consent_consentPurpose_fetch() throws ExternalErrorResponseException {
		String body = "{\"method\":\"consent.consentPurpose.fetch\",\"params\":{},\"meta\":{\"userId\":\"5\",\"db\":\"bulpros\",\"refreshExp\":1543499365252,\"iat\":1543499359,\"exp\":1543499959}}";

		// Act
		ResponseEntity<String> response = contentApiProxy.procedureHandling(auth, body);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void E_consent_personalAttributes_fetch() throws ExternalErrorResponseException {
		String body = "{\"method\":\"consent.personalAttributes.fetch\",\"params\":{},\"meta\":{\"userId\":\"5\",\"db\":\"bulpros\",\"refreshExp\":1543499365252,\"iat\":1543499359,\"exp\":1543499959}}";

		// Act
		ResponseEntity<String> response = contentApiProxy.procedureHandling(auth, body);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void F_consent_consentFormNames_fetch() throws ExternalErrorResponseException {
		String body = "{\"method\":\"consent.consentFormNames.fetch\",\"params\":{},\"meta\":{\"userId\":\"5\",\"db\":\"bulpros\",\"refreshExp\":1543499365252,\"iat\":1543499359,\"exp\":1543499959}}";

		// Act
		ResponseEntity<String> response = contentApiProxy.procedureHandling(auth, body);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void G_consent_consent_fetch() throws ExternalErrorResponseException {
		String body = "{\"method\":\"consent.consent.fetch\",\"params\":{\"dataSubjectId\":\"4\"},\"meta\":{\"userId\":\"5\",\"db\":\"bulpros\",\"refreshExp\":1543499365252,\"iat\":1543499359,\"exp\":1543499959}}";

		// Act
		ResponseEntity<String> response = contentApiProxy.procedureHandling(auth, body);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void H_user_personalData_fetch() throws ExternalErrorResponseException {
		String body = "{\"method\":\"user.personalData.fetch\",\"params\":{\"personalDataId\":[],\"email\":[],\"telephone\":[{\"value\":\"0889653265\"}]},\"meta\":{\"userId\":\"5\",\"db\":\"bulpros\",\"refreshExp\":1543499365252,\"iat\":1543499359,\"exp\":1543499959}}";

		// Act
		ResponseEntity<String> response = contentApiProxy.procedureHandling(auth, body);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void I_org_businessUnit_fetch() throws ExternalErrorResponseException {
		String body = "{\"method\":\"org.businessUnit.fetch\",\"params\":{},\"meta\":{\"userId\":\"5\",\"db\":\"bulpros\",\"refreshExp\":1543499365252,\"iat\":1543499359,\"exp\":1543499959}}";

		// Act
		ResponseEntity<String> response = contentApiProxy.procedureHandling(auth, body);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void J_user_user_get() throws ExternalErrorResponseException {
		String body = "{\"method\":\"user.user.get\",\"params\":{\"userId1\":\"5\"},\"meta\":{\"userId\":\"5\",\"db\":\"bulpros\",\"refreshExp\":1543499365252,\"iat\":1543499359,\"exp\":1543499959}}";

		// Act
		ResponseEntity<String> response = contentApiProxy.procedureHandling(auth, body);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void K_consent_purposeCategoryAttributes_add_text() throws ExternalErrorResponseException {
		String body = "{\"method\":\"consent.purposeCategoryAttributes.add\",\"meta\":{\"refreshExp\":1544112629581,\"exp\":1544113229,\"userId\":5,\"iat\":1544112629,\"db\":\"bulpros\"},\"params\":{\"attributeType\":\"text\",\"attributeKey\":\"a\"}}";
		// Act
		ResponseEntity<String> response = contentApiProxy.procedureHandling(auth, body);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void L_consent_purposeCategoryAttributes_add_number() throws ExternalErrorResponseException {
		String body = "{\"method\":\"consent.purposeCategoryAttributes.add\",\"meta\":{\"refreshExp\":1544112629581,\"exp\":1544113229,\"userId\":5,\"iat\":1544112629,\"db\":\"bulpros\"},\"params\":{\"attributeType\":\"number\",\"attributeKey\":\"b\"}}";

		// Act
		ResponseEntity<String> response = contentApiProxy.procedureHandling(auth, body);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void M_consent_purposeCategoryAttributes_add_date() throws ExternalErrorResponseException {
		String body = "{\"method\":\"consent.purposeCategoryAttributes.add\",\"meta\":{\"refreshExp\":1544112629581,\"exp\":1544113229,\"userId\":5,\"iat\":1544112629,\"db\":\"bulpros\"},\"params\":{\"attributeType\":\"date\",\"attributeKey\":\"c\"}}";

		// Act
		ResponseEntity<String> response = contentApiProxy.procedureHandling(auth, body);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void N_consent_consent_add() throws ExternalErrorResponseException {
		String body = "{\"method\":\"consent.consent.add\",\"params\":{\"consent\":{\"purposeId\":100,\"source\":\"aaa\",\"contactMethod\":\"aaa\",\"status\":\"Confirmed/Active\",\"consentType\":\"aa\",\"consentLink\":\"aa\",\"dataSubjectId\":\"4\"},\"consentAttributes\":[],\"consentAttachment\":[],\"consentCustomAttributes\":[{\"attributeId\":\"6\",\"attributeValue\":\"aaa\"},{\"attributeId\":\"7\",\"attributeValue\":\"121\"},{\"attributeId\":\"8\",\"attributeValue\":\"2018-12-12\"}]},\"meta\":{\"userId\":5,\"db\":\"bulpros\",\"refreshExp\":1544198985177,\"iat\":1544198979,\"exp\":1544199579}}";

		// Act
		ResponseEntity<String> response = contentApiProxy.procedureHandling(auth, body);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void O_consent_consent_get() throws ExternalErrorResponseException {
		String body = "{\"method\":\"consent.consent.get\",\"params\":{\"consentId\":\"5\"},\"meta\":{\"userId\":5,\"db\":\"bulpros\",\"refreshExp\":1544193523346,\"iat\":1544193517,\"exp\":1544194117}}";

		// Act
		ResponseEntity<String> response = contentApiProxy.procedureHandling(auth, body);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void P_consent_consent_delete() throws ExternalErrorResponseException {
		String body = "{\"method\":\"consent.consent.edit\",\"params\":{\"consent\":{\"consentId\":5,\"status\":\"Withdrawn\",\"endDate\":\"2018-12-10T08:56:32.627Z\"}},\"meta\":{\"userId\":5,\"db\":\"bulpros\",\"refreshExp\":1544432098493,\"iat\":1544432092,\"exp\":1544432692}}";

		// Act
		ResponseEntity<String> response = contentApiProxy.procedureHandling(auth, body);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void Q_subject_datasubjectconsentimport_add_category_fail() {
		String body = "{\"method\":\"subject.dataSubjectConsentImport.add\",\"params\":{\"dataSubjectConsentImport\":[{\"identifierType\":\"email\",\"identifierValue\":\"test@test.test123\",\"dataSubjectCategoryNumName\":\"aaa\",\"consentPurposeName\":\"aaa\",\"source\":\"tt\",\"contactMethod\":\"ttt\",\"formName\":\"tt\",\"expiryDate\":\"2021-12-06\",\"consentType\":\"ttt\",\"consentLink\":\"tt\"},{\"identifierType\":\"email\",\"identifierValue\":\"test@test.test123\",\"dataSubjectCategoryNumName\":\"business partner\",\"consentPurposeName\":\"Recruitment\",\"source\":\"tt\",\"contactMethod\":\"ttt\",\"formName\":\"tt\",\"expiryDate\":\"2021-12-07\",\"consentType\":\"ttt\",\"consentLink\":\"tt\"},{\"identifierType\":\"phone\",\"identifierValue\":\"888888885\",\"dataSubjectCategoryNumName\":\"business partner\",\"consentPurposeName\":\"Recruitment\",\"source\":\"cc\",\"contactMethod\":\"ccc\",\"formName\":\"cc\",\"expiryDate\":\"2021-12-08\",\"consentType\":\"cc\",\"consentLink\":\"cc\"}]},\"meta\":{\"userId\":5,\"db\":\"bulpros\",\"refreshExp\":1544435837598,\"iat\":1544435831,\"exp\":1544436431}}";

		// Act
		try {
			contentApiProxy.procedureHandling(auth, body);
		} catch (ExternalErrorResponseException ex) {
			// Assert
			assertEquals(
					"com.microsoft.sqlserver.jdbc.SQLServerException: *** [subject.dataSubjectConsentImport.add], Line 70. Errno 50000: JSON{\"name\": \"dataSubjectCategory.WrongCategory\", \"numberOfErrors\": 1, \"firstErrorOnLine\": 1}",
					ex.getMessage());
		}
	}

	@Test
	public void R_subject_datasubjectconsentimport_add_purpose_fail() {
		String body = "{\"method\":\"subject.dataSubjectConsentImport.add\",\"params\":{\"dataSubjectConsentImport\":[{\"identifierType\":\"email\",\"identifierValue\":\"test@test.test123\",\"dataSubjectCategoryNumName\":\"business partner\",\"consentPurposeName\":\"aaa\",\"source\":\"tt\",\"contactMethod\":\"ttt\",\"formName\":\"tt\",\"expiryDate\":\"2021-12-06\",\"consentType\":\"ttt\",\"consentLink\":\"tt\"},{\"identifierType\":\"email\",\"identifierValue\":\"test@test.test123\",\"dataSubjectCategoryNumName\":\"business partner\",\"consentPurposeName\":\"Recruitment\",\"source\":\"tt\",\"contactMethod\":\"ttt\",\"formName\":\"tt\",\"expiryDate\":\"2021-12-07\",\"consentType\":\"ttt\",\"consentLink\":\"tt\"},{\"identifierType\":\"phone\",\"identifierValue\":\"888888885\",\"dataSubjectCategoryNumName\":\"business partner\",\"consentPurposeName\":\"Recruitment\",\"source\":\"cc\",\"contactMethod\":\"ccc\",\"formName\":\"cc\",\"expiryDate\":\"2021-12-08\",\"consentType\":\"cc\",\"consentLink\":\"cc\"}]},\"meta\":{\"userId\":5,\"db\":\"bulpros\",\"refreshExp\":1544435837598,\"iat\":1544435831,\"exp\":1544436431}}";

		// Act
		try {
			contentApiProxy.procedureHandling(auth, body);
		} catch (ExternalErrorResponseException ex) {
			// Assert
			assertEquals(
					"com.microsoft.sqlserver.jdbc.SQLServerException: *** [subject.dataSubjectConsentImport.add], Line 83. Errno 50000: JSON{\"name\": \"consentPurpose.WrongPurpose\", \"numberOfErrors\": 1, \"firstErrorOnLine\": 1}",
					ex.getMessage());
		}
	}

	@Test
	public void S_subject_datasubjectconsentimport_add_duplicate_key_fail() {
		String body = "{\"method\":\"subject.dataSubjectConsentImport.add\",\"params\":{\"dataSubjectConsentImport\":[{\"identifierType\":\"email\",\"identifierValue\":\"test@test.test123\",\"dataSubjectCategoryNumName\":\"business partner\",\"consentPurposeName\":\"Recruitment\",\"source\":\"tt\",\"contactMethod\":\"ttt\",\"formName\":\"tt\",\"expiryDate\":\"2021-12-06\",\"consentType\":\"ttt\",\"consentLink\":\"tt\"},{\"identifierType\":\"email\",\"identifierValue\":\"test@test.test123\",\"dataSubjectCategoryNumName\":\"business partner\",\"consentPurposeName\":\"Recruitment\",\"source\":\"tt\",\"contactMethod\":\"ttt\",\"formName\":\"tt\",\"expiryDate\":\"2021-12-07\",\"consentType\":\"ttt\",\"consentLink\":\"tt\"},{\"identifierType\":\"phone\",\"identifierValue\":\"888888885\",\"dataSubjectCategoryNumName\":\"business partner\",\"consentPurposeName\":\"Recruitment\",\"source\":\"cc\",\"contactMethod\":\"ccc\",\"formName\":\"cc\",\"expiryDate\":\"2021-12-08\",\"consentType\":\"cc\",\"consentLink\":\"cc\"}]},\"meta\":{\"userId\":5,\"db\":\"bulpros\",\"refreshExp\":1544435837598,\"iat\":1544435831,\"exp\":1544436431}}";

		// Act
		try {
			contentApiProxy.procedureHandling(auth, body);
		} catch (ExternalErrorResponseException ex) {
			// Assert
			assertEquals(
					"com.microsoft.sqlserver.jdbc.SQLServerException: *** [subject.dataSubjectConsentImport.add], Line 91. Errno 2627: Violation of UNIQUE KEY constraint 'uk_typeValue'. Cannot insert duplicate key in object 'subject.dataSubject'. The duplicate key value is (email, test@test.test123).",
					ex.getMessage());
		}
	}

	@Test
	public void T_subject_datasubjectconsentimport_add_pass() throws ExternalErrorResponseException {
		String body = "{\"method\":\"subject.dataSubjectConsentImport.add\",\"params\":{\"dataSubjectConsentImport\":[{\"identifierType\":\"email\",\"identifierValue\":\"test@test.test123\",\"dataSubjectCategoryNumName\":\"business partner\",\"consentPurposeName\":\"Recruitment\",\"source\":\"tt\",\"contactMethod\":\"ttt\",\"formName\":\"tt\",\"expiryDate\":\"2021-12-06\",\"consentType\":\"ttt\",\"consentLink\":\"tt\"},{\"identifierType\":\"email\",\"identifierValue\":\"test@test.test1234\",\"dataSubjectCategoryNumName\":\"business partner\",\"consentPurposeName\":\"Recruitment\",\"source\":\"tt\",\"contactMethod\":\"ttt\",\"formName\":\"tt\",\"expiryDate\":\"2021-12-07\",\"consentType\":\"ttt\",\"consentLink\":\"tt\"},{\"identifierType\":\"phone\",\"identifierValue\":\"888888885\",\"dataSubjectCategoryNumName\":\"business partner\",\"consentPurposeName\":\"Recruitment\",\"source\":\"cc\",\"contactMethod\":\"ccc\",\"formName\":\"cc\",\"expiryDate\":\"2021-12-08\",\"consentType\":\"cc\",\"consentLink\":\"cc\"}]},\"meta\":{\"userId\":5,\"db\":\"bulpros\",\"refreshExp\":1544435837598,\"iat\":1544435831,\"exp\":1544436431}}";

		// Act
		ResponseEntity<String> response = contentApiProxy.procedureHandling(auth, body);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
}