package dev.iwilkey.terrafort.obj.runtime;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.gfx.TRenderableSprite;
import dev.iwilkey.terrafort.obj.type.TMob;
import dev.iwilkey.terrafort.obj.type.TObject;
import dev.iwilkey.terrafort.obj.type.TParticulate;
import dev.iwilkey.terrafort.world.TWorld;

/**
 * A concrete {@link TObject} that exists in the game world. It simulates specific object behavior.
 * @author Ian Wilkey (iwilkey)
 */
public final class TObjectRuntime implements TRenderableSprite {
	
	/**
	 * Memory reserved for the last sensed {@link TObject} from the ray(...) method.
	 */
	private static TObject lastRayResult = null;
	
	/**
	 * Instruction for the ray(...) raycasting procedure.
	 */
	private static final RayCastCallback RAY_CALLBACK = new RayCastCallback() {
	    @Override
	    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
	    	if(((TObjectRuntime)fixture.getBody().getUserData()).getAbstract() instanceof TParticulate)
	    		return -1;
	    	lastRayResult = ((TObjectRuntime)fixture.getBody().getUserData()).getAbstract();
	        return 0;
	    }
	};

	private TObject abstractState = null;
	private TWorld  world         = null;
	private Body    jniBody       = null;
	private Color   renderTint    = null;
	
	/**
	 * Creates a physical object, ready to simulate specific behavior while active in the game world.
	 */
	public TObjectRuntime(TWorld world, TObject abstractState) {
		this.world         = world;
		this.abstractState = abstractState;
		renderTint         = new Color().set(abstractState.tint);
		construct(world);
	}
	
	/**
	 * Constructs the physical body of the object, simulated by the physics engine.
	 */
	private void construct(TWorld world) {
		final BodyDef      bodyDef     = new BodyDef();
		final PolygonShape shape       = new PolygonShape();
		final FixtureDef   fixtureDef  = new FixtureDef();
		fixtureDef.shape               = shape;
		bodyDef.type                   = abstractState.isDynamic ? BodyDef.BodyType.DynamicBody : BodyDef.BodyType.StaticBody;
		fixtureDef.shape               = shape;
		bodyDef.position.set(abstractState.worldX, abstractState.worldY);
		jniBody                        = world.getPhysicalWorld().createBody(bodyDef);
		jniBody.setUserData(this);
		shape.setAsBox(abstractState.colliderWidth, abstractState.colliderHeight);
		jniBody.createFixture(fixtureDef);
		shape.dispose();
		jniBody.setTransform(abstractState.worldX, abstractState.worldY, abstractState.rotationRadians);
		jniBody.getFixtureList().get(0).setSensor(abstractState.isSensor);
		jniBody.getFixtureList().get(0).setDensity(abstractState.mass);
		jniBody.resetMassData();
		/*
		if(!isSensor) {
			getPhysicalFixture().getFilterData().categoryBits = TGraphics.BLOCKS_LIGHT;
			getPhysicalFixture().getFilterData().maskBits     = TGraphics.BLOCKS_LIGHT | TGraphics.LIGHT_PASSTHROUGH;
		} else {
			// sensors shouldn't block light.
			getPhysicalFixture().getFilterData().categoryBits = TGraphics.LIGHT_PASSTHROUGH;
			getPhysicalFixture().getFilterData().maskBits     = TGraphics.BLOCKS_LIGHT | TGraphics.LIGHT_PASSTHROUGH;
		}
		*/
	}
	
	/**
	 * Performs a simple raycast procedure with given ray. Returns a {@link TObject} if successful, null if none.
	 * Avoids returning itself as a ray result.
	 */
	public TObject ray(Vector2 origin, Vector2 to) {
		lastRayResult = null;
		getWorld().getPhysicalWorld().rayCast(RAY_CALLBACK, origin, to);
		// avoids returning self...
		if(lastRayResult != null) {
			if(lastRayResult == getAbstract())
				return null;
			return lastRayResult;
		}
		return null;
	}
	
	/**
	 * Called every frame that this implementation is active in the game world.
	 */
	public void tick(float dt) {
		abstractState.tick(this, dt);
	}
	
	/**
	 * Return the {@link TWorld} this object exists in.
	 */
	public final TWorld getWorld() {
		return world;
	}
	
	/**
	 * Returns the abstract state of the object.
	 * @return
	 */
	public final TObject getAbstract() {
		return abstractState;
	}
	
	/**
	 * Returns the physical body of the object.
	 */
	public final Body getPhysical() {
		return jniBody;
	}
	
	@Override
	public float getGraphicalX() {
		return abstractState.worldX;
	}
	
	@Override
	public float getGraphicalY() {
		return abstractState.worldY;
	}
	
	@Override
	public boolean shouldUseAdditiveBlending() {
		return abstractState.shouldUseAdditiveBlending;
	}

	@Override
	public float getX() {
		return Math.round(abstractState.worldX + abstractState.colliderOffX);
	}

	@Override
	public float getY() {
		return Math.round(abstractState.worldY + abstractState.colliderOffY);
	}

	@Override
	public float getWidth() {
		return abstractState.worldWidth;
	}

	@Override
	public float getHeight() {
		return abstractState.worldHeight;
	}

	@Override
	public float getRotationInRadians() {
		return abstractState.rotationRadians;
	}

	@Override
	public int getDataSelectionOffsetX() {
		return abstractState.dataX;
	}

	@Override
	public int getDataSelectionOffsetY() {
		return abstractState.dataY;
	}

	@Override
	public int getDataSelectionSquareWidth() {
		return abstractState.dataWidth;
	}

	@Override
	public int getDataSelectionSquareHeight() {
		return abstractState.dataHeight;
	}

	@Override
	public Color getRenderTint() {
		renderTint.set(abstractState.tint);
		return renderTint;
	}

	@Override
	public int getDepth() {
		return abstractState.depth;
	}

	@Override
	public String getSpriteSheet() {
		return abstractState.spriteSheet;
	}
	
	@Override
	public void render(final OrthographicCamera camera, final SpriteBatch batch) {
		// default rendering prodedure for non t-mobs.
		if(!batch.getColor().equals(getRenderTint()))
			batch.setColor(getRenderTint());
        float originX         = getWidth() / 2;
        float originY         = getHeight() / 2;
        float rotationDegrees = (float)Math.toDegrees(getRotationInRadians());
        if(shouldUseAdditiveBlending())
        	batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        // draw orig sprite...
        batch.draw(
            TGraphics.getSheetGLTex(getSpriteSheet()), 
            getX() - originX, 
            getY() - originY, 
            originX, 
            originY, 
            getWidth(), 
            getHeight(), 
            1, 
            1, 
            rotationDegrees,
            getDataSelectionOffsetX() * TGraphics.DATA_WIDTH, 
            getDataSelectionOffsetY() * TGraphics.DATA_HEIGHT,
            getDataSelectionSquareWidth() * TGraphics.DATA_WIDTH, 
            getDataSelectionSquareHeight() * TGraphics.DATA_HEIGHT, 
            false, 
            false
        );
        // TMob's have more to render...
        if(abstractState instanceof TMob) {
        	// draw accessories (clothing, hair, helmet, etc.) over original sprite.
        	final TMob    mob           = (TMob)abstractState;
        	final TFrame  clothingFrame = mob.getCurrentClothingFrame();
        	final Color   clothingColor = mob.getCurrentClothingColor();
        	// draw clothing...
        	if(!batch.getColor().equals(clothingColor))
    			batch.setColor(clothingColor);
        	batch.draw(
                TGraphics.getSheetGLTex(getSpriteSheet()), 
                getX() - originX, 
                getY() - originY,
                originX, 
                originY, 
                getWidth(), 
                getHeight(),
                1, 
                1, 
                rotationDegrees,
                clothingFrame.getDataOffsetX() * TGraphics.DATA_WIDTH, 
                clothingFrame.getDataOffsetY() * TGraphics.DATA_HEIGHT,
                getDataSelectionSquareWidth() * TGraphics.DATA_WIDTH, 
                getDataSelectionSquareHeight() * TGraphics.DATA_HEIGHT,
                false, 
                false
            );
        }
        if(shouldUseAdditiveBlending())
        	batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        if (!batch.getColor().equals(Color.WHITE))
            batch.setColor(Color.WHITE);
	}
}
