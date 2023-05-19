package dev.iwilkey.terrafort.asset;

import com.badlogic.gdx.math.Vector3;

public final class VoxelFaceGeometry {
	
	private Vector3 c00;
	private Vector3 c01;
	private Vector3 c10;
	private Vector3 c11;
	private Vector3 normal;
	
	public VoxelFaceGeometry() {
		c00 = new Vector3();
		c01 = new Vector3();
		c10 = new Vector3();
		c11 = new Vector3();
		normal = new Vector3();
	}
	
	public VoxelFaceGeometry(Vector3 c00, Vector3 c01, Vector3 c10, Vector3 c11, Vector3 normal) {
		this.c00 = c00;
		this.c01 = c01;
		this.c10 = c10;
		this.c11 = c11;
		this.normal = normal;
	}
	
	public void set00(Vector3 c00) {
		this.c00 = c00;
	}
	
	public void set01(Vector3 c01) {
		this.c01 = c01;
	}
	
	public void set10(Vector3 c10) {
		this.c10 = c10;
	}
	
	public void set11(Vector3 c11) {
		this.c11 = c11;
	}
	
	public void setNormal(Vector3 normal) {
		this.normal = normal;
	}
	
	public Vector3 get00() {
		return c00;
	}
	
	public Vector3 get01() {
		return c01;
	}
	
	public Vector3 get10() {
		return c10;
	}
	
	public Vector3 get11() {
		return c11;
	}
	
	public Vector3 getNormal() {
		return normal;
	}
}
