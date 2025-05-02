package com.intacct.xtera.model;

public class ItemCrossReference {
	private String vendorID;
	private String vendorName;
	private String id;
	private String desc;
	
	public ItemCrossReference(String vendorID, String vendorName, String id, String desc) {
		super();
		this.vendorID = vendorID;
		this.vendorName = vendorName;
		this.id = id;
		this.desc = desc;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getVendorID() {
		return vendorID;
	}
	public void setVendorID(String vendorID) {
		this.vendorID = vendorID;
	}
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
}
