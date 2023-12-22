package dev.iwilkey.terrafort.obj.entity.tile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

import dev.iwilkey.terrafort.TClock;
import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.gfx.shape.TCircle;
import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.item.canonical.turret.TBasicTurret;
import dev.iwilkey.terrafort.obj.TObject;
import dev.iwilkey.terrafort.obj.entity.mob.TBandit;
import dev.iwilkey.terrafort.obj.entity.mob.TMob;
import dev.iwilkey.terrafort.obj.particulate.TParticle;
import dev.iwilkey.terrafort.obj.particulate.TTurretProjectile;
import dev.iwilkey.terrafort.obj.world.TTerrain;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * Any building tile that acts as a turret.
 * @author Ian Wilkey (iwilkey)
 */
public final class TTurretTile extends TBuildingTile {
	
	public static final Color LOCKED_ON_COLOR = new Color().set(0xff0000ff);
	public static final Color SEARCHING_COLOR = new Color().set(0xffffffff);
	
	private final RayCastCallback cb = new RayCastCallback() {
	    @Override
	    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
	    	// turret only aims for mobs :)
	    	if(!((TObject)fixture.getBody().getUserData() instanceof TBandit))
	    		return -1;
	    	target = (TMob)fixture.getBody().getUserData();
	        return 0;
	    }
	};
	private final Vector2 origin;
	private final Vector2 magnitude;
	private final Vector2 barrel;
	private final TCircle rangeIndication;

	private TMob    target;
	private long    roundValue;
	private float   range;
	private float   sweepSpeed;
	private float   fireRate;
	private float   ftime;
	private boolean lockedOn;

	public TTurretTile(TWorld world, TItem item, int tileX, int tileY, int maxHP) {
		super(world, item, tileX, tileY, TTerrain.TILE_WIDTH / 2, TTerrain.TILE_HEIGHT / 2, maxHP);
		switch(item) {
			case BASIC_TURRET:
				range      = TBasicTurret.RANGE;
				sweepSpeed = TBasicTurret.SWEEP_SPEED;
				fireRate   = TBasicTurret.FIRE_RATE;
				roundValue = TBasicTurret.ROUND_VALUE;
				break;
			default: throw new IllegalArgumentException("You cannot give a " + item.is().getName() + " tile turret properties!");
		}
		ftime             = 0.0f;
		lockedOn          = false;
		rotationInRadians = 0;
		origin            = new Vector2(getActualX(), getActualY());
		magnitude         = origin.cpy().add(range, range);
		barrel            = new Vector2(0, 0);
		// rendLine          = new TLine();
		rangeIndication = new TCircle((int)origin.x, (int)origin.y, range);
		rangeIndication.setColor(new Color().set(0xffffff33));
		rangeIndication.setFilled(true);
		setManualRotation();
	}
	
	@Override
	public void task(float dt) {
		super.task(dt);
		// make sure the barrel matches the graphical representation...
		barrel.set(magnitude.cpy().rotateAroundRad(origin, rotationInRadians + ((float)Math.PI / 4f)));
		if(!lockedOn) {
			rotationInRadians += TClock.dt() * sweepSpeed;
			target = null;
			world.getPhysicalWorld().rayCast(cb, origin, barrel);
			if(target != null) {
				// we found something! Lock on.
				ftime = fireRate;
				lockedOn = true;
			}
		} else {
			final float targetPositionX = target.getActualX();
	        final float targetPositionY = target.getActualY();
	        final float turretPositionX = getActualX();
	        final float turretPositionY = getActualY();
	        final float angleToTarget   = (float)Math.atan2(targetPositionY - turretPositionY, targetPositionX - turretPositionX);
	        rotationInRadians           = angleToTarget - ((float)Math.PI / 2);
	        final float x2              = (turretPositionX - targetPositionX) * (turretPositionX - targetPositionX);
	        final float y2              = (turretPositionY - targetPositionY) * (turretPositionY - targetPositionY);
	        final float dist            = (float)Math.sqrt(x2 + y2);
	        if(dist <= range * 1.5f) {
	        	// stop, stop! he's already dead!
	        	if(!target.isAlive()) {
		        	target = null;
		            lockedOn = false;
		        }
	            // Aimed at the mob, ready to shoot
	        	ftime += TClock.dt();
	        	if(ftime >= fireRate) {
	        		// fire!
	        		long current = world.getPlayer().getNetWorth();
	        		if(current - roundValue >= 0) {
	        			// fire!
		        		world.getPlayer().takeCurrency(roundValue);
	        			final float dx = (float) Math.cos(rotationInRadians + ((float)Math.PI / 2f));
		        	    final float dy = (float) Math.sin(rotationInRadians + ((float)Math.PI / 2f));
		        		world.addObject(new TTurretProjectile(world, origin.x, origin.y, dx, dy));
		        		world.addObject(new TParticle(world, getActualX(), getActualY(), Color.GRAY));
		        		setRenderTint(Color.WHITE);
	        		} else {
	        			// blink it red because there's not enough money to shoot!
	        			setRenderTint(Color.RED);
	        		}
	        		ftime = 0.0f;
	        	}	
	        	
	        } else {
	            target = null;
	            lockedOn = false;
	        }
		}
		TGraphics.draw(rangeIndication, false);
	}

	@Override
	public void onInteraction(TMob interactee) {
		hurt(1);
	}
	
	@Override
	public void die() {
		super.die();
	}

}
