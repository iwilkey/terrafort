package dev.iwilkey.terrafort.gfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;

/**
 * The `Camera` class represents a camera in a 3D environment. It extends the `PerspectiveCamera` class.
 * The camera is responsible for capturing the view of the scene from a specific perspective.
 */
public class Camera extends PerspectiveCamera {
	
	/**
	 * The `CameraController` interface defines a contract for controlling the camera behavior.
	 * Implementations of this interface can provide custom logic for camera control.
	 */
	public static interface CameraController {
		/**
		 * Controls the camera behavior.
		 * @param camera The camera instance to control.
		 */
		public void control(Camera camera);
	}

	private CameraController controller = null;

	/**
	 * Constructs a new Camera object with the specified field of view (FOV).
	 * @param fov The field of view of the camera.
	 */
	public Camera(int fov) {
		super(fov, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		near = 0.1f;
		far = 1000.0f;
		update();
	}
	
	/**
	 * Updates the camera state on each tick.
	 */
	public void tick() {
		if(controller != null)
			controller.control(this);
		update();
	}
	
	/**
	 * Sets the camera controller that will control the camera behavior.
	 * @param controller The camera controller implementation.
	 */
	public void setController(CameraController controller) {
		this.controller = controller;
	}
	
}
