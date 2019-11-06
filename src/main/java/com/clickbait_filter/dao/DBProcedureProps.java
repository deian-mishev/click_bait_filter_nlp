package com.clickbait_filter.dao;

public class DBProcedureProps {

	private String name;
	private String type;
	private String domain;

	public DBProcedureProps() {

	}

	public DBProcedureProps(String name, String type, String domain) {
		this.name = name;
		this.type = type;
		this.domain = domain;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
}
