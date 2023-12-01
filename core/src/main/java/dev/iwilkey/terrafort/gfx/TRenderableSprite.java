package dev.iwilkey.terrafort.gfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * A segment of 2D color data retrieved from the master SpriteSheet to render on the screen at a specific location, rotation, and depth.
 * @author Ian Wilkey (iwilkey)
 */
public interface TRenderableSprite {
	
	float getRenderX();
    float getRenderY();
    float getRenderWidth();
    float getRenderHeight();
    float getRotationInRadians();
    /**
     * Depth value; 0 is top, < 0 bottom.
     * @return the depth value of this TRenderable.
     */
    public int getDepth();

    int getDataSelectionOffsetX();
    int getDataSelectionOffsetY();
    int getDataSelectionSquareWidth();
    int getDataSelectionSquareHeight();

    Color getRenderTint();
    
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
		final float rotation          = getRotationInRadians();
	    final float cos               = Math.abs((float)Math.cos(rotation));
	    final float sin               = Math.abs((float)Math.sin(rotation));
	    final float halfWidth         = getRenderWidth() / 2;
	    final float halfHeight        = getRenderHeight() / 2;
	    final float rotatedHalfWidth  = halfWidth * cos + halfHeight * sin;
	    final float rotatedHalfHeight = halfHeight * cos + halfWidth * sin;
	    final float centerX           = getRenderX();
	    final float centerY           = getRenderY();
	    final float aabbLeft          = centerX - rotatedHalfWidth;
	    final float aabbRight         = centerX + rotatedHalfWidth;
	    final float aabbBottom        = centerY - rotatedHalfHeight;
	    final float aabbTop           = centerY + rotatedHalfHeight;
	    final float camLeft           = camera.position.x - camera.viewportWidth / 2 * camera.zoom;
	    final float camRight          = camera.position.x + camera.viewportWidth / 2 * camera.zoom;
	    final float camBottom         = camera.position.y - camera.viewportHeight / 2 * camera.zoom;
	    final float camTop            = camera.position.y + camera.viewportHeight / 2 * camera.zoom;
	    final boolean outLeft         = aabbRight < camLeft;
	    final boolean outRight        = aabbLeft > camRight;
	    final boolean outBottom       = aabbTop < camBottom;
	    final boolean outTop          = aabbBottom > camTop;
	    return outLeft || outRight || outBottom || outTop;
	}
	
	/**
     * Renders the object using the provided camera and batch.
     * @param camera The {@link OrthographicCamera} used to view and render the scene.
     * @param batch The {@link SpriteBatch} used for rendering.
     */
	default public void render(final OrthographicCamera camera, final SpriteBatch batch) {
		if (shouldCull(camera))
            return;
        if (!batch.getColor().equals(getRenderTint()))
            batch.setColor(getRenderTint());
        float originX         = getRenderWidth() / 2;
        float originY         = getRenderHeight() / 2;
        float rotationDegrees = (float)Math.toDegrees(getRotationInRadians());
        if(shouldUseAdditiveBlending())
        	batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        batch.draw(
            TGraphics.DATA, 
            getRenderX() - originX, getRenderY() - originY, 
            originX, originY, 
            getRenderWidth(), getRenderHeight(), 
            1, 1, 
            rotationDegrees,
            getDataSelectionOffsetX() * TGraphics.DATA_WIDTH, 
            getDataSelectionOffsetY() * TGraphics.DATA_HEIGHT,
            getDataSelectionSquareWidth() * TGraphics.DATA_WIDTH, 
            getDataSelectionSquareHeight() * TGraphics.DATA_HEIGHT, 
            false, false
        );
        if(shouldUseAdditiveBlending())
        	batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        if (!batch.getColor().equals(Color.WHITE))
            batch.setColor(Color.WHITE);
	}
	
}
