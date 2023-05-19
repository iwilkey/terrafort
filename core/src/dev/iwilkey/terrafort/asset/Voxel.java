package dev.iwilkey.terrafort.asset;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public final class Voxel {
	
	private VoxelFaceGeometry upFace;
	private VoxelFaceGeometry downFace;
	private VoxelFaceGeometry forwardFace;
	private VoxelFaceGeometry backwardFace;
	private VoxelFaceGeometry leftFace;
	private VoxelFaceGeometry rightFace;
	
	private int x;
	private int y;
	private int z;
	private int rawColor;
	private Color color;
	
	public Voxel(int x, int y, int z, int color) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.rawColor = color;
		this.color = new Color();
		this.color.set(rawColor << 8);
	}
	
	public void calculateGeometry(float scale, float offX, float offY, float offZ) {
		float x = (this.x * scale) - offX;
		float y = (this.y * scale) - offY;
		float z = (this.z * scale) - offZ;
		Vector3 c00 = new Vector3(x, y + scale, z + scale);
    	Vector3 c01 = new Vector3(x + scale, y + scale, z + scale);
    	Vector3 c10 = new Vector3(x + scale, y + scale, z);
    	Vector3 c11 = new Vector3(x, y + scale, z);
    	Vector3 norm = new Vector3(0, 1, 0);
    	upFace = new VoxelFaceGeometry(c00.cpy(), c01.cpy(), c10.cpy(), c11.cpy(), norm.cpy());
    	c00 = new Vector3(x, y, z);
    	c01 = new Vector3(x + scale, y, z);
    	c10 = new Vector3(x + scale, y, z + scale);
    	c11 = new Vector3(x, y, z + scale);
    	norm = new Vector3(0, -1, 0);
    	downFace = new VoxelFaceGeometry(c00.cpy(), c01.cpy(), c10.cpy(), c11.cpy(), norm.cpy());
    	c00 = new Vector3(x, y, z + scale);
    	c01 = new Vector3(x + scale, y, z + scale);
    	c10 = new Vector3(x + scale, y + scale, z + scale);
    	c11 = new Vector3(x, y + scale, z + scale);
    	norm = new Vector3(0, 0, 1);
    	forwardFace = new VoxelFaceGeometry(c00.cpy(), c01.cpy(), c10.cpy(), c11.cpy(), norm.cpy());
    	c00 = new Vector3(x, y, z);
    	c01 = new Vector3(x, y + scale, z);
    	c10 = new Vector3(x + scale, y + scale, z);
    	c11 = new Vector3(x + scale, y, z);
    	norm = new Vector3(0, 0, -1);
    	backwardFace = new VoxelFaceGeometry(c00.cpy(), c01.cpy(), c10.cpy(), c11.cpy(), norm.cpy());
    	c00 = new Vector3(x, y, z);
    	c01 = new Vector3(x, y, z + scale);
    	c10 = new Vector3(x, y + scale, z + scale);
    	c11 = new Vector3(x, y + scale, z);
    	norm = new Vector3(-1, 0, 0);
    	leftFace = new VoxelFaceGeometry(c00.cpy(), c01.cpy(), c10.cpy(), c11.cpy(), norm.cpy());
    	c00 = new Vector3(x + scale, y, z + scale);
    	c01 = new Vector3(x + scale, y, z);
    	c10 = new Vector3(x + scale, y + scale, z);
    	c11 = new Vector3(x + scale, y + scale, z + scale);
    	norm = new Vector3(1, 0, 0);
    	rightFace = new VoxelFaceGeometry(c00.cpy(), c01.cpy(), c10.cpy(), c11.cpy(), norm.cpy());
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
	
	public VoxelFaceGeometry getUpFaceGeom() {
		return upFace;
	}
	
	public VoxelFaceGeometry getDownFaceGeom() {
		return downFace;
	}
	
	public VoxelFaceGeometry getForwardFaceGeom() {
		return forwardFace;
	}
	
	public VoxelFaceGeometry getBackwardFaceGeom() {
		return backwardFace;
	}
	
	public VoxelFaceGeometry getLeftFaceGeom() {
		return leftFace;
	}
	
	public VoxelFaceGeometry getRightFaceGeom() {
		return rightFace;
	}
		
}
