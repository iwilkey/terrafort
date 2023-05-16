package dev.iwilkey.terrafort.object;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;

import dev.iwilkey.terrafort.gfx.RenderableProvider25;
import dev.iwilkey.terrafort.state.State;

public abstract class GameObject25 extends GameObject implements RenderableProvider25 {
	
	private boolean shouldBillboard = true;
	private Decal decal;
	private Vector3 position;
	private float scale;
	
	public GameObject25(State state, String pathToLoadedTexture, boolean shouldBillboard) {
		super(state);
		Texture texture = (Texture)state.getAssetManager().get(pathToLoadedTexture);
		decal = Decal.newDecal(new TextureRegion(texture), true);
		this.shouldBillboard = shouldBillboard;
		position = new Vector3();
		scale = 1.0f;
	}
	
	public void setPosition(Vector3 pos) {
		setPosition(pos.x, pos.y, pos.z);
	}
	
	public void setPosition(float x, float y, float z) {
		position.set(x, y, z);
		decal.setPosition(position);
	}
	
	public void setRotation(Vector3 axis, float deg) {
		decal.setRotationX(deg * axis.x);
		decal.setRotationY(deg * axis.y);
		decal.setRotationZ(deg * axis.z);
	}
	
	public void setScale(float scale) {
		this.scale = scale;
		decal.setScale(scale);
	}
	
	public Vector3 getPosition() {
		return position.cpy();
	}
	
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
