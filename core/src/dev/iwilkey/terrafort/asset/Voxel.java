package dev.iwilkey.terrafort.asset;

import com.badlogic.gdx.graphics.Color;

public final class Voxel {
	
	// Voxel position and color.
	private int x;
	private int y;
	private int z;
	private int rawColor;
	private Color color;
	// Voxel context in model.
	private boolean hasTopNeighbor = false;
	private boolean hasBottomNeighbor = false;
	private boolean hasForwardNeighbor = false;
	private boolean hasBackwardNeighbor = false;
	private boolean hasLeftNeighbor = false;
	private boolean hasRightNeighbor = false;
	
	public Voxel(int x, int y, int z, int color) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.rawColor = color;
		this.color = new Color();
		this.color.set(rawColor << 8);
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	public int getRawColor() {
		return rawColor;
	}
	
	public Color getColor() {
		return color;
	}

	public boolean isHasTopNeighbor() {
		return hasTopNeighbor;
	}

	public void setHasTopNeighbor(boolean hasTopNeighbor) {
		this.hasTopNeighbor = hasTopNeighbor;
	}

	public boolean isHasBottomNeighbor() {
		return hasBottomNeighbor;
	}

	public void setHasBottomNeighbor(boolean hasBottomNeighbor) {
		this.hasBottomNeighbor = hasBottomNeighbor;
	}

	public boolean isHasForwardNeighbor() {
		return hasForwardNeighbor;
	}

	public void setHasForwardNeighbor(boolean hasForwardNeighbor) {
		this.hasForwardNeighbor = hasForwardNeighbor;
	}

	public boolean isHasBackwardNeighbor() {
		return hasBackwardNeighbor;
	}

	public void setHasBackwardNeighbor(boolean hasBackwardNeighbor) {
		this.hasBackwardNeighbor = hasBackwardNeighbor;
	}

	public boolean isHasLeftNeighbor() {
		return hasLeftNeighbor;
	}

	public void setHasLeftNeighbor(boolean hasLeftNeighbor) {
		this.hasLeftNeighbor = hasLeftNeighbor;
	}

	public boolean isHasRightNeighbor() {
		return hasRightNeighbor;
	}

	public void setHasRightNeighbor(boolean hasRightNeighbor) {
		this.hasRightNeighbor = hasRightNeighbor;
	}

}
