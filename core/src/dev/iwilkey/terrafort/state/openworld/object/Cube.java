package dev.iwilkey.terrafort.state.openworld.object;

import com.badlogic.gdx.Gdx;

import dev.iwilkey.terrafort.object.GameObject3;
import dev.iwilkey.terrafort.physics.Primitive;
import dev.iwilkey.terrafort.state.State;

public class Cube extends GameObject3 {

	public Cube(State state) {
		super(state, "vox/cube/cube.vox.obj", Primitive.CUBOID, 1.0f);
	}

	@Override
	public void instantiation() {}
	
	int scale = 1;
	float time = 0.0f;
	@Override
	public void tick() {
		time += Gdx.graphics.getDeltaTime();
		if(time >= 5.0f) {
			setShouldDispose();
			time = 0;
		}
	}
	@Override
	public void dispose() {}
	
}
