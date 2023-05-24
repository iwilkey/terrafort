package dev.iwilkey.terrafort.gfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import dev.iwilkey.terrafort.TerrafortEngine;
import dev.iwilkey.terrafort.physics.bullet.BulletWrapper;
import dev.iwilkey.terrafort.state.State;
import dev.iwilkey.terrafort.state.game.SinglePlayerEngineState;
import dev.iwilkey.terrafort.state.game.gfx.Space;
import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

public class Renderer implements ViewportResizable, Disposable {
	
	// Direct reference to the engine.
	private final TerrafortEngine engine;
	// Global GL and GL back buffer settings.
	public static final int RED_BITS = 1 << 3;
	public static final int GREEN_BITS = 1 << 3;
	public static final int BLUE_BITS = 1 << 3;
	public static final int ALPHA_BITS = 1 << 3;
	public static final int DEPTH_BITS = 1 << 5;
	public static final int STENCIL_BITS = 0;
	// TODO: Perhaps allow this to be configured by user since it has direct impact on overall graphical quality.
	public static final int MSAA_SAMPLES = 3;
	public static final int GLOBAL_GL_LINE_WIDTH = 1;
	// GLFW and GL runtime properties.
	private static int width;
	private static int height;
	private final Lwjgl3Graphics graphics;
	private final Lwjgl3Window window;
	// GL Impls and Batches.
	private static final ImGuiImplGlfw DI_GLFW = new ImGuiImplGlfw();
	private static final ImGuiImplGl3 DI_GL3 = new ImGuiImplGl3();
	private final ModelBatch batch3 = new ModelBatch();
	private final ModelCache batch3StaticCache = new ModelCache();
	private DecalBatch batch25 = null; // Can only be initialized when a valid state is set.
	private final SpriteBatch batch2 = new SpriteBatch();
	
	public Renderer(TerrafortEngine engine) {
		this.engine = engine;
		// Grab GL context from libGDX GLFW abstraction.
		graphics = (Lwjgl3Graphics)Gdx.graphics;
		window = graphics.getWindow();
		// GL settings.
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glDepthFunc(GL20.GL_LEQUAL);
		Gdx.gl.glClearColor(0.14f, 0.13f, 0.13f, 1.0f);
		Gdx.gl.glLineWidth(GLOBAL_GL_LINE_WIDTH);
		// Register initial display dimensions.
		registerViewportDimensions(graphics.getWidth(), graphics.getHeight());
		// Link Dear ImGui context to GL context.
		ImGui.createContext();
		ImGui.getIO().setIniFilename(null);
		ImGui.getIO().setWantCaptureKeyboard(false);
		ImGui.getIO().setWantCaptureMouse(false);
		DI_GLFW.init(window.getWindowHandle(), true);
		DI_GL3.init("#version 120");
		// Create the Null Object. See notes on "createNullObject()" method to understand why.
		createNullObject();
	}

	private Model nullObject;
	private ModelInstance nullObjectRenderable;
	/**
	 * NOTE: Why is this here? It's a Null Object pattern. This is my solution to the libGDX graphics pipeline expecting there to be at least something to render 
	 * (even if everything has been culled away.) Is it clever? I don't know; you tell me. I'm just trying to make a game.
	 */
	@SuppressWarnings("deprecation")
	private void createNullObject() {
		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();
		MeshPartBuilder meshBuilder;
		meshBuilder = modelBuilder.part("null", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material());
		meshBuilder.sphere(0, 0, 0, 10, 10);
		nullObject = modelBuilder.end();
		nullObjectRenderable = new ModelInstance(nullObject);
	}
	
	private void registerViewportDimensions(int w, int h) {
		HdpiUtils.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		width = w;
		height = h;
	}
	
	/**
	 * Rendering utilities
	 */
	
	public void clearGl(boolean depth) {
		Gdx.gl.glClearColor(0.14f, 0.13f, 0.13f, 1.0f);
		int clsbit = GL20.GL_COLOR_BUFFER_BIT;
		if(depth) clsbit |= GL20.GL_DEPTH_BUFFER_BIT;
		Gdx.gl.glClear(clsbit);
	}
	
	/**
	 * Render
	 */
	public void render(State state) {
		// Frustum culling for 3D objects.
		Array<ModelInstance> culled = FrustumCulling.cull3(state.getProvider3(), state.getCamera());
		if(culled.size != 0) {
			// Render shadows (if it is a single player engine state).
			if(state.getRenderable3Environment() instanceof Space) {
				Space space = ((SinglePlayerEngineState)state).getSpatialEnvironment();
				space.getShadowLight().begin(Vector3.Zero, state.getCamera().direction);
				space.getShadowBatch().begin(space.getShadowLight().getCamera());
				// Render shadows on static Renderables.
				space.getShadowBatch().render(batch3StaticCache);
				// Render shadows on dynamic Renderables.
				space.getShadowBatch().render(culled);
				space.getShadowBatch().end();
				space.getShadowLight().end();
			}
			clearGl(true);
			// Enable backface culling for Voxel rendering.
			Gdx.gl.glEnable(GL20.GL_CULL_FACE);
			Gdx.gl.glCullFace(GL20.GL_BACK);
			// Render 3D objects.
			batch3.begin(state.getCamera());
			// Render static Renderables.
			batch3.render(batch3StaticCache, state.getRenderable3Environment());
			// Render dynamic Renderables.
			for(ModelInstance prov : culled) 
				batch3.render(prov, state.getRenderable3Environment());
			batch3.end();
			// Disable backface culling because 3D rendering is over.
			Gdx.gl.glDisable(GL20.GL_CULL_FACE);
		} else {
			clearGl(true);
			// Render the Null Object. See notes about "createNullObject()" above to understand why.
			batch3.begin(state.getCamera());
			batch3.render(nullObjectRenderable, state.getRenderable3Environment());
			batch3.end();
		}
		// If physics debug mode is on, draw it.
		BulletWrapper physics = state.getObjectHandler().getPhysicsEngine();
		if(physics.debugMode) {
			DebugDrawer physics3Batch = physics.getDebugDrawer();
			physics3Batch.begin(state.getCamera());
		    physics.getDynamicsWorld().debugDrawWorld();
		    physics3Batch.end();
		}
		// Render 25D.
		if(batch25 != null) {
			Array<RenderableProvider25> providers25 = state.getProvider25();
			Array<Decal> culled25 = FrustumCulling.cull25(providers25, state.getCamera());
			for(Decal prov : culled25) 
				batch25.add(prov);
			batch25.flush();
		}
		// Render 2D.
		Array<RenderableProvider2> providers2 = state.getProvider2();
		batch2.begin();
		for(RenderableProvider2 prov : providers2) {
			batch2.setColor(prov.getTint());
			batch2.draw(prov.getBindedRaster(), prov.getX(), prov.getY(), prov.getWidth(), prov.getHeight());
		}
		batch2.end();
	}
	
	public void bakeStaticRenderable3(State state) {
		System.out.println("[Terrafort Engine] Baking static renderables.");
		batch3StaticCache.begin(state.getCamera());
		for(RenderableProvider3 statics : state.getProviderStatic3())
			batch3StaticCache.add(statics.getModelInstance());
		batch3StaticCache.end();
	}
	
	public void clearGui() {
		DI_GLFW.newFrame();
		ImGui.newFrame();
	}
	
	public void renderGui() {
		ImGui.render();
		DI_GL3.renderDrawData(ImGui.getDrawData());
	}
	
	public void setGuiScale(float scale) {
		ImGuiStyle style = ImGui.getStyle();
		style.scaleAllSizes(scale);
	}
	
	/**
	 * Getters and setters
	 */
	
	public static int getWidth() {
		return width;
	}
	
	public static int getHeight() {
		return height;
	}
	
	public Lwjgl3Graphics getGraphics() {
		return graphics;
	}
	
	public Lwjgl3Window getWindow() {
		return window;
	}
	
	public long getWindowHandle() {
		return getWindow().getWindowHandle();
	}
	
	public ModelBatch getBatch3() {
		return batch3;
	}
	
	public DecalBatch getBatch25() {
		return batch25;
	}
	
	public SpriteBatch getBatch2() {
		return batch2;
	}
	
	public void initBatch25() {
		State state = engine.getCurrentState();
		if(state == null) {
			System.out.println("[Terrafort Engine] You cannot initialze the Batch25 without a valid State running!");
			System.exit(-1);
		}
		batch25 = new DecalBatch(new CameraGroupStrategy(state.getCamera()));
	}
	
	public void disposeBatch25() {
		if(batch25 != null) {
			batch25.dispose();
			batch25 = null;
		}
	}

	/**
	 * Interfaces
	 */
	
	@Override
	public void onViewportResize(int newWidth, int newHeight) {
		batch2.getProjectionMatrix().setToOrtho2D(0, 0, newWidth, newHeight);
		if(engine.getCurrentState() != null) {
			Camera camera = engine.getCurrentState().getCamera();
			camera.viewportWidth = newWidth;
			camera.viewportHeight = newHeight;
			camera.update();
		}
		registerViewportDimensions(newWidth, newHeight);
	}
	
	@Override
	public void dispose() {
		// Dispose of batches.
		batch3.dispose();
		batch3StaticCache.dispose();
		if(batch25 != null)
			batch25.dispose();
		batch2.dispose();
		// Dispose of ImGui artifacts.
		DI_GLFW.dispose();
		DI_GL3.dispose();
		ImGui.destroyContext();
	}
	
}
