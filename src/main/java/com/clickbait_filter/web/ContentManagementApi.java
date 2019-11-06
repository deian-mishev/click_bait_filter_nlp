package com.clickbait_filter.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.clickbait_filter.exceptions.ExternalErrorResponseException;
import com.clickbait_filter.security.Auth;

@RequestMapping("${server.apiPath}")
public interface ContentManagementApi {
	@PostMapping(value = "/", produces = { "application/json" }, consumes = { "application/json" })
	ResponseEntity<String> procedureHandling(@RequestAttribute("auth") Auth auth, @RequestBody String body) throws ExternalErrorResponseException;
}
