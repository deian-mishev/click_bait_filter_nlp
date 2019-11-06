package com.clickbait_filter.dao;

public class DBParam {

//    "name": "consent",
//    "def": {
//        "type": "table",
//        "typeName": "consent.consentTT"
//    },
//    "out": false,
//    "default": false

	private String name;
	private DBParamDef def;
	private Boolean out;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public DBParamDef getDef() {
		return def;
	}
	public void setDef(DBParamDef def) {
		this.def = def;
	}
	public Boolean getOut() {
		return out;
	}
	public void setOut(Boolean out) {
		this.out = out;
	}
}
