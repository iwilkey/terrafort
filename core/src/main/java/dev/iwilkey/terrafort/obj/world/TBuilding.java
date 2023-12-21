package dev.iwilkey.terrafort.obj.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.item.TItemFunction;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.entity.mob.TPlayer;
import dev.iwilkey.terrafort.obj.entity.tile.TBuildingTile;
import dev.iwilkey.terrafort.obj.entity.tile.TLightTile;
import dev.iwilkey.terrafort.obj.entity.tile.TWoodTile;

/**
 * A {@link TWorld} utility to streamline the process of doing tile building calculations.
 * @author Ian Wilkey (iwilkey)
 */
public final class TBuilding {
	
	public enum TMaterial {
		WOOD,
		LIGHT
	}
	
	public static final float PLAYER_BUILDING_RANGE = 4.0f; // the maximum distance a player can place a tile from their position.
	
	/**
	 * Requests to place a {@link TBuildingTile} in the {@link TWorld} from the cursors perspective. 
	 * Returns the location the tile was placed, in world space.
	 */
	public static Vector2 place(TPlayer player, TItem tile, TMaterial material, int strength) {
		if(tile.is().getFunction() != TItemFunction.BULD)
			return null;
		final Vector2 tileLoc = cursorTileSelection(player);
		if(tileLoc != null) {
			if(!canPlaceAt(player, tileLoc))
				return null;
			switch(material) {
				case WOOD:
					player.getWorld().addObject(new TWoodTile(player.getWorld(), tile, (int)tileLoc.x, (int)tileLoc.y, strength));
					break;
				case LIGHT:
					player.getWorld().addObject(new TLightTile(player.getWorld(), tile, (int)tileLoc.x, (int)tileLoc.y, strength));
					break;
			}
			return new Vector2((int)tileLoc.x * TTerrain.TILE_WIDTH, (int)tileLoc.y * TTerrain.TILE_HEIGHT);
		}
		return null;
	}
	
	/**
	 * Query to see if the {@link TPlayer} can place a tile where their build cursor is.
	 */
	public static boolean canPlaceAt(TPlayer player, Vector2 tileLocation) {
		if(tileLocation == null)
			return false;
		if(player.getWorld().checkBuildingTileAt((int)tileLocation.x, (int)tileLocation.y) != null)
			return false;
		return true;
	}
	
	/**
	 * Query to see if the {@link TPlayer}'s build cursor is in range. Returns the users current tile selection in
	 * tile space, if in range. Null otherwise.
	 */
	public static Vector2 cursorTileSelection(TPlayer player) {
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
		if(dist <= PLAYER_BUILDING_RANGE * TTerrain.TILE_WIDTH)
			return new Vector2(tx, ty);
		return null;
	}
}
