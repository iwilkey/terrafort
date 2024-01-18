package dev.iwilkey.terrafort.gfx;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.GaussianBlurEffect;
import com.crashinvaders.vfx.effects.RadialDistortionEffect;

import dev.iwilkey.terrafort.TClock;
import dev.iwilkey.terrafort.TInput;
import dev.iwilkey.terrafort.gui.TUserInterface;
import dev.iwilkey.terrafort.math.TInterpolator;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.persistent.TPersistent;

/**
 * The central rendering module for the Terrafort game engine. This module follows a static API design, 
 * allowing it to be accessed from anywhere within the engine.
 * @author Ian Wilkey (iwilkey)
 */
public final class TGraphics implements Disposable {

	
	/**
	 * NUMERICAL CONSTANTS
	 */
	
	public static final  int                           MAX_RENDERABLES        = 0x1fff;
	public static final  int                           DATA_WIDTH             = 0x10;
	public static final  int                           DATA_HEIGHT            = 0x10;
	public static final  float                         SCREENSHOT_TIME        = 1.0f;
	public static final  float                         FOCUS_BLINK_TIME       = 0.2f;
	
	/**
	 * DATA STRUCTURES FOR MANAGING INTERNAL DATA AND RENDER PERPSECTIVE
	 */
	
	public static final  HashMap<String, TSpriteSheet> SPRITE_SHEETS          = new HashMap<>();
	public static final  OrthographicCamera            WORLD_PROJ_MAT         = new OrthographicCamera();
	public static final  OrthographicCamera            SCREEN_PROJ_MAT        = new OrthographicCamera();
	public static final  TInterpolator                 CAMERA_X               = new TInterpolator(0);
	public static final  TInterpolator                 CAMERA_Y               = new TInterpolator(0);
	public static final  TInterpolator                 CAMERA_ZOOM            = new TInterpolator(1);
	
	/**
	 * DATA STRUCTURES FOR MANAGING RENDER REQUESTS AND OPENGL STATE
	 */
	
	private static final Array<TRenderableSprite>      SPRITE_REQUESTS        = new Array<>();
	private static final Array<TRenderableShape>       GEOMETRIC_REQUESTS     = new Array<>();
	private static final Array<TRenderable>            RENDER_REQUESTS        = new Array<>();
	private static final Array<SpriteBatch>            SPRITE_BATCH_POOL      = new Array<>();
	private static final SpriteBatch                   UI_BATCH               = new SpriteBatch();
	private static final ShapeRenderer                 GEOMETRIC_RENDERER     = new ShapeRenderer();
	private static final Box2DDebugRenderer            PHYSICS_RENDERER       = new Box2DDebugRenderer();
	
	/**
	 * DATA STRUCTURES TO MANAGE THE STATE OF POST-PROCESSING EFFECTS
	 */
	
	private static final VfxManager                    POST_PROC_BUFFER       = new VfxManager(Pixmap.Format.RGBA8888);
	private static final GaussianBlurEffect            POST_GAUSSIAN_BLUR     = new GaussianBlurEffect();
	private static final RadialDistortionEffect        POST_RADIAL_DIST       = new RadialDistortionEffect();
	
	/**
	 * USED FOR PHYSICAL DEBUGGING
	 */
	private static       World                         physicsDebugRender     = null;
	
	/**
	 * CURRENT ZOOM FACTOR FOR WORLD RENDERING
	 */
	private static       int                           currentZoomTwoFactor   = 0x1;
	
	/**
	 * INTERNAL VARIABLES TO MANAGE BLUR STATE
	 */
	
	private static       byte                          postFxBlurState        = 0x0;
	private static       int                           postFxBlurStatePointer = 0x0;
	private static       TInterpolator                 postFxBlurPasses       = new TInterpolator(0x1);
	
	/**
	 * INTERNAL VARIABLES TO MANAGE "CLICK TO FOCUS" STATE
	 */
	
	private static       boolean                       focusRendered          = true;
	private static       float                         focusTimer             = 0.0f;
	
	/**
	 * INTERNAL VARIABLES TO FACILLIATE THE PROCESS OF TAKING A SCREENSHOT
	 */
	
	private static       boolean                       takingShot             = false;
	private static       float                         screenshotTimer        = SCREENSHOT_TIME;
	
	/**
	 * DEFAULT CONFIGURATION TO BE CREATED AT RUNTIME.
	 */
	static {
		CAMERA_X.setEquation(Interpolation.linear);
		CAMERA_Y.setEquation(Interpolation.linear);
		CAMERA_ZOOM.setEquation(Interpolation.linear);
		CAMERA_ZOOM.setSpeed(2.0f);
		
	}
	
	/**
	 * Creates the graphics module by loading submitted Sprite Sheets into memory...
	 */
	public TGraphics() {
		mAllocSpriteSheet("sheets/natural.png");
		mAllocSpriteSheet("sheets/items-icons.png");
		mAllocSpriteSheet("sheets/mob.png");
	}
	
	///////////////////////////////////////////////////////
	// BEGIN API
	///////////////////////////////////////////////////////
	
	/**
	 * Allocates memory for a {@link TSpriteSheet} with given name and internal path. Name must be unique!
	 */
	public static void mAllocSpriteSheet(String internalPath) {
		// check if the sheet is already registered, ignore if so.
		if(SPRITE_SHEETS.containsKey(internalPath))
			return;
		SPRITE_SHEETS.put(internalPath, new TSpriteSheet(internalPath));
	}
	
	/**
	 * Returns a {@link TSpriteSheet} texture. Must be registered!
	 */
	public static Texture getSheetGLTex(String internalPath) {
		if(!SPRITE_SHEETS.containsKey(internalPath))
			throw new IllegalArgumentException("[Terrafort Game Engine] Trying to reference a sprite sheet that hasn't been registered for bliting: " + internalPath + ". Use TGraphics.mAllocSpriteSheet(path) to register a sheet.");
		return SPRITE_SHEETS.get(internalPath).get();
	}
		
	/**
     * Adds a {@link TRenderableSprite} object to the sprite render queue.
     */
	public static void draw(TRenderableSprite renderable) {
		// Do not add it to the render request queue if it shouldn't be rendered.
		if(renderable.shouldCull(WORLD_PROJ_MAT))
			return;
		SPRITE_REQUESTS.add(renderable);
	}
	
	/**
	 * Add a {@link TRenderableShape} to the render queue.
	 */
	public static void draw(TRenderableShape renderable) {
		GEOMETRIC_REQUESTS.add(renderable);
	}
	
	/**
	 * Draw a {@link TFrame} at given location with given dimensions. Basically, quickly creates a renderable sprite without having to declare a type.
	 */
	public static void draw(String sheet, TFrame frame, float x, float y, int z, float width, float height, Color tint) {
		draw(new TRenderableSprite() {
			@Override
			public String getSpriteSheet()              { return sheet;                          }
			@Override
			public float getX()                         { return x;                         	 }
			@Override
			public float getY()                         { return y; 							 }
			@Override
			public float getWidth()                     { return width; 						 }
			@Override
			public float getHeight()                    { return height; 						 }
			@Override
			public float getRotationInRadians()         { return 0; 							 }
			@Override
			public int   getDepth()                     { return z; 							 }
			@Override
			public int   getDataSelectionOffsetX()      { return frame.getDataOffsetX();         }
			@Override
			public int   getDataSelectionOffsetY()      { return frame.getDataOffsetY();         }
			@Override
			public int   getDataSelectionSquareWidth()  { return frame.getDataSelectionWidth();  }
			@Override
			public int   getDataSelectionSquareHeight() { return frame.getDataSelectionHeight(); }
			@Override
			public Color getRenderTint() 				{ return tint;                           }
			@Override
			public float getGraphicalX()                { return x; }
			@Override
			public float getGraphicalY()                { return y; }
		});
	}
	
	/**
	 * Render the debug state of the physics engine, as it exists in the given world.
	 */
	public static void setDebug(World world) {
		physicsDebugRender = world;
	}
	
	/**
	 * If accepted, resets the screenshot timer, removes all GUI, and snaps a picture.
	 */
	public static void requestScreenshot() {
		if(screenshotTimer >= SCREENSHOT_TIME) {
			screenshotTimer = 0.0f;
			takingShot      = true;
		}
	}
	
	/**
     * Sets the target position for the camera to smoothly transition to.
     */
	public static void setCameraTargetPosition(float x, float y) {
		CAMERA_X.set(x);
		CAMERA_Y.set(y);
	}
	
	/**
	 * Force moves the camera it's current target position in the given direction.
	 */
	public static void forceMoveCameraTargetPosition(float dx, float dy) {
		forceCameraPosition(CAMERA_X.getTarget() + dx, CAMERA_Y.getTarget() + dy);
	}
	
	/**
     * Immediately sets the camera's position to the specified coordinates.
     */
	public static void forceCameraPosition(float x, float y) {
		CAMERA_X.force(x);
		CAMERA_Y.force(y);
	}
	
	/**
     * Sets the speed of the camera's movement towards its target position and zoom.
     */
	public static void setCameraSpeedToTarget(float speed) {
		CAMERA_X.setSpeed(speed);
		CAMERA_Y.setSpeed(speed);
	}
	
	/**
	 * Requests to zoom the camera to a factor of 2^level.
	 */
	public static void setZoomLevel(int level) {
		if(currentZoomTwoFactor == level)
			return;
		currentZoomTwoFactor = level;
		final float val      = ((level < 0) ? (1f / (1 << Math.abs(level))) : (1 << level));
		CAMERA_ZOOM.set(val);
	}
	
	/**
	 * Returns the immediate mode renderer responsible for rendering shapes.
	 */
	public static ShapeRenderer getGeometricRenderer() {
		return GEOMETRIC_RENDERER;
	}
	
	/**
	 * Changes the post blur state from true to false or false to true. Does nothing if the current state is the same as requested.
	 */
	public static void requestBlurState(boolean on) {
		postFxBlurStatePointer += (on) ? 1 : -1;
		if(on) {
			if(postFxBlurState == 1)
				return;
			postFxBlurState = 1;
			postFxBlurPasses.set(0x10);
			POST_PROC_BUFFER.addEffect(POST_GAUSSIAN_BLUR);
			POST_PROC_BUFFER.addEffect(POST_RADIAL_DIST);
		} else {
			if(postFxBlurStatePointer > 0)
				return;
			if(postFxBlurState == 0 || postFxBlurState == 2)
				return;
			postFxBlurState = 2;
			postFxBlurPasses.set(0x0);
		}
	}
	
	///////////////////////////////////////////////////////
	// END API
	///////////////////////////////////////////////////////
	
	public void render(TUserInterface ui, float dt) {
		sortAndCombineRenderRequests();
		calculateTileBatchPool();
		calculatePerspective();
		clearScreen();
		calculateFx(dt);
		if(shouldUsePost()) {
			POST_PROC_BUFFER.cleanUpBuffers(new Color(0.15f, 0.15f, 0.2f, 1f));
			POST_PROC_BUFFER.beginInputCapture();
		}
		if(RENDER_REQUESTS.size != 0) {
			int batch = 0;
	        int tileIDInBatch = 0;
			TRenderable.TRendererType renderer = null;
			for(final TRenderable renderable : RENDER_REQUESTS) {
				// handle switching the rendering context...
				if(renderable.type != renderer) {
					// we have to switch the rendering context...
					switch(renderable.type) {
						case SPRITE:
							// switch from shape renderer to sprite renderer...
							if(renderer != null) {
								GEOMETRIC_RENDERER.end();
								Gdx.gl.glDisable(GL20.GL_BLEND);
							}
							SPRITE_BATCH_POOL.get(batch).begin();
							Gdx.gl.glEnable(GL20.GL_BLEND);
							Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
							break;
						case SHAPE:
							// switch from sprite renderer to shape renderer...
							if(renderer != null) {
								SPRITE_BATCH_POOL.get(batch).end();
								Gdx.gl.glDisable(GL20.GL_BLEND);
							}
							GEOMETRIC_RENDERER.begin(ShapeRenderer.ShapeType.Filled);
							GEOMETRIC_RENDERER.setAutoShapeType(true);
					        GEOMETRIC_RENDERER.setProjectionMatrix(WORLD_PROJ_MAT.combined);
					        Gdx.gl.glEnable(GL20.GL_BLEND);
							Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
							break;
					}
				}
				// here, the rendering context should be correct for the renderables type...
				switch(renderable.type) {
					case SPRITE:
						if(tileIDInBatch >= MAX_RENDERABLES) {
			        		SPRITE_BATCH_POOL.get(batch).end();
			        		batch++;
			        		tileIDInBatch = 0;
			        		SPRITE_BATCH_POOL.get(batch).setProjectionMatrix(WORLD_PROJ_MAT.combined);
			        		SPRITE_BATCH_POOL.get(batch).begin();
			        	}
						boolean worldMat = renderable.sprite.useWorldProjectionMatrix();
						SPRITE_BATCH_POOL.get(batch).setProjectionMatrix((worldMat) 
								? WORLD_PROJ_MAT.combined : SCREEN_PROJ_MAT.combined);
						renderable.sprite.render((worldMat) 
								? WORLD_PROJ_MAT : SCREEN_PROJ_MAT, SPRITE_BATCH_POOL.get(batch));
			        	tileIDInBatch++;
						break;
					case SHAPE:
						GEOMETRIC_RENDERER.set(ShapeRenderer.ShapeType.Filled);
						renderable.shape.drawFilled(WORLD_PROJ_MAT, GEOMETRIC_RENDERER);
						GEOMETRIC_RENDERER.set(ShapeRenderer.ShapeType.Line);
						renderable.shape.drawLined(WORLD_PROJ_MAT, GEOMETRIC_RENDERER);
						break;
				}
				renderer = renderable.type;
			}
			if(renderer == TRenderable.TRendererType.SPRITE)
				SPRITE_BATCH_POOL.get(batch).end();
			else GEOMETRIC_RENDERER.end();
			Gdx.gl.glDisable(GL20.GL_BLEND);
			if(physicsDebugRender != null)
				PHYSICS_RENDERER.render(physicsDebugRender, WORLD_PROJ_MAT.combined);
		}
		if(screenshotTimer < SCREENSHOT_TIME) {
			screenshotTimer += dt;
			if(screenshotTimer >= (SCREENSHOT_TIME / 2f) && takingShot) {
				// actually take the shot...
				writeFrameBufferToPersistent();
				takingShot = false;
			}
		} else {
			if(shouldUsePost()) {
				POST_PROC_BUFFER.endInputCapture();
				POST_PROC_BUFFER.applyEffects();
				POST_PROC_BUFFER.renderToScreen();
			}
			if(TInput.focused && Gdx.input.isCursorCatched()) {
				ui.render(dt);
				UI_BATCH.begin();
				UI_BATCH.setProjectionMatrix(SCREEN_PROJ_MAT.combined);
				TInput.cursor.render(SCREEN_PROJ_MAT, UI_BATCH);
				UI_BATCH.end();
			} else {
				// Just render immediate mode text if not focused...
				focusTimer += dt;
				if(focusTimer >= FOCUS_BLINK_TIME) {
					focusRendered = !focusRendered;
					focusTimer = 0.0f;
				}
				if(focusRendered)
					TUserInterface.drawText(" CLICK TO FOCUS! ", Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 32, 0xffffffff, false, true);
				ui.renderImmediateModeText(dt);
			}
			screenshotTimer = SCREENSHOT_TIME;
		}
		flush();
	}
	
	/**
	 * Returns whether or not to render to a seperate buffer.
	 */
	private boolean shouldUsePost() {
		return (screenshotTimer >= SCREENSHOT_TIME) && (postFxBlurState != 0);
	}
	
	/**
	 * Called when the client resizes their screen.
	 */
	public void resize(int newWidth, int newHeight) {
		WORLD_PROJ_MAT.setToOrtho(false, newWidth, newHeight);
		SCREEN_PROJ_MAT.setToOrtho(false, newWidth, newHeight);
		WORLD_PROJ_MAT.update();
		SCREEN_PROJ_MAT.update();
    }
	
	/**
	 * Based on graphics module calls, calculate the state of the visual effects.
	 */
	private void calculateFx(float dt) {
		if(postFxBlurState == 0)
			return;
		postFxBlurPasses.update(dt);
		int passes = Math.round(postFxBlurPasses.get());
		POST_GAUSSIAN_BLUR.setPasses(Math.max(1, passes));
		POST_GAUSSIAN_BLUR.setAmount(2f);
		POST_RADIAL_DIST.setDistortion(postFxBlurPasses.get() / 128f);
		if(postFxBlurState == 2){
			if(passes <= 0x0) {
				POST_PROC_BUFFER.removeEffect(POST_GAUSSIAN_BLUR);
				POST_PROC_BUFFER.removeEffect(POST_RADIAL_DIST);
				postFxBlurState = 0;
			}
		}
	}
	
	/**
	 * Writes a snapshot of the frame buffer to the Terrafort screenshot directory.
	 */
	private void writeFrameBufferToPersistent() {
		try {
	        final int    width  = Gdx.graphics.getWidth();
	        final int    height = Gdx.graphics.getHeight();
	        final byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, width, height, true);
	        for (int i = 4; i < pixels.length; i += 4)
	            pixels[i - 1] = (byte)255;
	        final Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
	        BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
	        final DateTimeFormatter formatter      = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
	        final String            dateTimeString = LocalDateTime.now().format(formatter);
	        final String            filename       = dateTimeString + ".png";
	        final FileHandle        file           = Gdx.files.local(TPersistent.ROOT + "/screenshots/" + filename);
	        PixmapIO.writePNG(file, pixmap);
	        System.out.println("[Terrafort Game Engine] Saved screenshot to " + file.path());
	        pixmap.dispose();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Calculates the perspective of the camera based on the desired position and zoom. This method also minimizes the amount of visual artifacts that arise from 
	 * tile position rounding errors.
	 */
	private void calculatePerspective() {
		float dt = (float)TClock.dt();
		CAMERA_X.update(dt);
	    CAMERA_Y.update(dt);
	    float z                            = CAMERA_ZOOM.getTarget();
	    float effectivePixelsPerUnit       = 1f / z;
	    float screenWidthInWorldUnits      = Gdx.graphics.getWidth() / effectivePixelsPerUnit;
	    float screenHeightInWorldUnits     = Gdx.graphics.getHeight() / effectivePixelsPerUnit;
	    float halfScreenWidthInWorldUnits  = screenWidthInWorldUnits / 2f;
	    float halfScreenHeightInWorldUnits = screenHeightInWorldUnits / 2f;
	    float x                            = CAMERA_X.get();
	    float y                            = CAMERA_Y.get();
	    float rx                           = TMath.roundTo(x + halfScreenWidthInWorldUnits, effectivePixelsPerUnit) - halfScreenWidthInWorldUnits;
	    float ry                           = TMath.roundTo(y + halfScreenHeightInWorldUnits, effectivePixelsPerUnit) - halfScreenHeightInWorldUnits;
	    WORLD_PROJ_MAT.position.set(rx, ry, 0);
	    WORLD_PROJ_MAT.zoom = z;
	    WORLD_PROJ_MAT.update();
	}
	
	/**
	 * Calculates the dynamic allocation (or deallocation) of sprite batches according to the number of tile render requests.
	 */
	private void calculateTileBatchPool() {
		final int requestedSprites = SPRITE_REQUESTS.size;
		final int neededBatches    = (int)Math.ceil((float)requestedSprites / MAX_RENDERABLES);
		// if no batches are needed we know we can dispose of every available batch...
		if(neededBatches == 0) {
			for(final SpriteBatch batch : SPRITE_BATCH_POOL)
				batch.dispose();
			SPRITE_BATCH_POOL.clear();
			return;
		}
		final int currentPoolSize = SPRITE_BATCH_POOL.size;
		// we either need to allocate or deallocate memory here...
		if(currentPoolSize != neededBatches) {
			final int difference = neededBatches - currentPoolSize;
			if(difference < 0) {
				// destory only batches that will not be used next frame...
				int amt = Math.abs(difference);
				for(int i = currentPoolSize - 1; i > (currentPoolSize - 1) - amt; i--) {
					SPRITE_BATCH_POOL.get(i).dispose();
					SPRITE_BATCH_POOL.pop();
				}
			} else {
				// create new batches that are needed next frame...
				for(int i = 0; i < difference; i++)
					SPRITE_BATCH_POOL.add(new SpriteBatch(MAX_RENDERABLES));
			}
		}
		// TEngine.mTileBatches = neededBatches;
	}
	
	/**
	 * Sorts the sprite requests by y, combines both shape and sprites, then sorts all by depth.
	 */
	private void sortAndCombineRenderRequests() {
		SPRITE_REQUESTS.sort((r1, r2) -> Float.compare(r2.getGraphicalY(), r1.getGraphicalY()));
		for(final TRenderableSprite sprite : SPRITE_REQUESTS)
	        RENDER_REQUESTS.add(new TRenderable(sprite));
	    for(final TRenderableShape shape : GEOMETRIC_REQUESTS)
	    	RENDER_REQUESTS.add(new TRenderable(shape));
	    RENDER_REQUESTS.sort(((r1, r2) -> Integer.compare(r2.getDepth(), r1.getDepth())));
	}
	
	/**
	 * Clears the screen, only OpenGL color buffer should be cleared.
	 */
	private void clearScreen() {
		Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
	
	/**
	 * Clears sprite render requests for the next frame.
	 */
	private static void flush() {
		SPRITE_REQUESTS.clear();
		GEOMETRIC_REQUESTS.clear();
		RENDER_REQUESTS.clear();
	}
	
	/**
	 * The public garbage collection function for the graphics module.
	 */
	public static void gc() {
		flush();
		disposeGraphicalBatches();
	}

	@Override
	public void dispose() {
		gc();
		disposeSpriteSheets();
		GEOMETRIC_RENDERER.dispose();
	}
	
	private static void disposeGraphicalBatches() {
		for(SpriteBatch b : SPRITE_BATCH_POOL)
			b.dispose();
		SPRITE_BATCH_POOL.clear();
	}
	
	private static void disposeSpriteSheets() {
		for(final TSpriteSheet s : SPRITE_SHEETS.values())
			s.dispose();
		SPRITE_SHEETS.clear();
	}

}
