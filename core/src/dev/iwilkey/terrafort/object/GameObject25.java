package dev.iwilkey.terrafort.object;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;

import dev.iwilkey.terrafort.asset.TerrafortAssetHandler;
import dev.iwilkey.terrafort.asset.registers.Textures;
import dev.iwilkey.terrafort.gfx.RenderableProvider25;
import dev.iwilkey.terrafort.state.State;

/**
 * A 2D object that is rendered in 3D space.
 * @author iwilkey
 */
public class GameObject25 extends GameObject implements RenderableProvider25 {
	
	private boolean shouldBillboard = true;
	private Decal decal;
	private Vector3 position;
	private float scale;
	
	/**
	 * Constructs a GameObject25 instance with the specified parameters.
	 * @param state the state of the game object
	 * @param pathToLoadedTexture the path to the loaded texture
	 * @param shouldBillboard determines if the game object should billboard
	 */
	public GameObject25(State state, String pathToLoadedTexture, boolean shouldBillboard) {
		super(state);
		decal = Decal.newDecal(new TextureRegion(TerrafortAssetHandler.getTexture(pathToLoadedTexture)), true);
		this.shouldBillboard = shouldBillboard;
		position = new Vector3();
		scale = 1.0f;
	}
	
	/**
	 * Constructs a GameObject25 instance with the specified parameters.
	 */
	public GameObject25(State state, Textures texture) {
		super(state);
		decal = Decal.newDecal(new TextureRegion(TerrafortAssetHandler.getTexture(texture.getFileHandle().name())), true);
		position = new Vector3();
		scale = 1.0f;
	}
	
	/**
	 * Constructs a GameObject25 instance with the specified parameters.
	 */
	public GameObject25(State state, Textures texture, boolean shouldBillboard) {
		super(state);
		decal = Decal.newDecal(new TextureRegion(TerrafortAssetHandler.getTexture(texture.getFileHandle().name())), true);
		this.shouldBillboard = shouldBillboard;
		position = new Vector3();
		scale = 1.0f;
	}
	
	/**
	 * Sets the position of the game object.
	 * @param pos the position vector to set
	 * @return the game object itself
	 */
	public GameObject25 setPosition(Vector3 pos) {
		setPosition(pos.x, pos.y, pos.z);
		return this;
	}
	
	/**
	 * Sets the position of the game object.
	 * @param x the X coordinate of the position
	 * @param y the Y coordinate of the position
	 * @param z the Z coordinate of the position
	 * @return the game object itself
	 */
	public GameObject25 setPosition(float x, float y, float z) {
		position.set(x, y, z);
		decal.setPosition(position);
		return this;
	}
	
	/**
	 * Sets the rotation of the game object.
	 * @param axis the rotation axis vector
	 * @param deg the rotation angle in degrees
	 * @return the game object itself
	 */
	public GameObject25 setRotation(Vector3 axis, float deg) {
		decal.setRotationX(deg * axis.x);
		decal.setRotationY(deg * axis.y);
		decal.setRotationZ(deg * axis.z);
		return this;
	}
	
	/**
	 * Sets the scale of the game object.
	 * @param scale the scale value
	 * @return the game object itself
	 */
	public GameObject25 setScale(float scale) {
		this.scale = scale;
		decal.setScale(scale);
		return this;
	}
	
	/**
	 * Returns the position of the game object.
	 * @return the position vector
	 */
	public Vector3 getPosition() {
		return position.cpy();
	}
	
	/**
	 * Returns the scale of the game object.
	 * @return the scale value
	 */
	public float getScale() {
		return scale;
	}

	@Override
	public Decal getDecal() {
		return decal;
	}

	@Override
	public boolean shouldBillboard() {
		return shouldBillboard;
	}

}
