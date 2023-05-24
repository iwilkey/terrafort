package dev.iwilkey.terrafort.gfx;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import dev.iwilkey.terrafort.object.GameObject3;
import dev.iwilkey.terrafort.state.State;

public final class StaticRenderableProviderBatchSystem implements Disposable {
	
	private final State state;
	private final Array<StaticRenderableProviderBatch> cache;
	
	public StaticRenderableProviderBatchSystem(final State state) {
		this.state = state;
		cache = new Array<>();
		createNewBuffer();
	}
	
	public void renderThroughShadowShader(final ModelBatch shader) {
		for(final StaticRenderableProviderBatch src : cache)
			shader.render(src);
	}
	
	public void render(final ModelBatch batch3, final Environment r3env) {
		for(final StaticRenderableProviderBatch src : cache)
			if(FrustumCulling.cullStaticRenderableProviderCache(src, state.getCamera()))
				batch3.render(src, r3env);
	}
	
	public void bake() {
		for(final StaticRenderableProviderBatch src : cache)
			src.bake(state.getCamera());
	}
	
	public void addStatic(GameObject3 obj) {
		if(!obj.isStatic()) {
			System.out.println("[Terrafort Engine] You cannot add a non-static GameObject3 to the StaticRenderableProviderCacheSystem! You must set the GameObject's isStatic flag.");
			return;
		}
		// Try to add to any not full cache.
		boolean added = cache.get(cache.size - 1).register(obj);
		if(!added) {
			createNewBuffer();
			cache.get(cache.size - 1).register(obj);
		}
	}
	
	public boolean removeStatic(GameObject3 obj) {
		if(!obj.isStatic()) {
			System.out.println("[Terrafort Engine] You cannot remove a non-static GameObject3 from the StaticRenderableProviderCacheSystem! You must set the GameObject's isStatic flag.");
			return false;
		}
		for(StaticRenderableProviderBatch src : cache) {
			if(src.unregister(obj)) {
				balanceCacheStructure();
				return true;
			}
		}
		return false;
	}
	
	public boolean contains(GameObject3 obj) {
		// Check if any of the caches is currently supporting the object passed.
		for(StaticRenderableProviderBatch src : cache) 
			if(src.contains(obj))
				return true;
		return false;
	}
	
	private void balanceCacheStructure() {
		// Balance the cache structure where the only not at capacity buffer is the last index of the cache list.
		// NOTE: This is actually pretty expensive time-wise (O(n^3)). We must find a better solution, perhaps by using
		// a PriorityQueue or Heap, etc.
	    for(int i = 0; i < cache.size - 1; i++) {
	        StaticRenderableProviderBatch currentCache = cache.get(i);
	        if(!currentCache.isFull()) {
	            for(int j = i + 1; j < cache.size; j++) {
	                StaticRenderableProviderBatch nextCache = cache.get(j);
	                GameObject3[] buffer = nextCache.getBuffer();
	                for(int k = 0; k < buffer.length; k++) {
	                    if(buffer[k] != null) {
	                        if(currentCache.register(buffer[k])) {
	                            nextCache.unregister(buffer[k]);
	                        }
	                        if(currentCache.isFull()) {
	                            break;
	                        }
	                    }
	                }
	                if(currentCache.isFull()) {
	                    break;
	                }
	            }
	        }
	    }
	    // Iterate in reverse order to avoid ConcurrentModificationException.
	    for(int i = cache.size - 1; i >= 0; i--)
	        if(cache.get(i).isEmpty()) 
	            cache.removeIndex(i);
	    // Finally, make sure that if there are no caches active, one must be ready to go.
	    if(cache.size == 0)
	    	createNewBuffer();
	}
	
	private void createNewBuffer() {
		cache.add(new StaticRenderableProviderBatch(cache.size));
	}
	
	public void printBalance() {
		System.out.println("Cache Structure balance");
	    for(int i = 0; i < cache.size; i++) {
	    	System.out.println(" > Cache ID: " + cache.get(i).getID() + ", cap: " + cache.get(i).getCurrentCapacity());
	    }
	}
	
	public final Array<StaticRenderableProviderBatch> getCacheStructure() {
		return cache;
	}

	@Override
	public void dispose() {
		for(StaticRenderableProviderBatch src : cache)
			src.dispose();
	}
	
}
