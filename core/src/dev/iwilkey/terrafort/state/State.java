package dev.iwilkey.terrafort.state;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import dev.iwilkey.terrafort.TerrafortEngine;
import dev.iwilkey.terrafort.asset.AssetBuffer;
import dev.iwilkey.terrafort.gfx.Camera;
import dev.iwilkey.terrafort.gfx.RenderableProvider2;
import dev.iwilkey.terrafort.gfx.RenderableProvider25;
import dev.iwilkey.terrafort.gfx.RenderableProvider3;
import dev.iwilkey.terrafort.gfx.ViewportResizable;
import dev.iwilkey.terrafort.object.GameObject;
import dev.iwilkey.terrafort.object.GameObjectHandler;

public abstract class State implements ViewportResizable, Disposable {
	
	// Engine and assets.
	private TerrafortEngine engine;
	private AssetBuffer assetBuffer;
	private AssetManager assets;
	
	// GameObject handler and RenderableProviders.
	protected final Array<RenderableProvider3> provider3;
	protected final Array<RenderableProvider25> provider25;
	protected final Array<RenderableProvider2> provider2;
	protected final GameObjectHandler objectHandler;
	
	// For 3D rendering.
	protected Camera camera3;
	protected Environment environment3;
	
	public State(TerrafortEngine engine, AssetBuffer assetBuffer) {
		this.engine = engine;
		this.assetBuffer = assetBuffer;
		this.assets = null;
		provider3 = new Array<>();
		provider25 = new Array<>();
		provider2 = new Array<>();
		objectHandler = new GameObjectHandler(this);
		init3();
	}
	
	private void init3() {
		environment3 = new Environment();
		camera3 = new Camera(64);
	}
	
	public final void init() {
		if(assetBuffer == null) {
			assets = null;
			return;
		}
		assets = new AssetManager();
		assetBuffer.loadIntoMemory(assets);
	}
	
	public final boolean load() {
		if(assetBuffer == null || assets == null) 
			return true;
		if(assets.isFinished())
			return true;
		assets.update();
		return false;
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
	
	protected long addGameObject(GameObject o) {
		long id = objectHandler.create(o);
		return id;
	}
	
	protected GameObject getGameObject(long id) {
		return objectHandler.get(id);
	}
	
	/**
	 * Getters and setters
	 */
	
	public final Array<RenderableProvider3> getProvider3() {
		return provider3;
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
	
	public final AssetManager getAssetManager() {
		return assets;
	}
	
	public final AssetBuffer getAssetBuffer() {
		return assetBuffer;
	}
	
	public final GameObjectHandler getObjectHandler() {
		return objectHandler;
	}
	
	protected final TerrafortEngine getEngine() {
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
		objectHandler.dispose();
		if(assets != null)
			assets.dispose();
	}
	
}
