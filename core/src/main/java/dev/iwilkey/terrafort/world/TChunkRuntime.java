package dev.iwilkey.terrafort.world;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.obj.runtime.TObjectRuntime;
import dev.iwilkey.terrafort.obj.type.TEntity;
import dev.iwilkey.terrafort.obj.type.TMob;
import dev.iwilkey.terrafort.obj.type.TObject;
import dev.iwilkey.terrafort.obj.type.TParticulate;

/**
 * A non-serializable chunk of world data that manages runtime and simulation behavior.
 * @author Ian Wilkey (iwilkey)
 */
public final class TChunkRuntime implements Disposable {

	private final TChunk                data;
	private final Array<TObjectRuntime> activeObjects;
	private final Array<TObjectRuntime> garbageObjects;
	private final Array<TObjectRuntime> transferredObjects;
	
	/**
	 * Initiates a new chunk physical, dictated by the given {@link TChunk}.
	 */
	public TChunkRuntime(TChunk data) {
		this.data          = data;
		activeObjects      = new Array<>();
		garbageObjects     = new Array<>();
		transferredObjects = new Array<>();
	}
	
	/**
	 * Updates the state of the active object's runtime.
	 */
	public void update(float dt) {
		for(final TObjectRuntime r : activeObjects) {
			r.tick(dt);
			if(r.getAbstract() instanceof TEntity) {
				// this means we have to monitor this object's health...
				final TEntity e = (TEntity)r.getAbstract();
				if(e.currentHealthPoints <= 0) {
					// this thing is dead! notify the abstract chunk, and take care of it on this end.
					e.death(r);
					data.removeAbstractObject(e);
					garbageObjects.add(r);
					continue;
				}
			} else if(r.getAbstract() instanceof TParticulate) {
				// this means we need to check if the particulate is done...
				final TParticulate p = (TParticulate)r.getAbstract();
				if(p.isDone()) {
					data.removeAbstractObject(p);
					garbageObjects.add(r);
					continue;
				}
			}
			// draw objects only within render distance...
			final float centerX = TGraphics.WORLD_PROJ_MAT.position.x;
			final float centerY = TGraphics.WORLD_PROJ_MAT.position.y;
			final int   cx      = (int)Math.round(centerX / TWorld.TILE_SIZE);
		    final int   cy      = (int)Math.round(centerY / TWorld.TILE_SIZE);
		    final int   tx      = r.getAbstract().currentTileX;
		    final int   ty      = r.getAbstract().currentTileY;
		    int x2 = (tx - cx) * (tx - cx);
        	int y2 = (ty - cy) * (ty - cy);
        	if((x2 + y2) <= (TWorld.RENDER_DISTANCE * TWorld.RENDER_DISTANCE))
        		TGraphics.draw(r);
			// handle chunk transfer if applicable...
			final long juris = r.getAbstract().getChunkJurisdiction();
			if(juris != data.getPositionHash()) {
				final TWorld world = data.getParent();
				// The goal is to never get rid of the physical body and force it to rebuild!
				if(world.getChunkData().containsKey(juris)) {
					world.getChunkData().get(juris).transferObject(r.getAbstract(), r);
					transferredObjects.add(r);
				} else garbageObjects.add(r);
				data.removeAbstractObject(r.getAbstract());
			}
		}
		handleTransfers();
		handleGarbageCollection();
	}
	
	/**
	 * Called when a chunk gets a new object request. Creates the physical representation and holds it
	 * in memory.
	 */
	public void createObjectRuntimeFromAbstract(TObject object) {
		activeObjects.add(object.create(data.getParent()));
	}
	
	/**
	 * Marks an object runtime for garbage collection using abstract object. Returns whether or not
	 * there was a record of the abstract object's runtime in the chunk.
	 */
	public boolean removeObjectRuntimeFromAbstract(TObject object) {
		for(final TObjectRuntime r : activeObjects)
			if(r.getAbstract().equals(object)) {
				garbageObjects.add(r);
				return true;
			}
		return false;
	}
	
	/**
	 * Adds an already existing runtime to the chunk runtime's jurisdiction.
	 */
	public void addObjectRuntime(TObjectRuntime runtime) {
		if(runtime.getAbstract() instanceof TMob) 
			throw new IllegalArgumentException("[Terrafort Game Engine] TMob's aren't managed by TChunks. They can only be managed by a TWorld. Please use TWorld.addObject(TMob) instead.");
		activeObjects.add(runtime);
	}
	
	/**
	 * Returns the physical world this chunk is active in.
	 */
	public World getPhysicalWorld() {
		return data.getParent().getPhysicalWorld();
	}

	@Override
	public void dispose() {
		activeObjects.clear();
		garbageObjects.clear();
		transferredObjects.clear();
	}

	/**
	 * Efficiently and safley removes transferred objects from this runtime's jurisdiction without notifying the physics engine.
	 */
	private void handleTransfers() {
		if(transferredObjects.size != 0) {
			activeObjects.removeAll(transferredObjects, false);
			transferredObjects.clear();
		}
	}
	
	/**
	 * Efficiently and safley disposes of objects that are no longer able to be active in the world. Notifies the physics engine
	 * by destroying the body.
	 */
	private void handleGarbageCollection() {
		if(garbageObjects.size != 0) {
			for(final TObjectRuntime r : garbageObjects) 
				data.getParent().getPhysicalWorld().destroyBody(r.getPhysical());
			activeObjects.removeAll(garbageObjects, false);
			garbageObjects.clear();
		}
	}
	
}
