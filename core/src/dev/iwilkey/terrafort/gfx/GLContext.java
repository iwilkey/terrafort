package dev.iwilkey.terrafort.gfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import dev.iwilkey.terrafort.Terrafort;
import dev.iwilkey.terrafort.physics.TerrafortPhysicsCore;
import dev.iwilkey.terrafort.state.State;

import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

/**
 * An object holding all information pertaining to the GL batches, processes, lighting, and shadows.
 * @author iwilkey
 */
public final class GLContext implements Disposable, ViewportResizable {
	
	public static final int RED_BITS = 1 << 3;
    public static final int GREEN_BITS = 1 << 3;
    public static final int BLUE_BITS = 1 << 3;
    public static final int ALPHA_BITS = 1 << 3;
    public static final int DEPTH_BITS = (1 << 5) | (1 << 2);
    public static final int STENCIL_BITS = 0;
    // TODO: Perhaps allow this to be configured by the user since it has a direct impact on overall graphical quality.
    public static final int MSAA_SAMPLES = 4;
    public static final int GLOBAL_GL_LINE_WIDTH = 1;
	public static final boolean DEPTH_TEST = true;
	public static final int DEPTH_FUNCTION = GL20.GL_LEQUAL;
	public static final String GLSL_VERSION = "#version 120";
	private static final Color GL_CLEAR_COLOR = new Color(0.14f, 0.13f, 0.13f, 1.0f);
    private static final ImGuiImplGlfw DI_GLFW = new ImGuiImplGlfw();
    private static final ImGuiImplGl3 DI_GL3 = new ImGuiImplGl3();
    private static final ModelBatch BATCH_3D = new ModelBatch();
    private static final ModelBatch BATCH_SHADOW_SHADER = new ModelBatch(new DepthShaderProvider());
    private static final SpriteBatch BATCH_2D = new SpriteBatch();
    // Can only be initialized when a valid state is set.
    private static DecalBatch BATCH_25 = null;
    
    private final Renderer renderer;
    
    public GLContext(final Renderer renderer) {
    	this.renderer = renderer;
    	// GL Settings.
    	if (DEPTH_TEST)
    		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthFunc(DEPTH_FUNCTION);
        Gdx.gl.glLineWidth(GLOBAL_GL_LINE_WIDTH);
        // Create ImGUI Context.
        ImGui.createContext();
        ImGui.getIO().setIniFilename(null);
        ImGui.getIO().setWantCaptureKeyboard(false);
        ImGui.getIO().setWantCaptureMouse(false);
        DI_GLFW.init(this.renderer.getWindowHandle(), true);
        DI_GL3.init(GLSL_VERSION);
    }
    
    /**
     * Render real-time shadows.
     * 
     * @param culled the culled 3D objects active in the scene.
     * @param env the 3D Renderable environment.
     */
    public void renderRTShadows(final State state, final Array<ModelInstance> culled) {
    	// Begin rendering the scene through the perspective of the light.
    	state.getRenderable3Environment().getShadowLight().begin(Vector3.Zero, state.getCamera().direction);
    	BATCH_SHADOW_SHADER.begin(state.getRenderable3Environment().getShadowLight().getCamera());
    	// Factor in static Renderables providers.
    	state.getStaticRenderableProviderCacheSystem().renderThroughShadowShader(BATCH_SHADOW_SHADER);
    	// Factor in culled dynamic Renderables.
    	if (culled.size != 0)
    		BATCH_SHADOW_SHADER.render(culled);
    	// Flush.
    	BATCH_SHADOW_SHADER.end();
    	state.getRenderable3Environment().getShadowLight().end();
    }
    
    /**
     * Render the active 3D Renderables, static or dynamic.
     * 
     * @param culled the culled dynamic Renderables.
     */
    public void renderRenderables3(final State state, final Array<ModelInstance> culled) {
    	// Begin GL 3D render batch
    	BATCH_3D.begin(state.getCamera());
    	// Render static Renderables.
    	state.getStaticRenderableProviderCacheSystem().render(BATCH_3D, state.getRenderable3Environment());
    	// Render dynamic Renderables, if not culled away. Otherwise, render the null Renderable.
    	if (culled.size != 0) {
            for (final ModelInstance prov : culled)
            	BATCH_3D.render(prov, state.getRenderable3Environment());
        } else {
        	BATCH_3D.render(renderer.getNullRenderable(), state.getRenderable3Environment());
        }
    	// Flush.
    	BATCH_3D.end();
    }
    
    /**
     * Renders the debug graphics of the Terrafort physics engine.
     * 
     * @param state the current state.
     */
    public void renderPhysicsDebugRenderables3(final State state) {
    	// If physics debug mode is on, draw it.
        final TerrafortPhysicsCore physics = state.getObjectHandler().getPhysicsEngine();
        if (physics.debugMode) {
            final DebugDrawer physics3Batch = physics.getDebugDrawer();
            physics3Batch.begin(state.getCamera());
            physics.getDynamicsWorld().debugDrawWorld();
            physics3Batch.end();
        }
    }
    
    /**
     * Renders the 2.5D Renderables.
     * 
     * @param state the current state.
     */
    public void renderRenderables25(final State state) {
    	if(BATCH_25 != null) {
    		final Array<RenderableProvider25> providers25 = state.getProvider25();
    		// Cull the 2.5D Renderables.
            final Array<Decal> culled25 = FrustumCulling.cull25(providers25, state.getCamera());
            // Render them.
            for (Decal prov : culled25)
                BATCH_25.add(prov);
            BATCH_25.flush();
    	}
    }
    
    /**
     * Render the 2D Renderables.
     * 
     * @param state the current state.
     */
    public void renderRenderables2(final State state) {
    	final Array<RenderableProvider2> providers2 = state.getProvider2();
        BATCH_2D.begin();
        for (final RenderableProvider2 prov : providers2) {
        	BATCH_2D.setColor(prov.getTint());
        	BATCH_2D.draw(prov.getBindedRaster(), prov.getX(), prov.getY(), prov.getWidth(), prov.getHeight());
        }
        BATCH_2D.end();
    }
    
    /**
     * Clear the color or depth buffers of GL.
     * 
     * @param depth whether or not to clear the depth buffer.
     */
    public void clsgl(boolean depth) {
    	Gdx.gl.glClearColor(GL_CLEAR_COLOR.r, GL_CLEAR_COLOR.g, GL_CLEAR_COLOR.b, GL_CLEAR_COLOR.a);
        int clsbit = GL20.GL_COLOR_BUFFER_BIT;
        if (depth) clsbit |= GL20.GL_DEPTH_BUFFER_BIT;
        Gdx.gl.glClear(clsbit);
    }
    
    /**
     * Enable GL back face culling.
     */
    public void enableBackFaceCulling() {
    	Gdx.gl.glEnable(GL20.GL_CULL_FACE);
        Gdx.gl.glCullFace(GL20.GL_BACK);
    }
    
    /**
     * Disable GL back face culling.
     */
    public void disableBackFaceCulling() {
    	Gdx.gl.glDisable(GL20.GL_CULL_FACE);
    }
    
    /**
     * Clear the ImGui GL color buffer.
     */
    public void clsgui() {
    	DI_GLFW.newFrame();
        ImGui.newFrame();
    }
    
    /**
     * Render the ImGui state to the screen.
     */
    public void rendergui() {
    	ImGui.render();
        DI_GL3.renderDrawData(ImGui.getDrawData());
    }
    
    /**
     * Initializes the Decal batch for 2.5D rendering.
     * This method should only be called when a valid state is set.
     */
    public void initBatch25() {
        State state = renderer.getEngine().getCurrentState();
        if (state == null)
            Terrafort.fatal("You cannot initialize the BATCH_25 without a valid State running!");
        if (BATCH_25 != null) {
        	BATCH_25.dispose();
        	BATCH_25 = null;
        }
        BATCH_25 = new DecalBatch(new CameraGroupStrategy(state.getCamera()));
    }
    
    /**
     * Set the color that the screen is cleared to every frame.
     * @param r the RED value [0, 1].
     * @param g the GREEN value [0, 1].
     * @param b the BLUE value [0, 1].
     * @param a the ALPHA value [0, 1].
     */
    public void setClearColor(float r, float g, float b, float a) {
    	GL_CLEAR_COLOR.set(r, g, b, a);
    }
   
    @Override
	public void onViewportResize(int newWidth, int newHeight) {
    	BATCH_2D.getProjectionMatrix().setToOrtho2D(0, 0, newWidth, newHeight);
	}
    
	@Override
	public void dispose() {
		BATCH_3D.dispose();
		BATCH_SHADOW_SHADER.dispose();
        if (BATCH_25 != null)
        	BATCH_25.dispose();
        BATCH_2D.dispose();
        DI_GLFW.dispose();
        DI_GL3.dispose();
        ImGui.destroyContext();
	}
    
}
