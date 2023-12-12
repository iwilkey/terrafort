package dev.iwilkey.terrafort.ui;

/**
 * A callback function given to UI widgets that participate in the Drag & Drop functionality
 * of the Terrafort UI engine. This function is used primarily for synchronization purposes, 
 * making sure representation is balanced and equal across all engine modules.
 * @author Ian Wilkey (iwilkey)
 *
 */
public interface TDroppable {
	/**
	 * Indication that a droppable UI widget has just undergone an accepted Drag & Drop transaction 
	 * that probably calls for internal synchronization.
	 */
	public void dropcall();
}
