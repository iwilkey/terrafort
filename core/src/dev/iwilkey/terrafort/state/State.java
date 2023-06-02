package dev.iwilkey.terrafort.state;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import dev.iwilkey.terrafort.TerrafortEngine;
import dev.iwilkey.terrafort.gfx.Camera;
import dev.iwilkey.terrafort.gfx.RenderableProvider3Environment;
import dev.iwilkey.terrafort.gfx.RenderableProvider2;
import dev.iwilkey.terrafort.gfx.RenderableProvider25;
import dev.iwilkey.terrafort.gfx.RenderableProvider3;
import dev.iwilkey.terrafort.gfx.StaticRenderableProviderBatchSystem;
import dev.iwilkey.terrafort.gfx.ViewportResizable;
import dev.iwilkey.terrafort.input.KeyBinding;
import dev.iwilkey.terrafort.object.GameObject;
import dev.iwilkey.terrafort.object.GameObjectHandler;
import dev.iwilkey.terrafort.physics.TerrafortPhysicsRaycaster;
import dev.iwilkey.terrafort.physics.PhysicsTag;
import dev.iwilkey.terrafort.physics.TerrafortRigidbody;
import dev.iwilkey.terrafort.utilities.Pair;

/**
 * Abstract base class for game states.
 */
public abstract class State implements ViewportResizable, Disposable {
	
	// Engine and assets.
	protected TerrafortEngine engine;
	protected final KeyBinding stateKeyBindings;
	
	// GameObject handler and RenderableProviders.
	protected final Array<RenderableProvider3> provider3;
	protected final StaticRenderableProviderBatchSystem providerCache3;
	protected final Array<RenderableProvider25> provider25;
	protected final Array<RenderableProvider2> provider2;
	protected final GameObjectHandler objectHandler;
	
	// For 3D rendering and physics.
	protected Camera camera3;
	protected RenderableProvider3Environment environment3;
	protected TerrafortPhysicsRaycaster raycaster;
	
	public State(TerrafortEngine engine) {
		this.engine = engine;
		// Initialize RenderableProviders.
		provider3 = new Array<>();
		providerCache3 = new StaticRenderableProviderBatchSystem(this);
		provider25 = new Array<>();
		provider2 = new Array<>();
		// Initialize the object handler.
		objectHandler = new GameObjectHandler(this);
		// Initialize binding object and register custom bindings.
		stateKeyBindings = new KeyBinding();
		registerKeyBindings(stateKeyBindings);
		// Initialize 3D components.
		init3();
	}
	
	private void init3() {
		// Create the 3D rendering environment.
		environment3 = new RenderableProvider3Environment();
		// Init the 3D camera with a FOV of 64.
		camera3 = new Camera(64);
		// Init the physics raycaster.
		raycaster = new TerrafortPhysicsRaycaster();
	}

	/**
	 * Update method called every frame.
	 */
	public final void update() {
		objectHandler.tick();
		camera3.tick();
		tick();
	}
	
	/**
	 * Engine interface.
	 */
	
	/**
	 * Register custom key bindings for the state.
	 * 
	 * @param stateKeyBindings The key bindings object to register the bindings with.
	 */
	public abstract void registerKeyBindings(final KeyBinding stateKeyBindings);
	
	/**
	 * Method called when the state begins.
	 */
	public abstract void begin();
	
	/**
	 * Update method called every frame.
	 */
	public abstract void tick();
	
	/**
	 * Method for rendering the GUI.
	 */
	public abstract void gui();
	
	/**
	 * Method called when the state ends.
	 */
	public abstract void end();
	
	/**
	 * State API
	 */
	
	/**
	 * Input API.
	 */
	
	/**
	 * Checks if a binding was just pressed down.
	 * 
	 * @param binding The name of the binding to check.
	 * @return True if the binding was just pressed down, false otherwise.
	 */
	public boolean bindingJustDown(String binding) {
		return stateKeyBindings.bindingJustDown(binding);
	}
	
	/**
	 * Checks if a binding is currently being held down.
	 * 
	 * @param binding The name of the binding to check.
	 * @return True if the binding is being held down, false otherwise.
	 */
	public boolean bindingDown(String binding) {
		return stateKeyBindings.bindingCurrent(binding);
	}
	
	/**
	 * Checks if a binding was just released.
	 * 
	 * @param binding The name of the binding to check.
	 * @return True if the binding was just released, false otherwise.
	 */
	public boolean bindingJustUp(String binding) {
		return stateKeyBindings.bindingJustUp(binding);
	}
	
	/**
	 * GameObject API.
	 */
	
	/**
	 * Adds a GameObject to the state.
	 * 
	 * @param o The GameObject to add.
	 * @return The unique ID assigned to the GameObject.
	 */
	public long addGameObject(GameObject o) {
		final long id = objectHandler.create(o);
		return id;
	}
	
	/**
	 * Adds an array of GameObjects to the state.
	 * 
	 * @param objects The array of GameObjects to add.
	 * @return An array of unique IDs assigned to the GameObjects.
	 */
	public Array<Long> addGameObjects(Array<GameObject> objects) {
		final Array<Long> ids = new Array<>();
		for(GameObject obj : objects)
			ids.add(addGameObject(obj));
		return ids;
	}
	
	/**
	 * Retrieves a GameObject by its unique ID.
	 * 
	 * @param id The ID of the GameObject to retrieve.
	 * @return The GameObject with the specified ID, or null if not found.
	 */
	public GameObject getGameObject(long id) {
		return objectHandler.get(id);
	}
	
	/**
	 * Particle API.
	 * TODO: Implement this.
	 */
	
	/**
	 * Raycasting API.
	 */
	
	/**
	 * Performs a raycast and returns the hit point for a specified distance.
	 * 
	 * @param distance The maximum distance of the raycast.
	 * @param accept The accepted physics tags for the raycast.
	 * @return The hit point as a Vector3, or null if no hit.
	 */
	public Vector3 doRaycastForPoint(float distance, PhysicsTag... accept) {
		if(accept.length == 0)
			return null;
		final Pair<TerrafortRigidbody, Vector3> raycast = doRaycast(distance);
		if(raycast == null)
			return null;
		if(!rigidbodyAccepted(raycast.getFirst(), accept))
			return null;
		return raycaster.hitPoint(camera3, objectHandler.getPhysicsEngine().getDynamicsWorld(), distance);
	}
	
	/**
	 * Performs a raycast and returns the hit object for a specified distance.
	 * 
	 * @param distance The maximum distance of the raycast.
	 * @param accept The accepted physics tags for the raycast.
	 * @return The hit object as a TerrafortRigidbody, or null if no hit.
	 */
	public TerrafortRigidbody doRaycastForObject(float distance, PhysicsTag... accept) {
		if(accept.length == 0)
			return null;
		final Pair<TerrafortRigidbody, Vector3> raycast = doRaycast(distance);
		if(raycast == null)
			return null;
		if(!rigidbodyAccepted(raycast.getFirst(), accept))
			return null;
		return raycaster.hitObject(camera3, objectHandler.getPhysicsEngine().getDynamicsWorld(), distance);
	}
	
	/**
	 * Checks if there is an obstruction in the raycast path for a specified distance.
	 * 
	 * @param distance The maximum distance of the raycast.
	 * @param accept The accepted physics tags for the raycast.
	 * @return True if there is an obstruction, false otherwise.
	 */
	public boolean doRaycastCheckObstruction(float distance, PhysicsTag... accept) {
		if(accept.length == 0)
			return false;
		final Pair<TerrafortRigidbody, Vector3> raycast = doRaycast(distance);
		if(raycast == null)
			return false;
		if(!rigidbodyAccepted(raycast.getFirst(), accept))
			return false;
		return raycaster.hitPoint(camera3, objectHandler.getPhysicsEngine().getDynamicsWorld(), distance) != null;
	}
	
	private Pair<TerrafortRigidbody, Vector3> doRaycast(float distance) {
		return raycaster.hit(camera3, objectHandler.getPhysicsEngine().getDynamicsWorld(), distance);
	}
	
	private boolean rigidbodyAccepted(TerrafortRigidbody body, PhysicsTag... accept) {
		if(accept.length == 0)
			return false;
		boolean in = false;
		for(PhysicsTag t : accept) {
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
	
	/**
	 * Retrieves the TerrafortEngine associated with the state.
	 * 
	 * @return The TerrafortEngine instance.
	 */
	public final TerrafortEngine getEngine() {
		return engine;
	}
	
	/**
	 * Retrieves the KeyBinding object for the state.
	 * 
	 * @return The KeyBinding object.
	 */
	public final KeyBinding getKeyBindings() {
		return stateKeyBindings;
	}
	
	/**
	 * Retrieves the array of RenderableProvider3 objects.
	 * 
	 * @return The array of RenderableProvider3 objects.
	 */
	public final Array<RenderableProvider3> getProvider3() {
		return provider3;
	}
	
	/**
	 * Retrieves the StaticRenderableProviderBatchSystem.
	 * 
	 * @return The StaticRenderableProviderBatchSystem instance.
	 */
	public final StaticRenderableProviderBatchSystem getStaticRenderableProviderCacheSystem() {
		return providerCache3;
	}
	
	/**
	 * Retrieves the array of RenderableProvider25 objects.
	 * 
	 * @return The array of RenderableProvider25 objects.
	 */
	public final Array<RenderableProvider25> getProvider25() {
		return provider25;
	}
	
	/**
	 * Retrieves the array of RenderableProvider2 objects.
	 * 
	 * @return The array of RenderableProvider2 objects.
	 */
	public final Array<RenderableProvider2> getProvider2() {
		return provider2;
	}
	
	/**
	 * Retrieves the Environment for 3D rendering.
	 * 
	 * @return The Environment instance.
	 */
	public final RenderableProvider3Environment getRenderable3Environment() {
		return environment3;
	}
	
	/**
	 * Retrieves the Camera for 3D rendering.
	 * 
	 * @return The Camera instance.
	 */
	public final Camera getCamera() {
		return camera3;
	}

	/**
	 * Retrieves the GameObjectHandler.
	 * 
	 * @return The GameObjectHandler instance.
	 */
	public final GameObjectHandler getObjectHandler() {
		return objectHandler;
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
