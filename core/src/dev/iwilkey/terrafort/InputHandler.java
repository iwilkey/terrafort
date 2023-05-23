package dev.iwilkey.terrafort;

import java.util.HashMap;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

import dev.iwilkey.terrafort.gfx.Renderer;
import imgui.ImGui;

public final class InputHandler implements InputProcessor {
	
	public static final class KeyBinding {

		private static HashMap<String, Integer> bindings;
		
		public KeyBinding() {
			bindings = new HashMap<>();
		}
		
		public static void setBinding(String name, int key) {
			bindings.put(name, key);
		}
		
		public static int getBinding(String binding) {
			return bindings.get(binding);
		}
		
	}
	
	public static final byte ACTIVATE = 1 << 2;
	public static final byte CURRENT = ACTIVATE >> 1;
	public static final byte DISABLE = ACTIVATE >> 2;
	public static final byte NONE = ACTIVATE >> 3;
	private static byte[] keyState;
	private static byte[] cursorState;
	private static Vector2 cursorPos;
	private static Vector2 scrollWheelPos;
	private static boolean catchedCursor = true;
	private static boolean acceptImGuiInteraction = false;
	
	public InputHandler() {
		keyState = new byte[0x100];
		cursorState = new byte[0x10];
		cursorPos = new Vector2();
		scrollWheelPos = new Vector2();
		setBindings();
	}
	
	public void setBindings() {
		new KeyBinding();
		KeyBinding.setBinding("Move Foward", Keys.W);
		KeyBinding.setBinding("Move Backward", Keys.S);
		KeyBinding.setBinding("Strafe Right", Keys.D);
		KeyBinding.setBinding("Strafe Left", Keys.A);
		KeyBinding.setBinding("Ascend", Keys.SPACE);
		KeyBinding.setBinding("Descend", Keys.SHIFT_LEFT);
		KeyBinding.setBinding("Focus / Unfocus", Keys.ESCAPE);
		KeyBinding.setBinding("Action", Buttons.LEFT);
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
	
	public static boolean keyJustDown(int keycode) {
		return keyState[keycode] == ACTIVATE;
	}
	
	public static boolean keyCurrent(int keycode) {
		return keyState[keycode] == CURRENT;
	}
	
	public static boolean keyJustUp(int keycode) {
		return keyState[keycode] == DISABLE;
	}
	
	public static boolean cursorJustDown(int pointer) {
		return cursorState[pointer] == ACTIVATE;
	}

	public static boolean cursorCurrent(int pointer) {
		return cursorState[pointer] == CURRENT;
	}
	
	public static boolean cursorJustUp(int pointer) {
		return cursorState[pointer] == DISABLE;
	}
	
	public static Vector2 getCursorPosition() {
		return cursorPos;
	}

	public static Vector2 getScrollwheelPosition() {
		return scrollWheelPos;
	}
	
	public static boolean guiWantsInteraction() {
		return ImGui.getIO().getWantCaptureMouse() || ImGui.getIO().getWantCaptureKeyboard();
	}
	
	public static void denyImGuiInteraction() {
		acceptImGuiInteraction = false;
	}
	
	public static void acceptImGuiInteraction() {
		acceptImGuiInteraction = true;
	}
	
	public static void catchCursor() {
		catchedCursor = true;
	}
	
	public static void releaseCursor() {
		catchedCursor = false;
	}
	
	public static void centerCursor() {
		Gdx.input.setCursorPosition(Renderer.getWidth() / 2, Renderer.getHeight() / 2);
	}
	
	public void poll() {
		// Enforce catching and GUI interaction.
		ImGui.getIO().setWantCaptureMouse(acceptImGuiInteraction);
		ImGui.getIO().setWantCaptureKeyboard(acceptImGuiInteraction);
		Gdx.input.setCursorCatched(catchedCursor);
		// Update the states of input data structures.
		for(int i = 0; i < 256; i++) {
			// Handle keys.
			switch(keyState[i]) {
				case DISABLE:
					keyState[i] = NONE;
					break;
				case ACTIVATE:
					keyState[i] >>= 1;
					break;
			}
			// Handle cursor
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
		// Reset scrollwheel
		scrollWheelPos = Vector2.Zero;
	}
}
