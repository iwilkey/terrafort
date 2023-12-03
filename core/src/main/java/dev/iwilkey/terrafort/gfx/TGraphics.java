package dev.iwilkey.terrafort.gfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.BloomEffect;
import com.crashinvaders.vfx.effects.FxaaEffect;
import com.crashinvaders.vfx.effects.GaussianBlurEffect;

import dev.iwilkey.terrafort.TClock;
import dev.iwilkey.terrafort.math.TInterpolator;
import dev.iwilkey.terrafort.math.TMath;


/**
 * The TGraphics class is the central rendering module for the Terrafort game engine.
 * It handles the rendering of all in-game objects and manages the game's camera.
 * This class follows a static API design, allowing it to be accessed from anywhere within the engine.
 * @author Ian Wilkey (iwilkey)
 */
public final class TGraphics implements Disposable {
	
	public static final int                       MAX_RENDERABLES       = 8191;
	public static final int                       DATA_WIDTH            = 16;
	public static final int                       DATA_HEIGHT           = 16;
	public static final float                     PIXELS_PER_METER      = 1f;
	
	public static final Texture                   DATA                  = new Texture(Gdx.files.internal("dat.png"));
	public static final OrthographicCamera        CAMERA                = new OrthographicCamera();
	private static final TInterpolator            CAMERA_X              = new TInterpolator(0);
	private static final TInterpolator            CAMERA_Y              = new TInterpolator(0);
	private static final TInterpolator            CAMERA_ZOOM           = new TInterpolator(1);
	private static final Array<TRenderableSprite> OBJECT_RENDERABLES    = new Array<>();
	private static final Array<TRenderableSprite> TILE_RENDERABLES      = new Array<>();
	private static final Array<TRenderableShape>  OL_GEO_RENDERABLES    = new Array<>();
	private static final Array<TRenderableShape>  TL_GEO_RENDERABLES    = new Array<>();
	private static final SpriteBatch              OBJECT_BATCH          = new SpriteBatch(MAX_RENDERABLES);
	private static final Array<SpriteBatch>       TILE_BATCH_POOL       = new Array<>();
	private static final ShapeRenderer            GEOMETRIC_RENDERER    = new ShapeRenderer();
	
	private static final VfxManager               POST_PROCESSING       = new VfxManager(Pixmap.Format.RGBA8888);
	public static final FxaaEffect                POST_FXAA             = new FxaaEffect();
	public static final BloomEffect               POST_BLOOM            = new BloomEffect();
	public static final GaussianBlurEffect        POST_GAUSSIAN_BLUR    = new GaussianBlurEffect();
	
	public TGraphics() {
		
		DATA.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		
		CAMERA.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		CAMERA_X.setEquation(Interpolation.linear);
		CAMERA_Y.setEquation(Interpolation.linear);
		CAMERA_ZOOM.setEquation(Interpolation.linear);
		
		POST_PROCESSING.addEffect(POST_FXAA);
		POST_PROCESSING.addEffect(POST_BLOOM);
		// POST_PROCESSING.addEffect(POST_GAUSSIAN_BLUR);
		
		CAMERA_ZOOM.set((float)Math.pow(2, currentZoomTwoFactor));
	}
	
	///////////////////////////////////////////////////////
	// BEGIN API
	///////////////////////////////////////////////////////
	
	/**
	 * Adds a {@link TRenderableShape} to the geometric render queue.
	 * @param renderable the {@link TRenderableShape} to be rendered.
	 * @param objectLevel whether or not the shape should be drawn before (false) or after (true) object renderables.
	 */
	public static void draw(final TRenderableShape renderable, boolean objectLevel) {
		if(objectLevel) OL_GEO_RENDERABLES.add(renderable);
		else TL_GEO_RENDERABLES.add(renderable);
	}
	
	/**
     * Adds a {@link TRenderableSprite} object to the sprite render queue.
     * If the queue exceeds MAX_RENDERABLES, the oldest renderable is removed.
     * @param renderable The {@link TRenderableSprite} object to be rendered.
     */
	public static void draw(final TRenderableSprite renderable) {
		if(OBJECT_RENDERABLES.size + 1 > MAX_RENDERABLES)
			OBJECT_RENDERABLES.removeIndex(0);
		OBJECT_RENDERABLES.add(renderable);
	}
	
	/**
	 * Draw a {@link TFrame} at given location with given dimensions.
	 * @param frame the Sprite Sheet frame.
	 * @param x the world location x.
	 * @param y the world location y.
	 * @param width the world width.
	 * @param height the world height.
	 */
	public static void draw(final TFrame frame, int x, int y, int z, int width, int height) {
		TILE_RENDERABLES.add(new TRenderableSprite() {
			@Override
			public float getRenderX() {
				return x;
			}
			@Override
			public float getRenderY() {
				return y;
			}
			@Override
			public float getRenderWidth() {
				return width;
			}
			@Override
			public float getRenderHeight() {
				return height;
			}
			@Override
			public float getRotationInRadians() {
				return 0;
			}
			@Override
			public int getDepth() {
				return z;
			}
			@Override
			public int getDataSelectionOffsetX() {
				return frame.getDataOffsetX();
			}
			@Override
			public int getDataSelectionOffsetY() {
				return frame.getDataOffsetY();
			}
			@Override
			public int getDataSelectionSquareWidth() {
				return frame.getDataSelectionWidth();
			}
			@Override
			public int getDataSelectionSquareHeight() {
				return frame.getDataSelectionHeight();
			}
			@Override
			public Color getRenderTint() {
				return Color.WHITE.cpy();
			}
		});
	}
	
	/**
     * Sets the target position for the camera to smoothly transition to.
     * @param x The target X coordinate.
     * @param y The target Y coordinate.
     */
	public static void setCameraTargetPosition(float x, float y) {
		CAMERA_X.set(x);
		CAMERA_Y.set(y);
	}
	
	/**
     * Immediately sets the camera's position to the specified coordinates.
     * @param x The X coordinate to set.
     * @param y The Y coordinate to set.
     */
	public static void forceCameraPosition(float x, float y) {
		CAMERA_X.force(x);
		CAMERA_Y.force(y);
	}
	
	/**
     * Sets the speed of the camera's movement towards its target position and zoom.
     * @param speed The speed of the camera movement.
     */
	public static void setCameraSpeedToTarget(float speed) {
		CAMERA_X.setSpeed(speed);
		CAMERA_Y.setSpeed(speed);
		CAMERA_ZOOM.setSpeed(speed);
	}
	
	private static int currentZoomTwoFactor = -2;
	
	/**
     * Sets the target zoom level for the camera to smoothly transition to. Will be a power of two.
     * @param target The target zoom level.
     */
	public static void changeCameraZoom(boolean in) {
		int suggested = (!in) ? currentZoomTwoFactor + 1 : currentZoomTwoFactor - 1;
		currentZoomTwoFactor = Math.round(TMath.clamp(suggested, -3.0f, -1.0f));
		CAMERA_ZOOM.set((float)Math.pow(2, currentZoomTwoFactor));
	}
	
	/**
     * Immediately sets the camera's zoom level to the specified value.
     * @param zoom The zoom level to set.
     */
	public static void forceCameraZoom(float zoom) {
		CAMERA_ZOOM.force(zoom);
	}
	
	/**
	 * Set the OpenGL line width.
	 */
	public static void setGlLineWidth(float width) {
		Gdx.gl.glLineWidth(width);
	}
	
	///////////////////////////////////////////////////////
	// END API
	///////////////////////////////////////////////////////
	
	private void calculateTileBatchPool() {
		final int requestedTiles  = TILE_RENDERABLES.size;
		final int neededBatches   = (int)Math.ceil((float)requestedTiles / MAX_RENDERABLES);
		// if no batches are needed we know we can dispose of every available batch...
		if(neededBatches == 0) {
			for(final SpriteBatch batch : TILE_BATCH_POOL)
				batch.dispose();
			TILE_BATCH_POOL.clear();
			return;
		}
		final int currentPoolSize = TILE_BATCH_POOL.size;
		// we either need to allocate or deallocate memory here...
		if(currentPoolSize != neededBatches) {
			final int difference = neededBatches - currentPoolSize;
			if(difference < 0) {
				// destory only batches that will not be used next frame...
				int amt = Math.abs(difference);
				for(int i = currentPoolSize - 1; i > (currentPoolSize - 1) - amt; i--) {
					TILE_BATCH_POOL.get(i).dispose();
					TILE_BATCH_POOL.pop();
				}
			} else {
				// create new batches that are needed next frame...
				for(int i = 0; i < difference; i++)
					TILE_BATCH_POOL.add(new SpriteBatch(MAX_RENDERABLES));
			}
		}
	}
	
	/**
	 * Calculates the perspective of the camera based on the desired position and zoom. This method also minimizes the amount of visual artifacts that arise from 
	 * tile position rounding errors.
	 */
	private void calculatePerspective() {
		CAMERA_X.update();
	    CAMERA_Y.update();
	    CAMERA_ZOOM.update();
	    float z                            = CAMERA_ZOOM.getTarget();
	    float effectivePixelsPerUnit       = PIXELS_PER_METER / z;
	    float screenWidthInWorldUnits      = Gdx.graphics.getWidth() / effectivePixelsPerUnit;
	    float screenHeightInWorldUnits     = Gdx.graphics.getHeight() / effectivePixelsPerUnit;
	    float halfScreenWidthInWorldUnits  = screenWidthInWorldUnits / 2f;
	    float halfScreenHeightInWorldUnits = screenHeightInWorldUnits / 2f;
	    float x                            = CAMERA_X.get();
	    float y                            = CAMERA_Y.get();
	    float rx                           = TMath.roundTo(x + halfScreenWidthInWorldUnits, effectivePixelsPerUnit) - halfScreenWidthInWorldUnits;
	    float ry                           = TMath.roundTo(y + halfScreenHeightInWorldUnits, effectivePixelsPerUnit) - halfScreenHeightInWorldUnits;
	    CAMERA.position.set(rx, ry, 0);
	    CAMERA.zoom = z;
	    CAMERA.update();
	}
	
	/**
	 * Clears the screen, only OpenGL color buffer should be cleared.
	 */
	private void cls() {
		Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
	
	/**
	 * Sorts {@link TRenderableSprite}s by render Y value; objects with y values closer to the top of the 
	 * screen are rendered first.
	 */
	private void sortObjectRenderables() {
		TILE_RENDERABLES.sort((r1, r2) -> Integer.compare(r2.getDepth(), r1.getDepth()));
		OBJECT_RENDERABLES.sort((r1, r2) -> Float.compare(r2.getRenderY(), r1.getRenderY()));
		// OBJECT_RENDERABLES.sort((r1, r2) -> Integer.compare(r2.getDepth(), r1.getDepth()));
	}
	
	float t = 0.0f;
	
	/**
	 * Render process of the TGraphics module.
	 */
	public void render() {
		t += TClock.dt();
		if(t > 1.0f) {
			System.out.println("fps: " + (1 / TClock.dt()));
			t = 0;
		}
		calculateTileBatchPool();
		calculatePerspective();
		sortObjectRenderables();
		OBJECT_BATCH.setProjectionMatrix(CAMERA.combined);
		cls();
		POST_PROCESSING.cleanUpBuffers();
		POST_PROCESSING.beginInputCapture();
		// tile batch pooling for efficiency and "infinite" renderables allowed.
		if(TILE_BATCH_POOL.size >= 1) {
	        int batch         = 0;
	        int tileIDInBatch = 0;
	        TILE_BATCH_POOL.get(batch).setProjectionMatrix(CAMERA.combined);
	        TILE_BATCH_POOL.get(batch).begin();
	        for(final TRenderableSprite r : TILE_RENDERABLES) {
	        	if(tileIDInBatch >= MAX_RENDERABLES) {
	        		TILE_BATCH_POOL.get(batch).end();
	        		batch++;
	        		tileIDInBatch = 0;
	        		TILE_BATCH_POOL.get(batch).setProjectionMatrix(CAMERA.combined);
	        		TILE_BATCH_POOL.get(batch).begin();
	        	}
	        	r.render(CAMERA, TILE_BATCH_POOL.get(batch));
	        	tileIDInBatch++;
	        }
	        TILE_BATCH_POOL.get(batch).end();
		}
		// render tile level geometry
        GEOMETRIC_RENDERER.begin(ShapeRenderer.ShapeType.Filled);
        GEOMETRIC_RENDERER.setProjectionMatrix(CAMERA.combined);
        for(TRenderableShape s : TL_GEO_RENDERABLES)
        	s.drawFilled(CAMERA, GEOMETRIC_RENDERER);
        GEOMETRIC_RENDERER.end();
        GEOMETRIC_RENDERER.begin(ShapeRenderer.ShapeType.Line);
        GEOMETRIC_RENDERER.setProjectionMatrix(CAMERA.combined);
        for(TRenderableShape s : TL_GEO_RENDERABLES)
        	s.drawLined(CAMERA, GEOMETRIC_RENDERER);
        GEOMETRIC_RENDERER.end();
		// draw objects
        OBJECT_BATCH.begin();
        for(final TRenderableSprite r : OBJECT_RENDERABLES)
        	r.render(CAMERA, OBJECT_BATCH);
        OBJECT_BATCH.end();
        // render object level geometry
        GEOMETRIC_RENDERER.begin(ShapeRenderer.ShapeType.Filled);
        GEOMETRIC_RENDERER.setProjectionMatrix(CAMERA.combined);
        for(TRenderableShape s : OL_GEO_RENDERABLES)
        	s.drawFilled(CAMERA, GEOMETRIC_RENDERER);
        GEOMETRIC_RENDERER.end();
        GEOMETRIC_RENDERER.begin(ShapeRenderer.ShapeType.Line);
        GEOMETRIC_RENDERER.setProjectionMatrix(CAMERA.combined);
        for(TRenderableShape s : OL_GEO_RENDERABLES)
        	s.drawLined(CAMERA, GEOMETRIC_RENDERER);
        GEOMETRIC_RENDERER.end();
        POST_PROCESSING.endInputCapture();
        POST_PROCESSING.applyEffects();
        POST_PROCESSING.renderToScreen();
        flush();
	}
	
	/**
	 * Called when the screen is resized.
	 * @param newWidth the new width of the screen.
	 * @param newHeight the new height of the screen.
	 */
	public void resize(int newWidth, int newHeight) {
		CAMERA.setToOrtho(false, newWidth, newHeight);
		CAMERA.update();
		POST_PROCESSING.resize(newWidth, newHeight);
		GEOMETRIC_RENDERER.getProjectionMatrix().setToOrtho2D(0f, 0f, newWidth, newHeight);
		GEOMETRIC_RENDERER.updateMatrices();
    }
	
	/**
	 * Called when the engine would like to clear lingering memory in the graphics module. This is NOT the same as disposing!
	 */
	public static void gc() {
		flush();
		for(SpriteBatch batch : TILE_BATCH_POOL)
			batch.dispose();
		TILE_BATCH_POOL.clear();
	}

	@Override
	public void dispose() {
		gc();
		disposeGraphicsBatches();
		disposePostProcessing();
		DATA.dispose();
	}
	
	/**
	 * Flushes (clears) the render queues.
	 */
	private static void flush() {
		OL_GEO_RENDERABLES.clear();
		TL_GEO_RENDERABLES.clear();
	    OBJECT_RENDERABLES.clear();
	    TILE_RENDERABLES.clear();
	}
	
	private void disposeGraphicsBatches() {
		OBJECT_BATCH.dispose();
		GEOMETRIC_RENDERER.dispose();
	}
	
	private void disposePostProcessing() {
		POST_FXAA.dispose();
		POST_PROCESSING.dispose();
	}
	
}
