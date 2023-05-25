package dev.iwilkey.terrafort;

import org.lwjgl.glfw.GLFW;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

import dev.iwilkey.terrafort.asset.TerrafortAssetHandler;
import dev.iwilkey.terrafort.gfx.Renderer;
import dev.iwilkey.terrafort.state.State;
import dev.iwilkey.terrafort.state.game.SinglePlayerEngineState;

public class TerrafortEngine extends ApplicationAdapter {
	
	private InputHandler input = null;
	private Renderer renderer = null;
	private TerrafortAssetHandler assets = null;
	private State currentState = null;
	
	@Override
	public void create() {
		// Set up renderer.
		renderer = new Renderer(this);
		// Load the Terrafort assets.
		assets = new TerrafortAssetHandler(this);
        assets.load();
		// Initialize the input.
		input = new InputHandler();
		Gdx.input.setInputProcessor(input);
		// Set the initial state.
        setState(new SinglePlayerEngineState(this));
		// Show the window, as all of the initial application construction has been completed.
		GLFW.glfwShowWindow(renderer.getWindowHandle());
	}
	
	@Override
	public void render() {
		// Tick and render state.
		currentState.update();
		// Poll for input (next frame).
		input.poll();
		// Render the state renderables.
		renderer.render(currentState);
		// Render state GUI.
		renderer.clearGui();
		currentState.gui();
		renderer.renderGui();
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
		if(currentState != null) {
			renderer.initBatch25();
			currentState.begin();
		}
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
