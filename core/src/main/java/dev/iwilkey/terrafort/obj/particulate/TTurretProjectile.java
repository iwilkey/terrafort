package dev.iwilkey.terrafort.obj.particulate;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.math.Vector2;

import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * The {@link TParticulate} that a {@link TTurretTile} fires.
 * @author Ian Wilkey (iwilkey)
 */
public final class TTurretProjectile extends TParticulate {
	
	public TTurretProjectile(TWorld world, float originX, float originY, float dirX, float dirY) {
		super(world, 
				originX + dirX, 
				originY + dirY, 
				ThreadLocalRandom.current().nextInt(1, 2), 
				ThreadLocalRandom.current().nextInt(1, 2), 
				2.0f);
		this.dataOffsetX               = 3;
		this.dataOffsetY               = 1;
		this.dataSelectionSquareWidth  = 1;
		this.dataSelectionSquareHeight = 1;
		randomDirectedImpulseForce(127, 128, new Vector2(dirX, dirY), 1);
		getPhysicalFixture().setDensity(10f);
		getPhysicalBody().resetMassData();
		getPhysicalBody().applyTorque(ThreadLocalRandom.current().nextInt(5000, 500000), false);
		setAsSensor();
	}

	@Override
	public void behavior(float dt) {
	}

}
