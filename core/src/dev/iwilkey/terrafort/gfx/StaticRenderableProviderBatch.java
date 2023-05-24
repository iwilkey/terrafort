package dev.iwilkey.terrafort.gfx;

import java.util.Arrays;

import com.badlogic.gdx.graphics.g3d.ModelCache;

import dev.iwilkey.terrafort.object.GameObject3;

public final class StaticRenderableProviderBatch extends ModelCache {
	
	private int id;
	private final GameObject3 buffer[];
	private int currentCapacity = 0;
	private boolean dirty = false;	
	
	public StaticRenderableProviderBatch(int id) {
		this.id = id;
		buffer = new GameObject3[Renderer.STATIC_RENDERABLE_BUFFER_SIZE];
		Arrays.fill(buffer, null);
	}
	
	public void bake(Camera camera) {
		if(!dirty)
			return;
		System.out.println("[Terrafort Engine] StaticRenderableCache #" + id + " is baking.");
		begin(camera);
		// Add non null go3s in buffer.
		for(int i = 0; i < Renderer.STATIC_RENDERABLE_BUFFER_SIZE; i++) {
			if(buffer[i] == null)
				break;
			add(buffer[i].getModelInstance());
		}
		end();
		dirty = false;
	}
	
	public boolean register(GameObject3 go3) {
		// If the buffer cannot fit another go, then return false.
		if(currentCapacity + 1 >= Renderer.STATIC_RENDERABLE_BUFFER_SIZE)
			return false;
		// Add the go to the buffer.
		buffer[currentCapacity] = go3;
		currentCapacity++;
		// Readies the cache to be baked.
		dirty = true;
		// Everything went to plan.
		return true;
	}
	
	public boolean unregister(GameObject3 go3) {
		// Find the object in the buffer.
		for(int i = 0; i < Renderer.STATIC_RENDERABLE_BUFFER_SIZE; i++) {
			if(buffer[i] != null) {
				if(buffer[i].equals(go3)) {
					// Dispose of it.
					go3.dispose();
					// Set the buffer null at index.
					buffer[i] = null;
					// Since one was removed, the capacity should be reduced.
					currentCapacity--;
					// Shift all non-null buffer spaces in front back one.
					for(int j = i + 1; j < Renderer.STATIC_RENDERABLE_BUFFER_SIZE; j++) {
						if (buffer[j] != null) {
	                        buffer[j - 1] = buffer[j];
	                        buffer[j] = null;
	                    } else {
	                        break;
	                    }
					}
					// Readies the cache to be baked, since a game object was removed.
					dirty = true;
					// Return true, as in, the object was active in the buffer and removed.
					return true;
				}
			} else {
				break;
			}
		}
		// The object could not be found in the list, so nothing was removed.
		return false;
	}
	
	public boolean contains(GameObject3 go3) {
		for(int i = 0; i < Renderer.STATIC_RENDERABLE_BUFFER_SIZE; i++) {
			if(buffer[i] != null) {
				if(buffer[i].equals(go3))
					return true;
			} else {
				break;
			}
		}
		return false;
	}
	
	public int getID() {
		return id;
	}
	
	public int getCurrentCapacity() {
		return currentCapacity;
	}
	
	public boolean isFull() {
		return currentCapacity == Renderer.STATIC_RENDERABLE_BUFFER_SIZE;
	}
	
	public boolean isEmpty() {
		return currentCapacity == 0;
	}
	
	public GameObject3[] getBuffer() {
		return buffer;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
	/**
	 * Dumps all the buffer contents into the garbage and readies for baking itself.
	 */
	public void dump() {
		Arrays.fill(buffer, null);
		dirty = true;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		Arrays.fill(buffer, null);
	}

}
