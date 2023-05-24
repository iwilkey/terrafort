package dev.iwilkey.terrafort.state;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import dev.iwilkey.terrafort.TerrafortEngine;
import dev.iwilkey.terrafort.gfx.Camera;
import dev.iwilkey.terrafort.gfx.RenderableProvider2;
import dev.iwilkey.terrafort.gfx.RenderableProvider25;
import dev.iwilkey.terrafort.gfx.RenderableProvider3;
import dev.iwilkey.terrafort.gfx.StaticRenderableProviderBatchSystem;
import dev.iwilkey.terrafort.gfx.ViewportResizable;
import dev.iwilkey.terrafort.object.GameObject;
import dev.iwilkey.terrafort.object.GameObjectHandler;
import dev.iwilkey.terrafort.physics.bullet.BulletPhysicsTag;
import dev.iwilkey.terrafort.physics.bullet.BulletRaycaster;
import dev.iwilkey.terrafort.physics.bullet.BulletRigidbody;
import dev.iwilkey.terrafort.utilities.Pair;

public abstract class State implements ViewportResizable, Disposable {
	
	// Engine and assets.
	private TerrafortEngine engine;
	
	// GameObject handler and RenderableProviders.
	protected final Array<RenderableProvider3> provider3;
	protected final StaticRenderableProviderBatchSystem providerCache3;
	protected final Array<RenderableProvider25> provider25;
	protected final Array<RenderableProvider2> provider2;
	protected final GameObjectHandler objectHandler;
	
	// For 3D rendering and physics.
	protected Camera camera3;
	protected Environment environment3;
	protected BulletRaycaster raycaster;
	
	public State(TerrafortEngine engine) {
		this.engine = engine;
		provider3 = new Array<>();
		providerCache3 = new StaticRenderableProviderBatchSystem(this);
		provider25 = new Array<>();
		provider2 = new Array<>();
		objectHandler = new GameObjectHandler(this);
		init3();
	}
	
	private void init3() {
		environment3 = new Environment();
		camera3 = new Camera(64);
		raycaster = new BulletRaycaster();
	}
	
	public final void init() {
		
	}
	
	public final boolean load() {
		return true;
	}
	
	public final void update() {
		objectHandler.tick();
		camera3.tick();
		tick();
	}
	
	public abstract void begin();
	public abstract void tick();
	public abstract void gui();
	public abstract void end();
	
	
	/**
	 * GameObject methods.
	 */
	
	public long addGameObject(GameObject o) {
		long id = objectHandler.create(o);
		return id;
	}
	
	public Array<Long> addGameObjects(Array<GameObject> objects) {
		Array<Long> ids = new Array<>();
		for(GameObject obj : objects)
			ids.add(addGameObject(obj));
		return ids;
	}
	
	public GameObject getGameObject(long id) {
		return objectHandler.get(id);
	}
	
	/**
	 * Raycasting methods.
	 */
	
	public Vector3 doRaycastForPoint(float distance, BulletPhysicsTag... accept) {
		if(accept.length == 0)
			return null;
		Pair<BulletRigidbody, Vector3> raycast = doRaycast(distance);
		if(raycast == null)
			return null;
		if(!rigidbodyAccepted(raycast.getFirst(), accept))
			return null;
		return raycaster.hitPoint(camera3, objectHandler.getPhysicsEngine().getDynamicsWorld(), distance);
	}
	
	public BulletRigidbody doRaycastForObject(float distance, BulletPhysicsTag... accept) {
		if(accept.length == 0)
			return null;
		Pair<BulletRigidbody, Vector3> raycast = doRaycast(distance);
		if(raycast == null)
			return null;
		if(!rigidbodyAccepted(raycast.getFirst(), accept))
			return null;
		return raycaster.hitObject(camera3, objectHandler.getPhysicsEngine().getDynamicsWorld(), distance);
	}
	
	public boolean doRaycastCheckObstruction(float distance, BulletPhysicsTag... accept) {
		if(accept.length == 0)
			return false;
		Pair<BulletRigidbody, Vector3> raycast = doRaycast(distance);
		if(raycast == null)
			return false;
		if(!rigidbodyAccepted(raycast.getFirst(), accept))
			return false;
		return raycaster.hitPoint(camera3, objectHandler.getPhysicsEngine().getDynamicsWorld(), distance) != null;
	}
	
	private Pair<BulletRigidbody, Vector3> doRaycast(float distance) {
		return raycaster.hit(camera3, objectHandler.getPhysicsEngine().getDynamicsWorld(), distance);
	}
	
	private boolean rigidbodyAccepted(BulletRigidbody body, BulletPhysicsTag... accept) {
		if(accept.length == 0)
			return false;
		boolean in = false;
		for(BulletPhysicsTag t : accept) {
			if(t == body.getTag()) {
				in = true;
				break;
			}
		}	
		return in;
	}
	
	/**
	 * Getters and setters
	 */
	
	public final Array<RenderableProvider3> getProvider3() {
		return provider3;
	}
	
	public final StaticRenderableProviderBatchSystem getStaticRenderableProviderCacheSystem() {
		return providerCache3;
	}
	
	public final Array<RenderableProvider25> getProvider25() {
		return provider25;
	}
	
	public final Array<RenderableProvider2> getProvider2() {
		return provider2;
	}
	
	public final Environment getRenderable3Environment() {
		return environment3;
	}
	
	public final Camera getCamera() {
		return camera3;
	}

	public final GameObjectHandler getObjectHandler() {
		return objectHandler;
	}
	
	public final TerrafortEngine getEngine() {
		return engine;
	}
	
	/**
	 * Interfaces
	 */
	
	@Override
	public void onViewportResize(int newWidth, int newHeight) {
		objectHandler.onViewportResize(newWidth, newHeight);
	}
	
	@Override
	public void dispose() {
		providerCache3.dispose();
		objectHandler.dispose();
	}
	
}
