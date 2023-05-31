package dev.iwilkey.terrafort.gfx;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;

import dev.iwilkey.terrafort.object.GameObject3;

/**
 * The `FrustumCulling` class provides methods for frustum culling, which is used to determine
 * if objects are within the camera's view frustum and should be rendered.
 */
public class FrustumCulling {
	
	private static Array<ModelInstance> culled3 = new Array<>();
	private static Array<Decal> culled25 = new Array<>();
	private static Vector3 centerOfBounding = new Vector3();
	private static Vector3 dimensions = new Vector3();
	private static Vector3 position = new Vector3();
	private static float radius = 0.0f;
	
	/**
	 * Performs frustum culling on a collection of 3D renderable providers, returning only the
	 * instances that are within the camera's view frustum.
	 * @param providers The collection of 3D renderable providers.
	 * @param camera The camera used for frustum culling.
	 * @return An array of model instances that are within the frustum.
	 */
	public static Array<ModelInstance> cull3(Array<RenderableProvider3> providers, Camera camera) {
		culled3.clear();
		for(RenderableProvider3 prov : providers) {
			ModelInstance instance = prov.getModelInstance();
			if(sphericalTestWith(instance, prov.getBoundingBox(), camera))
				culled3.add(instance);
		}
		return culled3;
	}
	
	/**
	 * Performs frustum culling on a static renderable provider batch, determining if any of
	 * the renderable objects are within the camera's view frustum.
	 * @param staticProvider The static renderable provider batch.
	 * @param camera The camera used for frustum culling.
	 * @return `true` if at least one object is within the frustum, `false` otherwise.
	 */
	public static boolean cullStaticRenderableProviderCache(StaticRenderableProviderBatch staticProvider, Camera camera) {
		GameObject3[] buffer = staticProvider.getBuffer();
		for(int i = 0; i < Renderer.STATIC_RENDERABLE_BUFFER_SIZE; i++) {
			if(buffer[i] == null) {
				return false;
			} else {
				if(sphericalTestWith(buffer[i].getModelInstance(), buffer[i].getBoundingBox(), camera))
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Performs frustum culling on a collection of 2.5D renderable providers, returning only the
	 * decals that are within the camera's view frustum.
	 * @param providers The collection of 2.5D renderable providers.
	 * @param camera The camera used for frustum culling.
	 * @return An array of decals that are within the frustum.
	 */
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
