package dev.iwilkey.terrafort.obj.entity.element;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.gfx.TTerrainRenderer;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.entity.lifeform.TLifeform;
import dev.iwilkey.terrafort.obj.particle.TParticle;
import dev.iwilkey.terrafort.obj.world.TSinglePlayerWorld;

/**
 * A boulder. Found on asphalt and stone; near mountains.
 * @author Ian Wilkey (iwilkey)
 */
public final class TBoulder extends TNaturalElement {
	
	public static final int MAX_HP = 8;

	public TBoulder(TSinglePlayerWorld world, int tileX, int tileY) {
		super(world, 
		      false,
		      tileX * TTerrainRenderer.TERRAIN_TILE_WIDTH, 
		      tileY * TTerrainRenderer.TERRAIN_TILE_HEIGHT, 
		      0, 
		      TTerrainRenderer.TERRAIN_TILE_WIDTH * 2, 
		      TTerrainRenderer.TERRAIN_TILE_HEIGHT * 2, 
		      TTerrainRenderer.TERRAIN_TILE_WIDTH / 1.5f, 
		      TTerrainRenderer.TERRAIN_TILE_HEIGHT / 3f,
		      3, 
		      (int)TMath.equalPick(7.0f, 9.0f),
			  2, 
			  2, 
			  Color.WHITE.cpy(), 
			  MAX_HP);
		setGraphicsColliderOffset(-2, TTerrainRenderer.TERRAIN_TILE_HEIGHT / 2f);
	}

	@Override
	public void drops() {
		for(int i = 0; i < 8; i++)
			world.addObject(new TParticle(world, x, y, Color.GRAY));
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
		for(int i = 0; i < 2; i++)
			world.addObject(new TParticle(world, x, y, Color.GRAY));
	}

}
