package domain;

public class Asset {

	private int asset_id;
	private String name;
	private String category;
	private boolean availability;
	private double rate;
	
	public Asset() {
		super();
		this.asset_id = 0000;
		this.name = " ";
		this.category = " ";
		this.rate = 0.0;
	}
	
	public Asset(Asset a) {
		super();
		this.asset_id = a.asset_id;
		this.name = a.name;
		this.category = a.category;
		this.availability = a.availability;
		this.rate = a.rate;
	}
	
	public Asset(int asset_id, String name, String category, boolean availability, float rate) {
		super();
		this.asset_id = asset_id;
		this.name = name;
		this.category = category;
		this.availability = availability;
		this.rate = rate;
	}
	
	public int getAsset_id() {
		return asset_id;
	}
	public void setAsset_id(int asset_id) {
		this.asset_id = asset_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public boolean isAvailability() {
		return availability;
	}
	public void setAvailability(boolean availability) {
		this.availability = availability;
	}
	public double getRate() {
		return rate;
	}
	public void setRate(float rate) {
		this.rate = rate;
	}

	@Override
	public String toString() {
		return "ID: " + asset_id + "\nName: " + name + "\nCategory: " + category + "\nAvailability: "
				+ availability + "\nRate(Daily): " + rate + "\n";
	}
	
	
	
	
	
}
