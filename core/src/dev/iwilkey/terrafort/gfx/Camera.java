package dev.iwilkey.terrafort.gfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

public class Camera extends PerspectiveCamera {
	
	public static interface CameraController {
		public void control(Camera camera);
	}
	
	public static Vector3 INITIAL_POSITION;
	public static Vector3 INITIAL_DIRECTION;
	private CameraController controller = null;

	public Camera(int fov) {
		super(fov, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		position.set(0, 10, -10);
		direction.set(Vector3.X);
		near = 0.1f;
		far = 50.0f;
		update();
	}
	
	public void tick() {
		if(controller != null)
			controller.control(this);
		update();
	}
	
	public void setController(CameraController controller) {
		this.controller = controller;
	}
	
}
