package com.clickbait_filter.dao;

public class DBTable {

//    "type": "table",
//    "name": "[core].[arrayList]",
//    "schema": "core",
//    "table": "arrayList",
//    "doc": false,
//    "fields": [
//        {
//            "column": "value",
//            "type": "nvarchar",
//            "nullable": true,
//            "length": 100,
//            "scale": null,
//            "identity": false,
//            "isField": true,
//            "default": null
//        }
//    ]
    		
    private String type;
	private String name;
	private String schema;
	private String table;
	private Boolean doc;
	private DBField[] fields;
	
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
	public Boolean getDoc() {
		return doc;
	}
	public void setDoc(Boolean doc) {
		this.doc = doc;
	}
	public DBField[] getFields() {
		return fields;
	}
	public void setFields(DBField[] fields) {
		this.fields = fields;
	}
}
