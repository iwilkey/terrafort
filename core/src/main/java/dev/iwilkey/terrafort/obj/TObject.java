package dev.iwilkey.terrafort.obj;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

import dev.iwilkey.terrafort.gfx.TRenderableSprite;
import dev.iwilkey.terrafort.math.TCollisionManifold;
import dev.iwilkey.terrafort.obj.world.TSinglePlayerWorld;

/**
 * A {@link TRenderableSprite} with a physical presence; reacts with and (if desired) blocks light, 
 * interacts with physical force and collisions.
 * @author Ian Wilkey (iwilkey)
 */
public abstract class TObject implements TRenderableSprite {

	public boolean                     shouldDraw = true;

	protected final TSinglePlayerWorld world;
	protected final Array<TObject>     collisionManifold;
	
	protected Body                     body;
	protected float                    x;
	protected float                    y;
	protected float                    colliderOffX;
	protected float                    colliderOffY;
	protected int                      z; // z buffer render order.
	protected float                    width;
	protected float                    height;
	protected float                    colliderWidth;
	protected float                    colliderHeight;
	protected float                    rotationInRadians;
	protected int                      dataOffsetX;
	protected int                      dataOffsetY;
	protected int                      dataSelectionSquareWidth;
	protected int                      dataSelectionSquareHeight;
	protected Color                    renderTint;
	
	private   boolean                  isDynamic;
	private   boolean                  isSensor;
	private   boolean                  enabled;
	
	public TObject(TSinglePlayerWorld   world, 
				   boolean isDynamic, 
				   float   x,
				   float   y, 
				   int     z,
				   float   width, 
				   float   height,
				   float   colliderWidth,
				   float   colliderHeight,
				   int     dataOffsetX, 
				   int     dataOffsetY,
				   int     dataSelectionSquareWidth, 
				   int     dataSelectionSquareHeight,
				   Color   renderTint) {
		enabled                        = true;
		isSensor                       = false;
		collisionManifold              = new Array<>();
		this.world                     = world;
		this.x                         = x;
		this.y                         = y;
		this.width                     = width;
		this.height                    = height;
		this.colliderWidth             = colliderWidth;
		this.colliderHeight            = colliderHeight;
		this.z                         = z;
		this.dataOffsetX               = dataOffsetX;
		this.dataOffsetY               = dataOffsetY;
		this.dataSelectionSquareWidth  = dataSelectionSquareWidth;
		this.dataSelectionSquareHeight = dataSelectionSquareHeight;
		this.renderTint                = renderTint;
		this.isDynamic                 = isDynamic;
		rotationInRadians              = 0.0f;
		colliderOffX                   = 0.0f;
		colliderOffY                   = 0.0f;
		construct();
	}

	/**
	 * Constructs the physical portion of the {@link TObject}.
	 */
	private void construct() {
		final BodyDef bodyDef          = new BodyDef();
		final PolygonShape shape       = new PolygonShape();
		final FixtureDef fixtureDef    = new FixtureDef();
		fixtureDef.shape               = shape;
		bodyDef.type                   = isDynamic ? BodyDef.BodyType.DynamicBody : BodyDef.BodyType.StaticBody;
		fixtureDef.shape               = shape;
		bodyDef.position.set(x, y);
		body                           = world.getPhysicalWorld().createBody(bodyDef);
		body.setUserData(this);
		shape.setAsBox(colliderWidth, colliderHeight);
		body.createFixture(fixtureDef);
		shape.dispose();
		getPhysicalFixture().setSensor(isSensor);
	}
	
	/**
	 * Removes unnecessary overhead when object is not currently needed. maximize() should be called to make it useful again.
	 */
	public final void minimize() {
		shouldDraw = false;
		enabled    = false;
		if(body != null) {
			world.getPhysicalWorld().destroyBody(body);
			body = null;
		}
		collisionManifold.clear();
	}
	
	public final void maximize() {
		shouldDraw = true;
		enabled    = true;
		construct();
	}
	
	/**
	 * Syncs the object's physical translation with the graphical representation.
	 */
	public void sync() {
		if(!enabled)
			return;
		x                 = body.getPosition().x;
		y                 = body.getPosition().y;
		rotationInRadians = (float)body.getAngle();
	}
	
	/**
	 * Sets the objects velocity as the given constants.
	 * @param dx the velocity in the x direction.
	 * @param dy the velocity in the y direction.
	 */
	public final void setVelocity(float velx, float vely) {
		if(!enabled)
			return;
	    body.setLinearVelocity(new Vector2(velx, vely));
	}
	
	/**
	 * Applies a constant force to the center of the object.
	 * @param forceX the force in the x direction.
	 * @param forceY the force in the y direction.
	 */
	public final void applyForce(float forceX, float forceY) {
		if(!enabled)
			return;
	    body.applyForceToCenter(forceX, forceY, true);
	}
	
	/**
	 * Applies an impulse force to the center of the object.
	 * @param impulseX the impulse force in the x direction.
	 * @param impulseY the impulse force in the y direction.
	 */
	public final void applyImpulse(float impulseX, float impulseY) {
		if(!enabled)
			return;
	    body.applyLinearImpulse(impulseX, impulseY, body.getWorldCenter().x, body.getWorldCenter().y, true);
	}
	
	/**
	 * Called by the {@link TCollisionManifold} module. Never call this yourself.
	 */
	public final void addToCollisionManifold(TObject o) {
		if(!enabled)
			return;
		collisionManifold.add(o);
	}
	
	/**
	 * Called by the {@link TCollisionManifold} module. Never call this yourself.
	 */
	public final void removeFromCollisionManifold(TObject o) {
		if(!enabled)
			return;
		collisionManifold.removeValue(o, false);
	}
	
	/**
	 * Returns all the {@link TObject} that are currently colliding with (touching) this {@link TObject}.
	 */
	public final Array<TObject> getCollisionManifold() {
		if(!enabled)
			return null;
		return collisionManifold;
	}
	
	/**
	 * Set the offset value to be applied to the graphical representation of the body. This does not
	 * change any properties about the physical representation.
	 * @param x the offset in the x direction.
	 * @param y the offset in the y direction.
	 */
	public final void setGraphicsColliderOffset(float x, float y) {
		if(!enabled)
			return;
		colliderOffX = x;
		colliderOffY = y;
	}
	
	/**
	 * Marks the {@link TObject} as a sensor. This means it will not react physically with
	 * the colliding body, but will still get added to the collision manifold.
	 */
	public final void setAsSensor() {
		if(!enabled)
			return;
		isSensor = true;
		getPhysicalFixture().setSensor(true);
	}
	
	/**
	 * Marks the {@link TObject} as a physical. This means during collisions,
	 * it will be added to the collision manifold and react physically with the colliding
	 * body.
	 */
	public final void setAsPhysical() {
		if(!enabled)
			return;
		isSensor = false;
		getPhysicalFixture().setSensor(false);
	}
	
	public final void setRenderTint(Color color) {
		if(!enabled)
			return;
		this.renderTint.set(color);
	}
	
	public final Body getPhysicalBody() {
		if(!enabled)
			return null;
		return body;
	}
	
	public final Fixture getPhysicalFixture() {
		if(!enabled)
			return null;
		return body.getFixtureList().get(0);
	}
	
	public final boolean isEnabled() {
		return enabled;
	}
	
	public final TSinglePlayerWorld getWorld() {
		return world;
	}
	
	@Override
	public final float getActualX() {
		return x;
	}
	
	@Override
	public final float getActualY() {
		return y;
	}

	@Override
	public final float getRenderX() {
		return x + colliderOffX;
	}

	@Override
	public final float getRenderY() {
		return y + colliderOffY;
	}

	@Override
	public final float getRenderWidth() {
		return width;
	}

	@Override
	public final float getRenderHeight() {
		return height;
	}
	
	@Override
	public final float getRotationInRadians() {
		return rotationInRadians;
	}
	
	@Override
	public final int getDepth() {
		return z;
	}

	@Override
	public final int getDataSelectionOffsetX() {
		return dataOffsetX;
	}

	@Override
	public final int getDataSelectionOffsetY() {
		return dataOffsetY;
	}

	@Override
	public final int getDataSelectionSquareWidth() {
		return dataSelectionSquareWidth;
	}

	@Override
	public final int getDataSelectionSquareHeight() {
		return dataSelectionSquareHeight;
	}

	@Override
	public final Color getRenderTint() {
		return renderTint;
	}

}
