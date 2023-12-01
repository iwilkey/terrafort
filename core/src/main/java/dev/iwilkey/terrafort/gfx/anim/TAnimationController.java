package dev.iwilkey.terrafort.gfx.anim;

import com.badlogic.gdx.utils.Array;

import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.obj.TEntity;

/**
 * A utility class that manages the state of a {@link TEntity}'s animations.
 * @author iwilkey
 */
public final class TAnimationController {
	
	private TEntity           targetEntity;
	private int               targetAnimation;
	private float             targetFrameRate;
	private TFrame            nullFrame;
	private Array<TAnimation> animations;
	private int               currentFrame;
	private float             time;

	public TAnimationController(TEntity entity) {
		animations      = new Array<>();
		targetAnimation = -1;
		targetEntity    = entity;
		nullFrame       = new TFrame(entity.getDataSelectionOffsetX(), 
				                     entity.getDataSelectionOffsetY(), 
				                     entity.getDataSelectionSquareWidth(), 
				                     entity.getDataSelectionSquareHeight());
		currentFrame    = 0;
		time            = 0.0f;
		targetFrameRate = 2;
	}
	
	/**
	 * Adds an animation to this controllers animation vector. Will not accept an animation with an existing label.
	 */
	public void addAnimation(TAnimation anim) {
		if(labelExists(anim.getLabel()))
			throw new IllegalArgumentException("An animation by the name of " + anim.getLabel() + " already exists in this TAnimationController vector!");
		animations.add(anim);
	}
	
	/**
	 * Returns true if a given label exists in the current animation vector of this controller.
	 */
	public boolean labelExists(String label) {
		for(TAnimation a : animations)
			if(a.getLabel().equals(label))
				return true;
		return false;
	}
	
	/**
	 * Sets the current animation of the animation controller. Must exist.
	 */
	public void setAnimation(final String label) {
		// currentFrame = 0;
		//time         = 0.0f;
		for(int i = 0; i < animations.size; i++) {
			final String alab = animations.get(i).getLabel();
			if(alab.equals(label)) {
				targetAnimation = i;
				return;
			}
		}
		System.err.println("No animation by the name of " + label + " is found active in an animation controllers animation vector.");
	}
	
	/**
	 * Set the target frame rate of the animation controller.
	 */
	public void setTargetFrameRate(float targetFrameRate) {
		targetFrameRate = Math.max(0, targetFrameRate);
		this.targetFrameRate = targetFrameRate;
	}
	
	/**
	 * Updates the animation controller. Should be called every frame.
	 */
	public void tick(float dt) {
		if(animations.size <= 0 || targetAnimation == -1) {
			targetEntity.updateRenderingSprite(nullFrame);
			return;
		}
		time += dt;
		if(time > (1.0f / (float)targetFrameRate)) {
			currentFrame++;
			currentFrame %= animations.get(targetAnimation).getLength();
			time = 0;
		}
		targetEntity.updateRenderingSprite(animations.get(targetAnimation).getFrame(currentFrame));
	}
	
}
