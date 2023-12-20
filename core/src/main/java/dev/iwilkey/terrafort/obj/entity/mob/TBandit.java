package dev.iwilkey.terrafort.obj.entity.mob;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.TClock;
import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.gfx.anim.TLifeformAnimationArray;
import dev.iwilkey.terrafort.obj.TObject;
import dev.iwilkey.terrafort.obj.entity.TEntity;
import dev.iwilkey.terrafort.obj.world.TTerrain;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * A {@link TMob} that's sole purpose in life is to kill {@link TPlayer}s and take their goods.
 * @author Ian Wilkey (iwilkey)
 */
public final class TBandit extends TMob {
	
	public static final int   BANDIT_MAX_HP     = 8;
	public static final float BANDIT_SPEED      = 48.0f;
	public static final float BANDIT_WIDTH      = 16.0f;
	public static final float BANDIT_HEIGHT     = 32.0f;

	public TBandit(TWorld world, float tileX, float tileY) {
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
			  Color.GREEN.cpy(),
			  BANDIT_MAX_HP,
			  new TLifeformAnimationArray(new TFrame(8, 0, 1, 2), new TFrame(11, 0, 1, 2)));
		setGraphicsColliderOffset(-1, 8);
		setMoveSpeed(BANDIT_SPEED);
		setAttackCooldownTime(0.16f);
	}
	
	float attackTimer = 0.0f;

	@Override
	public boolean requestAttack() {
		attackTimer += TClock.dt();
		if(attackTimer > 0.5f) {
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
			// we've hit something, check what it is.
			if(hit instanceof TEntity) {
				final TEntity e = (TEntity)hit;
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
	        if (deltaX > 0) {
	            moveRight();
	        } else {
	            moveLeft();
	        }
	    }
	    if (Math.abs(deltaY) > tolerance) {
	        if (deltaY > 0) {
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
		hurt(ThreadLocalRandom.current().nextInt(1, 4));
	}

	@Override
	public void die() {

	}
	
}
