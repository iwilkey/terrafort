package dev.iwilkey.terrafort.object;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import dev.iwilkey.terrafort.asset.TerrafortAssetHandler;
import dev.iwilkey.terrafort.asset.registers.Textures;
import dev.iwilkey.terrafort.gfx.Alignment;
import dev.iwilkey.terrafort.gfx.Anchor;
import dev.iwilkey.terrafort.gfx.RenderableProvider2;
import dev.iwilkey.terrafort.gfx.ViewportResizable;
import dev.iwilkey.terrafort.state.State;

/**
 * A 2D object that is rendered in 2D viewport space.
 * @author iwilkey
 */
public class GameObject2 extends GameObject implements RenderableProvider2, ViewportResizable {
	
	private TextureRegion bindedRaster;
	private Color color;
	private int x;
	private int y;
	private int width;
	private int height;
	private Anchor anchor;
	private int padX = Integer.MIN_VALUE;
	private int padY = Integer.MIN_VALUE;
	private float lerpCenter = Float.MIN_VALUE;
	
	/**
	 * Constructs a GameObject2 instance with the specified parameters.
	 * @param state the state of the game object
	 * @param pathToLoadedTexture the path to the loaded texture
	 * @param width the width of the game object
	 * @param height the height of the game object
	 * @param anchor the anchor position of the game object
	 */
	public GameObject2(State state, String pathToLoadedTexture, int width, int height, Anchor anchor) {
		super(state);
		initProperties(state, pathToLoadedTexture, width, height, anchor);
		anchor();
	}
	
	/**
	 * Constructs a GameObject2 instance with the specified parameters.
	 */
	public GameObject2(State state, Textures texture, int width, int height, Anchor anchor) {
		super(state);
		initProperties(state, texture.getFileHandle().name(), width, height, anchor);
		anchor();
	}
	
	/**
	 * Constructs a GameObject2 instance with the specified parameters.
	 * @param state the state of the game object
	 * @param pathToLoadedTexture the path to the loaded texture
	 * @param width the width of the game object
	 * @param height the height of the game object
	 * @param anchor the anchor position of the game object
	 * @param padX the padding on the X-axis
	 * @param padY the padding on the Y-axis
	 */
	public GameObject2(State state, String pathToLoadedTexture, int width, int height, Anchor anchor, int padX, int padY) {
		super(state);
		initProperties(state, pathToLoadedTexture, width, height, anchor);
		setPadding(new Vector2(padX, padY));
		anchor();
	}
	
	/**
	 * Constructs a GameObject2 instance with the specified parameters.
	 * @param state the state of the game object
	 * @param pathToLoadedTexture the path to the loaded texture
	 * @param width the width of the game object
	 * @param height the height of the game object
	 * @param anchor the anchor position of the game object
	 * @param lerpCenter the lerp center value
	 */
	public GameObject2(State state, String pathToLoadedTexture, int width, int height, Anchor anchor, float lerpCenter) {
		super(state);
		initProperties(state, pathToLoadedTexture, width, height, anchor);
		setLerpCenter(lerpCenter);
		anchor();
	}
	
	private void initProperties(State state, String pathToLoadedTexture, int width, int height, Anchor anchor) {
		bindedRaster = new TextureRegion(TerrafortAssetHandler.getTexture(pathToLoadedTexture));
		color = Color.WHITE;
		this.width = width;
		this.height = height;
		this.anchor = anchor;
	}

	@Override
	public TextureRegion getBindedRaster() {
		return bindedRaster;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public Color getTint() {
		return color;
	}
	
	/**
	 * Sets the tint color of the game object.
	 * @param color the color to set
	 * @return the game object itself
	 */
	public GameObject2 setTint(Color color) {
		this.color = color;
		return this;
	}
	
	/**
	 * Sets the width of the game object.
	 * @param width the width to set
	 * @return the game object itself
	 */
	public GameObject2 setWidth(int width) {
		this.width = width;
		return this;
	}
	
	/**
	 * Sets the height of the game object.
	 * @param height the height to set
	 * @return the game object itself
	 */
	public GameObject2 setHeight(int height) {
		this.height = height;
		return this;
	}
	
	/**
	 * Sets the anchor position of the game object.
	 * @param anchor the anchor position to set
	 * @return the game object itself
	 */
	public GameObject2 setAnchor(Anchor anchor) {
		this.anchor = anchor;
		return this;
	}
	
	/**
	 * Sets the padding of the game object.
	 * @param pad the padding vector to set
	 * @return the game object itself
	 */
	public GameObject2 setPadding(Vector2 pad) {
		lerpCenter = Float.MIN_VALUE;
		padX = (int)pad.x;
		padY = (int)pad.y;
		return this;
	}
	
	/**
	 * Sets the lerp center value of the game object.
	 * @param lerpCenter the lerp center value to set
	 * @return the game object itself
	 */
	public GameObject2 setLerpCenter(float lerpCenter) {
		padX = Integer.MIN_VALUE;
		padY = Integer.MIN_VALUE;
		this.lerpCenter = lerpCenter;
		return this;
	}
	
	/**
	 * Anchors the game object based on the specified parameters.
	 * @return the game object itself
	 */
	public GameObject2 anchor() {
		Vector2 pos;
		if(padX != Integer.MIN_VALUE || padY != Integer.MIN_VALUE && lerpCenter == Float.MIN_VALUE) 
			pos =  Alignment.alignGameObject2(this, anchor, padX, padY);
		else if(padX == Integer.MIN_VALUE && padY == Integer.MIN_VALUE && lerpCenter != Float.MIN_VALUE) 
			pos =  Alignment.alignGameObject2(this, anchor, lerpCenter);
		else 
			pos = Alignment.alignGameObject2(this, anchor);
		this.x = (int)pos.x;
		this.y = (int)pos.y;
		return this;
	}

	@Override
	public void onViewportResize(int newWidth, int newHeight) {
		anchor();
	}
}

