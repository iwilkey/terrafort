package dev.iwilkey.terrafort.gfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import dev.iwilkey.terrafort.TerrafortEngine;
import dev.iwilkey.terrafort.state.State;
import imgui.ImGui;
import imgui.ImGuiStyle;

/**
 * The `Renderer` class is responsible for rendering the game world and user interface.
 * It manages various GL settings, model batches, and provides rendering utilities.
 * It implements the `ViewportResizable` and `Disposable` interfaces.
 */
public final class Renderer implements ViewportResizable, Disposable {
	
	// Buffer size for static renderables.
	public static final int STATIC_RENDERABLE_BUFFER_SIZE = 32;
    // RT Shadow Shader GFX settings.
    public static final int SHADOW_MAP_WIDTH = (int) Math.pow(2, 14);
    public static final int SHADOW_MAP_HEIGHT = SHADOW_MAP_WIDTH;
    public static final float SHADOW_VIEWPORT_WIDTH = 40f;
    public static final float SHADOW_VIEWPORT_HEIGHT = SHADOW_VIEWPORT_WIDTH;
    public static final float SHADOW_NEAR = 0.0f;
    public static final float SHADOW_FAR = (int) Math.pow(2, 12);
    // Direct reference to the engine.
    private final TerrafortEngine engine;
    // Terrafort Engine GL Context.
    private final GLContext glContext;
    // GLFW and GL runtime properties.
    private final Lwjgl3Graphics graphics;
    private final Lwjgl3Window window;
    // Renderer dimensions.
    private static int width;
    private static int height;
    
    /**
     * Constructs a `Renderer` object with the specified engine.
     *
     * @param engine The TerrafortEngine instance.
     */
    public Renderer(TerrafortEngine engine) {
        this.engine = engine;
        // Grab GL context from libGDX GLFW abstraction.
        graphics = (Lwjgl3Graphics) Gdx.graphics;
        window = graphics.getWindow();
        glContext = new GLContext(this);
        // Register initial display dimensions.
        registerViewportDimensions(graphics.getWidth(), graphics.getHeight());
        // Create the Null Object. See notes on "createNullObject()" method to understand why.
        createNullObject();
    }

    private Model nullObject;
    private ModelInstance nullObjectRenderable;

    /**
     * Creates a null object used for rendering when everything else has been culled away.
     * This is a Null Object pattern and a solution to the libGDX graphics pipeline expecting at least something to render.
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
     * Clears the OpenGL buffers.
     *
     * @param depth Specifies if the depth buffer should be cleared as well.
     */
    public void clearGl(boolean depth) {
        Gdx.gl.glClearColor(0.14f, 0.13f, 0.13f, 1.0f);
        int clsbit = GL20.GL_COLOR_BUFFER_BIT;
        if (depth) clsbit |= GL20.GL_DEPTH_BUFFER_BIT;
        Gdx.gl.glClear(clsbit);
    }

    /**
     * Renders the game world and user interface.
     *
     * @param state The current state of the game.
     */
    public void render(final State state) {
        // Bake the static cache. Note this will only bake caches that have been dirtied.
        state.getStaticRenderableProviderCacheSystem().bake();
        // Frustum culling for 3D.
        final Array<ModelInstance> culled3 = FrustumCulling.cull3(state.getProvider3(), state.getCamera());
        glContext.renderRTShadows(state, culled3);
        glContext.clsgl(true);
        glContext.enableBackFaceCulling();
        glContext.renderRenderables3(state, culled3);
        glContext.disableBackFaceCulling();
        glContext.renderPhysicsDebugRenderables3(state);
        glContext.renderRenderables25(state);
        glContext.renderRenderables2(state);
        glContext.clsgui();
        state.gui();
        glContext.rendergui();
    }

    /**
     * Sets the scale of the ImGui user interface.
     *
     * @param scale The scale value.
     */
    public void setGuiScale(float scale) {
        ImGuiStyle style = ImGui.getStyle();
        style.scaleAllSizes(scale);
    }

    /**
     * Returns the width of the viewport.
     *
     * @return The width of the viewport.
     */
    public static int getWidth() {
        return width;
    }

    /**
     * Returns the height of the viewport.
     *
     * @return The height of the viewport.
     */
    public static int getHeight() {
        return height;
    }

    /**
     * Returns the graphics object.
     *
     * @return The graphics object.
     */
    public Lwjgl3Graphics getGraphics() {
        return graphics;
    }

    /**
     * Returns the window object.
     *
     * @return The window object.
     */
    public Lwjgl3Window getWindow() {
        return window;
    }
    
    /**
     * Return the TerrafortEngine instance.
     * 
     * @return The TerrafortEngine instance.
     */
    public TerrafortEngine getEngine() {
    	return engine;
    }

    /**
     * Returns the handle of the window.
     *
     * @return The handle of the window.
     */
    public long getWindowHandle() {
        return getWindow().getWindowHandle();
    }

    /**
     * Returns the Terrafort GL context object.
     *
     * @return The Terrafort GL context object.
     */
    public GLContext getGLContext() {
    	return glContext;
    }
    
    /**
     * Return the Null Renderable.
     * 
     * @return the Null Renderable.
     */
    public ModelInstance getNullRenderable() {
    	return nullObjectRenderable;
    }

    /**
     * Resizes the viewport and updates the camera projection matrix.
     *
     * @param newWidth  The new width of the viewport.
     * @param newHeight The new height of the viewport.
     */
    @Override
    public void onViewportResize(int newWidth, int newHeight) {
        glContext.onViewportResize(newWidth, newHeight);
        if (engine.getCurrentState() != null) {
            Camera camera = engine.getCurrentState().getCamera();
            camera.viewportWidth = newWidth;
            camera.viewportHeight = newHeight;
            camera.update();
        }
        registerViewportDimensions(newWidth, newHeight);
    }

    /**
     * Disposes the resources used by the renderer.
     */
    @Override
    public void dispose() {
        glContext.dispose();
        nullObject.dispose();
    }
}

