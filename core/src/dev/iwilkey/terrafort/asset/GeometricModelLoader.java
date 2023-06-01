package dev.iwilkey.terrafort.asset;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public final class GeometricModelLoader {
	
	public static final int SPHERE_DIV = 40;
	public static Model createTexturedSphere(Texture texture) {
		final Material material = new Material(TextureAttribute.createDiffuse(texture));
		final ModelBuilder modelBuilder = new ModelBuilder();
		return modelBuilder.createSphere(
				2f, 2f, 2f, SPHERE_DIV, SPHERE_DIV, 
			    material, 
			    VertexAttributes.Usage.Position | 
			    VertexAttributes.Usage.Normal | 
			    VertexAttributes.Usage.TextureCoordinates
		);
	}
	
}
