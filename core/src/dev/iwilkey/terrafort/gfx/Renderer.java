package dev.iwilkey.terrafort.gfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import dev.iwilkey.terrafort.state.State;

import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

public class Renderer implements ViewportResizable, Disposable {
	
	private static final ImGuiImplGlfw DI_GLFW = new ImGuiImplGlfw();
	private static final ImGuiImplGl3 DI_GL3 = new ImGuiImplGl3();
	private final ModelBatch batch3 = new ModelBatch();
	private final SpriteBatch batch2 = new SpriteBatch();

	private int width;
	private int height;
	private final Lwjgl3Graphics graphics;
	private final Lwjgl3Window window;
	
	public Renderer() {
		graphics = (Lwjgl3Graphics)Gdx.graphics;
		window = graphics.getWindow();
		registerViewportDimensions(graphics.getWidth(), graphics.getHeight());
		// Init GUI
		ImGui.createContext();
		DI_GLFW.init(window.getWindowHandle(), true);
		DI_GL3.init("#version 120");
	}
	
	private void registerViewportDimensions(int w, int h) {
		HdpiUtils.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		width = w;
		height = h;
	}
	
	/**
	 * Rendering utilities
	 */
	
	public void clearGl() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	}
	
	/**
	 * Render
	 */
	public void render(State state) {
		// Render 3D.
		// Grab the model cache of all RenderableProvider3s, culled away.
		ModelCache culled = FrustumCulling.cull(state.getProvider3(), state.getCamera());
		batch3.begin(state.getCamera());
		batch3.render(culled, state.getRenderable3Environment());
		batch3.end();
		culled.dispose();
		
		// Render 25D. TODO: Implement.
		
		// Render 2D.
		Array<RenderableProvider2> providers = state.getProvider2();
		batch2.begin();
		for(RenderableProvider2 prov : providers) {
			batch2.setColor(prov.getTint());
			batch2.draw(prov.getBindedRaster(), prov.getX(), prov.getY(), prov.getWidth(), prov.getHeight());
		}
		batch2.end();
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
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
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

	/**
	 * Interfaces
	 */
	
	@Override
	public void onViewportResize(int newWidth, int newHeight) {
		registerViewportDimensions(newWidth, newHeight);
	}
	
	@Override
	public void dispose() {
		// Dispose of batches.
		batch3.dispose();
		// batch25.dispose();
		batch2.dispose();
		
		// Dispose of ImGui artifacts.
		DI_GLFW.dispose();
		DI_GL3.dispose();
		ImGui.destroyContext();
	}
	
}
