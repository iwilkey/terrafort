package dev.iwilkey.terrafort.gfx;

import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;

public class FrustumCulling {
	
	public static ModelCache cull(Array<RenderableProvider3> providers, Camera camera) {
		ModelCache ret = new ModelCache();
		ret.begin(camera);
		// Cull.
		for(RenderableProvider3 prov : providers) {
			ModelInstance instance = prov.getModelInstance();
			if(sphericalTestWith(instance, camera))
				ret.add(instance);
		}
		ret.end();
		return ret;
	}
	
	private static boolean sphericalTestWith(ModelInstance instance, Camera camera) {
		BoundingBox bounding = new BoundingBox();
		Vector3 centerOfBounding = new Vector3();
		Vector3 dimensions = new Vector3();
		float radius = 0.0f;
		instance.calculateBoundingBox(bounding);
		bounding.getCenter(centerOfBounding);
		bounding.getDimensions(dimensions);
		radius = dimensions.len() / 2f;
		Vector3 position = Vector3.Zero;
		instance.transform.getTranslation(position);
		position.add(centerOfBounding);
		return camera.frustum.sphereInFrustum(position, radius);
	}

}
