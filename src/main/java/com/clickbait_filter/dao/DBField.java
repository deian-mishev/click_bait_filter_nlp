package com.clickbait_filter.dao;

public class DBField {

	//  "column": "value",
	//  "type": "bigint",
	//  "nullable": true,
	//  "length": null,
	//  "scale": null,
	//  "identity": false,
	//  "isField": true,
	//  "default": null
	
	private String column;
	private String type;
	private Boolean nullable;
	private Integer length;
	private Integer scale;
	private Boolean identity;
	private Boolean isField;
	
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Boolean getNullable() {
		return nullable;
	}
	public void setNullable(Boolean nullable) {
		this.nullable = nullable;
	}
	public Integer getLength() {
		return length;
	}
	public void setLength(Integer length) {
		this.length = length;
	}
	public Integer getScale() {
		return scale;
	}
	public void setScale(Integer scale) {
		this.scale = scale;
	}
	public Boolean getIdentity() {
		return identity;
	}
	public void setIdentity(Boolean identity) {
		this.identity = identity;
	}
	public Boolean getIsField() {
		return isField;
	}
	public void setIsField(Boolean isField) {
		this.isField = isField;
	}
}
