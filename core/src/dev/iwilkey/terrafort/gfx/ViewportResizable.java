package dev.iwilkey.terrafort.gfx;

/**
 * The ViewportResizable interface defines a contract for objects that can be notified of viewport resize events.
 * Implementing classes should provide an implementation for the onViewportResize method.
 */
public interface ViewportResizable {
    
    /**
     * Called when the viewport is resized with the new width and height.
     *
     * @param newWidth  the new width of the viewport
     * @param newHeight the new height of the viewport
     */
    public void onViewportResize(int newWidth, int newHeight);
}
