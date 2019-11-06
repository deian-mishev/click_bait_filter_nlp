package com.clickbait_filter.security;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.clickbait_filter.exceptions.ErrorCodes;
import com.clickbait_filter.exceptions.model.ApiError;
import com.clickbait_filter.exceptions.model.InnerError;

import reactor.core.publisher.Mono;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;

public class AuthenticationFilter implements WebFilter {
	private static final String AUTH_ATTRIBUTE = "auth";

	@Value("${server.apiPath}")
	private String apiPath;

	@Value("${security.authentication.type}")
	private String authenticationType;

	@Autowired
	private AuthenticationService AuthenticationService;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		ServerHttpResponse response = exchange.getResponse();

		String path = request.getPath().pathWithinApplication().value();
		Map<String, Object> attributes = exchange.getAttributes();

		// only apply filter for JWT on apiPath
		if (!path.contains(apiPath) || (path.contains(apiPath) && request.getMethod().equals(HttpMethod.OPTIONS))
				|| attributes.containsKey(AUTH_ATTRIBUTE)) {
			return chain.filter(exchange);
		}

		Auth authentication = AuthenticationService.getAuthentication((ServerHttpRequest) request,
				SecurityWebFiltersOrder.valueOf(authenticationType));

		if (authentication == null) {
			response.setStatusCode(HttpStatus.UNAUTHORIZED);

			ApiError error = new ApiError();
			InnerError inner = new InnerError();
			inner.setCode(ErrorCodes.UNAUTHORIZED);
			inner.setMessage("Client is Unauthorized");
			error.setError(inner);
			try {
				response.getHeaders().add("Content-type", "application/json");
				return response.writeWith(
						Mono.just(new DefaultDataBufferFactory().wrap(objectMapper.writeValueAsBytes(error))));
			} catch (JsonProcessingException e) {
				System.out.println(e.getMessage());
			}
			return Mono.empty();
		}
		// extract token data and put in attributes,
		// throw unauthorized if token is not properly signed
		attributes.put(AUTH_ATTRIBUTE, authentication);

		return chain.filter(exchange);
	}
}
