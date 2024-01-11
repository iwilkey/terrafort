package dev.iwilkey.terrafort.obj.particulate;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.obj.runtime.TObjectRuntime;
import dev.iwilkey.terrafort.obj.type.THarvestable;
import dev.iwilkey.terrafort.obj.type.TObject;
import dev.iwilkey.terrafort.obj.type.TParticulate;

/**
 * Debris, dust; an insignificant {@link TParticulate} added for visual effect.
 * @author Ian Wilkey (iwilkey)
 */
public final class TParticle extends TParticulate {

	private static final long serialVersionUID = 7364054041388722970L;
	
	/**
	 * The x velocity of the particle.
	 */
	public float velX;
	
	/**
	 * The y velocity of the particle.
	 */
	public float velY;
	
	/**
	 * Internally managed position x for custom physics.
	 */
	public float posX;
	
	/**
	 * Internally managed position x for custom physics.
	 */
	public float posY;
	
	/**
	 * The ocillating frequency of the particle.
	 */
	public float freq;

	/**
	 * Current amplitude of the particles harmonic motion movement.
	 */
	public float amp;
	
	/**
	 * What is the damping factor of the particle?
	 */
	public float damping;
	
	/**
	 * Creates a new particle at given world coordinates with given color.
	 */
	public TParticle(float worldX, float worldY, int color) {
		super(worldX, 
			  worldY, 
			  ThreadLocalRandom.current().nextInt(2, 4), 
			  ThreadLocalRandom.current().nextInt(2, 4), 
			  color);
		posX            = worldX;
		posY            = worldY;
		shouldFade      = true;
		damping         = 0.95f;
		lifespan        = (float)ThreadLocalRandom.current().nextDouble(2, 8);
		final float ang = (float)ThreadLocalRandom.current().nextDouble(0, 2 * Math.PI);
		final float scl = (float)ThreadLocalRandom.current().nextDouble(100, 200);
		velX            = (float)((Math.cos(ang)) * scl);
		velY            = (float)((Math.sin(ang)) * scl);
		amp             = (float)ThreadLocalRandom.current().nextDouble(32, 64);
		freq            = (float)ThreadLocalRandom.current().nextDouble(2, 4);
	}

	@Override
	public void behavior(TObjectRuntime concrete, float dt) {
		float osc = Math.abs((float)Math.sin(aliveTime * freq * Math.PI)) * amp;
		posX      += velX * dt;
		posY      += velY * dt;
		worldX    = posX;
		worldY    = posY + osc;
		amp       *= damping;
		velX      *= damping;
		velY      *= damping;
		// render shadow.
		TGraphics.draw(spriteSheet, 
				       new TFrame(dataX, dataY, dataWidth, dataHeight), 
				       posX, 
				       posY, 
				       130, 
				       worldWidth, 
				       worldHeight, 
				       new Color().set(tint & 0x000000ff));
	}

	@Override
	public void onPhysicalConvergence(TObject convergingBody) {
		// stops cold if it touches something harvestable...
		if(convergingBody instanceof THarvestable) {
			velX = 0;
			velY = 0;
			amp  = 0;
		}
	}

	@Override
	public void onPhysicalDivergence(TObject divergingBody) {

	}

}
