package dev.iwilkey.terrafort.gfx.anim;

import com.badlogic.gdx.utils.Array;

import dev.iwilkey.terrafort.gfx.TFrame;

/**
 * Managed by the {@link TAnimationController} system. Defines the frames of one animation.
 * @author Ian Wilkey (iwilkey)
 */
public final class TAnimation {
	
	private final String        label;
	private final Array<TFrame> frameData;
	
	/**
	 * Defines an animation by a label and a pack of frames.
	 */
	public TAnimation(String label, TFrame... frames) {
		if(frames.length <= 0)
			throw new IllegalArgumentException("The TFramePack of an animation cannot contain 0 packed frames!");
		this.label = label;
		frameData  = new Array<>();
		for(int i = 0; i < frames.length; i++)
			frameData.add(frames[i]);
	}
	
	/**
	 * Get the animation's label.
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * Returns the animation's {@link TFramePack} length.
	 */
	public int getLength() {
		return frameData.size;
	}
	
	/**
	 * Returns the animation's ith frame. If out of bounds, the first frame will return.
	 */
	public TFrame getFrame(int i) {
		if(i >= getLength())
			return frameData.get(0);
		return frameData.get(i);
	}
	
}
