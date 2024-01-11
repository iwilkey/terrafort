package dev.iwilkey.terrafort.gui;

/**
 * Enum defining the set of anchor points on the screen for positioning UI elements.
 * These constants are used to specify a UI element's location on the screen, ensuring 
 * it maintains its position relative to specific screen edges or the center, regardless of screen size.
 *
 * <p>The anchor points correspond to nine possible standardized locations on the screen:</p>
 * <ul>
 *   <li>{@code TOP_RIGHT} - The top-right corner of the screen.</li>
 *   <li>{@code TOP_CENTER} - The center point along the top edge of the screen.</li>
 *   <li>{@code TOP_LEFT} - The top-left corner of the screen.</li>
 *   <li>{@code CENTER_RIGHT} - The center point along the right edge of the screen.</li>
 *   <li>{@code CENTER_CENTER} - The absolute center of the screen.</li>
 *   <li>{@code CENTER_LEFT} - The center point along the left edge of the screen.</li>
 *   <li>{@code BOTTOM_RIGHT} - The bottom-right corner of the screen.</li>
 *   <li>{@code BOTTOM_CENTER} - The center point along the bottom edge of the screen.</li>
 *   <li>{@code BOTTOM_LEFT} - The bottom-left corner of the screen.</li>
 * </ul>
 *
 * <p>These anchor points are utilized to maintain the relative positioning of UI elements on the screen, 
 * especially when the screen undergoes transformations such as resizing. This ensures a consistent user 
 * experience across devices with different screen sizes and resolutions.</p>
 * @author Ian Wilkey (iwilkey)
 */
public enum TAnchor {
	TOP_RIGHT,
	TOP_CENTER,
	TOP_LEFT,
	CENTER_RIGHT,
	CENTER_CENTER,
	CENTER_LEFT,
	BOTTOM_RIGHT,
	BOTTOM_CENTER,
	BOTTOM_LEFT
}

