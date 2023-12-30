package dev.iwilkey.terrafort.item;

import java.io.Serializable;

import dev.iwilkey.terrafort.item.canonical.TItemDefinition;
import dev.iwilkey.terrafort.item.canonical.structure.*;
import dev.iwilkey.terrafort.item.canonical.weapon.*;
import dev.iwilkey.terrafort.item.canonical.lighting.*;
import dev.iwilkey.terrafort.item.canonical.natural.*;
import dev.iwilkey.terrafort.item.canonical.food.*;

/**
 * An enumeration of every possible item, resource, or material that can be collected, crafted and/or utilized in-game.
 * @author Ian Wilkey (iwilkey)
 */
public enum TItem implements Serializable {
		
	///////////////////////////////////////////////////////////////////////////
	// NATURAL
	///////////////////////////////////////////////////////////////////////////
	
	SHELL(new TShellItemItem()),
	LOG(new TLogItem()),
	COAL(new TCoalItem()),
	ROCK(new TRockItem()),
	COPPER(new TCopperItem()),
	SILVER(new TSilverItem()),
	GOLD(new TGoldItem()),
	
	///////////////////////////////////////////////////////////////////////////
	// STRUCTURE
	///////////////////////////////////////////////////////////////////////////
	
	WOOD_WALL(new TWoodWallItem()),
	STONE_WALL(new TStoneWallItem()),
	WOOD_FLOOR(new TWoodFloorItem()),
	STONE_TILE_FLOOR(new TStoneFloorItem()),
	BROWN_CARPET(new TBrownCarpetItem()),
		
	///////////////////////////////////////////////////////////////////////////
	// LIGHTING
	///////////////////////////////////////////////////////////////////////////
	
	TORCH(new TTorchItem()),
	
	///////////////////////////////////////////////////////////////////////////
	// TURRETS / WEAPONS
	///////////////////////////////////////////////////////////////////////////
	
	RUSTY_TURRET(new TRustyTurretItem()),
	SILVER_TURRET(new TSilverTurretItem()),
	GOLDEN_TURRET(new TGoldenTurretItem()),
	THROWING_AXE(new TThrowingAxeItem()),
	
	///////////////////////////////////////////////////////////////////////////
	// FOOD
	///////////////////////////////////////////////////////////////////////////
	
	ESCARGOT(new TEscargotItem()),
	CHICKEN_WING(new TChickenWingItem());
	
	///////////////////////////////////////////////////////////////////////////
	// ENUM STRUCTURE (DO NOT MODIFY)
	///////////////////////////////////////////////////////////////////////////
	
	private final TItemDefinition canonical;
	
	TItem(final TItemDefinition canonical) {	
		this.canonical = canonical;
	}
	
	public TItemDefinition is() {
		return canonical;
	}
	
}
