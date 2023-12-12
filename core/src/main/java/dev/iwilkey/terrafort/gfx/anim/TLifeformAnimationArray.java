package dev.iwilkey.terrafort.gfx.anim;

import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.math.TMath;

/**
 * A special grouping of {@link TAnimation} that encapsulate and label all movement animation frames
 * for a {@link TMob}, following specific rules in the sprite sheet.
 * 
 * <p>
 * The rules are as follows:<br><br>
 * 
 * >> <strong>MOVEMENT FRAMES</strong>:<br>
 * - The amount of frames for each move direction should be of uniform length 3.<br>
 * - The first frame should be the idle for that direction, the second the left stride and the third, the right stride.<br>
 * - There should be 8 groups of frames, stacked vertically.<br><br>
 * 
 * >> <strong>ATTACK FRAMES</strong><br>
 * - The amount of frames for each attack direction should be of uniform length 2.<br>
 * - The first frame should be attack primer, the second should be blow.<br>
 * - There should be 8 groups of frames, stacked vertically.<br>
 * - Each group of frames should match up with the following order, same as Movement frames, given top to bottom:<br><br>
 * 
 * - MOVE/ATTACK SOUTH<br>
 * - MOVE/ATTACK SOUTH-EAST<br>
 * - MOVE/ATTACK EAST<br>
 * - MOVE/ATTACK NORTH-EAST<br>
 * - MOVE/ATTACK NORTH<br>
 * - MOVE/ATTACK NORTH-WEST<br>
 * - MOVE/ATTACK WEST<br>
 * - MOVE/ATTACK SOUTH-WEST<br>
 * </p>
 * 
 * See the official Sprite Sheet for an example.
 * 
 * @author Ian Wilkey (iwilkey)
 */
public final class TLifeformAnimationArray {
	
	public static final String LABELS[] = new String[8];
	
	static {
		LABELS[TMath.SOUTH]      = "move_south";
		LABELS[TMath.SOUTH_EAST] = "move_south_east";
		LABELS[TMath.EAST]       = "move_east";
		LABELS[TMath.NORTH_EAST] = "move_north_east";
		LABELS[TMath.NORTH]      = "move_north";
		LABELS[TMath.NORTH_WEST] = "move_north_west";
		LABELS[TMath.WEST]       = "move_west";
		LABELS[TMath.SOUTH_WEST] = "move_south_west";
	}
	
	private TAnimation idleAnimations[];
	private TAnimation movementAnimations[];
	private TFrame     attackFrames[][];
	
	public TLifeformAnimationArray(TFrame upperLeftFrameMovement, TFrame upperLeftFrameAttack) {
		movementAnimations = new TAnimation[8];
		idleAnimations     = new TAnimation[8];
		attackFrames       = new TFrame[8][2];
		for(int i = 0; i < 8; i++) {
			final TFrame frames[] = new TFrame[4];
			for(int j = 0; j < 4; j++) {
				int xOff = ((j % 2 == 0) ? 0 : ((j == 1) ? 1 : 2));
				frames[j] = new TFrame(upperLeftFrameMovement.getDataOffsetX() + xOff,
									   upperLeftFrameMovement.getDataOffsetY() + (i * 2),
									   1,
									   2);
			}
			movementAnimations[i] = new TAnimation(LABELS[i], frames);
			idleAnimations[i]     = new TAnimation(LABELS[i].replace("move_", "idle_"), frames[0]);
			attackFrames[i][0]    = new TFrame(upperLeftFrameAttack.getDataOffsetX(), upperLeftFrameMovement.getDataOffsetY() + (i * 2), 1, 2);
			attackFrames[i][1]    = new TFrame(upperLeftFrameAttack.getDataOffsetX() + 1, upperLeftFrameMovement.getDataOffsetY() + (i * 2), 1, 2);
		}
	}
	
	public void addToAnimationController(TAnimationController anim) {
		for(int i = 0; i < 8; i++) {
			anim.addAnimation(movementAnimations[i]);
			anim.addAnimation(idleAnimations[i]);
		}
	}
	
	public TFrame getAttackFrame(int direction, int frame) {
		return attackFrames[direction][frame];
	}
	
}
