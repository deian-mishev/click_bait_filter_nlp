package com.clickbait_filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.clickbait_filter.dao.DBProcedureProps;
import com.clickbait_filter.exceptions.ExternalErrorResponseException;

public class Utils {

	private Utils() {
	}

	public static Boolean isArray(Object testObj) throws ExternalErrorResponseException {
		if (testObj == null)
			return false;
		String testString = testObj.toString();
		return testString.length() >= 2 && testString.substring(0, 1).equals("[")
				&& testString.substring(testString.length() - 1).equals("]");
	}

	public static String getProcedureName(String rawProcedureName) throws ExternalErrorResponseException {
		String[] split = rawProcedureName.split(Pattern.quote("."));
		split[0] = "[" + split[0] + "]";
		split[1] = "[" + String.join(".", Arrays.copyOfRange(split, 1, split.length)) + "]";
		return String.join(".", Arrays.copyOfRange(split, 0, 2));
	}

	public static String buildSQLString(List<DBProcedureProps> passedProperties, String sql) {
		Iterator<DBProcedureProps> requestParamskeys = passedProperties.iterator();
		while (requestParamskeys.hasNext()) {
			DBProcedureProps params = requestParamskeys.next();
			sql += "@" + params.getName() + "=?";
			if (requestParamskeys.hasNext()) {
				sql += ",";
			} else {
				sql += ";";
			}
		}
		return sql;
	}

	public static boolean isJSONValid(String test) {
		try {
			new JSONObject(test);
		} catch (JSONException ex) {
			// edited, to include @Arthur's comment
			// e.g. in case JSONArray is valid as well...
			try {
				new JSONArray(test);
			} catch (JSONException ex1) {
				return false;
			}
		}
		return true;
	}

	public static JSONArray isJSONArray(String test) {
		try {
			return new JSONArray(test);
		} catch (JSONException ex) {
			return null;
		}
	}

	public static JSONObject isJSONObject(String test) {
		try {
			return new JSONObject(test);
		} catch (JSONException ex) {
			return null;
		}
	}

	public static Object getPropertyFromJSON(String value, String columnName) {
		try {
			JSONObject a = new JSONObject(value);
			if (a.has(columnName)) {
				Object ret = a.get(columnName);
				if (ret.toString().isEmpty()) {
					return null;
				}
				return ret;
			} else {
				return null;
			}
		} catch (JSONException ex) {
			// edited, to include @Arthur's comment
			// e.g. in case JSONArray is valid as well...
			try {
				new JSONArray(value);
				return value;
			} catch (JSONException ex1) {
				return null;
			}
		}
	}

	public static Boolean isNotEmptyString(String str) throws ExternalErrorResponseException {
		return !str.trim().replaceAll("\"", "").isEmpty();
	}

	public static void loadYamlResource(ConfigurableApplicationContext context, String userHomeDirectory,
			String yamlFile) throws ExternalErrorResponseException {
		ConfigurableEnvironment env = context.getEnvironment();
		String userHome = System.getProperty(userHomeDirectory);
		String filePath = userHome + yamlFile;
		File file = new File(filePath);
		if (file.exists()) {
			Resource resource = context.getResource("file:" + filePath);
			YamlPropertySourceLoader sourceLoader = new YamlPropertySourceLoader();
			List<PropertySource<?>> yamlProperties = null;
			try {
				yamlProperties = sourceLoader.load("yamlProperties", resource);
			} catch (IOException e) {
				throw new ExternalErrorResponseException(e);
			}
			env.getPropertySources().addFirst(yamlProperties.get(0));
		}
	}

	public static JSONObject readJsonFile(String userHomeDirectory, String rcFile)
			throws ExternalErrorResponseException {
		String userHome = System.getProperty(userHomeDirectory);
		String filePath = userHome + rcFile;
		try {
			File file = new File(filePath);
			if (file.exists()) {
				byte[] encoded = Files.readAllBytes(Paths.get(filePath));
				return new JSONObject(new String(encoded, "UTF-8")); // Parse the JSON to a JSONObject
			}
		} catch (JSONException | IOException e) {
			throw new ExternalErrorResponseException(e);
		}
		return null;
	}

	public static String readResource(String resourceFileName) throws ExternalErrorResponseException {
		Resource resource = new ClassPathResource(resourceFileName);
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(resource.getInputStream()), 1024);
			String line;
			while ((line = br.readLine()) != null) {
				stringBuilder.append(line).append("\r\n");
			}
			br.close();
		} catch (IOException e) {
			throw new ExternalErrorResponseException(e);
		}

		return stringBuilder.toString();
	}

	public static String readWithBufferedReader(String filePath) throws ExternalErrorResponseException {
		StringBuilder contentBuilder = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				contentBuilder.append(sCurrentLine).append("\n");
			}
		} catch (IOException ex) {
			throw new ExternalErrorResponseException(ex);
		}
		return contentBuilder.toString();
	}
}
