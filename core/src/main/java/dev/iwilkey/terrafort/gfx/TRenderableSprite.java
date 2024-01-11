package dev.iwilkey.terrafort.gfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import dev.iwilkey.terrafort.world.TWorld;

/**
 * A segment of GL texture data retrieved from a registered {@link TSpriteSheet} that is rendered on the screen at a specific location, rotation, and depth.
 * @author Ian Wilkey (iwilkey)
 */
public interface TRenderableSprite {
	
	public static final float CULL_PADDING = TWorld.TILE_SIZE;
	
	/**
	 * A path to the registered sprite sheet (TGraphics.mAllocSpriteSheet(path)) that will be referenced when rendering this sprite.
	 */
	String getSpriteSheet();
	
	float getX();
    float getY();
    
    /**
	 * Returns the object's actual graphical world x value, without the consideration of the collider offset.
	 */
    float getGraphicalX();
    
    /**
	 * Returns the object's actual graphical world y value, without the consideration of the collider offset.
	 */
    float getGraphicalY();
    
    float getWidth();
    float getHeight();
    float getRotationInRadians();
    
    int getDataSelectionOffsetX();
    int getDataSelectionOffsetY();
    int getDataSelectionSquareWidth();
    int getDataSelectionSquareHeight();
    
    Color getRenderTint();
    
    /**
     * z buffer value; 0 is rendered last (top), > 0 rendered first (bottom).
     */
    public int getDepth();
    
    /**
     * Whether or not to use the world projection matrix. If not, the sprite will be rendered in screen-space.
     */
    default boolean useWorldProjectionMatrix() {
    	return true;
    }
    
    /**
     * Determines whether or not a sprite should be rendered with additive blending. More specifically, this will
     * set the OpenGL blending mode to (GL_SRC_ALPHA, GL_ONE) during the sprite render time.
     */
    default boolean shouldUseAdditiveBlending() {
    	return false;
    }
	
    /**
     * Determines whether the renderable should be culled based on the camera's viewport.
     * @param camera The {@link OrthographicCamera} viewing the scene.
     * @return true if the tile should be culled, false otherwise.
     */
	default boolean shouldCull(final OrthographicCamera camera) {
		final float   rotation          = getRotationInRadians();
	    final float   cos               = Math.abs((float)Math.cos(rotation));
	    final float   sin               = Math.abs((float)Math.sin(rotation));
	    final float   halfWidth         = getWidth() / 2;
	    final float   halfHeight        = getHeight() / 2;
	    final float   rotatedHalfWidth  = halfWidth * cos + halfHeight * sin;
	    final float   rotatedHalfHeight = halfHeight * cos + halfWidth * sin;
	    final float   centerX           = getX();
	    final float   centerY           = getY();
	    final float   aabbLeft          = centerX - rotatedHalfWidth;
	    final float   aabbRight         = centerX + rotatedHalfWidth;
	    final float   aabbBottom        = centerY - rotatedHalfHeight;
	    final float   aabbTop           = centerY + rotatedHalfHeight;
	    final float   camLeft           = camera.position.x - (camera.viewportWidth / 2 * camera.zoom) - CULL_PADDING;
	    final float   camRight          = camera.position.x + (camera.viewportWidth / 2 * camera.zoom) + CULL_PADDING;
	    final float   camBottom         = camera.position.y - (camera.viewportHeight / 2 * camera.zoom) - CULL_PADDING;
	    final float   camTop            = camera.position.y + (camera.viewportHeight / 2 * camera.zoom) + CULL_PADDING;
	    final boolean outLeft           = aabbRight < camLeft;
	    final boolean outRight          = aabbLeft > camRight;
	    final boolean outBottom         = aabbTop < camBottom;
	    final boolean outTop            = aabbBottom > camTop;
	    return outLeft || outRight || outBottom || outTop;
	}
	
	/**
     * Renders the object using the provided camera and batch.
     * @param camera The {@link OrthographicCamera} used to view and render the scene.
     * @param batch The {@link SpriteBatch} used for rendering.
     */
	default public void render(final OrthographicCamera camera, final SpriteBatch batch) {
		if(!batch.getColor().equals(getRenderTint()))
			batch.setColor(getRenderTint());
        float originX         = getWidth() / 2;
        float originY         = getHeight() / 2;
        float rotationDegrees = (float)Math.toDegrees(getRotationInRadians());
        if(shouldUseAdditiveBlending())
        	batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
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
        if(shouldUseAdditiveBlending())
        	batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        if (!batch.getColor().equals(Color.WHITE))
            batch.setColor(Color.WHITE);
	}
	
}
