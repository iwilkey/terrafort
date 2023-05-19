package dev.iwilkey.terrafort.state.game.object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

import dev.iwilkey.terrafort.object.GameObject3;
import dev.iwilkey.terrafort.physics.bullet.BulletPrimitive;
import dev.iwilkey.terrafort.physics.bullet.BulletWrapper;
import dev.iwilkey.terrafort.state.State;

public class Core extends GameObject3 {
	
	public static class Clouds extends GameObject3 {
		
		float rotSpeed = 2.0f;
		Vector3 rotAxis = new Vector3(0, 1, 0);
		
		public Clouds(State state) {
			super(state, "cube.txt", BulletPrimitive.SPHERE, 1.0f);
		}
		
		@Override
		public void instantiation() {
			setPhysicsBodyType(BulletWrapper.STATIC_FLAG);
		}
		
		@Override
		public void tick() {
			setRotation(rotAxis, Gdx.graphics.getDeltaTime() * rotSpeed);
		}
		
		@Override
		public void dispose() {}

	}

	// long clouds;
	Vector3 rotAxis = new Vector3(0, 1, 0);

	public Core(State state) {
		super(state, "cube.txt", BulletPrimitive.CUBOID, 100.0f);
		// clouds = state.addGameObject(new Clouds(state).setPosition(0, 0, 0));
	}

	@Override
	public void instantiation() {
		setPhysicsBodyType(BulletWrapper.STATIC_FLAG);
	}
	
	@Override
	public void tick() {
		setRotation(rotAxis, -Gdx.graphics.getDeltaTime());
	}

	@Override
	public void dispose() {}

}
