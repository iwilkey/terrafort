package dev.iwilkey.terrafort.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import dev.iwilkey.terrafort.asset.registers.VoxelModels;
import dev.iwilkey.terrafort.gfx.Camera;
import dev.iwilkey.terrafort.gfx.Camera.CameraController;
import dev.iwilkey.terrafort.input.InputHandler;
import dev.iwilkey.terrafort.object.GameObject3;
import dev.iwilkey.terrafort.physics.PhysicsPrimitive;
import dev.iwilkey.terrafort.state.State;

public final class Player extends GameObject3 implements CameraController {
	
	public Player(State state) {
		super(state, VoxelModels.SPITTER_MK1, PhysicsPrimitive.MESH, 1.0f);
		state.getCamera().setController(this);
	}
	
	@Override
	public void instantiation() {
		setKinematic();
		getTransform().positionAbsolute(0, 0, 0);
		getTransform().rotateRelativeAxisDegrees(Vector3.Y, 180.0f);
		calculateCameraPositionFromYawAndPitch(state.getCamera());
	}
	
	/**
	 * Begin Camera steering.
	 */
	
	public static final float ORBIT_SENSITIVITY = 0.4f;
	public static final float CAMERA_LERP_SPEED = 0.3f;
	
	private boolean steeringInverted = false;
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
			cameraYawTarget += ((steeringInverted) ? -deltaX : deltaX);
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
                getTransform().getCurrentPosition().x + cameraDistance * xDirection,
                getTransform().getCurrentPosition().y + cameraDistance * yDirection,
                getTransform().getCurrentPosition().z + cameraDistance * zDirection
        );
        camera.lookAt(getTransform().getCurrentPosition());
        camera.up.set(Vector3.Y);
	}
	
	/**
	 * End Camera steering.
	 */
	
	/**
	 * Begin Ship movement.
	 */

	// Speed constants.
	public static final float TOP_SPEED_FORWARD_BACK = 0.4f;
	public static final float TOP_SPEED_STRAFE = 0.2f;
	public static final float TOP_SPEED_UP_DOWN = 0.3f;
	// Acceleration constants.
	public static final float ACCELERATION_MOVING = 0.1f;
	public static final float ACCELERATION_BREAKING = 0.2f;
	// Ship direction.
	private float shipYaw = -cameraYawTarget;
	private float shipYawTarget = shipYaw;
	
	private void updateObjectYaw(float dx) {
		shipYawTarget = -cameraYawTarget;
		shipYaw = Interpolation.exp5.apply(shipYaw, shipYawTarget, CAMERA_LERP_SPEED * 1.5f);
		getTransform().rotateAbsoluteToYawPitchRollDegrees(shipYaw, 0, 0);
	}

	@Override
	public void tick() {
		forwardBackTranslation();
		strafeLeftRightTranslation();
		upDownTranslation();
	}
	
	private float currentSpeedTargetForwardBack = 0.0f;
	private float currentSpeedForwardBack = currentSpeedTargetForwardBack;
	private float currentAccelForwardBack = ACCELERATION_MOVING;
	
	private void forwardBackTranslation() {
		if(state.bindingDown("forward")) {
			currentSpeedTargetForwardBack = TOP_SPEED_FORWARD_BACK;
			currentAccelForwardBack = ACCELERATION_MOVING;
		} else if(state.bindingDown("backward")) {
			currentSpeedTargetForwardBack = -TOP_SPEED_FORWARD_BACK;
			currentAccelForwardBack = ACCELERATION_MOVING;
		} else {
			currentSpeedTargetForwardBack = 0.0f;
			currentAccelForwardBack = ACCELERATION_BREAKING;
		}
		currentSpeedForwardBack = Interpolation.exp5.apply(currentSpeedForwardBack, currentSpeedTargetForwardBack, currentAccelForwardBack);
		getTransform().translateForwardFromCameraPersp(state.getCamera(), currentSpeedForwardBack);
	}
	
	private float currentSpeedTargetStrafe = 0.0f;
	private float currentSpeedStrafe = currentSpeedTargetStrafe;
	private float currentAccelStrafe = ACCELERATION_MOVING;
	
	private void strafeLeftRightTranslation() {
		if(state.bindingDown("strafe_left")) {
			currentSpeedTargetStrafe = -TOP_SPEED_STRAFE;
			currentAccelStrafe = ACCELERATION_MOVING;
		} else if(state.bindingDown("strafe_right")) {
			currentSpeedTargetStrafe = TOP_SPEED_STRAFE;
			currentAccelStrafe = ACCELERATION_MOVING;
		} else {
			currentSpeedTargetStrafe = 0.0f;
			currentAccelStrafe = ACCELERATION_BREAKING;
		}
		currentSpeedStrafe = Interpolation.exp5.apply(currentSpeedStrafe, currentSpeedTargetStrafe, currentAccelStrafe);
		getTransform().translateRightFromCameraPersp(state.getCamera(), currentSpeedStrafe);
	}
	
	private float currentSpeedTargetUpDown = 0.0f;
	private float currentSpeedUpDown = currentSpeedTargetUpDown;
	private float currentAccelUpDown = ACCELERATION_MOVING;
	
	private void upDownTranslation() {
		if(state.bindingDown("ascend")) {
			currentSpeedTargetUpDown = TOP_SPEED_UP_DOWN;
			currentAccelUpDown = ACCELERATION_MOVING;
		} else if(state.bindingDown("descend")) {
			currentSpeedTargetUpDown = -TOP_SPEED_UP_DOWN;
			currentAccelUpDown = ACCELERATION_MOVING;
		} else {
			currentSpeedTargetUpDown = 0.0f;
			currentAccelUpDown = ACCELERATION_BREAKING;
		}
		currentSpeedUpDown = Interpolation.exp5.apply(currentSpeedUpDown, currentSpeedTargetUpDown, currentAccelUpDown);
		getTransform().translateUp(currentSpeedUpDown);
	}
	
	/**
	 * End Ship movement.
	 */
	
	@Override
	public void dispose() {
		state.getCamera().setController(null);
	}
	
	
}
