package com.clickbait_filter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { 
		"com.bulpros", 
		"com.bulpros.web",
		"com.bulpros.exceptions", 
		"com.bulpros.configuration"
})
@EntityScan(basePackages = { "com.bulpros.domain" })
public class Application {
	private static final String PROCESS_ENVIORNMENT_VARIABLE = "ccmsEnv";

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Application.class);
		String environment = System.getenv(PROCESS_ENVIORNMENT_VARIABLE);
		if (environment == null || environment.isEmpty()) {
			environment = System.getProperty(PROCESS_ENVIORNMENT_VARIABLE);
		}
		if (environment != null && !environment.isEmpty()) {
			app.setAdditionalProfiles(environment);
		}
		app.addInitializers(new ApplicationInitializer());
		app.setWebApplicationType(WebApplicationType.REACTIVE);
		app.run(args);
	}
}