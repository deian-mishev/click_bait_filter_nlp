package com.clickbait_filter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.Properties;

import org.json.JSONObject;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

public class ApplicationInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	@Override
	public void initialize(ConfigurableApplicationContext context) {
		try {
			JSONObject rcObject = Utils.readJsonFile("user.home", "/.consent_management_back_db_devrc");
			JSONObject db = rcObject.getJSONObject("db");

			String server = db.getString("server");
			String port = db.getString("port");
			String database = db.getString("database");

			String serverAddress = rcObject.getString("address");
			int serverPort = rcObject.getInt("port");

			bootstrapDBConnection(server, port, database, serverAddress, serverPort, context);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private void bootstrapDBConnection(String server, String port, String database, String serverAddress,
			int serverPort, ConfigurableApplicationContext context) {
		String serverConnectionString = "jdbc:sqlserver://" + server + ":" + port + ";integratedSecurity=true;";
		try (Connection conn = DriverManager.getConnection(serverConnectionString)) {

			ConfigurableEnvironment env = context.getEnvironment();
			bootStrapDB(serverConnectionString, database);

			String dbString = "jdbc:sqlserver://" + server + ":" + port + ";databaseName=" + database
					+ ";integratedSecurity=true;";

			Properties props = new Properties();
			props.put("spring.datasource.databaseName", database);
			props.put("spring.datasource.url", dbString);
			props.put("server.address", serverAddress);
			props.put("server.port", serverPort);

			if (Arrays.stream(env.getActiveProfiles()).anyMatch(h -> (h.equalsIgnoreCase("dev")))) {
				env.getPropertySources().addFirst(new PropertiesPropertySource("dev", props));
			} else if (Arrays.stream(env.getActiveProfiles()).anyMatch(h -> (h.equalsIgnoreCase("prod")))) {
				env.getPropertySources().addFirst(new PropertiesPropertySource("prod", props));
			} else {
				env.getPropertySources().addFirst(new PropertiesPropertySource("default", props));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private void bootStrapDB(String url, String databaseName) {
		try (Connection conn = DriverManager.getConnection(url)) {
			String startupDatabaseQuery = Utils.readResource("/scripts/createDatabase.sql").replace("${name}",
					databaseName);
			try (PreparedStatement stmt = conn.prepareStatement(startupDatabaseQuery);) {
				System.out.println("Creating DB...");
				stmt.executeUpdate();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}