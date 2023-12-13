package dev.iwilkey.terrafort.obj.particulate;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * Debris, dust; an insignificant {@link TParticulate} added for visual effect.
 * @author Ian Wilkey (iwilkey)
 */
public final class TParticle extends TParticulate {
	
	public static final float SIZE_UNPREDICTABILITY              = 1.0f; // [1, any]
	public static final float COLOR_UNPREDICTABILITY             = 0.1f; // [0.0, 1.0]
	public static final float LIFETIME_UNPREDICTABILITY          = 4.0f; // [any, any]
	public static final float IMPULSE_MAGNITUDE_UNPREDICTABILITY = 64.0f; // [0, 128.0]
	public static final float LINEAR_DAMPING_UNPREDICABILITY     = 16.0f; // [0, any];

	public TParticle(TWorld world, float x, float y, Color baseColor) {
		super(world, x, y, 0.5f, 0.5f, TMath.nextFloat(1.0f, 1.0f + LIFETIME_UNPREDICTABILITY));
		renderTint   = baseColor.cpy();
		renderTint.a = TMath.nextFloat(0.5f, 0.8f);
		randomDimensionalAddition(0, SIZE_UNPREDICTABILITY);
		randomColorAddition(COLOR_UNPREDICTABILITY);
		randomImpulseForce(128.0f - IMPULSE_MAGNITUDE_UNPREDICTABILITY, 128.0f + IMPULSE_MAGNITUDE_UNPREDICTABILITY);
		randomLinearAndAngularDamping(32.0f, 32.0f + LINEAR_DAMPING_UNPREDICABILITY);
	}

	@Override
	public void behavior(float dt) {
		
	}

}
