package dev.iwilkey.terrafort.asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;

public class AssetBuffer {
	
	public boolean finalized = false;
	private Array<TerrafortAsset> buffer;
	
	public AssetBuffer(TerrafortAsset... pack) {
		buffer = new Array<>();
		for(TerrafortAsset ass : pack) 
			buffer.add(ass);
	}
	
	public void loadIntoMemory(AssetManager memory) {
		for(TerrafortAsset ass : buffer) {
			if(!Gdx.files.internal(ass.getPath()).exists()) {
				System.out.println("[Terrafort Engine] FATAL: Asset at path \"" + ass.getPath() + "\" does not exist!");
				System.exit(-1);
			}
			switch(ass.getType()) {
				case MODEL:
					memory.load(ass.getPath(), Model.class);
					break;
				case TEXTURE:
					memory.load(ass.getPath(), Texture.class);
					break;
				default: return;
			}
		}
	}
	
	public void finalizeMemory(AssetManager memory) {
		buffer.clear();
		// Process the loaded voxel models to work well with the Terrafort Engine.
		Array<String> assetNames = memory.getAssetNames();
		for(String name : assetNames) {
			Object asset = memory.get(name);
			if(asset instanceof Model) {
				Model model = (Model)asset;
				// Process Model.
				if (!model.nodes.isEmpty()) {
					Node node = model.nodes.get(0);
					node.id = "root";
					if (!model.materials.isEmpty() && !model.meshParts.isEmpty()) {
						node.parts.clear();
						node.parts.add(new NodePart(model.meshParts.first(), model.materials.get(0)));
					}
					// Get Model bounding box.
					BoundingBox box = new BoundingBox();
					model.calculateBoundingBox(box);
					Vector3 dim = new Vector3();
					box.getDimensions(dim);
					// Calculate center offset.
					Vector3 center = new Vector3(0, -(dim.y / 2f), 0);
					// Transform Model to be center.
					node.translation.add(center);
					model.calculateTransforms();
				}
			}
		}
		finalized = true;
	}
}
