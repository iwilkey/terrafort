package dev.iwilkey.terrafort.game.object;

import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;

import dev.iwilkey.terrafort.object.GameObject3;
import dev.iwilkey.terrafort.physics.PhysicsPrimitive;
import dev.iwilkey.terrafort.procedural.ProceduralTextureGenerator;
import dev.iwilkey.terrafort.state.State;

public final class Planet extends GameObject3 {
	
	public static final float MASS_MIN = (float)Math.pow(10, 5);
	public static final float MASS_MAX = (float)Math.pow(10, 10);
	
	private static final Random RANDOM = new Random();
	private static final ProceduralTextureGenerator GENERATOR = new ProceduralTextureGenerator();

	public Planet(final State state) {
		super(state, "tf_sphere", PhysicsPrimitive.SPHERE, MASS_MIN + RANDOM.nextFloat() * (MASS_MAX - MASS_MIN), 1.1f);
		changeTexture(GENERATOR.generate());
	}
	
	@Override
	public void instantiation() {
		setToScaling(100.0f);
	}
	
	/**
	 * Change the texture of the planet.
	 * 
	 * @param newTexture the new texture.
	 */
	public void changeTexture(final Texture newTexture) {
	    final Material material = getModelInstance().materials.get(0);
	    final TextureAttribute textureAttribute = (TextureAttribute)material.get(TextureAttribute.Diffuse);
	    textureAttribute.textureDescription.texture = newTexture;
	}
	
	public void setToScaling(float scale) {
		renderable.transform.setToScaling(scale, scale, scale);
	}
	
	/**
	 * Get the object that is in charge of creating the unique texture for the planet.
	 * 
	 * @return the planet's ProceduralTextureGenerator.
	 */
	public ProceduralTextureGenerator getTextureGenerator() {
		return GENERATOR;
	}
	
	@Override
	public void dispose() {
		GENERATOR.dispose();
	}

}
