package com.clickbait_filter.dao;

public class DBProcedure {
	
//	  "type": "procedure",
//    "name": "[consent].[consent.add]",
//    "schema": "consent",
//    "table": "consent.add",
//    "doc": false,
//    "params": [
//        {
//            "name": "consent",
//            "def": {
//                "type": "table",
//                "typeName": "consent.consentTT"
//            },
//            "out": false,
//            "default": false
//        },
//    ]
	
	private String type;
	private String name;
	private String schema;
	private String table;
	private String doc;
	private DBParam[] params;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSchema() {
		return schema;
	}
	public void setSchema(String schema) {
		this.schema = schema;
	}
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public String getDoc() {
		return doc;
	}
	public void setDoc(String doc) {
		this.doc = doc;
	}
	public DBParam[] getParams() {
		return params;
	}
	public void setParams(DBParam[] params) {
		this.params = params;
	}
}
