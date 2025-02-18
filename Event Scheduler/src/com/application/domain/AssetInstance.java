package com.application.domain;


public class AssetInstance extends Asset {

	private String serialNum;
	private String status;
	private String Condition;
	
	public AssetInstance() {
		super();
		this.serialNum = " ";
		this.status = " ";
		this.Condition = " ";

	}

	public AssetInstance(String serialNum, String status, String condition) {
		super();
		this.serialNum = serialNum;
		this.status = status;
		Condition = condition;
	}

	public AssetInstance(Asset a, String serialNum, String status, String Condition) {
		super(a);
		this.serialNum = serialNum;
		this.status = status;
		this.Condition = Condition;
	}
	
	public AssetInstance(int asset_id, String name, String category, boolean availability, float rate, String status, String Condition, String serialNum) {
		super(asset_id, name, category, availability, rate);
		this.serialNum = serialNum;
		this.status = status;
		this.Condition = Condition;
	}

	public String getSerialNum() {
		return serialNum;
	}
	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCondition() {
		return Condition;
	}
	public void setCondition(String condition) {
		Condition = condition;
	}

	@Override
	public String toString() {
		return "AssetInstance\n Serial Number: " + getSerialNum() + "\nStatus: " + getStatus() + "\nCondition: " + getCondition() + "ID: " + getAsset_id() + "\nName: " + getName() + "\nCategory: " + getCategory() + "\nAvailability: "
				+ isAvailability() + "\nRate(Daily): " + getRate() + "\n";
	}
	
	
	
	
}
