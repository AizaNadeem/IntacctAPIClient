package com.intacct.xtera.model;

import java.util.List;

public class Item {
	private String id;
	private String desc;
	private String productLines;
	List<ItemCrossReference> manufacturers;
	
	public Item(String id, String desc, String productLines, List<ItemCrossReference> manufacturers) {
		super();
		this.id = id;
		this.desc = desc;
		this.productLines = productLines;
		this.manufacturers = manufacturers;
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
	public String getProductLines() {
		return productLines;
	}
	public void setProductLines(String productLines) {
		this.productLines = productLines;
	}
	public List<ItemCrossReference> getManufacturers() {
		return manufacturers;
	}
	public void setManufacturers(List<ItemCrossReference> manufacturers) {
		this.manufacturers = manufacturers;
	}
	@Override
	public String toString() {
		return "Item [id=" + id + ", desc=" + desc + ", productLines=" + productLines + ", manufacturers="
				+ manufacturers + "]";
	}
}
