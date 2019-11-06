package com.clickbait_filter.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import com.clickbait_filter.constants.CommonConstants;
import com.clickbait_filter.security.AuthenticationFilter;
import com.clickbait_filter.security.AuthenticationService;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig implements WebFluxConfigurer {
	@Autowired
	AuthenticationFilter authenticationFilter;

	@Value("${server.apiPath}")
	private String apiPath;

	@Value("${management.endpoints.web.base-path}")
	private String actuatorPath;

	@Value("${security.wave.user.name}")
	private String adminUser;

	@Value("${security.wave.user.password}")
	private String adminPassword;

	@Value("${security.wave.user.roles}")
	private String adminRoles;

	@Value("${security.authentication.type}")
	private String authenticationType;

	private static final String ADMIN_ROELS_DELIMITER = ",";

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping(apiPath + "/**").allowedOrigins("*").allowedMethods("*").allowedHeaders("*")
				.allowCredentials(true).maxAge(CommonConstants.MAX_AGE);
	}

	@Bean
	public MapReactiveUserDetailsService userDetailsService() {
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		UserDetails user = User.withUsername(adminUser).password(encoder.encode(adminPassword))
				.roles(adminRoles.split(ADMIN_ROELS_DELIMITER)).build();
		return new MapReactiveUserDetailsService(user);
	}

	@Bean
	public SecurityWebFilterChain configure(ServerHttpSecurity http) {
		// disable csrf
		http.csrf().disable();

		// permit /info and /health endpoints
		http.authorizeExchange().pathMatchers(actuatorPath + "/info", actuatorPath + "/health").permitAll().and();

		// add basic auth for actuator endpoints
		http.authorizeExchange().pathMatchers(actuatorPath + "/**").authenticated();
		// .and().httpBasic();

		// permit any request to other path
		http.authorizeExchange().anyExchange().permitAll().and();

		// add filters
		http.addFilterAt(authenticationFilter, SecurityWebFiltersOrder.valueOf(authenticationType));

		return http.build();
	}

	@Bean
	ReactiveAuthenticationManager reactiveAuthenticationManager() {
		return new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService());
	}

	@Bean
	AuthenticationFilter filter() {
		return new AuthenticationFilter();
	}

	@Bean
	AuthenticationService getTokenAuthenticationService() {
		return new AuthenticationService();
	}
}
