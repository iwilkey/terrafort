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
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * A {@link TRenderableSprite} with a physical presence; reacts with and (if desired) blocks light, 
 * interacts with physical force and collisions.
 * @author Ian Wilkey (iwilkey)
 */
public class TObject implements TRenderableSprite {
	
	public boolean                 shouldDraw = true;
	
	protected final TWorld         world;
	protected final Body           body;
	protected final Array<TObject> collisionManifold;
	protected float                x;
	protected float                y;
	protected float                colliderOffX;
	protected float                colliderOffY;
	protected int                  z; // z buffer render order.
	protected float                width;
	protected float                height;
	protected float                rotationInRadians;
	protected int                  dataOffsetX;
	protected int                  dataOffsetY;
	protected int                  dataSelectionSquareWidth;
	protected int                  dataSelectionSquareHeight;
	protected Color                renderTint;
	
	public TObject(TWorld   world, 
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
		final BodyDef bodyDef          = new BodyDef();
		final PolygonShape shape       = new PolygonShape();
		final FixtureDef fixtureDef    = new FixtureDef();
		collisionManifold              = new Array<>();
		this.world                     = world;
		this.x                         = x;
		this.y                         = y;
		this.width                     = width;
		this.height                    = height;
		this.z                         = z;
		this.dataOffsetX               = dataOffsetX;
		this.dataOffsetY               = dataOffsetY;
		this.dataSelectionSquareWidth  = dataSelectionSquareWidth;
		this.dataSelectionSquareHeight = dataSelectionSquareHeight;
		this.renderTint                = renderTint;
		rotationInRadians              = 0.0f;
		colliderOffX                   = 0.0f;
		colliderOffY                   = 0.0f;
		bodyDef.position.set(x, y);
		bodyDef.type = isDynamic ? BodyDef.BodyType.DynamicBody : BodyDef.BodyType.StaticBody;
		body = world.getPhysicalWorld().createBody(bodyDef);
		body.setUserData(this);
		shape.setAsBox(colliderWidth, colliderHeight);
		fixtureDef.shape = shape;
		body.createFixture(fixtureDef);
		shape.dispose();
	}
	
	/**
	 * Syncs the object's physical translation with the graphical representation.
	 */
	public void sync() {
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
	    body.setLinearVelocity(new Vector2(velx, vely));
	}
	
	/**
	 * Applies a constant force to the center of the object.
	 * @param forceX the force in the x direction.
	 * @param forceY the force in the y direction.
	 */
	public final void applyForce(float forceX, float forceY) {
	    body.applyForceToCenter(forceX, forceY, true);
	}
	
	/**
	 * Applies an impulse force to the center of the object.
	 * @param impulseX the impulse force in the x direction.
	 * @param impulseY the impulse force in the y direction.
	 */
	public final void applyImpulse(float impulseX, float impulseY) {
	    body.applyLinearImpulse(impulseX, impulseY, body.getWorldCenter().x, body.getWorldCenter().y, true);
	}
	
	/**
	 * Called by the {@link TCollisionManifold} module. Never call this yourself.
	 */
	public final void addToCollisionManifold(TObject o) {
		collisionManifold.add(o);
	}
	
	/**
	 * Called by the {@link TCollisionManifold} module. Never call this yourself.
	 */
	public final void removeFromCollisionManifold(TObject o) {
		collisionManifold.removeValue(o, false);
	}
	
	/**
	 * Returns all the {@link TObject} that are currently colliding with (touching) this {@link TObject}.
	 */
	public final Array<TObject> getCollisionManifold() {
		return collisionManifold;
	}
	
	/**
	 * Set the offset value to be applied to the graphical representation of the body. This does not
	 * change any properties about the physical representation.
	 * @param x the offset in the x direction.
	 * @param y the offset in the y direction.
	 */
	public final void setGraphicsColliderOffset(float x, float y) {
		colliderOffX = x;
		colliderOffY = y;
	}
	
	/**
	 * Marks the {@link TObject} as a sensor. This means it will not react physically with
	 * the colliding body, but will still get added to the collision manifold.
	 */
	public final void setAsSensor() {
		getPhysicalFixture().setSensor(true);
	}
	
	/**
	 * Marks the {@link TObject} as a physical. This means during collisions,
	 * it will be added to the collision manifold and react physically with the colliding
	 * body.
	 */
	public final void setAsPhysical() {
		getPhysicalFixture().setSensor(false);
	}
	
	public final void setRenderTint(Color color) {
		this.renderTint.set(color);
	}
	
	public final Body getPhysicalBody() {
		return body;
	}
	
	public final Fixture getPhysicalFixture() {
		return body.getFixtureList().get(0);
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
