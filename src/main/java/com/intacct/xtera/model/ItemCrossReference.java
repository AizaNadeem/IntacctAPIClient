package com.intacct.xtera.model;

public class ItemCrossReference {
	private String vendor;
	private String id;
	private String desc;
	
	public ItemCrossReference(String vendor, String id, String desc) {
		super();
		this.vendor = vendor;
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

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	
}
