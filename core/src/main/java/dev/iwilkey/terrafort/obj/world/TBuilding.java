package dev.iwilkey.terrafort.obj.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.entity.mob.TPlayer;
import dev.iwilkey.terrafort.obj.entity.tile.TBuildingTile;
import dev.iwilkey.terrafort.obj.entity.tile.TWoodenBuildingTile;

/**
 * A {@link TWorld} utility to streamline the process of doing tile building calculations.
 * @author Ian Wilkey (iwilkey)
 */
public final class TBuilding {
	
	public enum TMaterial {
		WOOD;
	}
	
	public static final float PLAYER_BUILDING_RANGE = 4.0f; // the maximum distance a player can place a tile from their position.
	
	/**
	 * Requests to place a {@link TBuildingTile} in the {@link TWorld} from the cursors perspective. 
	 * Returns whether or not the tile was placed.
	 */
	public static Vector2 place(TPlayer player, TItem tile, TMaterial material, int strength) {
		final Vector2 tsm  = TMath.translateScreenToTileCoordinates(Gdx.input.getX(), Gdx.input.getY());
		final int     tx   = (int)tsm.x;
		final int     ty   = (int)tsm.y;
		final float   twsx = tx * TTerrain.TILE_WIDTH;
		final float   twsy = ty * TTerrain.TILE_HEIGHT;
		final float   pwsx = player.getActualX();
		final float   pwsy = player.getActualY();
		final float   x2   = (twsx - pwsx) * (twsx - pwsx);
		final float   y2   = (twsy - pwsy) * (twsy - pwsy);
		final float   dist = (float)Math.sqrt(x2 + y2);
		if(dist <= PLAYER_BUILDING_RANGE * TTerrain.TILE_WIDTH) {
			// we have to do some extra stuff here, like check if there's another physical body there with
			// raycasting.
			
			if(player.getWorld().checkBuildingTileAt(tx, ty) != null)
				return null;
			
			switch(material) {
				case WOOD:
					player.getWorld().addObject(new TWoodenBuildingTile(player.getWorld(), tile, tx, ty, strength));
					break;
			}
		
			return new Vector2(twsx, twsy);
		}
		return null;
	}
	
}
