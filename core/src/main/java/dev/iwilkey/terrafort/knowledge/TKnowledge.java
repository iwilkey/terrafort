package dev.iwilkey.terrafort.knowledge;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import dev.iwilkey.terrafort.obj.mob.TPlayer;
import dev.iwilkey.terrafort.world.TWorld;

/**
 * A skill or ability that allows a Terrafort user to carry out tasks in the game world.
 * @author Ian Wilkey (iwilkey)
 */
public interface TKnowledge {
	
	/**
	 * The name of the knowledge.
	 */
	public String getName();
	
	/**
	 * The description of the knowledge.
	 */
	public String getDescription();
	
	/**
	 * The icon used to represent the knowledge.
	 */
	public Drawable getIcon();
	
	/**
	 * Whether or not this knowledge can be equipped and used, or if it is required to do learn some other complex task.
	 */
	public boolean practical();
	
	/**
	 * Procedure called while the knowledge is equipped.
	 */
	public void equipped();
	
	/**
	 * Called if this skill is practical and the player has the knowledge equipped. Let's the engine know if the use of the knowledge is allowed
	 * based on internal factors.
	 */
	public boolean requestPractice();
	
	/**
	 * Called if this skill is practical, the player has the knowledge equipped, is allowed to use it, and requests to do so.
	 */
	public void practice(TPlayer player, TWorld world);
	
	/**
	 * The amount of funds required to practice the skill, if practical.
	 */
	public long getPracticeValue();
	
	/**
	 * The amount of funds to learn this knowledge.
	 */
	public long getLearnValue();
	
}
