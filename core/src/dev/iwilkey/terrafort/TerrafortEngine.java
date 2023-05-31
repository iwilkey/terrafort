package dev.iwilkey.terrafort;

import org.lwjgl.glfw.GLFW;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

import dev.iwilkey.terrafort.asset.TerrafortAssetHandler;
import dev.iwilkey.terrafort.game.SinglePlayer;
import dev.iwilkey.terrafort.gfx.Renderer;
import dev.iwilkey.terrafort.input.InputHandler;
import dev.iwilkey.terrafort.state.State;

/**
 * The TerrafortEngine class provides a more friendly and specific environment for the Terrafort game.
 * It abstracts libGDX utilities and serves as the main engine for the game.
 * It extends the ApplicationAdapter class and handles various callbacks.
 * It manages input, rendering, asset loading, and game state transitions.
 *
 * @author iwilkey
 */
public final class TerrafortEngine extends ApplicationAdapter {

    private InputHandler input = null;
    private Renderer renderer = null;
    private TerrafortAssetHandler assets = null;
    private State currentState = null;

    /**
     * Called when the application is first created.
     * Sets up the renderer, loads assets, initializes input, and shows the window.
     */
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
        // Set the entry state.
        setState(new SinglePlayer(this));
        // Show the window, as all of the initial application construction has been completed.
        GLFW.glfwShowWindow(renderer.getWindowHandle());
    }

    /**
     * Called when the game screen should be rendered.
     * Updates the current state, polls for input, and renders the game.
     */
    @Override
    public void render() {
        // Update state logic.
        currentState.update();
        // Poll for input (next frame).
        input.poll();
        // Render state renderables and GUI.
        renderer.render(currentState);
    }

    /**
     * Called when the game window is resized.
     *
     * @param width  the new window width
     * @param height the new window height
     */
    @Override
    public void resize(int width, int height) {
        renderer.onViewportResize(width, height);
        if (currentState != null)
            currentState.onViewportResize(width, height);
    }

    /**
     * Called when the application is about to be disposed.
     * Disposes of the current state, renderer, and assets.
     */
    @Override
    public void dispose() {
        if (currentState != null)
            currentState.dispose();
        renderer.dispose();
        assets.dispose();
    }

    /**
     * Sets the current game state.
     * Disposes the current state, if exists, and initializes the new state.
     *
     * @param state the new game state to set
     */
    public void setState(State state) {
        if (currentState != null) {
            currentState.end();
            currentState.dispose();
        }
        currentState = state;
        if (currentState != null) {
            renderer.getGLContext().initBatch25();;
            currentState.begin();
        }
    }

    /**
     * Retrieves the renderer instance used by the engine.
     *
     * @return the renderer instance
     */
    public Renderer getRenderer() {
        return renderer;
    }

    /**
     * Retrieves the input handler instance used by the engine.
     *
     * @return the input handler instance
     */
    public InputHandler getInputHandler() {
        return input;
    }

    /**
     * Retrieves the current game state.
     *
     * @return the current game state
     */
    public State getCurrentState() {
        return currentState;
    }
}