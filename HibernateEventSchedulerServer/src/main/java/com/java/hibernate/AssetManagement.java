package com.java.hibernate;

import java.util.List;

import com.java.domain.Asset;

public interface AssetManagement {
	
	public abstract void exit();
	
	public abstract void create(Asset asset);
	
	public abstract Asset read(int assetId);
	
	public abstract void update(Asset asset);
	
	public abstract void delete(int assetId);
	
	public abstract List<?> showAll();

	public abstract int availableAssets();
	
	public abstract int totalAssets();
}
