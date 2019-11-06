package com.clickbait_filter.security;

public class Auth {
	private String timezone;
	private String xsrfToken;
	private Integer actorId;
	private String username;
	private String password;
	private String sessionId;
	private String token;

	public Auth() {
	}

	public Auth(String timezone, String xsrfToken, Integer actorId, String username, String password, String sessionId,
			String token) {
		this.timezone = timezone;
		this.xsrfToken = xsrfToken;
		this.actorId = actorId;
		this.username = username;
		this.password = password;
		this.sessionId = sessionId;
		this.token = token;
	}

	public Auth(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public Auth(Auth auth) {
		this.timezone = auth.getTimezone();
		this.xsrfToken = auth.getXsrfToken();
		this.actorId = auth.getActorId();
		this.username = auth.getUsername();
		this.password = auth.getPassword();
		this.sessionId = auth.getSessionId();
		this.token = auth.getToken();
	}

	public String getTimezone() {
		return timezone;
	}

	public String getXsrfToken() {
		return xsrfToken;
	}

	public Integer getActorId() {
		return actorId;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getSessionId() {
		return sessionId;
	}

	public String getToken() {
		return token;
	}
}
