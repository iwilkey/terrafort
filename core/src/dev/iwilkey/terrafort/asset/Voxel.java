package dev.iwilkey.terrafort.asset;

import com.badlogic.gdx.graphics.Color;

/**
 * The Voxel class represents a voxel in a 3D model.
 * It stores the position and color information of the voxel.
 * It also keeps track of the voxel's context in the model, such as neighboring voxels.
 */
public final class Voxel {

    // Voxel position and color.
    private final int x;
    private final int y;
    private final int z;
    private final int rawColor;
    private final Color color;

    // Voxel context in model.
    private boolean hasTopNeighbor = false;
    private boolean hasBottomNeighbor = false;
    private boolean hasForwardNeighbor = false;
    private boolean hasBackwardNeighbor = false;
    private boolean hasLeftNeighbor = false;
    private boolean hasRightNeighbor = false;

    /**
     * Constructs a new Voxel with the specified position and color.
     *
     * @param x     the x-coordinate of the voxel
     * @param y     the y-coordinate of the voxel
     * @param z     the z-coordinate of the voxel
     * @param color the color of the voxel
     */
    public Voxel(int x, int y, int z, int color) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.rawColor = color;
        this.color = new Color();
        this.color.set(rawColor << 8);
    }

    /**
     * Retrieves the x-coordinate of the voxel.
     *
     * @return the x-coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Retrieves the y-coordinate of the voxel.
     *
     * @return the y-coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Retrieves the z-coordinate of the voxel.
     *
     * @return the z-coordinate
     */
    public int getZ() {
        return z;
    }

    /**
     * Retrieves the raw color value of the voxel.
     *
     * @return the raw color value
     */
    public int getRawColor() {
        return rawColor;
    }

    /**
     * Retrieves the color of the voxel.
     *
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Checks if the voxel has a top neighbor.
     *
     * @return true if it has a top neighbor, false otherwise
     */
    public boolean isHasTopNeighbor() {
        return hasTopNeighbor;
    }

    /**
     * Sets the voxel's top neighbor flag to true.
     */
    public void setHasTopNeighbor() {
        this.hasTopNeighbor = true;
    }

    /**
     * Checks if the voxel has a bottom neighbor.
     *
     * @return true if it has a bottom neighbor, false otherwise
     */
    public boolean isHasBottomNeighbor() {
        return hasBottomNeighbor;
    }

    /**
     * Sets the voxel's bottom neighbor flag to true.
     */
    public void setHasBottomNeighbor() {
        this.hasBottomNeighbor = true;
    }

    /**
     * Checks if the voxel has a forward neighbor.
     *
     * @return true if it has a forward neighbor, false otherwise
     */
    public boolean isHasForwardNeighbor() {
        return hasForwardNeighbor;
    }

    /**
     * Sets the voxel's forward neighbor flag to true.
     */
    public void setHasForwardNeighbor() {
        this.hasForwardNeighbor = true;
    }

    /**
     * Checks if the voxel has a backward neighbor.
     *
     * @return true if it has a backward neighbor, false otherwise
     */
    public boolean isHasBackwardNeighbor() {
        return hasBackwardNeighbor;
    }

    /**
     * Sets the voxel's backward neighbor flag to true.
     */
    public void setHasBackwardNeighbor() {
        this.hasBackwardNeighbor = true;
    }

    /**
     * Checks if the voxel has a left neighbor.
     *
     * @return true if it has a left neighbor, false otherwise
     */
    public boolean isHasLeftNeighbor() {
        return hasLeftNeighbor;
    }

    /**
     * Sets the voxel's left neighbor flag to true.
     */
    public void setHasLeftNeighbor() {
        this.hasLeftNeighbor = true;
    }

    /**
     * Checks if the voxel has a right neighbor.
     *
     * @return true if it has a right neighbor, false otherwise
     */
    public boolean isHasRightNeighbor() {
        return hasRightNeighbor;
    }

    /**
     * Sets the voxel's right neighbor flag to true.
     */
    public void setHasRightNeighbor() {
        this.hasRightNeighbor = true;
    }
}