package dev.iwilkey.terrafort.obj.entity.mob;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

import dev.iwilkey.terrafort.TClock;
import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.gfx.anim.TLifeformAnimationArray;
import dev.iwilkey.terrafort.obj.TObject;
import dev.iwilkey.terrafort.obj.entity.TEntity;
import dev.iwilkey.terrafort.obj.particulate.TParticle;
import dev.iwilkey.terrafort.obj.particulate.TTurretProjectile;
import dev.iwilkey.terrafort.obj.world.TTerrain;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * A {@link TMob} that's sole purpose in life is to kill {@link TPlayer}s and take their goods.
 * @author Ian Wilkey (iwilkey)
 */
public final class TBandit extends TMob {
	
	public static final float MAX_POWER_MULTIPLIER = 10.0f;
	public static final float BASE_HP              = 8.0f;
	public static final float BANDIT_SPEED         = 48.0f;
	public static final float BANDIT_WIDTH         = 16.0f;
	public static final float BANDIT_HEIGHT        = 32.0f;
	
	private float powerMultiplier;
	
	public TBandit(TWorld world, float tileX, float tileY, float powerMultiplier) {
		super(world,
			  true,
			  tileX * TTerrain.TILE_WIDTH,
			  tileY * TTerrain.TILE_HEIGHT,
			  0,
			  BANDIT_WIDTH,
			  BANDIT_HEIGHT,
			  3.0f,
			  3.0f,
			  0,
			  0,
			  1,
			  2,
			  Color.WHITE.cpy().add(powerMultiplier / MAX_POWER_MULTIPLIER, 
					  -(powerMultiplier / MAX_POWER_MULTIPLIER), 
					  -(powerMultiplier / MAX_POWER_MULTIPLIER), 0),
			  (int)Math.ceil(BASE_HP * Math.min(powerMultiplier, MAX_POWER_MULTIPLIER)),
			  new TLifeformAnimationArray(new TFrame(8, 0, 1, 2), new TFrame(11, 0, 1, 2)));
		this.powerMultiplier = Math.min(powerMultiplier, MAX_POWER_MULTIPLIER);
		setGraphicsColliderOffset(-1, 8);
		setMoveSpeed(BANDIT_SPEED + (this.powerMultiplier * 4));
	}
	
	float attackTimer = 0.0f;
	
	@Override
	public void task(float dt) {
		super.task(dt);
		// play fair and take damage from turrets...
		final Array<TObject> manifold = getCollisionManifold();
		for(final TObject o : manifold)
			if(o instanceof TTurretProjectile)
				hurt(1);
	}

	@Override
	public boolean requestAttack() {
		attackTimer += TClock.dt() * (powerMultiplier * 3);
		if(attackTimer > 1.0f) {
			attackTimer = 0.0f;
			return true;
		}
		return false;
	}

	@Override
	public void attackProcedure() {
		final TObject hit;
		hit = sense(6f, (float)Math.PI / 8f, 2);
		if(hit != null) {
			if(hit instanceof TEntity) {
				final TEntity e = (TEntity)hit;
				for(int i = 0; i < ThreadLocalRandom.current().nextInt(1, (int)Math.ceil(powerMultiplier)); i++)
					e.onInteraction(this);
			}
		}
	}

	@Override
	public void movementProcedure() {
		// worlds simplest AI...
		final TPlayer target = world.getPlayer();
		final float playerX = target.getActualX();
		final float playerY = target.getActualY();
		final float myX = getActualX();
		final float myY = getActualY();
	    final float tolerance = 5.0f;
	    float deltaX = playerX - myX;
	    float deltaY = playerY - myY;
	    if (Math.abs(deltaX) > tolerance) {
	        if(deltaX > 0) {
	            moveRight();
	        } else {
	            moveLeft();
	        }
	    }
	    if (Math.abs(deltaY) > tolerance) {
	        if(deltaY > 0) {
	            moveUp();
	        } else {
	            moveDown();
	        }
	    }
	}

	@Override
	public void spawn() {

	}

	@Override
	public void onInteraction(TMob interactee) {
		// bandits can't hurt themselves.
		if(interactee instanceof TBandit)
			return;
		// Players can deal [1, 4] damage.
		hurt(ThreadLocalRandom.current().nextInt(1, 4));
	}

	@Override
	public void die() {
		world.getPlayer().giveCurrency(ThreadLocalRandom.current().nextInt((int)(1000 * powerMultiplier), (int)(10000 * powerMultiplier)));
		for(int i = 0; i < 16; i++)
			world.addObject(new TParticle(world, getActualX(), getActualY(), Color.RED.cpy()));
	}
	
}
