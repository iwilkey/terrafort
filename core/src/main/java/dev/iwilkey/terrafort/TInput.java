package dev.iwilkey.terrafort;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;

import dev.iwilkey.terrafort.gfx.TRenderableSprite;
import dev.iwilkey.terrafort.gui.TUserInterface;
import dev.iwilkey.terrafort.math.TMath;

/**
 * Module that facilitates all methods of input into general, game-specific variables that are used globally.
 * @author Ian Wilkey (iwilkey)
 */
public final class TInput implements InputProcessor {
	
	/**
	 * Graphical representation of the in-game cursor.
	 * @author Ian Wilkey (iwilkey)
	 */
	public static final class TCursor implements TRenderableSprite {
		@Override
		public float getX() { return TInput.cursorX; }
		@Override
		public float getY() { return Gdx.graphics.getHeight() - TInput.cursorY; }
		@Override
		public float getWidth() { return 32; }
		@Override
		public float getHeight() { return 32; }
		@Override
		public float getRotationInRadians() { return 0; }
		@Override
		public int getDataSelectionOffsetX() { return 0; }
		@Override
		public int getDataSelectionOffsetY() { return 0; }
		@Override
		public int getDataSelectionSquareWidth() { return 1; }
		@Override
		public int getDataSelectionSquareHeight() { return 1; }
		@Override
		public Color getRenderTint() { return Color.WHITE; }
		@Override
		public int getDepth() { return 0; }
		@Override
		public String getSpriteSheet() { return "sheets/items-icons.png"; }
		// cursor is rendered in screen-space...
		@Override
		public boolean useWorldProjectionMatrix() { return false; }
		// the cursor is clamped in screen-space, therefore it will always be in view...
		@Override
		public boolean shouldCull(final OrthographicCamera cam) { return false; }
		@Override
		public float getGraphicalX() { return getX(); }
		@Override
		public float getGraphicalY() { return getY(); }
	}
	
	/**
	 * The speed at which the cursor moves when controlled by a controller (Controller only!)
	 */
	public static float cursorSpeed = 8.0f;
	
	/**
	 * The scrollwheel value (Mouse only!)
	 */
	public static float scroll = 0.0f;
	
	/**
	 * The global x position of the cursor on the screen.
	 */
	public static float cursorX = 0.0f;
	
	/**
	 * The global y position of the cursor on the screen.
	 */
	public static float cursorY = 0.0f;
	
	/**
	 * Whether or not the Terrafort application is currently focused (Desktop not escaped.)
	 */
	public static boolean focused = false;
	
	/**
	 * Whether or not a controller user is trying to move the cursor left (Controller only!)
	 */
	public static boolean cursorLeft = false;
	
	/**
	 * Whether or not a controller user is trying to move the cursor right (Controller only!)
	 */
	public static boolean cursorRight = false;
	
	/**
	 * Whether or not a controller user is trying to move the cursor up (Controller only!)
	 */
	public static boolean cursorUp = false;
	
	/**
	 * Whether or not a controller user is trying to move the cursor down (Controller only!)
	 */
	public static boolean cursorDown = false;
	
	/**
	 * Whether or not the user is trying to move the player up.
	 */
	public static boolean moveUp = false;
	
	/**
	 * Whether or not the user is trying to move the player down.
	 */
	public static boolean moveDown = false;
	
	/**
	 * Whether or not the user is trying to move the player right.
	 */
	public static boolean moveRight = false;
	
	/**
	 * Whether or not the user is trying to move the player left.
	 */
	public static boolean moveLeft = false;
	
	/**
	 * Whether or not the user is trying to move to the Knowledge slot directly to the right of the current.
	 */
	public static boolean slotRight = false;
	
	/**
	 * Whether or not the user is trying to move to the Knowledge slot directly to the left of the current.
	 */
	public static boolean slotLeft = false;
	
	/**
	 * Whether or not the user is requesting the tech tree.
	 */
	public static boolean techTree = false;
	
	/**
	 * Whether or not the user is trying to perform a run request.
	 */
	public static boolean run = false;
	
	/**
	 * Whether or not the user is trying to perform a slide action.
	 */
	public static boolean slide = false;
	
	/**
	 * Whether or not the user is trying to perform an attack action.
	 */
	public static boolean interact = false;
	
	/**
	 * Whether or not the user is trying to perform a zoom in action.
	 */
	public static boolean zoomIn = false;
	
	/**
	 * Whether or not the user is trying to perform a zoom out action.
	 */
	public static boolean zoomOut = false;
	
	/**
	 * Terrafort controller support.
	 * @author Ian Wilkey (iwilkey)
	 */
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
			if(!focused)
				return true;
			System.out.println("Button down: " + buttonCode);
			switch(buttonCode) {
				case 0: // A
					if(TUserInterface.guiFocused())
						TUserInterface.getIO().touchDown((int)cursorX, (int)cursorY, 0, 0);
					else interact = true;
					break;
				case 2: // X
					slide = true;
					break;
				case 6: // start button
					techTree = true;
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
			}
			return false; 
		}

		@Override
		public boolean buttonUp(Controller controller, int buttonCode) {
			if(!focused)
				return true;
			switch(buttonCode) {
				case 0:
					TUserInterface.getIO().touchUp((int)cursorX, (int)cursorY, 0, 0);
					interact = false;
					break;
				case 2:
					slide = false;
					break;
				case 6: // start button
					techTree = false;
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
			}
			return false;
		}

		@Override
		public boolean axisMoved(Controller controller, int axisCode, float value) {
			if(!focused)
				return true;
	        if (axisCode == 0) {
	            moveLeft = value < -DEAD_ZONE;
	            moveRight = value > DEAD_ZONE;
	        } else if (axisCode == 1) {
	            moveUp = value < -DEAD_ZONE;
	            moveDown = value > DEAD_ZONE;
	        }
	        if(axisCode == 2) {
	        	cursorLeft = value < -DEAD_ZONE;
	            cursorRight = value > DEAD_ZONE;
	        } else if(axisCode == 3) {
	        	cursorUp = value < -DEAD_ZONE;
	            cursorDown = value > DEAD_ZONE;
	        }
			return false;
		}
	}
	
	public TInput() {
		Controllers.addListener(new TController());
		Gdx.input.setCursorCatched(true);
		focused = true;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if(!focused)
			return true;
		if(keycode == Keys.ESCAPE) {
			Gdx.input.setCursorCatched(false);
			focused = false;
			return false;
		}
		if(TUserInterface.guiFocused())
			TUserInterface.getIO().keyDown(keycode);
		switch(keycode) {
			case Keys.W:
				moveUp = true;
				break;
			case Keys.S:
				moveDown = true;
				break;
			case Keys.A:
				moveLeft = true;
				break;
			case Keys.D:
				moveRight = true;
				break;
			case Keys.SPACE:
				techTree = true;
				break;
			case Keys.SHIFT_LEFT:
				run = true;
				break;
			case Keys.Q:
				zoomOut = true;
				break;
			case Keys.E:
				zoomIn = true;
				break;
			case Keys.ENTER:
				interact = true;
				break;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(!focused)
			return true;
		TUserInterface.getIO().keyUp(keycode);
		switch(keycode) {
			case Keys.W:
				moveUp = false;
				break;
			case Keys.S:
				moveDown = false;
				break;
			case Keys.A:
				moveLeft = false;
				break;
			case Keys.D:
				moveRight = false;
				break;
			case Keys.SPACE:
				techTree = false;
				break;
			case Keys.SHIFT_LEFT:
				run = false;
				break;
			case Keys.Q:
				zoomOut = false;
				break;
			case Keys.E:
				zoomIn = false;
				break;
			case Keys.ENTER:
				interact = false;
				break;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		if(!focused)
			return true;
		if(TUserInterface.guiFocused()) {
			TUserInterface.getIO().keyTyped(character);
			return false;
		}
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(!focused) {
			if(button == Buttons.LEFT) {
				Gdx.input.setCursorCatched(true);
				focused = true;
				return false;
			} else return true;
		}
		switch(button) {
			case Buttons.LEFT:
				if(TUserInterface.guiFocused())
					TUserInterface.getIO().touchDown((int)cursorX, (int)cursorY, 0, 0);
				else interact = true;
				break;
		}
		cursorX = screenX;
		cursorY = screenY;
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(!focused)
			return true;
		switch(button) {
			case Buttons.LEFT:
				TUserInterface.getIO().touchUp((int)cursorX, (int)cursorY, 0, 0);
				interact = false;
				break;
		}
		cursorX = screenX;
		cursorY = screenY;
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(!focused)
			return true;
		if(TUserInterface.guiFocused())
			TUserInterface.getIO().touchDragged(screenX, screenY, pointer);
		cursorX = screenX;
		cursorY = screenY;
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if(!focused)
			return true;
		if(TUserInterface.guiFocused())
			TUserInterface.getIO().mouseMoved(screenX, screenY);
		cursorX = screenX;
		cursorY = screenY;
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		if(!focused)
			return true;
		if(TUserInterface.guiFocused())
			TUserInterface.getIO().scrolled(amountX, amountY);
		scroll = amountY;
		return false;
	}
	
	/**
	 * Renderable cursor.
	 */
	public static final TCursor cursor = new TCursor();
	
	/**
	 * Called every frame. Used to update the state of the virtual cursor, and manage any other internal input data that needs to be reset.
	 */
	public void tick() {
		if(!focused)
			return;
		Gdx.input.setCursorCatched(true);
		if(scroll > 0) {
			slotRight = true;
		} else if(scroll < 0) {
			slotLeft = true;
		} else {
			slotRight = false;
			slotLeft = false;
		}
		scroll = 0;
		handleUIInteraction();
		clampCursorToScreenSpace();
		handleControllerCursorMovement();
	}
	
	@Override
	public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
		if(!focused)
			return true;
		if(TUserInterface.guiFocused())
			TUserInterface.getIO().touchCancelled(screenX, screenY, pointer, button);
		return false;
	}
	
	/**
	 * Filters in-game bindings and gesters as input events.
	 */
	private void handleUIInteraction() {
		// make sure the gui modules know where the cursor is...
		TUserInterface.getIO().mouseMoved((int)cursorX, (int)cursorY);
	}
	
	/**
	 * Make sure the cursor always stays within the bounds of the screen.
	 */
	private void clampCursorToScreenSpace() {
		final float vpw   = Gdx.graphics.getWidth();
		final float vph   = Gdx.graphics.getHeight();
		final float cursW = cursor.getWidth() / 8f;
		final float cursH = cursor.getHeight() / 8f;
		cursorX = TMath.clamp(cursorX, cursW, vpw - cursW);
		cursorY = TMath.clamp(cursorY, cursH, vph - cursH);
		Gdx.input.setCursorPosition((int)cursorX, (int)cursorY);
	}
	
	/**
	 * Handle controller requests to move the cursor.
	 */
	private void handleControllerCursorMovement() {
		if(cursorDown || cursorUp || cursorLeft || cursorRight) {
			int dx = 0;
			int dy = 0;
			if(cursorDown)  dy += cursorSpeed;
			if(cursorUp)    dy -= cursorSpeed;
			if(cursorLeft)  dx -= cursorSpeed;
			if(cursorRight) dx += cursorSpeed;
			Gdx.input.setCursorPosition(Gdx.input.getX() + dx, Gdx.input.getY() + dy);
		}
	}

}
