package dev.iwilkey.terrafort.item.canonical;

import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.item.TItemFunction;

/**
 * A canonical definition (or blueprint) for a Terrafort item.
 * @author Ian Wilkey (iwilkey)
 */
public abstract class TItemDefinition implements TItemAction {
	
	private final String        canonicalName;
	private final String        canonicalDescription;
	private final int           maxStackSize;
	private final TFrame        icon;
	private final TItemFunction function;
	private final long          baseSellValuePerUnit;
	private final long          baseBuyValuePerUnit;

	public TItemDefinition(final String        canonicalName,
						   final String        canonicalDescription,
						   final int           maxStackSize,
						   final TFrame        icon,
						   final TItemFunction function,
						   final long          baseSellValue,
						   final long          baseBuyValue) {
		this.canonicalName        = canonicalName;
		this.canonicalDescription = canonicalDescription;
		this.maxStackSize         = maxStackSize;
		this.icon                 = icon;
		this.function             = function;
		this.baseSellValuePerUnit = baseSellValue;
		this.baseBuyValuePerUnit  = baseBuyValue;
	}
	
	/**
	 * Returns the {@link TItem}s base sell value per unit.
	 */
	public final long getBaseSellValuePerUnit() {
		return baseSellValuePerUnit;
	}
	
	/**
	 * Returns the {@link TItem}s base buy value per unit.
	 */
	public final long getBaseBuyValuePerUnit() {
		return baseBuyValuePerUnit;
	}
	
	/**
	 * Returns the {@link TItem}s {@link TItemFunction}.
	 */
	public final TItemFunction getFunction() {
		return function;
	}
	
	/**
	 * Returns the {@link TFrame} associated with the item. It is the item's icon; it is meant to be
	 * a static, consistent graphical representation of the item no matter where it is rendered in the engine.
	 */
	public final TFrame getIcon() {
		return icon;
	}
	
	/**
	 * The canonical description of the {@link TItem}.
	 */
	public final String getDescription() {
		return canonicalDescription;
	}
	
	/**
	 * The canonical name of the {@link TItem}.
	 */
	public final String getName() {
		return canonicalName;
	}
	
	/**
	 * The total amount that the item can stack to. The idea is to make sure that small, insignificant items can stack higher than
	 * large, heavy items.
	 */
	public final int getStackSize() {
		return maxStackSize;
	}

}
