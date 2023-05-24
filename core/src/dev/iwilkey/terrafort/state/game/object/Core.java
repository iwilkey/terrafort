package dev.iwilkey.terrafort.state.game.object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

import dev.iwilkey.terrafort.object.GameObject3;
import dev.iwilkey.terrafort.physics.bullet.BulletPrimitive;
import dev.iwilkey.terrafort.physics.bullet.BulletWrapper;
import dev.iwilkey.terrafort.state.State;

public class Core extends GameObject3 {
	
	private Vector3 rotAxis = new Vector3(0, 1, 0);

	public Core(State state) {
		super(state, "core.txt", BulletPrimitive.SPHERE, 100.0f);
	}

	@Override
	public void instantiation() {
		setPhysicsBodyType(BulletWrapper.STATIC_FLAG);
	}
	
	@Override
	public void tick() {
		setRotation(rotAxis, -Gdx.graphics.getDeltaTime());
	}

}
