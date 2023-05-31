package dev.iwilkey.terrafort.asset;

import com.badlogic.gdx.utils.Array;

/**
 * The VoxelModel class represents a 3D model made up of voxels.
 * It stores the voxels that make up the model and provides methods to retrieve information about the model.
 */
public final class VoxelModel {

    private final Array<Voxel> voxels;

    private int width;
    private int height;
    private int depth;
    private float centerX;
    private float centerY;
    private float centerZ;

    /**
     * Constructs a new VoxelModel with the given array of voxels.
     *
     * @param voxels the array of voxels that make up the model
     */
    public VoxelModel(final Array<Voxel> voxels) {
        this.voxels = voxels;
        findContext();
        findDimensions();
    }

    /**
     * Sets the context for each voxel in the model.
     * TODO: This is O(n^2). It would be beneficial to find a solution with lower complexity.
     */
    private void findContext() {
        for (int i = 0; i < voxels.size; i++) {
            for (int j = 0; j < voxels.size; j++) {
                if (i != j) {
                    final int v1x = voxels.get(i).getX();
                    final int v1y = voxels.get(i).getY();
                    final int v1z = voxels.get(i).getZ();
                    final int v2x = voxels.get(j).getX();
                    final int v2y = voxels.get(j).getY();
                    final int v2z = voxels.get(j).getZ();
                    if (v1x == v2x && v1y == v2y + 1 && v1z == v2z)
                        voxels.get(i).setHasTopNeighbor();
                    if (v1x == v2x && v1y == v2y - 1 && v1z == v2z)
                        voxels.get(i).setHasBottomNeighbor();
                    if (v1x == v2x && v1y == v2y && v1z == v2z + 1)
                        voxels.get(i).setHasForwardNeighbor();
                    if (v1x == v2x && v1y == v2y && v1z == v2z - 1)
                        voxels.get(i).setHasBackwardNeighbor();
                    if (v1x == v2x + 1 && v1y == v2y && v1z == v2z)
                        voxels.get(i).setHasRightNeighbor();
                    if (v1x == v2x - 1 && v1y == v2y && v1z == v2z)
                        voxels.get(i).setHasLeftNeighbor();
                }
            }
        }
    }

    /**
     * Finds the dimensions of the voxel model.
     */
    private void findDimensions() {
        int maxX = Integer.MIN_VALUE;
        int minX = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE;
        for (final Voxel vox : voxels) {
            final int x = vox.getX();
            final int y = vox.getY();
            final int z = vox.getZ();
            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
            if (z < minZ) minZ = z;
            if (z > maxZ) maxZ = z;
        }
        width = maxX - minX;
        height = maxY - minY;
        depth = maxZ - minZ;
        centerX = maxX - (width / 2f);
        centerY = maxY - (height / 2f);
        centerZ = maxZ - (depth / 2f);
    }

    /**
     * Retrieves the array of voxels that make up the model.
     *
     * @return the array of voxels
     */
    public Array<Voxel> getVoxels() {
        return voxels;
    }

    /**
     * Retrieves the width of the voxel model.
     *
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Retrieves the height of the voxel model.
     *
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Retrieves the depth of the voxel model.
     *
     * @return the depth
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Retrieves the X-coordinate of the center of the voxel model.
     *
     * @return the X-coordinate of the center
     */
    public float getCenterX() {
        return centerX;
    }

    /**
     * Retrieves the Y-coordinate of the center of the voxel model.
     *
     * @return the Y-coordinate of the center
     */
    public float getCenterY() {
        return centerY;
    }

    /**
     * Retrieves the Z-coordinate of the center of the voxel model.
     *
     * @return the Z-coordinate of the center
     */
    public float getCenterZ() {
        return centerZ;
    }

}
