package dev.iwilkey.terrafort.gfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

public class Camera extends PerspectiveCamera {

	public Camera(int fov) {
		super(fov, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		position.set(0, 0, 0);
		direction.set(Vector3.X);
		near = 0.1f;
		setRenderDistance(200.0f);
	}
	
	public void setRenderDistance(float distance) {
		far = distance;
		update();
	}
	
}
