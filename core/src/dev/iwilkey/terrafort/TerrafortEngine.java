package dev.iwilkey.terrafort;

import org.lwjgl.glfw.GLFW;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

import dev.iwilkey.terrafort.gfx.Alignment;
import dev.iwilkey.terrafort.gfx.Anchor;
import dev.iwilkey.terrafort.gfx.Renderer;
import dev.iwilkey.terrafort.state.State;
import dev.iwilkey.terrafort.state.openworld.OpenWorld;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

public class TerrafortEngine extends ApplicationAdapter {
	
	private InputHandler input;
	private Renderer renderer;
	private State currentState;
	
	@Override
	public void create() {
		input = new InputHandler();
		Gdx.input.setInputProcessor(input);
		renderer = new Renderer();
		// Set the initial state.
		setState(new OpenWorld(this));
		GLFW.glfwShowWindow(renderer.getWindowHandle());
	}
	
	@Override
	public void render() {
		renderer.clearGl();
		// Return if null state.
		if(currentState == null)
			return;
		// Check to see if the currentState is done loading. If not, show progress.
		if(!currentState.load()) {
			System.out.println("[Terrafort Engine] Loading state...");
			renderLoading(false);
			return;
		} else if(!currentState.getAssetBuffer().finalized) {
			System.out.println("[Terrafort Engine] Finishing up...");
			renderLoading(true);
			// Finalize assets after loading.
			currentState.getAssetBuffer().finalizeMemory(currentState.getAssetManager());
			currentState.begin();
		}
		// Differentiate GUI input vs engine input
		if(ImGui.getIO().getWantCaptureMouse() || ImGui.getIO().getWantCaptureKeyboard()) {
			if(Gdx.input.getInputProcessor() != null)
				Gdx.input.setInputProcessor(null);
		} else {
			if(Gdx.input.getInputProcessor() == null)
				Gdx.input.setInputProcessor(input);
		}
		// Tick and render state.
		currentState.tick();
		renderer.render(currentState);
		// Render state GUI.
		renderer.clearGui();
		currentState.gui();
		renderer.renderGui();
		// Poll input events.
		input.poll();
	}
	
	@Override
	public void resize(int width, int height) {
		renderer.onViewportResize(width, height);
	}
	
	@Override
	public void pause() {}
	
	@Override
	public void resume() {}
	
	@Override
	public void dispose() {
		if(currentState != null)
			currentState.dispose();
		renderer.dispose();
	}
	
	public void setState(State state) {
		if(currentState != null) 
			currentState.end();
		currentState = state;
		if(currentState != null) 
			currentState.init();
	}
	
	private void renderLoading(boolean finalizing) {
		renderer.clearGui();
		ImGui.begin("Terrafort", ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.AlwaysAutoResize);
		if(!finalizing) {
			final float loadPercentage = currentState.getAssetManager().getProgress();
			ImGui.text("Loading " + loadPercentage + "%" + "|/-\\".charAt((int)(ImGui.getTime() / 0.05f) & 3));
		} else {
			ImGui.text("Finishing up...");
		}
		Alignment.alignGui(Anchor.BOTTOM_RIGHT, 5.0f);
		ImGui.end();
		renderer.renderGui();
	}
	
	public Renderer getRenderer() {
		return renderer;
	}
	
	public InputHandler getInputHandler() {
		return input;
	}
	
}
