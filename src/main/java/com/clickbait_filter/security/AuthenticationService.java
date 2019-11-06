package com.clickbait_filter.security;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.clickbait_filter.constants.CommonConstants;

public class AuthenticationService {
	@Value("${security.authentication.token.secret}")
	private String secret;
	@Value("${security.authentication.token.token_prefix}")
	private String tokenPrefix;
	@Value("${security.authentication.token.header_string}")
	private String headerString;

	private final String CREDENTIALS_REGEX = "^%s (.*$)";

	private final String USER_PASS_REGEXP = "^([^:]*):(.*)$";

	public Auth getAuthentication(ServerHttpRequest serverHttpRequest, SecurityWebFiltersOrder orderType) {
		String token = serverHttpRequest.getHeaders().getFirst(headerString);

		if (token != null) {
			try {
				switch (orderType) {
				case AUTHORIZATION:
					DecodedJWT jwt = null;
					Auth auth = null;

					Algorithm algorithm = Algorithm.HMAC256(secret);
					JWTVerifier verifier = JWT.require(algorithm).acceptIssuedAt(CommonConstants.TEN).build();
					jwt = verifier.verify(token);

					String timezone = jwt.getClaim("timezone").asString();
					String xsrfToken = jwt.getClaim("xsrfToken").asString();
					Integer actorId = Integer.parseInt(jwt.getClaim("actorId").asString());
					String username = jwt.getClaim("username").asString();
					String sessionId = jwt.getClaim("sessionId").asString();

					if (username == null) {
						username = "BULPROS";
					}

					auth = new Auth(timezone, xsrfToken, actorId, username,
							(jwt.getClaim("password").asString() == null ? "123456789"
									: jwt.getClaim("password").asString()),
							sessionId, token);

					return auth;
				case HTTP_BASIC:
					Pattern tokenPattern = Pattern.compile(String.format(CREDENTIALS_REGEX, tokenPrefix));
					Matcher matcher = tokenPattern.matcher(token);
					if (matcher.matches()) {
						String trueToken = matcher.group(1);
						String decoded = new String(DatatypeConverter.parseBase64Binary(trueToken));
						tokenPattern = Pattern.compile(USER_PASS_REGEXP);
						matcher = tokenPattern.matcher(decoded);
						if (matcher.matches()) {
							String user = matcher.group(1);
							String password = matcher.group(2);
							auth = new Auth(user, password);
							// MORE BASIC AUTHENTICATION CAN BE MADE HERE,
							// BUT SINCE THERE IS NOT USER CREATE PROCESS I LEAVE THIS ONE OPEN
							// AND PASS TO THE HANDLER
							return auth;
						}
					} else {
						System.out.println("Authentication service: exception parsing token: " + token);
					}
					return null;
				default:
					break;
				}

			} catch (Exception ex) {
				System.out.println("Authentication service: exception parsing token: " + ex.getMessage());
			}
		}

		System.out.println("Authentication not present in header: " + headerString);
		return null;
	}
}
