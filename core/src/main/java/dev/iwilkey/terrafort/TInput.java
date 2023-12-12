package dev.iwilkey.terrafort;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;

/**
 * Module that facilitates all methods of input into general, game-specific variables that are used globally.
 * @author Ian Wilkey (iwilkey)
 */
public final class TInput implements InputProcessor {
	
	public static boolean controller    = false;
	public static float   scroll        = 0.0f;
	public static float   cursorX       = 0.0f;
	public static float   cursorY       = 0.0f;
	public static boolean up            = false;
	public static boolean down          = false;
	public static boolean right         = false;
	public static boolean left          = false;
	public static boolean upArrow       = false;
	public static boolean downArrow     = false;
	public static boolean leftArrow     = false;
	public static boolean rightArrow    = false;
	public static boolean run           = false;
	public static boolean slide         = false;
	public static boolean attack        = false;
	public static boolean drop          = false;
	public static boolean zoomIn        = false;
	public static boolean zoomOut       = false;
	
	public static final class TController implements ControllerListener {
		
		public static final float DEAD_ZONE = 0.5f;

		@Override
		public void connected(Controller controller) {
		}

		@Override
		public void disconnected(Controller controller) {
		}

		@Override
		public boolean buttonDown(Controller controller, int buttonCode) {
			System.out.println("Button down: " + buttonCode);
			switch(buttonCode) {
				case 0: // A
					attack = true;
					break;
				case 2: // X
					slide = true;
					break;
				case 7: // LS
					run = true;
					break;
				case 9:  // LB
					zoomOut = true;
					break;
				case 10: // RB
					zoomIn = true;
					break;
				case 11: // DPAD UP
					upArrow = true;
					break;
				case 12: // DPAD DOWN
					downArrow = true;
					break;
				case 13: // DPAD LEFT
					leftArrow = true;
					break;
				case 14: // DPAD RIGHT
					rightArrow = true;
					break;
			}
			return false; 
		}

		@Override
		public boolean buttonUp(Controller controller, int buttonCode) {
			switch(buttonCode) {
				case 0:
					attack = false;
					break;
				case 2:
					slide = false;
					break;
				case 7:
					run = false;
					break;
				case 9:
					zoomOut = false;
					break;
				case 10:
					zoomIn = false;
					break;
				case 11:
					upArrow = false;
					break;
				case 12:
					downArrow = false;
					break;
				case 13:
					leftArrow = false;
					break;
				case 14:
					rightArrow = false;
					break;
			}
			return false;
		}

		@Override
		public boolean axisMoved(Controller controller, int axisCode, float value) {
	        if (axisCode == 0) {
	            left = value < -DEAD_ZONE;
	            right = value > DEAD_ZONE;
	        } else if (axisCode == 1) {
	            up = value < -DEAD_ZONE;
	            down = value > DEAD_ZONE;
	        }
			return false;
		}
		
	}
	
	public TInput() {
		if(controller)
			Controllers.addListener(new TController());
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if(controller)
			return true;
		switch(keycode) {
			case Keys.W:
				up = true;
				break;
			case Keys.S:
				down = true;
				break;
			case Keys.A:
				left = true;
				break;
			case Keys.D:
				right = true;
				break;
			case Keys.SHIFT_LEFT:
				run = true;
				break;
			case Keys.SPACE:
				slide = true;
				break;
			case Keys.Q:
				zoomOut = true;
				break;
			case Keys.E:
				zoomIn = true;
				break;
			case Keys.ENTER:
				attack = true;
				break;
			case Keys.UP:
				upArrow = true;
				break;
			case Keys.DOWN:
				downArrow = true;
				break;
			case Keys.LEFT:
				leftArrow = true;
				break;
			case Keys.RIGHT:
				rightArrow = true;
				break;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(controller)
			return true;
		switch(keycode) {
			case Keys.W:
				up = false;
				break;
			case Keys.S:
				down = false;
				break;
			case Keys.A:
				left = false;
				break;
			case Keys.D:
				right = false;
				break;
			case Keys.SHIFT_LEFT:
				run = false;
				break;
			case Keys.SPACE:
				slide = false;
				break;
			case Keys.Q:
				zoomOut = false;
				break;
			case Keys.E:
				zoomIn = false;
				break;
			case Keys.ENTER:
				attack = false;
				break;
			case Keys.UP:
				upArrow = false;
				break;
			case Keys.DOWN:
				downArrow = false;
				break;
			case Keys.LEFT:
				leftArrow = false;
				break;
			case Keys.RIGHT:
				rightArrow = false;
				break;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		if(controller)
			return true;
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(controller)
			return true;
		switch(button) {
			case Buttons.LEFT:
				attack = true;
				break;
			case Buttons.RIGHT:
				drop = true;
				break;
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(controller)
			return true;
		switch(button) {
			case Buttons.LEFT:
				attack = false;
				break;
			case Buttons.RIGHT:
				drop = false;
				break;
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(controller)
			return true;
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if(controller)
			return true;
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		if(controller)
			return true;
		scroll = amountY;
		return false;
	}
	
	public void tick() {
		scroll = 0;
		cursorX = Gdx.input.getX();
		cursorY = Gdx.input.getY();
	}

}
