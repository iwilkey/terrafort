package dev.iwilkey.terrafort.object;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;

import dev.iwilkey.terrafort.asset.TerrafortAssetHandler;
import dev.iwilkey.terrafort.gfx.RenderableProvider25;
import dev.iwilkey.terrafort.state.State;

public abstract class GameObject25 extends GameObject implements RenderableProvider25 {
	
	private boolean shouldBillboard = true;
	private Decal decal;
	private Vector3 position;
	private float scale;
	
	public GameObject25(State state, String pathToLoadedTexture, boolean shouldBillboard) {
		super(state);
		decal = Decal.newDecal(new TextureRegion(TerrafortAssetHandler.getTexture(pathToLoadedTexture)), true);
		this.shouldBillboard = shouldBillboard;
		position = new Vector3();
		scale = 1.0f;
	}
	
	public GameObject25 setPosition(Vector3 pos) {
		setPosition(pos.x, pos.y, pos.z);
		return this;
	}
	
	public GameObject25 setPosition(float x, float y, float z) {
		position.set(x, y, z);
		decal.setPosition(position);
		return this;
	}
	
	public GameObject25 setRotation(Vector3 axis, float deg) {
		decal.setRotationX(deg * axis.x);
		decal.setRotationY(deg * axis.y);
		decal.setRotationZ(deg * axis.z);
		return this;
	}
	
	public GameObject25 setScale(float scale) {
		this.scale = scale;
		decal.setScale(scale);
		return this;
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
