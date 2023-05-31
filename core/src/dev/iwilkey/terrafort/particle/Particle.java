package dev.iwilkey.terrafort.particle;

import org.lwjgl.opengl.GL20;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

import dev.iwilkey.terrafort.object.GameObject3;
import dev.iwilkey.terrafort.physics.PhysicsPrimitive;
import dev.iwilkey.terrafort.state.State;

/**
 * Represents a particle object in the game.
 */
public final class Particle extends GameObject3 {
	
	private final Vector3 centralForce;
	private final float magnitude;
	private final float lifetime;
	private float time = 0.0f;
	
	/**
	 * Constructs a Particle object with the given parameters.
	 * @param state the game state
	 * @param centralForce the central force to apply to the particle
	 * @param magnitude the magnitude of the central force
	 * @param lifetime the lifetime of the particle
	 * @param mass the mass of the particle
	 */
	public Particle(final State state, Vector3 centralForce, final float magnitude, final float lifetime, final float mass) {
		super(state, "tf_particle", PhysicsPrimitive.CUBOID, mass, 1.0f);
		this.lifetime = lifetime;
		this.centralForce = centralForce;
		this.magnitude = magnitude;
		setDynamic();
	}
	
	/**
	 * Creates the model for the particle.
	 * @return the particle model
	 */
	@SuppressWarnings("deprecation")
	public static Model createParticleModel() {
		final ModelBuilder builder = new ModelBuilder();
		builder.begin();
		final Color initialColor = new Color(Color.WHITE);
		final Material material = new Material(ColorAttribute.createDiffuse(initialColor));
		final MeshPartBuilder mesher = builder.part("tf_particle", GL20.GL_LINES, Usage.Position | Usage.Normal, material);
		mesher.box(0.1f, 0.1f, 0.1f);
		return builder.end();
	}
	
	@Override
	public void instantiation() {
		applyCentralForce(centralForce, magnitude);
	}
	
	@Override
	public void tick() {
		time += Gdx.graphics.getDeltaTime();
		if(time >= lifetime) {
			// This object is in charge of killing itself so the particle system doesn't have to.
			setShouldDispose();
			time = 0;
		}
	}
}
