package dev.iwilkey.terrafort.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

import dev.iwilkey.terrafort.gfx.Renderer;

import imgui.ImGui;

/**
 * The InputHandler class handles input events such as key presses, mouse movements, and touch events.
 * It provides methods to query the state of keys and cursors, as well as interact with ImGui.
 */
public final class InputHandler implements InputProcessor {
	
	public static final byte ACTIVATE = 1 << 2;
	public static final byte CURRENT = ACTIVATE >> 1;
	public static final byte DISABLE = ACTIVATE >> 2;
	public static final byte NONE = ACTIVATE >> 3;
	
	private static byte[] keyState;
	private static byte[] cursorState;
	private static Vector2 cursorPos;
	private static Vector2 scrollWheelPos;
	private static boolean catchedCursor = false;
	private static boolean acceptImGuiInteraction = false;
	
	/**
	 * Constructs a new InputHandler.
	 */
	public InputHandler() {
		keyState = new byte[0x100];
		cursorState = new byte[0x10];
		cursorPos = new Vector2();
		scrollWheelPos = new Vector2();
	}
	
	@Override
	public boolean keyDown(int keycode) {
		keyState[keycode] = ACTIVATE;
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		keyState[keycode] = DISABLE;
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		cursorState[button] = ACTIVATE;
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		cursorState[button] = DISABLE;
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		cursorPos.set(new Vector2(screenX, screenY));
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		scrollWheelPos.set(new Vector2(amountX, amountY));
		return false;
	}
	
	/**
	 * Checks if the specified key was just pressed down.
	 *
	 * @param keycode the key code to check
	 * @return true if the key was just pressed down, false otherwise
	 */
	public static boolean keyJustDown(int keycode) {
		return keyState[keycode] == ACTIVATE;
	}
	
	/**
	 * Checks if the specified key is currently being held down.
	 *
	 * @param keycode the key code to check
	 * @return true if the key is currently being held down, false otherwise
	 */
	public static boolean keyCurrent(int keycode) {
		return keyState[keycode] == CURRENT;
	}
	
	/**
	 * Checks if the specified key was just released.
	 *
	 * @param keycode the key code to check
	 * @return true if the key was just released, false otherwise
	 */
	public static boolean keyJustUp(int keycode) {
		return keyState[keycode] == DISABLE;
	}
	
	/**
	 * Checks if the specified cursor button was just pressed down.
	 *
	 * @param pointer the cursor button index to check
	 * @return true if the cursor button was just pressed down, false otherwise
	 */
	public static boolean cursorJustDown(int pointer) {
		return cursorState[pointer] == ACTIVATE;
	}

	/**
	 * Checks if the specified cursor button is currently being held down.
	 *
	 * @param pointer the cursor button index to check
	 * @return true if the cursor button is currently being held down, false otherwise
	 */
	public static boolean cursorCurrent(int pointer) {
		return cursorState[pointer] == CURRENT;
	}
	
	/**
	 * Checks if the specified cursor button was just released.
	 *
	 * @param pointer the cursor button index to check
	 * @return true if the cursor button was just released, false otherwise
	 */
	public static boolean cursorJustUp(int pointer) {
		return cursorState[pointer] == DISABLE;
	}
	
	/**
	 * Returns the position of the cursor.
	 *
	 * @return the cursor position as a Vector2
	 */
	public static Vector2 getCursorPosition() {
		return cursorPos;
	}

	/**
	 * Returns the position of the scroll wheel.
	 *
	 * @return the scroll wheel position as a Vector2
	 */
	public static Vector2 getScrollwheelPosition() {
		return scrollWheelPos;
	}
	
	/**
	 * Checks if ImGui wants to capture mouse or keyboard interactions.
	 *
	 * @return true if ImGui wants to capture interactions, false otherwise
	 */
	public static boolean guiWantsInteraction() {
		return ImGui.getIO().getWantCaptureMouse() || ImGui.getIO().getWantCaptureKeyboard();
	}
	
	/**
	 * Denies ImGui interaction.
	 */
	public static void denyImGuiInteraction() {
		acceptImGuiInteraction = false;
	}
	
	/**
	 * Allows ImGui interaction.
	 */
	public static void acceptImGuiInteraction() {
		acceptImGuiInteraction = true;
	}
	
	/**
	 * Enables cursor capture.
	 */
	public static void catchCursor() {
		catchedCursor = true;
	}
	
	/**
	 * Releases cursor capture.
	 */
	public static void releaseCursor() {
		catchedCursor = false;
	}
	
	/**
	 * Centers the cursor on the screen.
	 */
	public static void centerCursor() {
		Gdx.input.setCursorPosition(Renderer.getWidth() / 2, Renderer.getHeight() / 2);
	}
	
	/**
	 * Polls the input state and updates the internal state accordingly.
	 */
	public void poll() {
		ImGui.getIO().setWantCaptureMouse(acceptImGuiInteraction);
		ImGui.getIO().setWantCaptureKeyboard(acceptImGuiInteraction);
		Gdx.input.setCursorCatched(catchedCursor);
		for(int i = 0; i < 256; i++) {
			switch(keyState[i]) {
				case DISABLE:
					keyState[i] = NONE;
					break;
				case ACTIVATE:
					keyState[i] >>= 1;
					break;
			}
			if(i < 16) {
				switch(cursorState[i]) {
					case DISABLE:
						cursorState[i] = NONE;
						break;
					case ACTIVATE:
						cursorState[i] >>= 1;
						break;
				}
			}
		}
		scrollWheelPos = Vector2.Zero.cpy();
	}
}
