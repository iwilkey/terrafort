package dev.iwilkey.terrafort.particle;

import java.util.Random;

import com.badlogic.gdx.math.Vector3;

import dev.iwilkey.terrafort.state.State;

public final class ParticleSystem {
	
	public static final float POSITION_OFFSET_MIN = -0.4f;
	public static final float POSITION_OFFSET_MAX = 0.4f;
	public static final float LIFETIME_MIN = 5.0f;
	public static final float LIFETIME_MAX = 10.0f;
	public static final float FORCE_MAG_MIN = 1000.0f;
	public static final float FORCE_MAG_MAX = 100000.0f;
	public static final float MASS_MIN = 100.0f;
	public static final float MASS_MAX = 1000.0f;	
	
	private final Random random;
	private final State state;
	
	public ParticleSystem(final State state) {
		this.state = state;
		random = new Random();
	}
	
	private Vector3 force = new Vector3();
	public void createScatterAt(Vector3 position, int amount) {
		for(int i = 0; i < amount; i++) {
			// final float scale = SCALE_MIN + random.nextFloat() * (SCALE_MAX - SCALE_MIN);
			float lifetime = LIFETIME_MIN + random.nextFloat() * (LIFETIME_MAX - LIFETIME_MIN);
			// Find a random direction to apply the force (this creates the scatter.)
			float forceDirX = -1.0f + random.nextFloat() * (1.0f - -1.0f);
			float forceDirY = -1.0f + random.nextFloat() * (1.0f - -1.0f);
			float forceDirZ = -1.0f + random.nextFloat() * (1.0f - -1.0f);
			float forceMagnitude = FORCE_MAG_MIN + random.nextFloat() * (FORCE_MAG_MAX - FORCE_MAG_MIN);
			force.set(forceDirX, forceDirY, forceDirZ);
			float posOffX = POSITION_OFFSET_MIN + random.nextFloat() * (POSITION_OFFSET_MAX - POSITION_OFFSET_MIN);
			float posOffY = POSITION_OFFSET_MIN + random.nextFloat() * (POSITION_OFFSET_MAX - POSITION_OFFSET_MIN);
			float posOffZ = POSITION_OFFSET_MIN + random.nextFloat() * (POSITION_OFFSET_MAX - POSITION_OFFSET_MIN);
			float mass = MASS_MIN + random.nextFloat() * (MASS_MAX - MASS_MIN);
			Particle particle = new Particle(state, force, forceMagnitude, lifetime, mass);
			particle.setPosition(position.cpy().add(posOffX, posOffY, posOffZ));
			state.addGameObject(particle);
		}
	}
}
