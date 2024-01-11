package dev.iwilkey.terrafort.gui.container;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import dev.iwilkey.terrafort.gui.TUserInterface;

/**
 * A {@link TContainer} that exists in world-space.
 * @author Ian Wilkey (iwilkey)
 */
public abstract class TDynamicContainer extends TContainer implements Disposable {

	private Vector2 worldPos = null;
	
	/**
	 * Initiates a new dynamic container. Will not be added to the scene until explicity done so with {@link TUserInterface}{@code .mAllocContainer(this)}.
	 */
	public TDynamicContainer(Vector2 worldPos, Object... objReference) {
		super(objReference);
		this.worldPos = worldPos;
		pack(internal, objReference);
		window.add(internal);
	}
	
	/**
	 * Returns the target world position of the dynamic container.
	 */
	public final Vector2 getWorldPosition() {
		return worldPos;
	}

}
