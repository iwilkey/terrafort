package dev.iwilkey.terrafort.obj.entity.vegetation;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.gfx.TTerrainRenderer;
import dev.iwilkey.terrafort.obj.entity.lifeform.TLifeform;
import dev.iwilkey.terrafort.obj.particle.TParticle;
import dev.iwilkey.terrafort.obj.world.TWorld;

public final class TTree extends TVegetation {
	
	public static final int MAX_HP = 8;

	public TTree(TWorld world, int tileX, int tileY) {
		super(world, 
		      false, 
		      tileX * TTerrainRenderer.TERRAIN_TILE_WIDTH, 
		      tileY * TTerrainRenderer.TERRAIN_TILE_HEIGHT, 
		      0, 
		      TTerrainRenderer.TERRAIN_TILE_WIDTH * 4, 
		      TTerrainRenderer.TERRAIN_TILE_HEIGHT * 4, 
		      TTerrainRenderer.TERRAIN_TILE_WIDTH / 2f, 
		      TTerrainRenderer.TERRAIN_TILE_HEIGHT / 2f,
		      8, 
		      0,
			  1, 
			  1, 
			  Color.WHITE.cpy(), 
			  MAX_HP);
		setGraphicsColliderOffset(0, TTerrainRenderer.TERRAIN_TILE_HEIGHT * 1.5f);
	}

	@Override
	public void spawn() {

	}

	@Override
	public void task(float dt) {

	}
	
	@Override
	public void onInteraction(TLifeform interactee) {
		hurt(1);
		world.addObject(new TParticle(world, x, y, Color.BROWN));
	}

	@Override
	public void drops() {
		for(int i = 0; i < 8; i++)
			world.addObject(new TParticle(world, x, y, Color.BROWN));
	}

}
