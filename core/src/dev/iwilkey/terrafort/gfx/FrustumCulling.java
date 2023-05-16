package dev.iwilkey.terrafort.gfx;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;

public class FrustumCulling {
	
	private static Array<ModelInstance> culled3 = new Array<>();
	private static Array<Decal> culled25 = new Array<>();
	private static Vector3 centerOfBounding = new Vector3();
	private static Vector3 dimensions = new Vector3();
	private static Vector3 position = new Vector3();
	private static float radius = 0.0f;
	
	public static Array<ModelInstance> cull3(Array<RenderableProvider3> providers, Camera camera) {
		culled3.clear();
		for(RenderableProvider3 prov : providers) {
			ModelInstance instance = prov.getModelInstance();
			if(sphericalTestWith(instance, prov.getBoundingBox(), camera))
				culled3.add(instance);
		}
		return culled3;
	}
	
	public static Array<Decal> cull25(Array<RenderableProvider25> providers, Camera camera) {
		culled25.clear();
		for(RenderableProvider25 prov : providers) {
			Decal decal = prov.getDecal();
			if(prov.shouldBillboard())
				decal.lookAt(camera.position, camera.up);
			position = decal.getPosition();
	        float radius = Math.max(decal.getWidth() * decal.getScaleX(), decal.getHeight() * decal.getScaleY()) / 2f;
			if(camera.frustum.sphereInFrustum(position, radius))
				culled25.add(decal);
		}
		return culled25;
	}
	
	private static boolean sphericalTestWith(ModelInstance instance, BoundingBox instanceBounding, Camera camera) {
		instanceBounding.getCenter(centerOfBounding);
		instanceBounding.getDimensions(dimensions);
		radius = dimensions.len() / 2f;
		position = Vector3.Zero;
		instance.transform.getTranslation(position);
		position.add(centerOfBounding);
		return camera.frustum.sphereInFrustum(position, radius);
	}

}
