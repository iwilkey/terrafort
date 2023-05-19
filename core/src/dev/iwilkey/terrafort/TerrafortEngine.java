package dev.iwilkey.terrafort;

import org.lwjgl.glfw.GLFW;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

import dev.iwilkey.terrafort.asset.TerrafortAssetHandler;
import dev.iwilkey.terrafort.gfx.Alignment;
import dev.iwilkey.terrafort.gfx.Anchor;
import dev.iwilkey.terrafort.gfx.Renderer;
import dev.iwilkey.terrafort.state.State;
import dev.iwilkey.terrafort.state.game.SinglePlayerEngineState;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

public class TerrafortEngine extends ApplicationAdapter {
	
	private InputHandler input;
	private Renderer renderer;
	private Thread loadingThread;
	private TerrafortAssetHandler assets;
	private State currentState;
	
	@Override
	public void create() {
		// Set up renderer and input.
		renderer = new Renderer(this);
		input = new InputHandler();
		Gdx.input.setInputProcessor(input);
		// Create asset manager and begin loading thread.
		assets = new TerrafortAssetHandler(this);
		loadingThread = new Thread(assets);
		loadingThread.run();
		// Set the initial state.
		setState(new SinglePlayerEngineState(this));
		// Show the window, as initialization is done.
		GLFW.glfwShowWindow(renderer.getWindowHandle());
	}
	
	@Override
	public void render() {
		renderer.clearGl(false);
		if(!assets.isFinished()) {
			renderLoading();
			return;
		} else {
			if(loadingThread != null) {
				try {
					loadingThread.join();
				} catch (InterruptedException e) {
					System.out.println("[Terrafort Engine] A InterruptedException has occurred when waiting for the loading thread to finish!");
					e.printStackTrace();
					System.exit(-1);
				}
				loadingThread = null;
				// The current state is ready to begin.
				if(currentState == null) {
					System.out.println("[Terrafort Engine] The engine must have a valid State to run!");
					System.exit(-1);
				}
				currentState.begin();
				renderer.initBatch25();
			}
		}
		
		// Differentiate GUI input vs engine input.
		if(InputHandler.guiWantsInteraction()) {
			if(Gdx.input.getInputProcessor() != null)
				Gdx.input.setInputProcessor(null);
		} else {
			if(Gdx.input.getInputProcessor() == null)
				Gdx.input.setInputProcessor(input);
		}
		
		// Tick and render state.
		currentState.update();
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
		if(currentState != null)
			currentState.onViewportResize(width, height);
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
		assets.dispose();
	}
	
	public void setState(State state) {
		renderer.disposeBatch25();
		if(currentState != null) {
			currentState.end();
			currentState.dispose();
		}
		currentState = state;
		if(currentState != null) 
			currentState.init();
	}
	
	private void renderLoading() {
		renderer.clearGui();
		ImGui.begin("Terrafort", ImGuiWindowFlags.NoCollapse 
				| ImGuiWindowFlags.NoResize 
				| ImGuiWindowFlags.NoMove 
				| ImGuiWindowFlags.AlwaysAutoResize);
		final float loadPercentage = assets.getPercentageDone();
		ImGui.text("Loading " + loadPercentage + "%" + "|/-\\".charAt((int)(ImGui.getTime() / 0.05f) & 3));
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
	
	public State getCurrentState() {
		return currentState;
	}
	
}
