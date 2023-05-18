package dev.iwilkey.terrafort.state.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;

import dev.iwilkey.terrafort.InputHandler;
import dev.iwilkey.terrafort.InputHandler.KeyBinding;
import dev.iwilkey.terrafort.gfx.Camera;
import dev.iwilkey.terrafort.gfx.Camera.CameraController;
import dev.iwilkey.terrafort.state.State;

public class Player implements CameraController {

	private final State state;
	private boolean isFocused = false;
	private long crosshair;
	
	public Player(State state, long crosshair) {
		this.state = state;
		this.crosshair = crosshair;
	}
	
	/**
	 * Translation and perspective variables
	 */
	
	private static final float VERT_DEGREE_CLAMP = 88.0f;
	private Vector3 xzDirection = Vector3.X.cpy();
	private Vector3 position = new Vector3(0, 10, -10);
	private double rotVertAngle = 0.0f;
	private float horLookSens = 0.2f;
	private float vertLookSens = 0.2f;
	private float forwardSpeed = 0.05f;
	private float strafeSpeed = 0.05f;
	private float upSpeed = 0.02f;
	private float translationSmoothingConstant = 0.01f;
	private Interpolation translationInterpolationType = Interpolation.exp10Out;
	
	@Override
	public void control(Camera camera) {
		camera.position.interpolate(position, translationSmoothingConstant, translationInterpolationType);
		handleFocus();
		if(!isFocused)
			return;
		// Handle translation.
		if(InputHandler.keyCurrent(KeyBinding.getBinding("Move Foward")))
			position.add(new Vector3(xzDirection.x * forwardSpeed, 0, xzDirection.z * forwardSpeed));
		if(InputHandler.keyCurrent(KeyBinding.getBinding("Move Backward")))
			position.sub(new Vector3(xzDirection.x * forwardSpeed, 0, xzDirection.z * forwardSpeed));
		if(InputHandler.keyCurrent(KeyBinding.getBinding("Strafe Right")))
			position.add(new Vector3(-xzDirection.z * strafeSpeed, 0, xzDirection.x * strafeSpeed));
		if(InputHandler.keyCurrent(KeyBinding.getBinding("Strafe Left")))
			position.sub(new Vector3(-xzDirection.z * strafeSpeed, 0, xzDirection.x * strafeSpeed));
		if(InputHandler.keyCurrent(KeyBinding.getBinding("Ascend")))
			position.add(Vector3.Y.cpy().scl(upSpeed));
		if(InputHandler.keyCurrent(KeyBinding.getBinding("Descend")))
			position.sub(Vector3.Y.cpy().scl(upSpeed));
		// Handle look.
		int deltaX = Gdx.input.getDeltaX();
		int deltaY = Gdx.input.getDeltaY();
		double deltaHor = -deltaX * horLookSens;
		camera.direction.rotate(Vector3.Y, (float)deltaHor);
		xzDirection.rotate(Vector3.Y, (float)deltaHor);
		double newVertRotAngle = rotVertAngle;
		double deltaVert = -deltaY * vertLookSens;
		newVertRotAngle += deltaVert;
		if(newVertRotAngle >= -VERT_DEGREE_CLAMP && newVertRotAngle <= VERT_DEGREE_CLAMP) {
			rotVertAngle = newVertRotAngle;
			camera.direction.rotate(new Vector3(-camera.direction.z, 0, camera.direction.x), (float)deltaVert);
		}
	}
	
	private void handleFocus() {
		if(InputHandler.cursorJustDown(KeyBinding.getBinding("Action")) && !isFocused)
			if(!InputHandler.guiWantsInteraction())
				isFocused = true;
		if(InputHandler.keyJustDown(KeyBinding.getBinding("Focus / Unfocus")))
			isFocused = !isFocused;
		state.getObjectHandler().get(crosshair).setShouldRender(isFocused);
		Gdx.input.setCursorCatched(isFocused);
	}
	
	public boolean isFocused() {
		return isFocused;
	}
}
