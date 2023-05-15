package dev.iwilkey.terrafort;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

public class InputHandler implements InputProcessor {
	
	public static final byte ACTIVATE = 1 << 2;
	public static final byte CURRENT = ACTIVATE >> 1;
	public static final byte DISABLE = ACTIVATE >> 2;
	public static final byte NONE = ACTIVATE >> 3;
	private static byte[] keyState;
	private static byte[] cursorState;
	private static Vector2 cursorPos;
	private static Vector2 scrollWheelPos;
	
	public InputHandler() {
		keyState = new byte[0x100];
		cursorState = new byte[0x10];
		cursorPos = new Vector2();
		scrollWheelPos = new Vector2();
	}
	

	@Override
	public boolean keyDown(int keycode) {
		keyState[keycode] = ACTIVATE;
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		keyState[keycode] = DISABLE;
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		cursorState[button] = ACTIVATE;
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		cursorState[button] = DISABLE;
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		cursorPos.set(new Vector2(screenX, screenY));
		return true;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		scrollWheelPos.set(new Vector2(amountX, amountY));
		return true;
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
	
	public void poll() {
		// Update the states of input data structures
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
