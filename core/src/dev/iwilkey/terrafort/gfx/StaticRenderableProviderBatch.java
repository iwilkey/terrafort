package dev.iwilkey.terrafort.gfx;

import java.util.Arrays;

import com.badlogic.gdx.graphics.g3d.ModelCache;

import dev.iwilkey.terrafort.object.GameObject3;

/**
 * The StaticRenderableProviderBatch class represents a batch of static renderable objects.
 * It provides methods for registering, unregistering, and managing static renderable objects.
 * The batch can be baked to optimize rendering performance.
 */
public final class StaticRenderableProviderBatch extends ModelCache {

    private int id;
    private final GameObject3 buffer[];
    private int currentCapacity = 0;
    private boolean dirty = false;

    /**
     * Constructs a StaticRenderableProviderBatch with the given ID.
     *
     * @param id the ID of the batch
     */
    public StaticRenderableProviderBatch(int id) {
        this.id = id;
        buffer = new GameObject3[Renderer.STATIC_RENDERABLE_BUFFER_SIZE];
        Arrays.fill(buffer, null);
    }

    /**
     * Bakes the batch with the specified camera.
     * This method prepares the batch for rendering.
     *
     * @param camera the camera used for rendering
     */
    public void bake(Camera camera) {
        if (!dirty)
            return;
        System.out.println("[Terrafort Engine] StaticRenderableCache #" + id + " is baking.");
        begin(camera);
        // Add non-null GameObject3s from the buffer.
        for (int i = 0; i < Renderer.STATIC_RENDERABLE_BUFFER_SIZE; i++) {
            if (buffer[i] == null)
                break;
            add(buffer[i].getModelInstance());
        }
        end();
        dirty = false;
    }

    /**
     * Registers a GameObject3 in the batch.
     *
     * @param go3 the GameObject3 to register
     * @return true if the object was successfully registered, false if the buffer is full
     */
    public boolean register(GameObject3 go3) {
        // If the buffer cannot fit another object, return false.
        if (currentCapacity + 1 >= Renderer.STATIC_RENDERABLE_BUFFER_SIZE)
            return false;
        // Add the object to the buffer.
        buffer[currentCapacity] = go3;
        currentCapacity++;
        // Set the batch as dirty to be baked.
        dirty = true;
        // Object registration successful.
        return true;
    }

    /**
     * Unregisters a GameObject3 from the batch.
     *
     * @param go3 the GameObject3 to unregister
     * @return true if the object was successfully unregistered, false if the object was not found
     */
    public boolean unregister(GameObject3 go3) {
        // Find the object in the buffer.
        for (int i = 0; i < Renderer.STATIC_RENDERABLE_BUFFER_SIZE; i++) {
            if (buffer[i] != null) {
                if (buffer[i].equals(go3)) {
                    // Dispose of the object.
                    go3.dispose();
                    // Set the buffer entry to null.
                    buffer[i] = null;
                    // Decrease the current capacity.
                    currentCapacity--;
                    // Shift non-null buffer entries forward by one.
                    for (int j = i + 1; j < Renderer.STATIC_RENDERABLE_BUFFER_SIZE; j++) {
                        if (buffer[j] != null) {
                            buffer[j - 1] = buffer[j];
                            buffer[j] = null;
                        } else {
                            break;
                        }
                    }
                    // Set the batch as dirty since an object was removed.
                    dirty = true;
                    // Object unregistration successful.
                    return true;
                }
            } else {
                break;
            }
        }
        // The object was not found in the buffer.
        return false;
    }

    /**
     * Checks if the batch contains the specified GameObject3.
     *
     * @param go3 the GameObject3 to check
     * @return true if the batch contains the object, false otherwise
     */
    public boolean contains(GameObject3 go3) {
        for (int i = 0; i < Renderer.STATIC_RENDERABLE_BUFFER_SIZE; i++) {
            if (buffer[i] != null) {
                if (buffer[i].equals(go3))
                    return true;
            } else {
                break;
            }
        }
        return false;
    }

    /**
     * Returns the ID of the batch.
     *
     * @return the ID of the batch
     */
    public int getID() {
        return id;
    }

    /**
     * Returns the current capacity of the batch.
     *
     * @return the current capacity of the batch
     */
    public int getCurrentCapacity() {
        return currentCapacity;
    }

    /**
     * Checks if the batch is full.
     *
     * @return true if the batch is full, false otherwise
     */
    public boolean isFull() {
        return currentCapacity == Renderer.STATIC_RENDERABLE_BUFFER_SIZE;
    }

    /**
     * Checks if the batch is empty.
     *
     * @return true if the batch is empty, false otherwise
     */
    public boolean isEmpty() {
        return currentCapacity == 0;
    }

    /**
     * Returns the buffer of the batch.
     *
     * @return the buffer of the batch
     */
    public GameObject3[] getBuffer() {
        return buffer;
    }

    /**
     * Sets the ID of the batch.
     *
     * @param id the ID of the batch
     */
    public void setID(int id) {
        this.id = id;
    }

    /**
     * Dumps all the buffer contents and marks the batch as dirty.
     * The batch needs to be baked again after calling this method.
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
