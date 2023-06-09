package dev.iwilkey.terrafort.asset;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;

import dev.iwilkey.terrafort.Terrafort;

/**
 * The VoxelModelLoader class provides utility methods to create and build voxel models from raw data.
 * It also manages voxel materials for rendering.
 */
public final class VoxelModelLoader {

    public static final int SHADER_ATTRIBUTES = Usage.Position | Usage.Normal;
    public static final HashMap<Integer, Material> VOXEL_MATERIAL = new HashMap<>();

    /**
     * Creates a voxel model from the raw voxel data.
     *
     * @param raw the raw voxel data
     * @return the voxel model
     */
    public static VoxelModel createVoxelModelFromRaw(final String raw) {
        Array<Voxel> voxels = new Array<>();
        String[] lines = raw.split("\n");
        for (int i = 0; i < lines.length; i++) {
            if (i == 0) {
                if (!lines[0].contains("# Goxel 0.12.0"))
                    Terrafort.fatal("The TerrafortAssetHandler has encountered raw Voxel Model data that is not in the correct Goxel 0.12.0 format!");
            } else if (i > 2) {
                try {
                    String[] values = lines[i].split(" ");
                    int x = Integer.parseInt(values[0]);
                    int y = Integer.parseInt(values[1]);
                    int z = Integer.parseInt(values[2]);
                    int rawColor = Integer.parseInt(values[3].substring(0, 6), 16);
                    Voxel voxel = new Voxel(x, z, y, rawColor);
                    voxels.add(voxel);
                } catch (Exception e) {
                    e.printStackTrace();
                    Terrafort.fatal("The TerrafortAssetHandler has encountered raw Voxel Model data that is corrupted!");
                }
            }
        }
        return new VoxelModel(voxels);
    }

    /**
     * Builds a model from the raw voxel data using the specified scale and rendering primitive.
     *
     * @param name               the name of the voxel model
     * @param raw                the raw voxel data
     * @param MODEL_SCALE        the scale of the model
     * @param RENDERING_PRIMITIVE the rendering primitive
     * @return the built model
     */
    public static Model buildModelFromRaw(String name, final String raw, final float MODEL_SCALE, final int RENDERING_PRIMITIVE) {
        // Voxel representation of raw data.
        final VoxelModel voxelModel = createVoxelModelFromRaw(raw);
        // Properties of VoxelModel.
        final float cx = voxelModel.getCenterX() * MODEL_SCALE;
        final float cy = voxelModel.getCenterY() * MODEL_SCALE;
        final float cz = voxelModel.getCenterZ() * MODEL_SCALE;
        // Begin building vertices and indices.
        final ModelBuilder builder = new ModelBuilder();
        builder.begin();
        // Build the model cube by cube, and make sure to perform greedy mesh culling.
        for (final Voxel vox : voxelModel.getVoxels()) {
            // Voxel properties.
            final float x = vox.getX() * MODEL_SCALE;
            final float y = vox.getY() * MODEL_SCALE;
            final float z = vox.getZ() * MODEL_SCALE;
            final int rawColor = vox.getRawColor();
            final Color color = vox.getColor();
            // Register the Voxel material, if applicable.
            registerVoxelMaterialForColor(rawColor, color);
            // Unique ID for every Voxel.
            final String uid = String.format(name + ",%.2f,%.2f,%.2f,%d_", x, y, z, rawColor);
            // Mesher for each face.
            MeshPartBuilder mesher;
            // Local geometric calculations.
            float xx = x - cx;
            float yy = y - cy;
            float zz = z - cz;
            float cxx = (MODEL_SCALE / 2f);
            float cyy = (MODEL_SCALE / 2f);
            float czz = (MODEL_SCALE / 2f);
            // Mesh faces with Greedy Meshing.
            if (!vox.isHasBottomNeighbor()) {
                mesher = builder.part(uid + "bottom", RENDERING_PRIMITIVE, SHADER_ATTRIBUTES, getVoxelMaterialForColor(rawColor));
                mesher.rect(xx - cxx, yy + cyy, zz + czz,
                        xx + cxx, yy + cyy, zz + czz,
                        xx + cxx, yy + cyy, zz - czz,
                        xx - cxx, yy + cyy, zz - czz,
                        0f, 1f, 0f);
            }
            if (!vox.isHasTopNeighbor()) {
                mesher = builder.part(uid + "top", RENDERING_PRIMITIVE, SHADER_ATTRIBUTES, getVoxelMaterialForColor(rawColor));
                mesher.rect(xx - cxx, yy - cyy, zz - czz,
                        xx + cxx, yy - cyy, zz - czz,
                        xx + cxx, yy - cyy, zz + czz,
                        xx - cxx, yy - cyy, zz + czz,
                        0f, -1f, 0f);
            }
            if (!vox.isHasBackwardNeighbor()) {
                mesher = builder.part(uid + "back", RENDERING_PRIMITIVE, SHADER_ATTRIBUTES, getVoxelMaterialForColor(rawColor));
                mesher.rect(xx - cxx, yy - cyy, zz + czz,
                        xx + cxx, yy - cyy, zz + czz,
                        xx + cxx, yy + cyy, zz + czz,
                        xx - cxx, yy + cyy, zz + czz,
                        0f, 0f, 1f);
            }
            if (!vox.isHasForwardNeighbor()) {
                mesher = builder.part(uid + "front", RENDERING_PRIMITIVE, SHADER_ATTRIBUTES, getVoxelMaterialForColor(rawColor));
                mesher.rect(xx + cxx, yy - cyy, zz - czz,
                        xx - cxx, yy - cyy, zz - czz,
                        xx - cxx, yy + cyy, zz - czz,
                        xx + cxx, yy + cyy, zz - czz,
                        0f, 0f, -1f);
            }
            if (!vox.isHasLeftNeighbor()) {
                mesher = builder.part(uid + "left", RENDERING_PRIMITIVE, SHADER_ATTRIBUTES, getVoxelMaterialForColor(rawColor));
                mesher.rect(xx + cxx, yy - cyy, zz + czz,
                        xx + cxx, yy - cyy, zz - czz,
                        xx + cxx, yy + cyy, zz - czz,
                        xx + cxx, yy + cyy, zz + czz,
                        1f, 0f, 0f);
            }
            if (!vox.isHasRightNeighbor()) {
                mesher = builder.part(uid + "right", RENDERING_PRIMITIVE, SHADER_ATTRIBUTES, getVoxelMaterialForColor(rawColor));
                mesher.rect(xx - cxx, yy - cyy, zz - czz,
                        xx - cxx, yy - cyy, zz + czz,
                        xx - cxx, yy + cyy, zz + czz,
                        xx - cxx, yy + cyy, zz - czz,
                        -1f, 0f, 0f);
            }
        }
        return builder.end();
    }

    /**
     * Registers a voxel material for the specified color.
     *
     * @param rawColor the raw color value
     * @param color    the color of the voxel
     */
    private static void registerVoxelMaterialForColor(int rawColor, Color color) {
        final Material mat = new Material(ColorAttribute.createDiffuse(color));
        VOXEL_MATERIAL.put(rawColor, mat);
    }

    /**
     * Retrieves the voxel material for the specified color.
     *
     * @param color the raw color value
     * @return the voxel material
     */
    private static Material getVoxelMaterialForColor(int color) {
        if (!VOXEL_MATERIAL.containsKey(color))
            Terrafort.fatal("The TerrafortAssetHandler has encountered a NullMaterial exception, which is where it cannot find the registered Material to match desired color!");
        return VOXEL_MATERIAL.get(color);
    }

}
