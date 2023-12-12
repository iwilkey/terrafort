package dev.iwilkey.terrafort.obj.entity.element;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.gfx.TTerrainRenderer;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.entity.mob.TMob;
import dev.iwilkey.terrafort.obj.particulate.TParticle;
import dev.iwilkey.terrafort.obj.world.TSinglePlayerWorld;

/**
 * A tree. Found in grasslands.
 * @author Ian Wilkey (iwilkey)
 */
public final class TTree extends TNaturalElement {
	
	public static final int MAX_HP = 4;

	public TTree(TSinglePlayerWorld world, int tileX, int tileY) {
		super(world, 
		      false, 
		      tileX * TTerrainRenderer.TERRAIN_TILE_WIDTH, 
		      tileY * TTerrainRenderer.TERRAIN_TILE_HEIGHT, 
		      0, 
		      TTerrainRenderer.TERRAIN_TILE_WIDTH * 4, 
		      TTerrainRenderer.TERRAIN_TILE_HEIGHT * 4, 
		      TTerrainRenderer.TERRAIN_TILE_WIDTH / 2f, 
		      TTerrainRenderer.TERRAIN_TILE_HEIGHT / 3.5f,
		      3, 
		      (int)TMath.equalPick(3.0f, 5.0f),
			  2, 
			  2, 
			  Color.WHITE.cpy(), 
			  MAX_HP);
		setGraphicsColliderOffset(0, TTerrainRenderer.TERRAIN_TILE_HEIGHT * 1.75f);
	}

	@Override
	public void spawn() {

	}

	@Override
	public void task(float dt) {

	}
	
	@Override
	public void onInteraction(TMob interactee) {
		hurt(1);
		world.addObject(new TParticle(world, x, y, Color.BROWN));
	}

	@Override
	public void drops() {
		for(int i = 0; i < 8; i++)
			world.addObject(new TParticle(world, x, y + TMath.nextFloat(0.0f, height), Color.BROWN));
	}

}
