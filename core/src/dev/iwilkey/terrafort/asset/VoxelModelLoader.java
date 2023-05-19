package dev.iwilkey.terrafort.asset;

import org.lwjgl.opengl.GL20;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public final class VoxelModelLoader {
	
	public static final int MODEL_SCALE = 1;
	
	public static VoxelModel createVoxelModelFromRaw(final String raw) {
		Array<Voxel> voxels = new Array<>();
		String[] lines = raw.split("\n");
		for(int i = 0; i < lines.length; i++) {
			if(i == 0) {
				if(!lines[0].contains("# Goxel 0.12.0")) {
					System.out.println("[Terrafort Engine] The TerrafortAssetHandler has encountered raw Voxel Model data that is not in the correct Goxel 0.12.0 format!");
					System.exit(-1);
				}
			} else if(i > 2) {
				try {
					String[] values = lines[i].split(" ");
					int x = Integer.parseInt(values[0]);
					int y = Integer.parseInt(values[1]);
					int z = Integer.parseInt(values[2]);
					int rawColor = Integer.parseInt(values[3], 16);
					Voxel voxel = new Voxel(x, y, z, rawColor);
					voxels.add(voxel);
				} catch(Exception e) {
					System.out.println("[Terrafort Engine] The TerrafortAssetHandler has encountered raw Voxel Model data that is corrupted!");
					e.printStackTrace();
					System.exit(-1);
				}
			}
		}
		return new VoxelModel(voxels);
	}
	
	/*
	@SuppressWarnings("deprecation")
	public static Model buildModelFromRaw(String name, final String raw) {
		// Voxel representation of raw data.
		VoxelModel voxelModel = createVoxelModelFromRaw(raw);
		// Returned model.
		Model model = new Model();
        // Builder.
		ModelBuilder builder = new ModelBuilder();
		builder.begin();
		MeshPartBuilder mesher;
		// Build the model.
		for(final Voxel vox : voxelModel.getVoxels()) {
			float x = vox.getX() * MODEL_SCALE;
			float y = vox.getY() * MODEL_SCALE;
			float z = vox.getZ() * MODEL_SCALE;
            int rawColor = vox.getRawColor();
            Color color = vox.getColor();
            // UID for the voxel.
            String uid = String.format(name + ",%.2f,%.2f,%.2f,%d", x, y, z, rawColor);
            // Node for Voxel.
    		Node voxel = builder.node();
    		voxel.id = uid;
    		mesher = builder.part(uid, GL20.GL_TRIANGLES, 
    				Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(color)));
    		float cx = voxelModel.getCenterX() * MODEL_SCALE;
    		float cy = voxelModel.getCenterY() * MODEL_SCALE;
    		float cz = voxelModel.getCenterZ() * MODEL_SCALE;
    		mesher.box(x - cx, y - cy, z - cz, MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
		}
		model = builder.end();
		return model;
	}
	*/
	
	public static Model buildModelFromRaw(String name, final String raw) {
		// Voxel representation of raw data.
		VoxelModel voxelModel = createVoxelModelFromRaw(raw);
		float width = voxelModel.getWidth();
		float height = voxelModel.getHeight();
		float depth = voxelModel.getDepth();
		float cx = voxelModel.getCenterX();
		float cy = voxelModel.getCenterY();
		float cz = voxelModel.getCenterZ();
		
		System.out.println(cx);
		// Returned model.
		Model model = new Model();
	    // Builder.
		ModelBuilder builder = new ModelBuilder();
		builder.begin();
		MeshPartBuilder mesher;
	    // Build the model.
	    for(Voxel vox : voxelModel.getVoxels()) {
	        int x = vox.getX();
	        int y = vox.getY();
	        int z = vox.getZ();
	        int rawColor = vox.getRawColor();
            Color color = vox.getColor();
         // UID for the voxel.
            String uid = String.format(name + ",%d,%d,%d,%d", x, y, z, rawColor);
            // Node for Voxel.
    		Node voxel = builder.node();
    		voxel.id = uid;
    		mesher = builder.part(uid, GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(color)));
    		vox.calculateGeometry(MODEL_SCALE, cx, cy, cz);
	        // Check each neighbor; if it doesn't exist, create a face in that direction
	        if (!voxelModel.hasVoxelAt((int)x, (int)y + 1, (int)z)) { // Check up
	        	VoxelFaceGeometry face = vox.getUpFaceGeom(); 
	        	mesher.rect(face.get00(), face.get01(), face.get10(), face.get11(), face.getNormal());
	        }
	        if (!voxelModel.hasVoxelAt((int)x, (int)y - 1, (int)z)) { // Check down
	            // Create bottom face
	        	VoxelFaceGeometry face = vox.getDownFaceGeom(); 
	        	mesher.rect(face.get00(), face.get01(), face.get10(), face.get11(), face.getNormal());
	        }
	        if (!voxelModel.hasVoxelAt((int)x, (int)y, (int)z + 1)) { // Check forward
	            // Create front face
	        	VoxelFaceGeometry face = vox.getForwardFaceGeom(); 
	        	mesher.rect(face.get00(), face.get01(), face.get10(), face.get11(), face.getNormal());
	        }
	        if (!voxelModel.hasVoxelAt((int)x, (int)y, (int)z - 1)) { // Check back
	            // Create back face
	        	VoxelFaceGeometry face = vox.getBackwardFaceGeom(); 
	        	mesher.rect(face.get00(), face.get01(), face.get10(), face.get11(), face.getNormal());
	        }
	        if (!voxelModel.hasVoxelAt((int)x + 1, (int)y, (int)z)) { // Check left
	            // Create left face
	        	VoxelFaceGeometry face = vox.getLeftFaceGeom(); 
	        	mesher.rect(face.get00(), face.get01(), face.get10(), face.get11(), face.getNormal());
	        }
	        if (!voxelModel.hasVoxelAt((int)x - 1, (int)y, (int)z)) { // Check right
	            // Create right face
	        	VoxelFaceGeometry face = vox.getRightFaceGeom(); 
	        	mesher.rect(face.get00(), face.get01(), face.get10(), face.get11(), face.getNormal());
	        }
	    }
	    model = builder.end();
	    return model;
	}
	
}
