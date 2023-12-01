package dev.iwilkey.terrafort.input;

import com.badlogic.gdx.InputProcessor;

/**
 * @author Ian Wilkey (iwilkey)
 */
public final class TDefaultInput implements InputProcessor {
	
	private static float scroll = 0.0f;
	
	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		scroll = amountY;
		return false;
	}
	
	/**
	 * Get the current value of the scroll wheel (in the y direction).
	 */
	public static float getScroll() {
		return scroll;
	}
	
	public void tick() {
		scroll = 0;
	}

}
