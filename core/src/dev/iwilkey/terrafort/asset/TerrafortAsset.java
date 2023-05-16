package dev.iwilkey.terrafort.asset;

public class TerrafortAsset {
	
	private String path;
	private AssetType type;
	
	public TerrafortAsset(String path, AssetType type) {
		this.path = path;
		this.type = type;
	}

	public String getPath() {
		return path;
	}

	public AssetType getType() {
		return type;
	}

}
