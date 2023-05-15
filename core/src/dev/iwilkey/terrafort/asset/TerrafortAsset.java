package dev.iwilkey.terrafort.asset;

public class TerrafortAsset {
	
	private String name;
	private String path;
	private AssetType type;
	
	public TerrafortAsset(String name, String path, AssetType type) {
		this.name = name;
		this.path = path;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public AssetType getType() {
		return type;
	}

}
