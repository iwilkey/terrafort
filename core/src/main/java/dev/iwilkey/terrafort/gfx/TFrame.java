package dev.iwilkey.terrafort.gfx;

import java.io.Serializable;

/**
 * Specific selection on the Sprite Sheet.
 * @author Ian Wilkey (iwilkey)
 */
public class TFrame implements Serializable {

	private static final long serialVersionUID = -8489115798728072588L;
	
	private int x;
	private int y;
	private int w;
	private int h;
	
	public TFrame(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.w = width;
		this.h = height;
	}
	
	/**
	 * The X value (multiplied by {@link TGraphics.DATA_WIDTH}) of the Sprite Sheet selection.
	 * <p>
	 * Example: dataOffsetX = 2 means 2 * 16 = x pixel 32 of the Sprite Sheet.
	 * </p>
	 */
	public int getDataOffsetX() {
		return x;
	}
	
	/**
	 * The Y value (multiplied by {@link TGraphics.DATA_HEIGHT}) of the Sprite Sheet selection.
	 * <p>
	 * Example: dataOffsetY = 4 means 4 * 16 = y pixel 64 of the Sprite Sheet.
	 * </p>
	 */
	public int getDataOffsetY() {
		return y;
	}
	
	/**
	 * The width (multiplied by {@link TGraphics.DATA_WIDTH}) of the Sprite Sheet selection.
	 * <p>
	 * Example: dataSelectionWidth = 1 means 1 * 16 = from (xOff, yOff), the selection will proceed right by 16 pixels.
	 * </p>
	 */
	public int getDataSelectionWidth() {
		return w;
	}
	
	/**
	 * The width (multiplied by {@link TGraphics.DATA_HEIGHT}) of the Sprite Sheet selection.
	 * <p>
	 * Example: dataSelectionWidth = 2 means 2 * 16 = from (xOff, yOff), the selection will proceed down by 32 pixels.
	 * </p>
	 */
	public int getDataSelectionHeight() {
		return h;
	}
	
}
