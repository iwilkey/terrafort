package dev.iwilkey.terrafort.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import dev.iwilkey.terrafort.Terrafort;
import dev.iwilkey.terrafort.asset.registers.VoxelModels;
import dev.iwilkey.terrafort.gfx.Camera;
import dev.iwilkey.terrafort.gfx.Camera.CameraController;
import dev.iwilkey.terrafort.input.InputHandler;
import dev.iwilkey.terrafort.object.GameObject3;
import dev.iwilkey.terrafort.physics.PhysicsPrimitive;
import dev.iwilkey.terrafort.state.State;

public final class Player extends GameObject3 implements CameraController {
	
	public Player(State state) {
		super(state, VoxelModels.SPITTER_MK1, PhysicsPrimitive.CUBOID, 1.0f);
		state.getCamera().setController(this);
	}
	
	@Override
	public void instantiation() {
		setKinematic();
		setPosition(0, 0, 0);
		setRotationAxis(Vector3.Y, 180.0f);
		calculateCameraPositionFromYawAndPitch(state.getCamera());
	}
	
	/**
	 * Camera orbit.
	 */
	
	private static final float ORBIT_SENSITIVITY = 0.4f;
	public static final float CAMERA_LERP_SPEED = 0.30f;
	
	private float cameraDistance = 2f;
	private float targetCameraDistance = cameraDistance;
	private float cameraPitch = 30f;
	private float cameraPitchTarget = cameraPitch;
	private float cameraYaw = 0f;
	private float cameraYawTarget = cameraYaw;
	
	@Override
	public void control(Camera camera) {
		targetCameraDistance += InputHandler.getScrollwheelPosition().y;
		targetCameraDistance = MathUtils.clamp(targetCameraDistance, 2.0f, 15.0f);
		cameraDistance = Interpolation.exp5.apply(cameraDistance, targetCameraDistance, CAMERA_LERP_SPEED);
		if(state.bindingDown("action")) {
			// Update yaw and pitch via input.
			float deltaX = Gdx.input.getDeltaX() * ORBIT_SENSITIVITY;
			float deltaY = Gdx.input.getDeltaY() * ORBIT_SENSITIVITY;
			cameraYawTarget += deltaX;
			cameraPitchTarget += deltaY;
			cameraPitchTarget = MathUtils.clamp(cameraPitchTarget, -89.9f, 89.9f);
			updateObjectYaw(deltaX);
		}
		cameraYaw = Interpolation.exp5.apply(cameraYaw, cameraYawTarget, CAMERA_LERP_SPEED);
		cameraPitch = Interpolation.exp5.apply(cameraPitch, cameraPitchTarget, CAMERA_LERP_SPEED);
		calculateCameraPositionFromYawAndPitch(camera);
	}
	
	private void calculateCameraPositionFromYawAndPitch(final Camera camera) {
		// Calculate camera position.
		float cosPitch = MathUtils.cosDeg(cameraPitch);
        float sinPitch = MathUtils.sinDeg(cameraPitch);
        float cosYaw = MathUtils.cosDeg(cameraYaw);
        float sinYaw = MathUtils.sinDeg(cameraYaw);
        float xDirection = cosPitch * cosYaw;
        float yDirection = sinPitch;
        float zDirection = cosPitch * sinYaw;
        // Set position.
        camera.position.set(
                getPosition().x + cameraDistance * xDirection,
                getPosition().y + cameraDistance * yDirection,
                getPosition().z + cameraDistance * zDirection
        );
        camera.lookAt(getPosition());
        camera.up.set(0, 1, 0);
	}
	
	/**
	 * End Camera orbit.
	 */
	
	private float shipYaw = -cameraYawTarget;
	private float shipYawTarget = shipYaw;
	private void updateObjectYaw(float dx) {
		Terrafort.log("" + getYawPitchRoll().y);
		shipYawTarget = -cameraYawTarget;
		shipYaw = Interpolation.exp5.apply(shipYaw, shipYawTarget, CAMERA_LERP_SPEED * 2);
		setRotation(shipYaw, 0, 0);
	}
	
	@Override
	public void tick() {
		if(state.bindingDown("forward")) {

		}
	}
	
	@Override
	public void dispose() {
		state.getCamera().setController(null);
	}
	
	
}
