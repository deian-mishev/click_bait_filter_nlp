package com.clickbait_filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.clickbait_filter.exceptions.ExternalErrorResponseException;

@Component
public class Startup {
	@Autowired
	private DataSource dataSource;

	@PostConstruct
	public void runNativeSql() throws ExternalErrorResponseException, IOException {
		try (Connection dbConnection = dataSource.getConnection()) {

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
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw new ExternalErrorResponseException(e);
		}
	}

}
