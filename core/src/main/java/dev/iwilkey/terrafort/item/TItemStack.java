package dev.iwilkey.terrafort.item;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import dev.iwilkey.terrafort.gfx.TGraphics;

/**
 * An abstract representation of a collection of in-game items, resources, or materials.
 * @author Ian Wilkey (iwilkey)
 */
public final class TItemStack {

	private final TItem         item;
	private final TextureRegion icon;
	
	private int amt;

	public TItemStack(TItem item) {
		amt       = 1;
		this.item = item;
		this.icon = new TextureRegion(TGraphics.DATA, 
									  item.getIcon().getDataOffsetX() * TGraphics.DATA_WIDTH,
									  item.getIcon().getDataOffsetY() * TGraphics.DATA_HEIGHT, 
									  item.getIcon().getDataSelectionWidth() * TGraphics.DATA_WIDTH,
									  item.getIcon().getDataSelectionHeight() * TGraphics.DATA_HEIGHT);
	}
	
	public void setAmount(int amt) {
		this.amt = amt;
	}
	
	public void incAmount() {
		amt++;
	}
	
	public void decAmount() {
		amt--;
	}
	
	public void incAmountBy(int amt) {
		this.amt += amt;
	}
	
	public void decAmountBy(int amt) {
		this.amt -= amt;
	}

	public int getAmount() {
		return amt;
	}

	public TItem getItem() {
		return item;
	}

	public TextureRegion getIcon() {
		return icon;
	}

}
