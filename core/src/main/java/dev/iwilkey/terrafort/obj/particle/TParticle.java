package dev.iwilkey.terrafort.obj.particle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.TObject;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * Debris, dust; a small physical object that is simulated with realistic physics until it dies, shortly after it's created.
 * @author Ian Wilkey (iwilkey)
 */
public final class TParticle extends TObject {
	
	public static final float SIZE_UNPREDICTABILITY              = 2.0f; // [any, any]
	public static final float COLOR_UNPREDICTABILITY             = 0.1f; // [0.0, 1.0]
	public static final float LIFETIME_UNPREDICTABILITY          = 4.0f; // [any, any]
	public static final float IMPULSE_MAGNITUDE_UNPREDICTABILITY = 64.0f; // [0, 128.0]
	public static final float LINEAR_DAMPING_UNPREDICABILITY     = 16.0f; // [0, any];
	
	private boolean done;
	private float   aliveTime;
	private float   lifetime;
	
	public TParticle(TWorld world, float x, float y, Color baseColor) {
		super(world, 
			  true, 
			  x, 
			  y, 
			  3, 
			  1, 
			  1, 
			  0.1f, 
			  0.1f, 
			  3, 
			  1,
			  1, 
			  1, 
			  baseColor);
		
		// actual particle spawn x and y should be a tiny bit random...
		// this.x                       += TMath.uniFloat(-POSITION_UNPREDICTABLITY, POSITION_UNPREDICTABLITY);
		// this.y                       += TMath.uniFloat(-POSITION_UNPREDICTABLITY, POSITION_UNPREDICTABLITY);
		this.width                      += TMath.uniFloat(0, SIZE_UNPREDICTABILITY);
		this.height                     += TMath.uniFloat(0, SIZE_UNPREDICTABILITY);
		// particles should use the base color as a reference, but not copy it directly...
		renderTint                    = baseColor.cpy().add(TMath.uniFloat(-COLOR_UNPREDICTABILITY, COLOR_UNPREDICTABILITY), 
										 TMath.uniFloat(-COLOR_UNPREDICTABILITY, COLOR_UNPREDICTABILITY), 
										 TMath.uniFloat(-COLOR_UNPREDICTABILITY, COLOR_UNPREDICTABILITY), 
										 0.0f);
		renderTint.a                  = TMath.uniFloat(0.5f, 0.8f);
		
		done                         = false;
		aliveTime                    = 0.0f;
		lifetime                     = TMath.uniFloat(1 + LIFETIME_UNPREDICTABILITY, 4 + LIFETIME_UNPREDICTABILITY);
		
		// particles should spawn with a random impulse in any direction...
	    final float impulseMagnitude = TMath.uniFloat(128.0f - IMPULSE_MAGNITUDE_UNPREDICTABILITY, 128.0f + IMPULSE_MAGNITUDE_UNPREDICTABILITY);
	    final float angle            = TMath.uniFloat(0, (float)(2 * Math.PI));
	    final Vector2 impulse        = new Vector2((float)(impulseMagnitude * Math.cos(angle)), (float)(impulseMagnitude * Math.sin(angle)));
	    
	    // particles should be very light...
	    getFixture().setDensity(0.01f);
		getPhysicalBody().resetMassData();
	    getPhysicalBody().applyLinearImpulse(impulse, getPhysicalBody().getWorldCenter(), true);
	    
	    // particles should come to rest at some point during their life...
	    getPhysicalBody().setLinearDamping(TMath.uniFloat(32.0f, 32.0f + LINEAR_DAMPING_UNPREDICABILITY));
	    getPhysicalBody().setAngularDamping(TMath.uniFloat(32.0f, 32.0f + LINEAR_DAMPING_UNPREDICABILITY));
	}
	
	public void tick(float dt) {
		aliveTime += dt;
		// particles should fade as they get closer to death.
		final float percentDone = (aliveTime / lifetime);
		renderTint.a = 1.0f - percentDone;
		if(aliveTime >= lifetime)
			done = true;
	}
	
	public boolean isDone() {
		return done;
	}
	
}
