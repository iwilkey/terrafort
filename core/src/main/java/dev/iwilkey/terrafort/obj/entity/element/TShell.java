package dev.iwilkey.terrafort.obj.entity.element;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.gfx.TTerrainRenderer;
import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.entity.mob.TMob;
import dev.iwilkey.terrafort.obj.particulate.TItemDrop;
import dev.iwilkey.terrafort.obj.particulate.TParticle;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * A shell. Found in dunes.
 * @author Ian Wilkey (iwilkey)
 *
 */
public final class TShell extends TNaturalElement {
	
	public static final int MAX_HP = 1;
	
	public TShell(TWorld world, int tileX, int tileY) {
		super(world, 
		      false,
		      tileX * TTerrainRenderer.TERRAIN_TILE_WIDTH, 
		      tileY * TTerrainRenderer.TERRAIN_TILE_HEIGHT, 
		      1, 
		      TTerrainRenderer.TERRAIN_TILE_WIDTH / 2f, 
		      TTerrainRenderer.TERRAIN_TILE_HEIGHT / 2f, 
		      TTerrainRenderer.TERRAIN_TILE_WIDTH / 2f, 
		      TTerrainRenderer.TERRAIN_TILE_HEIGHT / 2f,
		      (int)TMath.equalPick(5.0f, 6.0f), 
		      (int)TMath.equalPick(7.0f, 9.0f),
			  1, 
			  1, 
			  Color.WHITE.cpy(), 
			  MAX_HP);
		setAsSensor();
		x += ThreadLocalRandom.current().nextDouble(-8.0f, 8.0f);
		y += ThreadLocalRandom.current().nextDouble(-8.0f, 8.0f);
	}

	@Override
	public void drops() {
		world.addObject(new TParticle(world, x, y, Color.YELLOW));
		world.addObject(new TItemDrop(world, x, y, TItem.SHELL));
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
	}
	
}
